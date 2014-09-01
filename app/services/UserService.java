package services;

import java.util.List;

import javax.persistence.Query;

import models.User;
import play.db.jpa.JPA;


public class UserService extends User{

	public UserService() {
	}
	
	public static List<User> getAllUsers(){
		Query q = JPA.em().createNativeQuery("Select * from User u",User.class);
		List<User> users = q.getResultList();
		System.out.println(users+" ::::::::::::::::::: Users Size :: "+ users.size() +" ::::::::::::::::: ");
		return users;
	}

}
