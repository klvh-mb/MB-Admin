package viewmodel;

import java.util.ArrayList;
import java.util.List;

import common.utils.DateTimeUtil;

import models.Community;
import models.DeletedInfo;
import models.Post;
import models.Comment;
import models.Resource;
import models.User;
import models.UserChild;

public class DeletedInfoVM {
	
	public Long id;
	public String objectType;
	public Long socialObjectID;
	public String description;
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

	public DeletedInfoVM(DeletedInfo deletedInfo,Post post) {
		this.reportType = deletedInfo.reportType.name();
		this.description = deletedInfo.description;
		if(post.objectType != null) {
			this.objectType = deletedInfo.objectType.name();
		}
		this.id = deletedInfo.id;
		this.socialObjectID = deletedInfo.socialObjectID;
		this.reportedBy = deletedInfo.reportedBy;
		this.reportedDate = DateTimeUtil.toString(deletedInfo.reportedDate);
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
	
	public DeletedInfoVM(DeletedInfo deletedInfo, Comment comment) {
		this.reportType = deletedInfo.reportType.name();
		this.description = deletedInfo.description;
		if(comment.objectType != null) {
			this.objectType = deletedInfo.objectType.name();
		}
		this.id = deletedInfo.id;
		this.socialObjectID = deletedInfo.socialObjectID;
		this.reportedBy = deletedInfo.reportedBy;
		this.reportedDate = DateTimeUtil.toString(deletedInfo.reportedDate);
		this.commentBody = comment.body;
		if(comment.deleted != null) {
			this.deleted = comment.deleted;
		}
		if(comment.folder != null) {
			List<Resource> resource = Resource.findAllResourceOfFolder(comment.folder.id);
			this.image = resource.get(0).id;
		}
		
	}
	
	public DeletedInfoVM(DeletedInfo deletedInfo,Community community) {
		this.reportType = deletedInfo.reportType.name();
		this.description = deletedInfo.description;
		this.objectType = deletedInfo.objectType.name();
		this.id = deletedInfo.id;
		this.socialObjectID = deletedInfo.socialObjectID;
		this.reportedBy = deletedInfo.reportedBy;
		this.reportedDate = DateTimeUtil.toString(deletedInfo.reportedDate);
		if(community.deleted != null) {
			this.deleted = community.deleted;
		}
		this.communityName = community.name;
		this.createdDate = DateTimeUtil.toString(community.auditFields.getCreatedDate());
		if(community.owner != null) {
			this.owner = community.owner.displayName;
		}	
		this.posts = Post.getPosts(community.id);
		if(community.albumPhotoProfile != null) {
			List<Resource> resource = Resource.findAllResourceOfFolder(community.albumPhotoProfile.id);
			this.image = resource.get(0).id;
		}
		
	}
	
	public DeletedInfoVM(DeletedInfo deletedInfo,User user) {
		this.reportType = deletedInfo.reportType.name();
		this.description = deletedInfo.description;
		this.objectType = deletedInfo.objectType.name();
		this.id = deletedInfo.id;
		this.socialObjectID = deletedInfo.socialObjectID;
		this.reportedBy = deletedInfo.reportedBy;
		this.reportedDate = DateTimeUtil.toString(deletedInfo.reportedDate);
		if(user.deleted != null) {
			this.deleted = user.deleted;
		}
		this.userName = user.displayName;
		this.lastLogin = DateTimeUtil.toString(user.lastLogin);
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
		this.createdDate = DateTimeUtil.toString(user.auditFields.getCreatedDate());
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
