package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Announcement;
import models.Comment;
import models.Community;
import models.DeletedInfo;
import models.Post;
import models.ReportedObject;
import models.Resource;
import models.User;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CommentVM;
import viewmodel.CommunityVM;
import viewmodel.DeletedInfoVM;
import viewmodel.PostVM;
import viewmodel.ReportedObjectVM;
import viewmodel.UserVM;

public class ReportsController extends Controller {

	@Transactional
	public static Result getReportedPosts(int currentPage) {
		long totalPages = ReportedObject.getAllPostsTotal(3);
		long size = ReportedObject.getSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedPosts(currentPage, 3, totalPages);
		List<ReportedObjectVM> reportedObjectVMs = new ArrayList<>();
		Post post = new Post();
		for (ReportedObject r:reportedObjects) {
			post = Post.findById(r.socialObjectID);
			ReportedObjectVM vm = new ReportedObjectVM(r,post);
			reportedObjectVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", reportedObjectVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result deleteReportedObject(Long id,Long postId) {
		System.out.println(":::::::"+id+"::::"+postId);
		Post post = Post.findById(postId);
		post.deleted = true;
		System.out.println("::::::::::"+post.body);
		post.merge();
		ReportedObject reportedObject = ReportedObject.findById(id);
		DeletedInfo deletedInfo = new DeletedInfo(reportedObject);
		deletedInfo.save();
		reportedObject.delete();
		return ok();
	}
	
	@Transactional
	public static Result getDeletedPosts(int currentPage) {
		long totalPages = DeletedInfo.getAllPostsTotal(3);
		long size = DeletedInfo.getSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedPosts(currentPage, 3, totalPages);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		Post post = new Post();
		for (DeletedInfo d:deletedInfos) {
			post = Post.findById(d.socialObjectID);
			DeletedInfoVM vm = new DeletedInfoVM(d,post);
			deletedInfoVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", deletedInfoVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result deletedInfoUnDelete(Long id,Long postId) {
		System.out.println(":::::::"+id+"::::"+postId);
		Post post = Post.findById(postId);
		post.deleted = false;
		System.out.println("::::::::::"+post.body);
		post.merge();
		//DeletedInfo deletedInfo = DeletedInfo.findById(id);
		//deletedInfo.delete();
		return ok();
	}
	
	@Transactional
	public static Result getAllPosts(int currentPage,String title) {
		long totalPages = Post.getAllPostsTotal(3,title);
		List<Post> postList = Post.findAllPosts(currentPage, 3, totalPages, title);
		List<PostVM> postVMs = new ArrayList<>();
		for (Post p:postList) {
			PostVM vm = new PostVM(p);
			postVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		if(totalPages == 0) {
			currentPage = 0;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", postVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result setDeletePost(Long id) {
		Post post = Post.findById(id);
		post.deleted = true;
		post.merge();
		return ok();
	}
	
	@Transactional
    public static Result setUnDeletePost(Long id) {
		Post post = Post.findById(id);
		post.deleted = false;
		post.merge();
		return ok();
	}
	
	@Transactional
	public static Result getReportedComments(int currentPage) {
		long totalPages = ReportedObject.getAllCommentsTotal(3);
		long size = ReportedObject.getCommentSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedComments(currentPage, 3, totalPages);
		List<ReportedObjectVM> reportedObjectVMs = new ArrayList<>();
		Comment comment = new Comment();
		for (ReportedObject r:reportedObjects) {
			comment = Comment.findById(r.socialObjectID);
			ReportedObjectVM vm = new ReportedObjectVM(r,comment);
			reportedObjectVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", reportedObjectVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result deleteReportedObjectComment(Long id,Long commentId) {
		System.out.println("delete:::::::"+id+"::::"+commentId);
		Comment comment = Comment.findById(commentId);
		comment.deleted = true;
		System.out.println("::::::::::"+comment.body);
		comment.merge();
		ReportedObject reportedObject = ReportedObject.findById(id);
		DeletedInfo deletedInfo = new DeletedInfo(reportedObject);
		deletedInfo.save();
		reportedObject.delete();
		return ok();
	}
	
	@Transactional
    public static Result getDeletedComments(int currentPage) {
		long totalPages = DeletedInfo.getAllCommentsTotal(3);
		long size = DeletedInfo.getCommentsSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedComments(currentPage, 3, totalPages);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		Comment comment = new Comment();
		for (DeletedInfo d:deletedInfos) {
			comment = Comment.findById(d.socialObjectID);
			DeletedInfoVM vm = new DeletedInfoVM(d,comment);
			deletedInfoVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", deletedInfoVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result getAllComments(int currentPage,String title) {
		long totalPages = Comment.getAllCommentsTotal(3,title);
		List<Comment> commentList = Comment.findAllComments(currentPage, 3, totalPages, title);
		List<CommentVM> commentVMs = new ArrayList<>();
		for (Comment c:commentList) {
			CommentVM vm = new CommentVM(c);
			commentVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		if(totalPages == 0) {
			currentPage = 0;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", commentVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result setDeleteComment(Long id) {
		Comment comment = Comment.findById(id);
		comment.deleted = true;
		comment.merge();
		return ok();
	}
	
	@Transactional
    public static Result setUnDeleteComment(Long id) {
		Comment comment = Comment.findById(id);
		comment.deleted = false;
		comment.merge();
		return ok();
	}
	
	@Transactional
	public static Result getReportedQuestions(int currentPage) {
		long totalPages = ReportedObject.getAllQuestionsTotal(3);
		long size = ReportedObject.getQuestionSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedQuestions(currentPage, 3, totalPages);
		List<ReportedObjectVM> reportedObjectVMs = new ArrayList<>();
		Post post = new Post();
		for (ReportedObject r:reportedObjects) {
			post = Post.findById(r.socialObjectID);
			ReportedObjectVM vm = new ReportedObjectVM(r,post);
			reportedObjectVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", reportedObjectVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result deleteReportedObjectQuestion(Long id,Long questionId) {
		System.out.println(":::::::"+id+"::::"+questionId);
		Post post = Post.findById(questionId);
		post.deleted = true;
		System.out.println("::::::::::"+post.body);
		post.merge();
		ReportedObject reportedObject = ReportedObject.findById(id);
		DeletedInfo deletedInfo = new DeletedInfo(reportedObject);
		deletedInfo.save();
		reportedObject.delete();
		return ok();
	}
	
	@Transactional
	public static Result getDeletedQuestions(int currentPage) {
		long totalPages = DeletedInfo.getAllQuestionsTotal(3);
		long size = DeletedInfo.getQuestionSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedQuestions(currentPage, 3, totalPages);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		Post post = new Post();
		for (DeletedInfo d:deletedInfos) {
			post = Post.findById(d.socialObjectID);
			DeletedInfoVM vm = new DeletedInfoVM(d,post);
			deletedInfoVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", deletedInfoVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result setDeleteQuestion(Long id) {
		Post post = Post.findById(id);
		post.deleted = true;
		post.merge();
		return ok();
	}
	
	@Transactional
    public static Result setUnDeleteQuestion(Long id) {
		Post post = Post.findById(id);
		post.deleted = false;
		post.merge();
		return ok();
	}
	
	@Transactional
	public static Result getAllQuestions(int currentPage,String title) {
		long totalPages = Post.getAllQuestionsTotal(3,title);
		List<Post> questionList = Post.findAllQuestions(currentPage, 3, totalPages, title);
		List<PostVM> questionVMs = new ArrayList<>();
		for (Post p:questionList) {
			PostVM vm = new PostVM(p);
			questionVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		if(totalPages == 0) {
			currentPage = 0;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", questionVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result getReportedAnswers(int currentPage) {
		long totalPages = ReportedObject.getAllAnswersTotal(3);
		long size = ReportedObject.getAnswerSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedAnswers(currentPage, 3, totalPages);
		List<ReportedObjectVM> reportedObjectVMs = new ArrayList<>();
		Comment comment = new Comment();
		for (ReportedObject r:reportedObjects) {
			comment = Comment.findById(r.socialObjectID);
			ReportedObjectVM vm = new ReportedObjectVM(r,comment);
			reportedObjectVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", reportedObjectVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result deleteReportedObjectAnswer(Long id,Long answerId) {
		Comment comment = Comment.findById(answerId);
		comment.deleted = true;
		comment.merge();
		ReportedObject reportedObject = ReportedObject.findById(id);
		DeletedInfo deletedInfo = new DeletedInfo(reportedObject);
		deletedInfo.save();
		reportedObject.delete();
		return ok();
	}
	
	@Transactional
    public static Result getDeletedAnswers(int currentPage) {
		long totalPages = DeletedInfo.getAllAnswersTotal(3);
		long size = DeletedInfo.getAnswersSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedAnswers(currentPage, 3, totalPages);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		Comment comment = new Comment();
		for (DeletedInfo d:deletedInfos) {
			comment = Comment.findById(d.socialObjectID);
			DeletedInfoVM vm = new DeletedInfoVM(d,comment);
			deletedInfoVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", deletedInfoVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result setDeleteAnswer(Long id) {
		Comment comment = Comment.findById(id);
		comment.deleted = true;
		comment.merge();
		return ok();
	}
	
	@Transactional
    public static Result setUnDeleteAnswer(Long id) {
		Comment comment = Comment.findById(id);
		comment.deleted = false;
		comment.merge();
		return ok();
	}
	
	@Transactional
	public static Result getAllAnswers(int currentPage,String title) {
		long totalPages = Comment.getAllAnswersTotal(3,title);
		List<Comment> answerList = Comment.findAllAnswers(currentPage, 3, totalPages, title);
		List<CommentVM> commentVMs = new ArrayList<>();
		for (Comment c:answerList) {
			CommentVM vm = new CommentVM(c);
			commentVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		if(totalPages == 0) {
			currentPage = 0;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", commentVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result getReportedCommunities(int currentPage) {
		long totalPages = ReportedObject.getAllCommunitiesTotal(3);
		long size = ReportedObject.getCommunitySize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedCommunities(currentPage, 3, totalPages);
		List<ReportedObjectVM> reportedObjectVMs = new ArrayList<>();
		Community community = new Community();
		for (ReportedObject r:reportedObjects) {
			community = Community.findById(r.socialObjectID);
			ReportedObjectVM vm = new ReportedObjectVM(r,community);
			reportedObjectVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", reportedObjectVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result deleteReportedObjectCommunity(Long id,Long communityId) {
		Community community = Community.findById(communityId);
		community.deleted = true;
		community.merge();
		ReportedObject reportedObject = ReportedObject.findById(id);
		DeletedInfo deletedInfo = new DeletedInfo(reportedObject);
		deletedInfo.save();
		reportedObject.delete();
		return ok();
	}
	
	@Transactional
    public static Result getDeletedCommunities(int currentPage) {
		long totalPages = DeletedInfo.getAllCommunityTotal(3);
		long size = DeletedInfo.getCommunitySize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedcommunities(currentPage, 3, totalPages);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		Community community = new Community();
		for (DeletedInfo d:deletedInfos) {
			community = Community.findById(d.socialObjectID);
			DeletedInfoVM vm = new DeletedInfoVM(d,community);
			deletedInfoVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", deletedInfoVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result setDeleteCommunity(Long id) {
		Community community = Community.findById(id);
		community.deleted = true;
		community.merge();
		return ok();
	}
	
	@Transactional
    public static Result setUnDeleteCommunity(Long id) {
		Community community = Community.findById(id);
		community.deleted = false;
		community.merge();
		return ok();
	}
	
	@Transactional
	public static Result getAllCommunities(int currentPage,String title) {
		long totalPages = Community.getAllCommunitiesTotal(3,title);
		List<Community> communityList = Community.findAllCommunities(currentPage, 3, totalPages, title);
		List<CommunityVM> communityVMs = new ArrayList<>();
		for (Community c:communityList) {
			CommunityVM vm = new CommunityVM(c);
			communityVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		if(totalPages == 0) {
			currentPage = 0;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", communityVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result getReportedUsers(int currentPage) {
		long totalPages = ReportedObject.getAllUsersTotal(3);
		long size = ReportedObject.getUsersSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedUsers(currentPage, 3, totalPages);
		List<ReportedObjectVM> reportedObjectVMs = new ArrayList<>();
		User user = new User();
		for (ReportedObject r:reportedObjects) {
			user = User.findById(r.socialObjectID);
			ReportedObjectVM vm = new ReportedObjectVM(r,user);
			reportedObjectVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", reportedObjectVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result deleteReportedObjectUser(Long id,Long userId) {
		User user = User.findById(userId);
		user.deleted = true;
		user.merge();
		ReportedObject reportedObject = ReportedObject.findById(id);
		DeletedInfo deletedInfo = new DeletedInfo(reportedObject);
		deletedInfo.save();
		reportedObject.delete();
		return ok();
	}
	
	@Transactional
	public static Result getDeletedUsers(int currentPage) {
		long totalPages = DeletedInfo.getAllUsersTotal(3);
		long size = DeletedInfo.getUserSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedUsers(currentPage, 3, totalPages);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		User user = new User();
		for (DeletedInfo d:deletedInfos) {
			user = User.findById(d.socialObjectID);
			DeletedInfoVM vm = new DeletedInfoVM(d,user);
			deletedInfoVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("size", size);
		map.put("currentPage", currentPage);
		map.put("results", deletedInfoVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result setDeleteUser(Long id) {
		User user = User.findById(id);
		user.deleted = true;
		user.merge();
		return ok();
	}
	
	@Transactional
    public static Result setUnDeleteUser(Long id) {
		User user = User.findById(id);
		user.deleted = false;
		user.merge();
		return ok();
	}
	
	@Transactional
	public static Result getAllUsers(int currentPage,String title) {
		long totalPages = User.getAllUsersTotal(3,title);
		List<User> userList = User.findAllUsers(currentPage, 3, totalPages, title);
		List<UserVM> userVMs = new ArrayList<>();
		for (User u:userList) {
			UserVM vm = new UserVM(u);
			userVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		if(totalPages == 0) {
			currentPage = 0;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", userVMs);
		return ok(Json.toJson(map));
	}
	
	/*@Transactional
    public static Result getPostImageById(Long id) {
    	response().setHeader("Cache-Control", "max-age=604800");
    	System.out.println("IMAGE.................................");
        return ok(Resource.findById(id).getThumbnailFile());
        GET 	/image/get-post-image-by-id/:id 	controllers.ReportsController.getPostImageById(id : Long)
    }*/
}
