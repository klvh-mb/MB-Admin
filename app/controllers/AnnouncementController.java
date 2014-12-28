package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import models.Announcement;
import models.Announcement.AnnouncementType;
import models.Icon;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.AnnouncementVM;

public class AnnouncementController extends Controller{
    private static final play.api.Logger logger = play.api.Logger.apply(AnnouncementController.class);
    
	@Transactional
	public static Result getAnnouncements(String title,int currentPage) {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
		long totalPages = Announcement.getAllAnnouncementsTotal(title, 10);
		List<Announcement> allAnnouncements = Announcement.getAllAnnouncements(title, currentPage, 10, totalPages);
			
		List<AnnouncementVM> listOfAnnouncements = new ArrayList<>();
		for (Announcement announcement:allAnnouncements) {
			AnnouncementVM vm = new AnnouncementVM(announcement);
			listOfAnnouncements.add(vm);
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", listOfAnnouncements);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result saveAnnouncement() {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
		Announcement announcement = new Announcement();
		announcement.setAnnouncementType(AnnouncementType.valueOf(form.get("announcementType")));
		announcement.description = form.get("description");
		if(form.get("ic.id") != null){
			announcement.icon = Icon.findById((Long.parseLong(form.get("icon"))));
		}
		announcement.title = form.get("title");
		try {
			String fd = form.get("fd");
            String td = form.get("td");
            announcement.fromDate = DateTime.parse(fd).toDate();
            announcement.toDate = DateTime.parse(td).toDate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		announcement.save();
		return ok();
	}
	
	@Transactional
	public static Result deleteAnnouncement(Long id) {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
		Announcement announcement =  Announcement.findById(id);
		announcement.delete();
		return ok();
	}
	
	@Transactional
	public static Result updateAnnouncement() {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
		DynamicForm form = DynamicForm.form().bindFromRequest();
		Announcement announcement = Announcement.findById(Long.parseLong(form.get("id")));
		announcement.setAnnouncementType(AnnouncementType.valueOf(form.get("ty")));
		announcement.description = form.get("d");
		if(form.get("ic.id") != null){
			announcement.icon = Icon.findById((Long.parseLong(form.get("ic.id"))));
		}
		announcement.title = form.get("t");
		try {
		    String fd = form.get("fd");
            String td = form.get("td");
            announcement.fromDate = DateTime.parse(fd).toDate();
            announcement.toDate = DateTime.parse(td).toDate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		announcement.merge();
		return ok();
	}
	
	@Transactional
	public static Result getAllAnnouncementIcons() {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
		List<Icon> icons = Icon.getAllIcons();
		return ok(Json.toJson(icons));
	}
	
}
