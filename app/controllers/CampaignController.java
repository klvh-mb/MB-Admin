package controllers;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Campaign;
import models.Campaign.AnnouncementType;
import models.CampaignActionsUser;
import models.Campaign.CampaignState;
import models.Campaign.CampaignType;
import models.CampaignWinner;
import models.CampaignWinner.WinnerState;
import models.Notification;
import models.User;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import common.utils.ImageUploadUtil;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import viewmodel.CampaignVM;
import viewmodel.CampaignWinnerVM;

public class CampaignController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(CampaignController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("campaign");
    
    public static Long getJoinedUsersCount(Long campaignId) {
        Campaign campaign = Campaign.findById(campaignId);
        if (campaign == null) {
            return -1L;
        }
        
        Long count = -1L;
        if (CampaignType.ACTIONS == campaign.campaignType) {
            count = CampaignActionsUser.getJoinedUsersCount(campaignId);
        } else if (CampaignType.QUESTIONS == campaign.campaignType) {
            // TODO
        } else if (CampaignType.VOTING == campaign.campaignType) {
            // TODO
        }
        
        return count;
    }
    
    @Transactional
    public static Result getCampaignWinners(Long campaignId) {
        List<CampaignWinner> winners = CampaignWinner.getWinners(campaignId);
        List<CampaignWinnerVM> vms = new ArrayList<>();
        for (CampaignWinner winner : winners) {
            CampaignWinnerVM vm = new CampaignWinnerVM(winner);
            vms.add(vm);
        }
        return ok(Json.toJson(vms));
    }
    
    @Transactional
    public static Result searchCampaignWinners(Long campaignId, String positions) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        Campaign campaign = Campaign.findById(campaignId);
        if (campaign == null) {
            logger.underlyingLogger().error(
                    String.format("[c=%d] Campaign not exists! Failed to search winners by positions", campaignId));
            return status(500, "Campaign not exist!");    
        }
        
        positions = positions.trim();
        
        // display winners if not a search
        if (StringUtils.isEmpty(positions)) {
            return getCampaignWinners(campaignId);
        }
        
        // remove duplicates
        Set<Long> set = new HashSet<Long>();
        String[] tokens = positions.split(",");
        for (String token : tokens) {
            try {
                Long position = Long.parseLong(token.trim());
                if (position > 0) {
                    set.add(position);
                }
            } catch (NumberFormatException e) {
                ;
            }
        }
        List<Long> pos = new ArrayList<Long>(set);
        
        List<CampaignWinnerVM> vms = new ArrayList<CampaignWinnerVM>(); 
        if (CampaignType.ACTIONS == campaign.campaignType) {
            List<CampaignActionsUser> campaignUsers = CampaignActionsUser.getCampaignUsersByPositions(campaignId, pos);
            if (campaignUsers != null) {
                for (CampaignActionsUser campaignUser : campaignUsers) {
                    // see if user already selected as winner
                    CampaignWinner winner = CampaignWinner.getWinner(campaignUser.campaignId, campaignUser.userId);
                    if (winner == null) {
                        vms.add(new CampaignWinnerVM(campaignUser.userId, campaignUser.campaignId));
                    } else {
                        vms.add(new CampaignWinnerVM(winner));
                    }
                }
            }
        } else if (CampaignType.QUESTIONS == campaign.campaignType) {
            // TODO
        } else if (CampaignType.VOTING == campaign.campaignType) {
            // TODO
        } 
        
        return ok(Json.toJson(vms));
    }
    
    @Transactional
    public static Result notifyWinners(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        Map<String, String> responseMap = new HashMap<>();
        Campaign campaign = Campaign.findById(id);
        if (campaign != null) {
            try {
                // send notifications
                List<CampaignWinner> winners = CampaignWinner.getWinners(campaign.id);
                List<Long> selectedWinnerIds = new ArrayList<Long>();
                for (CampaignWinner winner : winners) {
                    if (winner.winnerState == WinnerState.SELECTED) {
                        selectedWinnerIds.add(winner.userId);
                        
                        // update winner state
                        winner.winnerState = WinnerState.ANNOUNCED;
                        winner.note += formatWinnerNote(WinnerState.ANNOUNCED, "Notification sent");
                    }
                }
                
                if (!selectedWinnerIds.isEmpty()) {
                    Notification.createCampaignNotification(selectedWinnerIds, campaign);
                } else {
                    logger.underlyingLogger().error(selectedWinnerIds.size()+" SELECTED winners ("+winners.size()+" winners total) for campaign ["+id+"]");
                    responseMap.put("status", "ERROR");
                    responseMap.put("message", selectedWinnerIds.size()+" SELECTED winners ("+winners.size()+" winners total)");
                    return ok(Json.toJson(responseMap));
                }
                
                // update campaign state
                campaign.campaignState = CampaignState.ANNOUNCED;
                
                logger.underlyingLogger().info(loggedInUser+" notified "+selectedWinnerIds.size()+" SELECTED winners ("+winners.size()+" winners total) for campaign ["+id+"]");
                responseMap.put("status", "SUCCESS");
                responseMap.put("message", "Notified "+selectedWinnerIds.size()+" SELECTED winners ("+winners.size()+" winners total)");
                return ok(Json.toJson(responseMap));
            } catch (Exception e) {
                logger.underlyingLogger().error("Failed to send notifications to winners for campaign ["+id+"]");
                responseMap.put("status", "ERROR");
                responseMap.put("message", "Failed to send notifications to winners for campaign ["+id+"]");
                return ok(Json.toJson(responseMap));
            }
        }
        
        logger.underlyingLogger().info(loggedInUser+" failed to notify winners for campaign ["+id+"] - Campaign not found");
        responseMap.put("status", "ERROR");
        responseMap.put("message", "Failed to notify winners for campaign ["+id+"] - Campaign not found");
        return ok(Json.toJson(responseMap));
    }
    
    private static CampaignWinner selectCampaignWinner(Long campaignId, Long userId) {
       
        Campaign campaign = Campaign.findById(campaignId);
        User user = User.findById(userId);
        
        if (campaign == null) {
            logger.underlyingLogger().error(
                    String.format("[u=%d][c=%d] Campaign not exists! Failed to select winner", userId, campaignId));
            return null;    
        }
        
        if (user == null) {
            logger.underlyingLogger().error(
                    String.format("[u=%d][c=%d] User not exists! Failed to select winner", userId, campaignId));
            return null;
        }
        
        CampaignWinner winner = new CampaignWinner(campaignId, userId);
        winner.save();
        
        logger.underlyingLogger().info(
                String.format("[u=%d][c=%d] Selected winner for campaign", userId, campaignId));
        return winner;
    }
    
    private static String formatWinnerNote(WinnerState winnerState, String note) {
        final String loggedInUser = Application.getLoggedInUser();
        return String.format("[%s -> %s]\n%s: %s\n", DateFormat.getInstance().format(new Date()), winnerState, loggedInUser, note);
    }
    
    @Transactional
    public static Result changeWinnerState() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        Long id = Long.parseLong(form.get("id"));
        Long campaignId = Long.parseLong(form.get("campaignId"));
        Long userId = Long.parseLong(form.get("userId"));
        String winnerState = form.get("winnerState");
        String note = form.get("note");
        
        CampaignWinner winner = CampaignWinner.findById(id);
        if (winner == null) {
            winner = selectCampaignWinner(campaignId, userId);
        }
        
        try {
            winner.winnerState = WinnerState.valueOf(winnerState);
            if (!StringUtils.isEmpty(note)) {
                winner.note += formatWinnerNote(winner.winnerState, note);
            }
        } catch(Exception e) {
            logger.underlyingLogger().error(
                    String.format("[w=%d] Winner state incorrect! Failed to change winner state to %s", id, winnerState));
            return status(500, "WINNER STATE INCORRECT");
        }
        
        winner.update();
        
        logger.underlyingLogger().info(loggedInUser+" changed winner state ["+winner.id+"|"+winner.winnerState.name()+"]");
        return ok(Json.toJson(new CampaignWinnerVM(winner)));
    }
    
    @Transactional
    public static Result addCampaign() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        Form<Campaign> campaignForm = DynamicForm.form(Campaign.class).bindFromRequest();
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        Campaign campaign = campaignForm.get();
        if (!Campaign.checkTitleExists(campaign.name)) {
            return status(505, "PLEASE CHOOSE OTHER NAME");
        }
        
        try {
            campaign.campaignType = CampaignType.valueOf(form.get("campaignType"));
        } catch(Exception e) {
            return status(506, "PLEASE CHOOSE CAMPAIGN TYPE");
        }
        
        try {
            String sd = form.get("startDate");
            campaign.startDate = DateTime.parse(sd).toDate();
        } catch (Exception e) {
            return status(507, "PLEASE ENTER START DATE");
        }
        
        try {
            String ed = form.get("endDate");
            campaign.endDate = DateTime.parse(ed).toDate();
        } catch (Exception e) {
            return status(507, "PLEASE ENTER END DATE");
        }
        
        if (campaign.startDate.after(campaign.endDate)) {
            return status(508, "START DATE AFTER END DATE");
        }
        
        try {
            campaign.announcementType = AnnouncementType.valueOf(form.get("announcementType"));
        } catch(Exception e) {
            return status(509, "PLEASE CHOOSE ANNOUNCEMENT TYPE");
        }
        
        campaign.save();
        
        logger.underlyingLogger().info(loggedInUser+" saved campaign ["+campaign.id+"|"+campaign.name+"]");
        return ok();
    }
    
    @Transactional
    public static Result updateCampaign() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        Long id = Long.parseLong(form.get("id"));
        Campaign campaign = Campaign.findById(id);
        campaign.name = form.get("name");
        campaign.image = form.get("image");
        String description = form.get("description");
        if (!StringUtils.isEmpty(description)) {
            campaign.description = description;
        }
        String announcement = form.get("announcement");
        if (!StringUtils.isEmpty(announcement)) {
            campaign.announcement = announcement;
        }
        
        try {
            campaign.campaignType = CampaignType.valueOf(form.get("campaignType"));
        } catch(Exception e) {
            return status(506, "PLEASE CHOOSE CAMPAIGN TYPE");
        }
        
        try {
            String sd = form.get("startDate");
            campaign.startDate = DateTime.parse(sd).toDate();
        } catch (Exception e) {
            ;
        }
        
        try {
            String ed = form.get("endDate");
            campaign.endDate = DateTime.parse(ed).toDate();
        } catch (Exception e) {
            ;
        }
        
        if (campaign.startDate.after(campaign.endDate)) {
            return status(508, "START DATE AFTER END DATE");
        }
        
        try {
            campaign.announcementType = AnnouncementType.valueOf(form.get("announcementType"));
        } catch(Exception e) {
            return status(509, "PLEASE CHOOSE ANNOUNCEMENT TYPE");
        }
        
        campaign.update();
        
        logger.underlyingLogger().info(loggedInUser+" updated campaign ["+campaign.id+"|"+campaign.name+"]");
        return ok();
    }
    
    @Transactional
    public static Result changeCampaignState() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        Long id = Long.parseLong(form.get("id"));
        Campaign campaign = Campaign.findById(id);
        
        try {
            String campaignState = form.get("campaignState");
            campaign.campaignState = CampaignState.valueOf(campaignState);
        } catch(Exception e) {
            return status(509, "CAMPAIGN STATE INCORRECT");
        }
        
        campaign.update();
        
        logger.underlyingLogger().info(loggedInUser+" changed campaign state ["+campaign.id+"|"+campaign.name+"|"+campaign.campaignState.name()+"]");
        return ok(Json.toJson(new CampaignVM(campaign)));
    }
    
    @Transactional
    public static Result getLatestCampaigns() {
        List<Campaign> allCampaigns = Campaign.getLatestCampaigns();
        List<CampaignVM> listOfCampaigns = new ArrayList<>();
        for (Campaign campaign:allCampaigns) {
            CampaignVM vm = new CampaignVM(campaign);
            listOfCampaigns.add(vm);
        }
        return ok(Json.toJson(listOfCampaigns));
    }

    @Transactional
    public static Result searchCampaigns(String id, String name) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }

        List<Campaign> allCampaigns = Campaign.getCampaigns(id, name);
        List<CampaignVM> listOfCampaigns = new ArrayList<>();
        for (Campaign campaign:allCampaigns) {
            CampaignVM vm = new CampaignVM(campaign);
            listOfCampaigns.add(vm);
        }
        return ok(Json.toJson(listOfCampaigns));
    }
    
    @Transactional
    public static Result getDescriptionOfCampaign(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        Campaign campaign = Campaign.findById(id);
        Map<String, String> description = new HashMap<>();
        description.put("description", campaign.description);
        return ok(Json.toJson(description));
    }
    
    @Transactional
    public static Result deleteCampaign(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        Campaign campaign = Campaign.findById(id);
        if (campaign != null) {
            campaign.delete();
        }
        logger.underlyingLogger().info(loggedInUser+" deleted campaign ["+id+"]");
        return ok();
    }
    
    @Transactional
    public static Result getCampaign(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        Campaign campaign = Campaign.findById(id);
        return ok(Json.toJson(campaign));
    }
    
    @Transactional
    public static Result infoCampaign(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        Campaign campaign = Campaign.findById(id);
        return ok(Json.toJson(new CampaignVM(campaign)));
    }
    
    @Transactional
    public static Result uploadImage() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        FilePart picture = request().body().asMultipartFormData().getFile("url-photo0");
        String fileName = picture.getFilename();
        logger.underlyingLogger().info("uploadImage. fileName=" + fileName);

        DateTime  now = new DateTime();
        File file = picture.getFile();
        try {
            String imagePath = imageUploadUtil.getImagePath(now, fileName);
            FileUtils.copyFile(file, new File(imagePath));
        } catch (IOException e) {
            logger.underlyingLogger().error(loggedInUser+" failed to upload photo", e);
            return status(500);
        }

        String imageUrl = imageUploadUtil.getImageUrl(now, fileName);
        logger.underlyingLogger().info("uploadImage. imageUrl=" + imageUrl);

        Map<String, String> map = new HashMap<>();
        map.put("URL", imageUrl);
        logger.underlyingLogger().info(loggedInUser+" uploaded photo - "+imageUrl);
        return ok(Json.toJson(map));
    }

    @Transactional
    public static Result getImage(Long year, Long month, Long date, String name) {
        String path = imageUploadUtil.getImagePath(year, month, date, name);
        return ok(new File(path));
    }
}