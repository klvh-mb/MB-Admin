package models;


import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonIgnore;

import domain.SocialObjectType;
import play.Play;
import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import scala.Array;

@Entity
public class User extends SocialObject {
    private static final play.api.Logger logger = play.api.Logger.apply(User.class);

    private static User SUPER_ADMIN;
    
    public String firstName;
    public String lastName;
    public String displayName;
    public String email;
    public String gender;
    
    // Targeting info
    
    @OneToOne
    public UserInfo userInfo;
    
    @OneToMany
    public List<UserChild> children;
   
    // fb info
    
    public boolean fbLogin;
    
    // stats
    
    public int questionsCount = 0;
    
    public int answersCount = 0;
    
    public int postsCount = 0;
    
    public int commentsCount = 0;
    
    public int likesCount = 0;
    
    // system
    
    @JsonIgnore
    public boolean active;

    @JsonIgnore
    public boolean emailValidated;

    @JsonIgnore
    public boolean newUser;
    
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    public Date lastLogin;
       
    @ManyToOne
    public Location location;
    
    @Enumerated(EnumType.STRING)
	public SocialObjectType objectType;
    
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public Folder albumPhotoProfile;
    
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public Folder albumCoverProfile;
    
    public static User findById(Long id) {
    	Query query = JPA.em().createQuery("Select u from User u where u.id = ?1");
		query.setParameter(1, id);
    	return (User) query.getSingleResult();
    }
    
    @Transactional
    public static long getUsersTotalPages(String title,  String userStatus, int rowsPerPage) {
    	long totalPages = 0, size;
    	
    	String sql = "";
    	if(userStatus.equals("0")|| userStatus.equals("1")) {
    		if(title.trim().equals("")) {
    			sql = "select count(*) from User u where u.active = ?2";
    		}
    		else {
    			sql = "select count(*) from User u where u.displayName LIKE ?1 AND u.active = ?2";
    		}
    	}
    	else {
    		if(title.trim().equals("")) {
    			sql = "select count(*) from User u";
    		}
    		else {
    			sql = "select count(*) from User u where u.displayName LIKE ?1";
    		}
    	}
    	
    	Query q = JPA.em().createQuery(sql);
    	if(!title.trim().equals("")) {
    		q.setParameter(1, "%"+title+"%");
    	}
    	if(userStatus.equals("0")|| userStatus.equals("1")) {
    		if(userStatus.equals("0"))
    		q.setParameter(2, false);
    		
    		if(userStatus.equals("1"))
        		q.setParameter(2, true);
    	}
    	
    	size= (long) q.getSingleResult();
    	
    	totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
    }

    @Transactional
    public static List<User> getAllUsers(String title,String userStatus,int rowsPerPage,Long totalPages,int currentPage) {
    	int  start=0;
    	String sql = "";
    	if(userStatus.equals("0")|| userStatus.equals("1")) {
    		if(title.trim().equals("")) {
    			sql = "select u from User u where u.active = ?2";
    		}
    		else {
    			sql = "select u from User u where u.displayName LIKE ?1 AND u.active = ?2";
    		}
    	}
    	else {
    		if(title.trim().equals("")) {
    			sql = "select u from User u";
    		}
    		else {
    			sql = "select u from User u where u.displayName LIKE ?1";
    		}
    	}
    	
    	if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
    	
    	Query q = JPA.em().createQuery(sql).setFirstResult(start).setMaxResults(rowsPerPage);
    	
    	if(!title.trim().equals("")) {
    		q.setParameter(1, "%"+title+"%");
    	}
    	if(userStatus.equals("0")|| userStatus.equals("1")) {
    		if(userStatus.equals("0"))
    		q.setParameter(2, false);
    		
    		if(userStatus.equals("1"))
        		q.setParameter(2, true);
    	}
    	return (List<User>)q.getResultList();
    }

    @Transactional
    public static long getAllUsersTotal(int rowsPerPage,String title) {
		long totalPages = 0, size;
		if(title.trim().equals("")) {
			size = (long) JPA.em().createQuery("Select count(*) from User u where u.objectType = 'USER'").getSingleResult();
		}
		else
		{
			Query q = JPA.em().createQuery("SELECT count(*) FROM User u where u.id = ?1 AND u.objectType = 'USER'");
			q.setParameter(1, Long.parseLong(title));
			size = q.getMaxResults();
			if(size == 0) {
				totalPages = 0;
			}
			else {
				totalPages = 1;
			}
			return totalPages;
		}
			
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
    
    @Transactional
    public static long getAllSubscribedUsersTotal(int rowsPerPage,String title,String gender,String location,String subscription) {
		long totalPages = 0;
		String sql="";
		if(title.trim().equals("") && gender.trim().equals("") && location.trim().equals("") && subscription.trim().equals("")) {
    		sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription)";
    	} else {
    		if(location.trim().equals("") && !(subscription.trim().equals(""))) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+")";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"'";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    				
    		}
    		if(!(location.trim().equals("")) && subscription.trim().equals("")) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription) AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription) AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			
    		}
    		if(location.trim().equals("") && subscription.trim().equals("")) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription)";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"'";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription) AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			
    		}
    		if(!(location.trim().equals("")) && !(subscription.trim().equals(""))) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select count(*) from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			
    		}	
    	}
		
		BigInteger size = (BigInteger) JPA.em().createNativeQuery(sql).getSingleResult();
		
		totalPages = size.longValue()/rowsPerPage;
		
    	if(size.longValue() % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
    
    @Transactional
	public static List<Object[]> getAllSubscribedUsers(int currentPage, int rowsPerPage, long totalPages,String title,String gender,String location,String subscription) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		String sql="";
		if(title.trim().equals("") && gender.trim().equals("") && location.trim().equals("") && subscription.trim().equals("")) {
    		sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription)";
    	} else {
    		if(location.trim().equals("") && !(subscription.trim().equals(""))) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+")";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"'";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    				
    		}
    		if(!(location.trim().equals("")) && subscription.trim().equals("")) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			
    		}
    		if(location.trim().equals("") && subscription.trim().equals("")) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription)";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"'";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			
    		}
    		if(!(location.trim().equals("")) && !(subscription.trim().equals(""))) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			
    		}	
    	}
		
		Query q = JPA.em().createNativeQuery(sql).setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<Object[]>)q.getResultList();
	}
    
    public static List<Long> getAllUsersId(String title,String gender,String location,String subscription) {
    	String sql="";
		if(title.trim().equals("") && gender.trim().equals("") && location.trim().equals("") && subscription.trim().equals("")) {
    		sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription)";
    	} else {
    		if(location.trim().equals("") && !(subscription.trim().equals(""))) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+")";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"'";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    				
    		}
    		if(!(location.trim().equals("")) && subscription.trim().equals("")) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			
    		}
    		if(location.trim().equals("") && subscription.trim().equals("")) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription)";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"'";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription) AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			
    		}
    		if(!(location.trim().equals("")) && !(subscription.trim().equals(""))) {
    			if(title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(!title.trim().equals("") && gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+")";
    			}
    			if(title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			if(!title.trim().equals("") && !gender.trim().equals("")) {
    				sql = "select * from user where user.id in (select user_subscription.user_id from user_subscription where user_subscription.subscriptions_id = "+Long.parseLong(subscription)+") AND user.displayName LIKE '"+"%"+title+"%"+"' AND user.userInfo_id in (select id from userinfo where userinfo.location_id = "+Long.parseLong(location)+") AND user.userInfo_id in (select id from userinfo where userinfo.gender = '"+gender+"')";
    			}
    			
    		}	
    	}
    	Query query = JPA.em().createNativeQuery(sql);
		List<Object[]> list = (List<Object[]>)query.getResultList();
		List<Long> ids = new ArrayList<>();
		for(Object[] obj: list) {
			ids.add(((BigInteger)obj[0]).longValue());
		}
    	return ids;
    }
    public static List<Long> getSubscriptionIds(Long id) {
    	Query query = JPA.em().createNativeQuery("select subscriptions_id from user_subscription where User_id = ?1");
    	query.setParameter(1, id);
		List<BigInteger> list = (List<BigInteger>)query.getResultList();
		List<Long> ids = new ArrayList<>();
		for(BigInteger obj: list) {
			ids.add((obj).longValue());
		}
    	return ids;
    }
    
    public static List<User> findAllUsers(int currentPage, int rowsPerPage, long totalPages, String title) {
		int  start=0;
		String sql="";
    	if(title.trim().equals("")) {
    		sql = "SELECT u FROM User u where u.objectType = 'USER'";
    	} else {
    		sql = "SELECT u FROM User u where u.id = ?1 AND u.objectType = 'USER'";
    	}
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
        try {
            Query q = JPA.em().createQuery(sql).setFirstResult(start).setMaxResults(rowsPerPage);
            if(!title.trim().equals("")) {
                q.setParameter(1, Long.parseLong(title));
            }
            return (List<User>) q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
	}

    @Transactional
	public void merge() {
	    JPA.em().merge(this);
	}
    
    @JsonIgnore
    public Resource getPhotoProfile() {
        if (this.albumPhotoProfile != null) {
            Resource file = this.albumPhotoProfile.getHighPriorityFile();
            if (file != null) {
                return file;
            }
        }
        return null;
    }

    public String getPhotoProfileURL() {
        Resource resource = getPhotoProfile();
        if (resource == null) {
            return "";
        }
        return resource.getPath();
    }
    
    @JsonIgnore
    public Resource getCoverProfile() {
        if (this.albumCoverProfile != null) {
            Resource file = this.albumCoverProfile.getHighPriorityFile();
            if (file != null) {
                return file;
            }
        }
        return null;
    }

    @JsonIgnore
    public Resource getMiniProfileImage() {
        if (this.albumPhotoProfile != null) {
            Resource file = this.albumPhotoProfile.getHighPriorityFile();
            if (file != null) {
                return file;
            }
        }
        return null;
    }
    
    public String getCoverProfileURL() {
        Resource resource = getCoverProfile();
        if (resource == null) {
            return "";
        }
        return resource.getPath();
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn(this);
    }
    
    public static boolean isLoggedIn(User user) {
        return user != null && user.id != -1L;
    }
    
    public static User noLoginUser() {
        User noLoginUser = new User();
        noLoginUser.id = -1L;
        return noLoginUser;
    }
    
    public static File getDefaultUserPhoto() throws FileNotFoundException {
        return new File(Play.application().configuration().getString("storage.user.noimage"));
    }

    public static File getDefaultCoverPhoto() throws FileNotFoundException {
        return new File(Play.application().configuration().getString("storage.user.cover.noimage"));
    }
	
		public static List<User> findAllUsers(String gender, String location) {
			String sql="";
	    	sql = "select u from user u wher system <> 1 ";
	    
	    	Query query = JPA.em().createNativeQuery(sql,User.class);
			List<User> list = (List<User>)query.getResultList();
			return null;

		}
}
