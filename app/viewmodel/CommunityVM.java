package viewmodel;

import java.util.List;

import common.utils.DateTimeUtil;

import models.Community;
import models.Post;
import models.Resource;

public class CommunityVM {

	public Long id;
	public boolean deleted;
	public String communityName;
	public String createdDate;
	public String owner;
	public Long posts;
	public Long image;
	
	public CommunityVM() {
	}
	
	public CommunityVM(Community community) {
		this.id = community.id;
		if(community.deleted != null) {
			this.deleted = community.deleted;
		}
		this.communityName = community.name;
		if(community.auditFields.getCreatedDate() != null) {
			this.createdDate = DateTimeUtil.toString(community.auditFields.getCreatedDate());
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
}
