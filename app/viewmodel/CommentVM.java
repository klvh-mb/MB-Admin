package viewmodel;

import models.Comment;
import models.Post;

public class CommentVM {
	
	public Long id;
	public String body;
	public boolean deleted;
	
	public CommentVM() {
		
	}
	
	public CommentVM(Comment comment) {
		this.id = comment.id;
		this.body = comment.body;
		this.deleted = comment.deleted;
	}
}
