package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.GameGift;
import models.GameGift.FeatureType;
import models.GameGift.GiftState;
import models.GameGift.GiftType;
import models.GameGift.RedeemType;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import common.utils.ImageUploadUtil;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import viewmodel.GameGiftVM;

public class GameController extends ImageUploadController {
    private static play.api.Logger logger = play.api.Logger.apply(GameController.class);

    static {
    	setImageUploadUtil(new ImageUploadUtil("game"));
    }
    
    public static Long getRedeemedUsersCount(Long giftId) {
    	GameGift gameGift = GameGift.findById(giftId);
        if (gameGift == null) {
            return -1L;
        }
        
        //return GameGiftRedeemTransaction.getRedeemedUsersCount(giftId);
        return -1L;
    }
    
    // Manage redeem
    // - deduct pts
    // - record transaction
    // - notify user
    
    /*
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
    */
    
    @Transactional
    public static Result addGameGift() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        Form<GameGift> gameGiftForm = DynamicForm.form(GameGift.class).bindFromRequest();
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        GameGift gameGift = gameGiftForm.get();
        try {
        	gameGift.featureType = FeatureType.valueOf(form.get("featureType"));
        } catch(Exception e) {
            return status(504, "PLEASE CHOOSE FEATURE TYPE");
        }
        
        try {
        	gameGift.redeemType = RedeemType.valueOf(form.get("redeemType"));
        } catch(Exception e) {
            return status(505, "PLEASE CHOOSE REDEEM TYPE");
        }
        
        try {
        	gameGift.giftType = GiftType.valueOf(form.get("giftType"));
        } catch(Exception e) {
            return status(506, "PLEASE CHOOSE GIFT TYPE");
        }
        
        try {
            String sd = form.get("startDate");
            gameGift.startDate = DateTime.parse(sd).toDate();
        } catch (Exception e) {
            return status(507, "PLEASE ENTER START DATE");
        }
        
        try {
            String ed = form.get("endDate");
            gameGift.endDate = DateTime.parse(ed).toDate();
        } catch (Exception e) {
            return status(507, "PLEASE ENTER END DATE");
        }
        
        if (gameGift.startDate.after(gameGift.endDate)) {
            return status(508, "START DATE AFTER END DATE");
        }
        
        gameGift.save();
        
        logger.underlyingLogger().info(loggedInUser+" saved gameGift ["+gameGift.id+"|"+gameGift.name+"]");
        return ok();
    }
    
    @Transactional
    public static Result updateGameGift() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        Long id = Long.parseLong(form.get("id"));
        GameGift gameGift = GameGift.findById(id);
        gameGift.name = form.get("name");
        gameGift.image = form.get("image");
        gameGift.imageThumb = form.get("imageThumb");

        String requiredPoints = form.get("requiredPoints");
        if (!StringUtils.isEmpty(requiredPoints)) {
        	gameGift.requiredPoints = Long.parseLong(requiredPoints);
        }
        
        String quantityTotal = form.get("quantityTotal");
        if (!StringUtils.isEmpty(quantityTotal)) {
        	gameGift.quantityTotal = Long.parseLong(quantityTotal);
        }
        
        String quantityAvailable = form.get("quantityAvailable");
        if (!StringUtils.isEmpty(quantityAvailable)) {
        	gameGift.quantityAvailable = Long.parseLong(quantityAvailable);
        }
        
        gameGift.description = form.get("description");
        gameGift.redeemInfo = form.get("redeemInfo");
        gameGift.expirationInfo = form.get("expirationInfo");
        gameGift.shippingInfo = form.get("shippingInfo");
        gameGift.customerCareInfo = form.get("customerCareInfo");
        gameGift.moreInfo = form.get("moreInfo");
        
        try {
        	gameGift.featureType = FeatureType.valueOf(form.get("featureType"));
        } catch(Exception e) {
            return status(504, "PLEASE CHOOSE FEATURE TYPE");
        }
        
        try {
        	gameGift.redeemType = RedeemType.valueOf(form.get("redeemType"));
        } catch(Exception e) {
            return status(505, "PLEASE CHOOSE REDEEM TYPE");
        }
        
        try {
        	gameGift.giftType = GiftType.valueOf(form.get("giftType"));
        } catch(Exception e) {
            return status(506, "PLEASE CHOOSE GIFT TYPE");
        }
        
        try {
            String sd = form.get("startDate");
            gameGift.startDate = DateTime.parse(sd).toDate();
        } catch (Exception e) {
            ;
        }
        
        try {
            String ed = form.get("endDate");
            gameGift.endDate = DateTime.parse(ed).toDate();
        } catch (Exception e) {
            ;
        }
        
        if (gameGift.startDate.after(gameGift.endDate)) {
            return status(508, "START DATE AFTER END DATE");
        }
        
        gameGift.update();
        
        logger.underlyingLogger().info(loggedInUser+" updated gameGift ["+gameGift.id+"|"+gameGift.name+"]");
        return ok();
    }
    
    @Transactional
    public static Result changeGameGiftState() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        Long id = Long.parseLong(form.get("id"));
        GameGift gameGift = GameGift.findById(id);
        
        try {
            String giftState = form.get("giftState");
            gameGift.giftState = GiftState.valueOf(giftState);
        } catch(Exception e) {
            return status(509, "GIFT STATE INCORRECT");
        }
        
        gameGift.update();
        
        logger.underlyingLogger().info(loggedInUser+" changed gameGift state ["+gameGift.id+"|"+gameGift.name+"|"+gameGift.giftState.name()+"]");
        return ok(Json.toJson(new GameGiftVM(gameGift)));
    }
    
    @Transactional
    public static Result getLatestGameGifts() {
        List<GameGift> gameGifts = GameGift.getLatestGameGifts();
        List<GameGiftVM> vms = new ArrayList<>();
        for (GameGift gameGift : gameGifts) {
            GameGiftVM vm = new GameGiftVM(gameGift);
            vms.add(vm);
        }
        return ok(Json.toJson(vms));
    }

    @Transactional
    public static Result searchGameGifts(String id, String name) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }

        List<GameGift> gameGifts = GameGift.getGameGifts(id, name);
        List<GameGiftVM> vms = new ArrayList<>();
        for (GameGift gameGift : gameGifts) {
        	GameGiftVM vm = new GameGiftVM(gameGift);
            vms.add(vm);
        }
        return ok(Json.toJson(vms));
    }
    
    @Transactional
    public static Result getDescriptionOfGameGift(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        GameGift gameGift = GameGift.findById(id);
        Map<String, String> description = new HashMap<>();
        description.put("description", gameGift.description);
        return ok(Json.toJson(description));
    }
    
    @Transactional
    public static Result deleteGameGift(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        GameGift gameGift = GameGift.findById(id);
        if (gameGift != null) {
        	gameGift.delete();
        }
        logger.underlyingLogger().info(loggedInUser+" deleted gameGift ["+id+"]");
        return ok();
    }
    
    @Transactional
    public static Result getGameGift(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        GameGift gameGift = GameGift.findById(id);
        return ok(Json.toJson(gameGift));
    }
    
    @Transactional
    public static Result infoGameGift(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        GameGift gameGift = GameGift.findById(id);
        return ok(Json.toJson(new GameGiftVM(gameGift)));
    }
    
    @Transactional
    public static Result uploadImage() {
        return ImageUploadController.uploadImage();
    }

    @Transactional
    public static Result getImage(Long year, Long month, Long date, String name) {
        return ImageUploadController.getImage(year, month, date, name);
    }
}