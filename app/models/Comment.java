package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import domain.CommentType;
import domain.SocialObjectType;

/**
 * A Comment by an User on a SocialObject 
 *
 */
@Entity
public class Comment extends SocialObject {
    private static final play.api.Logger logger = play.api.Logger.apply(Comment.class);

    @Required
    public Long socialObject;       // e.g. Post Id

    @Required
    public Date date = new Date();

    @Required
    @Column(length=2000)
    public String body;

    @Required
    public CommentType commentType;

    public int noOfLikes=0;

    private String attribute;

    @ManyToOne(cascade = CascadeType.REMOVE)
  	public Folder folder;

    @Enumerated(EnumType.STRING)
   	public SocialObjectType objectType;


    /**
     * Ctor
     */
    public Comment() {}
    
    public Comment(SocialObject socialObject, User user, String body) {
        this.owner = user;
        this.socialObject = socialObject.id;
        this.body = body;
    }

    @Transactional
    public static long getAllCommentsTotal(int rowsPerPage,String title) {
		long totalPages = 0, size;
		if(title.trim().equals("")) {
			size = (long) JPA.em().createQuery("Select count(*) from Comment c where c.objectType = 'COMMENT'").getSingleResult();
		}
		else
		{
			Query q = JPA.em().createQuery("SELECT count(*) FROM Comment c where c.id = ?1 AND c.objectType = 'COMMENT'");
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
    public static long getAllAnswersTotal(int rowsPerPage,String title) {
		long totalPages = 0, size;
		if(title.trim().equals("")) {
			size = (long) JPA.em().createQuery("Select count(*) from Comment c where c.objectType = 'ANSWER'").getSingleResult();
		}
		else
		{
			Query q = JPA.em().createQuery("SELECT count(*) FROM Comment c where c.id = ?1 AND c.objectType = 'ANSWER'");
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
    
    public static List<Comment> findAllComments(int currentPage, int rowsPerPage, long totalPages, String title) {
		int  start=0;
		String sql="";
    	if(title.trim().equals("")) {
    		sql = "SELECT c FROM Comment c where c.objectType = 'COMMENT'";
    	} else {
    		sql = "SELECT c FROM Comment c where c.id = ?1 AND c.objectType = 'COMMENT'";
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
            return (List<Comment>) q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    public static List<Comment> findAllAnswers(int currentPage, int rowsPerPage, long totalPages, String title) {
		int  start=0;
		String sql="";
    	if(title.trim().equals("")) {
    		sql = "SELECT c FROM Comment c where c.objectType = 'ANSWER'";
    	} else {
    		sql = "SELECT c FROM Comment c where c.id = ?1 AND c.objectType = 'ANSWER'";
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
            return (List<Comment>) q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    public static Comment findById(Long id) {
        Query q = JPA.em().createQuery("SELECT c FROM Comment c where id = ?1");
        q.setParameter(1, id);
        return (Comment) q.getSingleResult();
    }
    
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}