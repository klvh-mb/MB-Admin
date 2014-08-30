package viewmodel;

import models.Post;

public class PostVM {

	public Long id;
	public String title;
	public String body;
	public boolean deleted;
	
	public PostVM() {
		
	}
	
	public PostVM(Post post) {
		this.id = post.id;
		this.title = post.title;
		this.body = post.body;
		this.deleted = post.deleted;
	}
}
