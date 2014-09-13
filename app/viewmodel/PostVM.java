package viewmodel;

import java.util.List;

import models.Post;
import models.Resource;

public class PostVM {

	public Long id;
	public String title;
	public String body;
	public boolean deleted;
	public Long image;
	
	public PostVM() {
		
	}
	
	public PostVM(Post post) {
		this.id = post.id;
		this.title = post.title;
		this.body = post.body;
		if(post.deleted != null) {
			this.deleted = post.deleted;
		}
		if(post.folder != null) {
			List<Resource> resource = Resource.findAllResourceOfFolder(post.folder.id);
			this.image = resource.get(0).id;
		}	
	}
}
