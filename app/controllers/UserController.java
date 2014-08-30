package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import models.Announcement;
import models.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.AnnouncementVM;
import viewmodel.UserVM;

public class UserController extends Controller{

	@Transactional
	public static Result getUsers(String title,String userStatus,int currentPage) {
		
		long totalPages = User.getUsersTotalPages(title, userStatus, 5);
		List<User> listOfUsers = User.getAllUsers(title,userStatus,5,totalPages,currentPage);
		List<UserVM> listOfUserVMs = new ArrayList<>();
		for (User user:listOfUsers) {
			UserVM vm = new UserVM(user);
			listOfUserVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		
		System.out.println("Inside controller.............."+"Title  :: "+title+"   status :: "+userStatus+"   currentPage ::: "+currentPage);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", listOfUserVMs);
		
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result suspendUser(Long id) {
		User user = User.findById(id);
		user.active = false;
		user.merge();
		return ok();
	}
	
	@Transactional
	public static Result activateUser(Long id) {
		User user = User.findById(id);
		user.active = true;
		user.merge();
		return ok();
	}
	
}
