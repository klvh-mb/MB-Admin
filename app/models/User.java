package models;


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

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

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
	    	System.out.println("total pages ::"+totalPages);
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
	    	System.out.println("total pages ::"+totalPages);
	    	return totalPages;
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
    
   }
