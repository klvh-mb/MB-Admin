package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		final String value = session().get("NAME");
        if (value == null) {
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
		final String value = session().get("NAME");

        if (value == null) {
        	return ok(views.html.login.render());
        }
		DynamicForm form = DynamicForm.form().bindFromRequest();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Announcement announcement = new Announcement();
		announcement.setAnnouncementType(AnnouncementType.valueOf(form.get("announcementType")));
		announcement.description = form.get("description");
		announcement.icon = Icon.findById((Long.parseLong(form.get("icon"))));
		announcement.title = form.get("title");
		try {
			announcement.fromDate = (Date)formatter.parse(form.get("fromDate"));
			announcement.toDate = (Date)formatter.parse(form.get("toDate"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		announcement.save();
		return ok();
	}
	
	@Transactional
	public static Result deleteAnnouncement(Long id) {
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		Announcement announcement =  Announcement.findById(id);
		announcement.delete();
		return ok();
	}
	
	@Transactional
	public static Result updateAnnouncement() {
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		DynamicForm form = DynamicForm.form().bindFromRequest();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Announcement announcement = Announcement.findById(Long.parseLong(form.get("id")));
		announcement.setAnnouncementType(AnnouncementType.valueOf(form.get("ty")));
		announcement.description = form.get("d");
		announcement.icon = Icon.findById((Long.parseLong(form.get("ic.id"))));
		announcement.title = form.get("t");
		try {
			announcement.fromDate = (Date)formatter.parse(form.get("fd"));
			announcement.toDate = (Date)formatter.parse(form.get("td"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		announcement.merge();
		return ok();
	}
	
	@Transactional
	public static Result getAllAnnouncementIcons() {
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		List<Icon> icons = Icon.getAllIcons();
		return ok(Json.toJson(icons));
	}
	
}
