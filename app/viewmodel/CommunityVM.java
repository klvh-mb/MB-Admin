package viewmodel;

import java.text.SimpleDateFormat;
import java.util.List;

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
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	public CommunityVM() {
		
	}
	
	public CommunityVM(Community community) {
		this.id = community.id;
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
	
}
