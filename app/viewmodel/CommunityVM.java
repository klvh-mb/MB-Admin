package viewmodel;

import java.text.SimpleDateFormat;

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
		this.deleted = community.deleted;
		this.communityName = community.name;
		this.createdDate = formatter.format(community.createDate);
		this.owner = community.owner.displayName;
		this.posts = Post.getPosts(community.id);
		Resource resource = Resource.findAllResourceOfFolder(community.albumPhotoProfile.id);
		this.image = resource.id;
	}
	
}
