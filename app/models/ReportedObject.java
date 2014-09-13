package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import domain.SocialObjectType;
import domain.categoryType;

@Entity
public class ReportedObject {

	public ReportedObject() {
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType objectType;
	
	public Long socialObjectID;

	public String Comment;
	
	
	@Enumerated(EnumType.STRING)
	public categoryType category;
	
	public String reportedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date reportedDate;
	
	public ReportedObject(DeletedInfo deletedInfo) {
		this.Comment = deletedInfo.Comment;
		this.category = deletedInfo.category;
		this.objectType = deletedInfo.objectType;
		this.reportedBy = deletedInfo.reportedBy;
		this.reportedDate = deletedInfo.reportedDate;
		this.socialObjectID = deletedInfo.socialObjectID;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SocialObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(SocialObjectType objectType) {
		this.objectType = objectType;
	}

	public Long getSocialObjectID() {
		return socialObjectID;
	}

	public void setSocialObjectID(Long socialObjectID) {
		this.socialObjectID = socialObjectID;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public categoryType getCategory() {
		return category;
	}

	public void setCategory(categoryType category) {
		this.category = category;
	}

	public String getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(String reportedBy) {
		this.reportedBy = reportedBy;
	}

	public Date getReportedDate() {
		return reportedDate;
	}

	public void setReportedDate(Date reportedDate) {
		this.reportedDate = reportedDate;
	}

	@Transactional
    public static long getSize() {
		long size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'POST'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getUsersSize() {
		long size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'USER'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getCommunitySize() {
		long size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'COMMUNITY'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getQuestionSize() {
		long size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'QUESTION'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getCommentSize() {
		long size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'COMMENT'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getAnswerSize() {
		long size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'ANSWER'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getAllPostsTotal(int rowsPerPage) {
		long totalPages = 0, size;
		size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'POST'").getSingleResult();
		
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllUsersTotal(int rowsPerPage) {
		long totalPages = 0, size;
		size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'USER'").getSingleResult();
		
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllCommunitiesTotal(int rowsPerPage) {
		long totalPages = 0, size;
		size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'COMMUNITY'").getSingleResult();
		
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllQuestionsTotal(int rowsPerPage) {
		long totalPages = 0, size;
		size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'QUESTION'").getSingleResult();
		
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllCommentsTotal(int rowsPerPage) {
		long totalPages = 0, size;
		size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'COMMENT'").getSingleResult();
		
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllAnswersTotal(int rowsPerPage) {
		long totalPages = 0, size;
		size = (long) JPA.em().createQuery("Select count(*) from ReportedObject r where r.objectType = 'ANSWER'").getSingleResult();
		
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
	public static List<ReportedObject> getAllReportedPosts(int currentPage, int rowsPerPage, long totalPages) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		Query q = JPA.em().createQuery("Select r from ReportedObject r where r.objectType = 'POST'").setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<ReportedObject>)q.getResultList();
	}
	
	@Transactional
	public static List<ReportedObject> getAllReportedUsers(int currentPage, int rowsPerPage, long totalPages) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		Query q = JPA.em().createQuery("Select r from ReportedObject r where r.objectType = 'USER'").setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<ReportedObject>)q.getResultList();
	}
	
	@Transactional
	public static List<ReportedObject> getAllReportedCommunities(int currentPage, int rowsPerPage, long totalPages) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		Query q = JPA.em().createQuery("Select r from ReportedObject r where r.objectType = 'COMMUNITY'").setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<ReportedObject>)q.getResultList();
	}
	
	@Transactional
	public static List<ReportedObject> getAllReportedQuestions(int currentPage, int rowsPerPage, long totalPages) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		Query q = JPA.em().createQuery("Select r from ReportedObject r where r.objectType = 'QUESTION'").setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<ReportedObject>)q.getResultList();
	}
	
	
	@Transactional
	public static List<ReportedObject> getAllReportedComments(int currentPage, int rowsPerPage, long totalPages) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		Query q = JPA.em().createQuery("Select r from ReportedObject r where r.objectType = 'COMMENT'").setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<ReportedObject>)q.getResultList();
	}
	
	@Transactional
	public static List<ReportedObject> getAllReportedAnswers(int currentPage, int rowsPerPage, long totalPages) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		Query q = JPA.em().createQuery("Select r from ReportedObject r where r.objectType = 'ANSWER'").setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<ReportedObject>)q.getResultList();
	}
	
	public static ReportedObject findById(Long id) {
		Query q = JPA.em().createQuery("SELECT r FROM ReportedObject r where id = ?1");
		q.setParameter(1, id);
		return (ReportedObject) q.getSingleResult();
	}
	
	@Transactional
    public void save() {
        JPA.em().persist(this);
        JPA.em().flush();     
    }
      
    @Transactional
    public void delete() {
        JPA.em().remove(this);
    }
    
    @Transactional
    public void merge() {
        JPA.em().merge(this);
    }
    
    @Transactional
    public void refresh() {
        JPA.em().refresh(this);
    }
	
	
}
