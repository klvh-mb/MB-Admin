package viewmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.Community;
import models.Post;
import models.Comment;
import models.ReportedObject;
import models.Resource;
import models.User;
import models.SocialObject;
import models.UserChild;
import domain.categoryType;
import domain.SocialObjectType;
import domain.Entity;
import domain.AuditFields;

public class ReportedObjectVM {

	public Long id;
	public String objectType;
	public Long socialObjectID;
	public String Comment;
	public String category;
	public String reportedBy;
	public String reportedDate;
	public String postTitle;
	public String postBody;
	public String commentBody;
	public boolean deleted;
	public String communityName;
	public String createdDate;
	public String owner;
	public Long posts;
	public String userName;
	public String lastLogin;
	
	public String aboutMe;
	public String birthYear;
	public String location;
	public int numChildren;
	public String parentType;
	public List<UserChildVM> userChildVms = new ArrayList<>();
	public Long image;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	public ReportedObjectVM(ReportedObject reportedObject,Post post) {
		this.category = reportedObject.category.name();
		this.Comment = reportedObject.Comment;
		this.objectType = reportedObject.objectType.name();
		this.id = reportedObject.id;
		this.socialObjectID = reportedObject.socialObjectID;
		this.reportedBy = reportedObject.reportedBy;
		this.reportedDate = formatter.format(reportedObject.reportedDate);
		this.postBody = post.body;
		this.postTitle = post.title;
		this.deleted = post.deleted;
		Resource resource = Resource.findAllResourceOfFolder(post.folder.id);
			this.image = resource.id;
	}
	
	public ReportedObjectVM(ReportedObject reportedObject,Comment comment) {
		this.category = reportedObject.category.name();
		this.Comment = reportedObject.Comment;
		this.objectType = reportedObject.objectType.name();
		this.id = reportedObject.id;
		this.socialObjectID = reportedObject.socialObjectID;
		this.reportedBy = reportedObject.reportedBy;
		this.reportedDate = formatter.format(reportedObject.reportedDate);
		this.commentBody = comment.body;
		this.deleted = comment.deleted;
		Resource resource = Resource.findAllResourceOfFolder(comment.folder.id);
		this.image = resource.id;
	}
	
	public ReportedObjectVM(ReportedObject reportedObject,Community community) {
		this.category = reportedObject.category.name();
		this.Comment = reportedObject.Comment;
		this.objectType = reportedObject.objectType.name();
		this.id = reportedObject.id;
		this.socialObjectID = reportedObject.socialObjectID;
		this.reportedBy = reportedObject.reportedBy;
		this.reportedDate = formatter.format(reportedObject.reportedDate);
		this.deleted = community.deleted;
		this.communityName = community.name;
		this.createdDate = formatter.format(community.createDate);
		this.owner = community.owner.displayName;
		this.posts = Post.getPosts(community.id);
		Resource resource = Resource.findAllResourceOfFolder(community.albumPhotoProfile.id);
		this.image = resource.id;
	}
	
	public ReportedObjectVM(ReportedObject reportedObject,User user) {
		this.category = reportedObject.category.name();
		this.Comment = reportedObject.Comment;
		this.objectType = reportedObject.objectType.name();
		this.id = reportedObject.id;
		this.socialObjectID = reportedObject.socialObjectID;
		this.reportedBy = reportedObject.reportedBy;
		this.reportedDate = formatter.format(reportedObject.reportedDate);
		this.deleted = user.deleted;
		this.userName = user.displayName;
		this.lastLogin = formatter.format(user.lastLogin);
		this.aboutMe = user.userInfo.aboutMe;
		this.birthYear = user.userInfo.birthYear;
		this.location = user.userInfo.location.displayName;
		this.numChildren = user.userInfo.numChildren;
		this.parentType = user.userInfo.parentType.name();
		List<UserChild> userChilds = UserChild.findListById(user.id);
		for (UserChild u:userChilds) {
			UserChildVM vm = new UserChildVM(u);
			this.userChildVms.add(vm);
		}
		Resource resource = Resource.findAllResourceOfFolder(user.albumPhotoProfile.id);
		this.image = resource.id;
	}
	
	
	public String getObjectType() {
		return objectType;
	}


	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(String reportedBy) {
		this.reportedBy = reportedBy;
	}

	public String getReportedDate() {
		return reportedDate;
	}

	public void setReportedDate(String reportedDate) {
		this.reportedDate = reportedDate;
	}
	
}
