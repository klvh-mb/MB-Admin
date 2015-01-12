package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import models.Comment;
import models.Community;
import domain.PostType;
import domain.SocialObjectType;

@Entity
public class Post extends SocialObject {
    private static final play.api.Logger logger = play.api.Logger.apply(Post.class);

    public String title;

    @Required
    @Column(length=2000)
    public String body;

    @ManyToOne(cascade=CascadeType.REMOVE)
    public Community community;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Set<Comment> comments;

    @Required
    public PostType postType;

    @ManyToOne(cascade = CascadeType.REMOVE)
    public Folder folder;

    public int noOfComments = 0;
    public int noOfLikes = 0;
    public int noWantAns = 0;
    public int noOfViews = 0;
    public int shortBodyCount = 0;

	public Date socialUpdatedDate = new Date();

    @Enumerated(EnumType.STRING)
	public SocialObjectType objectType;


    /**
     * Ctor
     */
    public Post() {}

    /**
     * Ctor
     * @param adminUserName
     * @param title
     * @param post
     * @param community
     */
    public Post(String adminUserName, String title, String post, Community community) {
        this.auditFields.setCreatedBy(adminUserName);
        this.title = title;
        this.body = post;
        this.community = community;
    }

	@Transactional
    public static long getAllPostsTotal(int rowsPerPage,String title) {
		long totalPages = 0, size;
		if(title.trim().equals("")) {
			size = (long) JPA.em().createQuery("Select count(*) from Post p where p.objectType = 'POST'").getSingleResult();
		}
		else
		{
			Query q = JPA.em().createQuery("SELECT count(*) FROM Post p where p.id = ?1 AND p.objectType = 'POST'");
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
	
	public static Post findById(Long id) {
        try {
            Query q = JPA.em().createQuery("SELECT p FROM Post p where id = ?1");
            q.setParameter(1, id);
            return (Post) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
	
	public static long getPosts(Long id) {
		Query q = JPA.em().createQuery("SELECT count(*) FROM Post p where p.community.id = ?1");
        q.setParameter(1, id);
        long size = (long) q.getSingleResult();
		return size;
	}
	
	public static List<Post> findAllPosts(int currentPage, int rowsPerPage, long totalPages, String title) {
		int  start=0;
		String sql="";
    	if(title.trim().equals("")) {
    		sql = "SELECT p FROM Post p where p.objectType = 'POST'";
    	} else {
    		sql = "SELECT p FROM Post p where p.id = ?1 AND p.objectType = 'POST'";
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
            return (List<Post>) q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

	public static List<Post> findAllQuestions(int currentPage, int rowsPerPage, long totalPages, String title) {
		int  start=0;
		String sql="";
    	if(title.trim().equals("")) {
    		sql = "SELECT p FROM Post p where p.objectType = 'QUESTION'";
    	} else {
    		sql = "SELECT p FROM Post p where p.id = ?1 AND p.objectType = 'QUESTION'";
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
            return (List<Post>) q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
	
	@Transactional
    public static long getAllQuestionsTotal(int rowsPerPage,String title) {
		long totalPages = 0, size;
		if(title.trim().equals("")) {
			size = (long) JPA.em().createQuery("Select count(*) from Post p where p.objectType = 'QUESTION'").getSingleResult();
		}
		else
		{
			Query q = JPA.em().createQuery("SELECT count(*) FROM Post p where p.id = ?1 AND p.objectType = 'QUESTION'");
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
	
	@Override
	public void merge() {
		super.merge();
	}
	
	public Date getSocialUpdatedDate() {
        return socialUpdatedDate;
    }
}
