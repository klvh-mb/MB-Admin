package models;

import java.math.BigInteger;
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
import domain.ReportType;
import domain.SocialObjectType;

@Entity
public class DeletedInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType objectType;
	
	public Long socialObjectID;

	public String description;
	
	@Enumerated(EnumType.STRING)
	public ReportType reportType;
	
	public String reportedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date reportedDate;
	
	public DeletedInfo() {
		
	}
	
	public DeletedInfo(ReportedObject reportedObject) {
		this.description = reportedObject.description;
		this.reportType = reportedObject.reportType;
		this.objectType = reportedObject.objectType;
		this.reportedBy = reportedObject.reportedBy;
		this.reportedDate = reportedObject.reportedDate;
		this.socialObjectID = reportedObject.socialObjectID;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
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
		long size = (long) JPA.em().createQuery("Select count(*) from DeletedInfo d where d.objectType = 'POST'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getQuestionSize() {
		long size = (long) JPA.em().createQuery("Select count(*) from DeletedInfo d where d.objectType = 'QUESTION'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getUserSize() {
		long size = (long) JPA.em().createQuery("Select count(*) from DeletedInfo d where d.objectType = 'USER'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getCommentsSize() {
		long size = (long) JPA.em().createQuery("Select count(*) from DeletedInfo d where d.objectType = 'COMMENT'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getAnswersSize() {
		long size = (long) JPA.em().createQuery("Select count(*) from DeletedInfo d where d.objectType = 'ANSWER'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getCommunitySize() {
		long size = (long) JPA.em().createQuery("Select count(*) from DeletedInfo d where d.objectType = 'COMMUNITY'").getSingleResult();
		return size;
	}
	
	@Transactional
    public static long getAllPostsTotal(int rowsPerPage, String communityId) {
		long totalPages = 0, size;
		String sql="";
    	if(communityId.trim().equals("")) {
    		sql = "Select count(*) from DeletedInfo d where d.objectType = 'POST'";
    	} else {
    		sql = "Select count(*) from DeletedInfo d,Post p where d.objectType = 'POST' AND  d.socialObjectID = p.id AND p.community.id = "+Long.parseLong(communityId)+"";
    	}
		
    	size = (long) JPA.em().createQuery(sql).getSingleResult();
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllQuestionsTotal(int rowsPerPage, String communityId) {
		long totalPages = 0, size;
		String sql="";
    	if(communityId.trim().equals("")) {
    		sql = "Select count(*) from DeletedInfo d where d.objectType = 'QUESTION'";
    	} else {
    		sql = "Select count(*) from DeletedInfo d,Post p where d.objectType = 'QUESTION' AND  d.socialObjectID = p.id AND p.community.id = "+Long.parseLong(communityId)+"";
    	}
		size = (long) JPA.em().createQuery(sql).getSingleResult();
		
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllUsersTotal(int rowsPerPage) {
		long totalPages = 0, size;
		size = (long) JPA.em().createQuery("Select count(*) from DeletedInfo d where d.objectType = 'USER'").getSingleResult();
		
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllCommentsTotal(int rowsPerPage,String communityId) {
		long totalPages = 0;
		String sql="";
    	if(communityId.trim().equals("")) {
    		sql = "Select count(*) from DeletedInfo d where d.objectType = 'COMMENT'";
    	} else {
    		sql = "select count(*) from deletedinfo di where di.objectType = 'COMMENT' and di.socialObjectID in (select pc.comments_id from post p, post_comment pc where p.community_id = "+Long.parseLong(communityId)+"  and p.id = pc.Post_id)";
    	}
		
    	BigInteger size = (BigInteger) JPA.em().createNativeQuery(sql).getSingleResult();
    	
		totalPages = size.longValue()/rowsPerPage;
		
    	if(size.longValue() % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllAnswersTotal(int rowsPerPage,String communityId) {
		long totalPages = 0;
		String sql="";
    	if(communityId.trim().equals("")) {
    		sql = "Select count(*) from deletedinfo d where d.objectType = 'ANSWER'";
    	} else {
    		sql = "select count(*) from deletedinfo di where di.objectType = 'ANSWER' and di.socialObjectID in (select pc.comments_id from post p, post_comment pc where p.community_id = "+Long.parseLong(communityId)+"  and p.id = pc.Post_id)";
    	}
		
    	BigInteger size = (BigInteger) JPA.em().createNativeQuery(sql).getSingleResult();
    	
		totalPages = size.longValue()/rowsPerPage;
		
    	if(size.longValue() % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
    public static long getAllCommunityTotal(int rowsPerPage) {
		long totalPages = 0, size;
		size = (long) JPA.em().createQuery("Select count(*) from DeletedInfo d where d.objectType = 'COMMUNITY'").getSingleResult();
		
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	return totalPages;
	}
	
	@Transactional
	public static List<DeletedInfo> getAllDeletedPosts(int currentPage, int rowsPerPage, long totalPages, String communityId) {
		int  start=0;
		String sql = "";
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		if(communityId.trim().equals("")) {
			sql = "Select d from DeletedInfo d where d.objectType = 'POST'";
    	} else {
    		sql = "Select d from DeletedInfo d,Post p where d.objectType = 'POST' AND  d.socialObjectID = p.id AND p.community.id = ?2";
    	}
		Query q = JPA.em().createQuery(sql).setFirstResult(start).setMaxResults(rowsPerPage);
		if(!communityId.trim().equals("")) {
			q.setParameter(2, Long.parseLong(communityId));
		}
		return (List<DeletedInfo>)q.getResultList();
	}
	
	@Transactional
	public static List<DeletedInfo> getAllDeletedQuestions(int currentPage, int rowsPerPage, long totalPages, String communityId) {
		int  start=0;
		String sql = "";
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		if(communityId.trim().equals("")) {
			sql = "Select d from DeletedInfo d where d.objectType = 'QUESTION'";
    	} else {
    		sql = "Select d from DeletedInfo d,Post p where d.objectType = 'QUESTION' AND  d.socialObjectID = p.id AND p.community.id = ?2";
    	}
		Query q = JPA.em().createQuery(sql).setFirstResult(start).setMaxResults(rowsPerPage);
		if(!communityId.trim().equals("")) {
			q.setParameter(2, Long.parseLong(communityId));
		}
		return (List<DeletedInfo>)q.getResultList();
	}
	
	@Transactional
	public static List<DeletedInfo> getAllDeletedUsers(int currentPage, int rowsPerPage, long totalPages) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		Query q = JPA.em().createQuery("Select d from DeletedInfo d where d.objectType = 'USER'").setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<DeletedInfo>)q.getResultList();
	}
	
	@Transactional
	public static List<Object[]> getAllDeletedComments(int currentPage, int rowsPerPage, long totalPages,String communityId) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		String sql="";
    	if(communityId.trim().equals("")) {
    		sql = "Select * from deletedinfo d where d.objectType = 'COMMENT'";
    	} else {
    		sql = "select * from deletedinfo di where di.objectType = 'COMMENT' and di.socialObjectID in (select pc.comments_id from post p, post_comment pc where p.community_id = "+Long.parseLong(communityId)+" and p.id = pc.Post_id)";
    	}
    	
		Query q = JPA.em().createNativeQuery(sql).setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<Object[]>)q.getResultList();
	}
	
	@Transactional
	public static List<Object[]> getAllDeletedAnswers(int currentPage, int rowsPerPage, long totalPages,String communityId) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		String sql="";
    	if(communityId.trim().equals("")) {
    		sql = "Select * from deletedinfo d where d.objectType = 'ANSWER'";
    	} else {
    		sql = "select * from deletedinfo di where di.objectType = 'ANSWER' and di.socialObjectID in (select pc.comments_id from post p, post_comment pc where p.community_id = "+Long.parseLong(communityId)+" and p.id = pc.Post_id)";
    	}
    	
		Query q = JPA.em().createNativeQuery(sql).setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<Object[]>)q.getResultList();
	}
	
	@Transactional
	public static List<DeletedInfo> getAllDeletedcommunities(int currentPage, int rowsPerPage, long totalPages) {
		int  start=0;
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
		
		Query q = JPA.em().createQuery("Select d from DeletedInfo d where d.objectType = 'COMMUNITY'").setFirstResult(start).setMaxResults(rowsPerPage);
		return (List<DeletedInfo>)q.getResultList();
	}
	
	public static DeletedInfo findById(Long id) {
		Query q = JPA.em().createQuery("SELECT d FROM DeletedInfo d where id = ?1");
		q.setParameter(1, id);
		return (DeletedInfo) q.getSingleResult();
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
