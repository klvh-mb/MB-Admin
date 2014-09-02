package viewmodel;

import models.Comment;
import models.Post;
import models.Resource;

public class CommentVM {
	
	public Long id;
	public String body;
	public boolean deleted;
	public Long image;
	
	public CommentVM() {
		
	}
	
	public CommentVM(Comment comment) {
		try {
			this.id = comment.id;
			this.body = comment.body;
			this.deleted = comment.deleted;
			Resource resource = Resource.findAllResourceOfFolder(comment.folder.id);
			this.image = resource.id;
		}
		catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
}
