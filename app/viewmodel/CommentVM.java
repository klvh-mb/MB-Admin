package viewmodel;

import java.util.List;

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
			if(comment.deleted != null) {
				this.deleted = comment.deleted;
			}
			if(comment.folder != null) {
				List<Resource> resource = Resource.findAllResourceOfFolder(comment.folder.id);
				this.image = resource.get(0).id;
			}
			
		}
		catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
}
