package viewmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import models.Community;
import models.Post;
import models.Comment;
import models.ReportedObject;
import models.Resource;
import models.User;
import models.UserChild;

public class ReportedObjectVM {

	public Long id;
	public String objectType;
	public Long socialObjectID;
	public String comment;
	public String reportType;
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
		this.reportType = reportedObject.reportType.name();
		this.comment = reportedObject.comment;
		this.objectType = reportedObject.objectType.name();
		this.id = reportedObject.id;
		this.socialObjectID = reportedObject.socialObjectID;
		this.reportedBy = reportedObject.reportedBy;
		this.reportedDate = formatter.format(reportedObject.reportedDate);
		this.postBody = post.body;
		this.postTitle = post.title;
		if(post.deleted != null) {
			this.deleted = post.deleted;
		}
		if(post.folder != null) {
			List<Resource> resource = Resource.findAllResourceOfFolder(post.folder.id);
			this.image = resource.get(0).id;
		}	
	}
	
	public ReportedObjectVM(ReportedObject reportedObject,Comment comment) {
		this.reportType = reportedObject.reportType.name();
		this.comment = reportedObject.comment;
		this.objectType = reportedObject.objectType.name();
		this.id = reportedObject.id;
		this.socialObjectID = reportedObject.socialObjectID;
		this.reportedBy = reportedObject.reportedBy;
		this.reportedDate = formatter.format(reportedObject.reportedDate);
		this.commentBody = comment.body;
		if(comment.deleted != null) {
			this.deleted = comment.deleted;
		}
		if(comment.folder != null) {
			List<Resource> resource = Resource.findAllResourceOfFolder(comment.folder.id);
			this.image = resource.get(0).id;
		}
		
	}
	
	public ReportedObjectVM(ReportedObject reportedObject,Community community) {
		this.reportType = reportedObject.reportType.name();
		this.comment = reportedObject.comment;
		this.objectType = reportedObject.objectType.name();
		this.id = reportedObject.id;
		this.socialObjectID = reportedObject.socialObjectID;
		this.reportedBy = reportedObject.reportedBy;
		this.reportedDate = formatter.format(reportedObject.reportedDate);
		if(community.deleted != null) {
			this.deleted = community.deleted;
		}
		this.communityName = community.name;
		if(community.auditFields.getCreatedDate() != null) {
			this.createdDate = formatter.format(community.auditFields.getCreatedDate());
		}
		if(community.owner != null) {
			this.owner = community.owner.displayName;
		}
		this.posts = Post.getPosts(community.id);
		if(community.albumPhotoProfile != null) {
			List<Resource> resource = Resource.findAllResourceOfFolder(community.albumPhotoProfile.id);
			this.image = resource.get(0).id;
		}
	}
	
	public ReportedObjectVM(ReportedObject reportedObject,User user) {
		this.reportType = reportedObject.reportType.name();
		this.comment = reportedObject.comment;
		this.objectType = reportedObject.objectType.name();
		this.id = reportedObject.id;
		this.socialObjectID = reportedObject.socialObjectID;
		this.reportedBy = reportedObject.reportedBy;
		this.reportedDate = formatter.format(reportedObject.reportedDate);
		if(user.deleted != null) {
			this.deleted = user.deleted;
		}
		this.userName = user.displayName;
		this.lastLogin = formatter.format(user.lastLogin);
		if(user.userInfo != null) {
			this.aboutMe = user.userInfo.aboutMe;
			this.birthYear = user.userInfo.birthYear;
			if(user.userInfo.location != null) {
				this.location = user.userInfo.location.displayName;
			}
			this.numChildren = user.userInfo.numChildren;
		}
		if(user.userInfo.parentType != null) {
			this.parentType = user.userInfo.parentType.name();
		}
		this.createdDate = formatter.format(user.auditFields.getCreatedDate());
		List<UserChild> userChilds = UserChild.findListById(user.id);
		for (UserChild u:userChilds) {
			UserChildVM vm = new UserChildVM(u);
			this.userChildVms.add(vm);
		}
		if(user.albumPhotoProfile != null) {
			List<Resource> resource = Resource.findAllResourceOfFolder(user.albumPhotoProfile.id);
			this.image = resource.get(0).id;
		}
	}
	
	
	public String getObjectType() {
		return objectType;
	}


	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}


	public String getReportType() {
		return reportType;
	}


	public void setReportType(String reportType) {
		this.reportType = reportType;
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
		return comment;
	}

	public void setComment(String comment) {
	    this.comment = comment;
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
