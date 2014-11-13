package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Campaign;
import models.Campaign.CampaignState;
import models.Campaign.CampaignType;

import org.apache.commons.io.FileUtils;
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

public class CampaignController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(CampaignController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("campaign");
    
    @Transactional
    public static Result addCampaign() {
    	final String value = session().get("NAME");
        if (value == null) {
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
        
        campaign.save();
        
        logger.underlyingLogger().info(value+" saved campaign ["+campaign.id+"|"+campaign.name+"]");
        return ok();
    }
    
    @Transactional
    public static Result updateCampaign() {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        Long id = Long.parseLong(form.get("id"));
        Campaign campaign = Campaign.findById(id);
        campaign.name = form.get("name");
        campaign.image = form.get("image");
        campaign.description = form.get("description");
        
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
        
        campaign.update();
        
        logger.underlyingLogger().info(value+" updated campaign ["+campaign.id+"|"+campaign.name+"]");
        return ok();
    }
    
    @Transactional
    public static Result changeCampaignState() {
        final String value = session().get("NAME");
        if (value == null) {
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
        
        logger.underlyingLogger().info(value+" changed campaign state ["+campaign.id+"|"+campaign.name+"|"+campaign.campaignState.name()+"]");
        return ok();
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
    	final String value = session().get("NAME");
        if (value == null) {
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
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        Campaign campaign = Campaign.findById(id);
        Map<String, String> description = new HashMap<>();
        description.put("description", campaign.description);
        return ok(Json.toJson(description));
    }
    
    @Transactional
    public static Result deleteCampaign(Long id) {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        Campaign campaign = Campaign.findById(id);
        if (campaign != null) {
            campaign.delete();
        }
        logger.underlyingLogger().info(value+" deleted campaign ["+id+"]");
        return ok();
    }
    
    @Transactional
    public static Result getCampaign(Long id) {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        Campaign campaign = Campaign.findById(id);
        return ok(Json.toJson(campaign));
    }
    
    @Transactional
    public static Result uploadImage() {
    	final String value = session().get("NAME");
        if (value == null) {
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
            logger.underlyingLogger().error(value+" failed to upload photo", e);
            return status(500);
        }

        String imageUrl = imageUploadUtil.getImageUrl(now, fileName);
        logger.underlyingLogger().info("uploadImage. imageUrl=" + imageUrl);

        Map<String, String> map = new HashMap<>();
        map.put("URL", imageUrl);
        logger.underlyingLogger().info(value+" uploaded photo - "+imageUrl);
        return ok(Json.toJson(map));
    }

    @Transactional
    public static Result getImage(Long year, Long month, Long date, String name) {
        String path = imageUploadUtil.getImagePath(year, month, date, name);
        return ok(new File(path));
    }
}