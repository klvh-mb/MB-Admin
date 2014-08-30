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
import models.Article;
import models.Icon;
import models.Location;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.AnnouncementVM;
import viewmodel.LocationVM;

public class AnnouncementController extends Controller{

	@Transactional
	public static Result getAnnouncements(String title,int currentPage) {
		System.out.println("::::::::::::::::AAAAAAAAAAAAAAAA"+title+"Current Page ::"+currentPage);
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
	public static Result getLocations() {
		List<Location> locations = Location.getAllLocations();
		List<LocationVM> locationVms = new ArrayList<>();
		for(Location l : locations) {
			LocationVM vm = new LocationVM(l);
			locationVms.add(vm);
		}
		return ok(Json.toJson(locationVms));
	}
	
	@Transactional
	public static Result saveAnnouncement() {
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
		announcement.location = Location.getLocationById(Long.parseLong(form.get("location")));
		System.out.println("From Date :::::::"+announcement.icon.name);
		announcement.save();
		return ok();
	}
	
	@Transactional
	public static Result deleteAnnouncement(Long id) {
		Announcement announcement =  Announcement.findById(id);
		announcement.delete();
		return ok();
	}
	
	@Transactional
	public static Result updateAnnouncement() {
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
		announcement.location = Location.getLocationById(Long.parseLong(form.get("location")));
		announcement.merge();
		System.out.println("INSIDE UPDATE"+form.get("ic.id"));
		return ok();
	}
	
	@Transactional
	public static Result getAllAnnouncementIcons() {
		List<Icon> icons = Icon.getAllIcons();
		return ok(Json.toJson(icons));
	}
	
}
