package controllers;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import models.Comment;
import models.Community;
import models.DeletedInfo;
import models.EDMJob;
import models.GameAccount;
import models.GameRedemption;
import models.Post;
import models.ReportedObject;
import models.Resource;
import models.Subscription;
import models.User;
import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

import play.Play;
import play.data.DynamicForm;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import viewmodel.CommentVM;
import viewmodel.CommunityVM;
import viewmodel.DeletedInfoVM;
import viewmodel.GameAccountVM;
import viewmodel.GameRedemptionVM;
import viewmodel.PostVM;
import viewmodel.ReportedObjectVM;
import viewmodel.SubscriptionVM;
import viewmodel.UserVM;
import akka.actor.ActorSystem;

import com.ning.http.util.DateUtil;
import common.utils.ImageUploadUtil;

import domain.ReportType;
import domain.SocialObjectType;
import email.EDMUtility;

public class ReportsController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(ReportsController.class);
    
    public static final int PAGINATION_SIZE = 20;
    
    public static final String EDM_LOGGING_PATH = Play.application().configuration().getString("edm.logging.path");
    
    private static final EDMUtility edmUtility = new EDMUtility();
    
    public ReportsController() {}
    
	@Transactional
	public static Result getReportedPosts(int currentPage) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = ReportedObject.getAllPostsTotal(PAGINATION_SIZE);
		long size = ReportedObject.getSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedPosts(currentPage, PAGINATION_SIZE, totalPages);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		Post post = Post.findById(postId);
		post.deleted = true;
		post.merge();
		ReportedObject reportedObject = ReportedObject.findById(id);
		DeletedInfo deletedInfo = new DeletedInfo(reportedObject);
		deletedInfo.save();
		reportedObject.delete();
		return ok();
	}
	
	@Transactional
	public static Result getDeletedPosts(int currentPage,String communityId) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = DeletedInfo.getAllPostsTotal(PAGINATION_SIZE,communityId);
		long size = DeletedInfo.getSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedPosts(currentPage, PAGINATION_SIZE, totalPages, communityId);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		Post post = Post.findById(postId);
		post.deleted = false;
		post.merge();
		return ok();
	}
	
	@Transactional
	public static Result getAllPosts(int currentPage,String title) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = Post.getAllPostsTotal(PAGINATION_SIZE,title);
		List<Post> postList = Post.findAllPosts(currentPage, PAGINATION_SIZE, totalPages, title);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		Post post = Post.findById(id);
		post.deleted = true;
		post.merge();
		GameAccount.deletePostByAdmin(post.owner.id);
		return ok();
	}
	
	@Transactional
    public static Result setUnDeletePost(Long id) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		Post post = Post.findById(id);
		post.deleted = false;
		post.merge();
		return ok();
	}
	
	@Transactional
	public static Result getReportedComments(int currentPage) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = ReportedObject.getAllCommentsTotal(PAGINATION_SIZE);
		long size = ReportedObject.getCommentSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedComments(currentPage, PAGINATION_SIZE, totalPages);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		Comment comment = Comment.findById(commentId);
		comment.deleted = true;
		comment.merge();
		ReportedObject reportedObject = ReportedObject.findById(id);
		DeletedInfo deletedInfo = new DeletedInfo(reportedObject);
		deletedInfo.save();
		reportedObject.delete();
		return ok();
	}
	
	@Transactional
    public static Result getDeletedComments(int currentPage,String communityId) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = DeletedInfo.getAllCommentsTotal(PAGINATION_SIZE,communityId);
		long size = DeletedInfo.getCommentsSize();
		List<Object[]> deletedInfos = DeletedInfo.getAllDeletedComments(currentPage, PAGINATION_SIZE, totalPages,communityId);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		Comment comment = new Comment();
		for (Object[] d:deletedInfos) {
			comment = Comment.findById(((BigInteger)d[6]).longValue());
			DeletedInfo deletedInfo = new DeletedInfo();
			deletedInfo.setReportType(ReportType.valueOf(d[2].toString()));
			deletedInfo.comment = d[1].toString();
			deletedInfo.setObjectType(SocialObjectType.valueOf(d[3].toString()));
			deletedInfo.id = ((BigInteger)d[0]).longValue();
			deletedInfo.socialObjectID = ((BigInteger)d[6]).longValue();
			deletedInfo.reportedBy = d[4].toString();
			deletedInfo.reportedDate = (Date)d[5];
			DeletedInfoVM vm = new DeletedInfoVM(deletedInfo,comment);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = Comment.getAllCommentsTotal(PAGINATION_SIZE,title);
		List<Comment> commentList = Comment.findAllComments(currentPage, PAGINATION_SIZE, totalPages, title);
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
		GameAccount.deleteCommentByAdmin(comment.owner.id);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = ReportedObject.getAllQuestionsTotal(PAGINATION_SIZE);
		long size = ReportedObject.getQuestionSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedQuestions(currentPage, PAGINATION_SIZE, totalPages);
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
		Post post = Post.findById(questionId);
		post.deleted = true;
		post.merge();
		ReportedObject reportedObject = ReportedObject.findById(id);
		DeletedInfo deletedInfo = new DeletedInfo(reportedObject);
		deletedInfo.save();
		reportedObject.delete();
		return ok();
	}
	
	@Transactional
	public static Result getDeletedQuestions(int currentPage, String communityId) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = DeletedInfo.getAllQuestionsTotal(PAGINATION_SIZE,communityId);
		long size = DeletedInfo.getQuestionSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedQuestions(currentPage, PAGINATION_SIZE, totalPages, communityId);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = Post.getAllQuestionsTotal(PAGINATION_SIZE,title);
		List<Post> questionList = Post.findAllQuestions(currentPage, PAGINATION_SIZE, totalPages, title);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = ReportedObject.getAllAnswersTotal(PAGINATION_SIZE);
		long size = ReportedObject.getAnswerSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedAnswers(currentPage, PAGINATION_SIZE, totalPages);
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
    public static Result getDeletedAnswers(int currentPage,String communityId) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = DeletedInfo.getAllAnswersTotal(PAGINATION_SIZE,communityId);
		long size = DeletedInfo.getAnswersSize();
		List<Object[]> deletedInfos = DeletedInfo.getAllDeletedAnswers(currentPage, PAGINATION_SIZE, totalPages,communityId);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		Comment comment = new Comment();
		for (Object[] d:deletedInfos) {
			comment = Comment.findById(((BigInteger)d[6]).longValue());
			DeletedInfo deletedInfo = new DeletedInfo();
			deletedInfo.setReportType(ReportType.valueOf(d[2].toString()));
			deletedInfo.comment = d[1].toString();
			deletedInfo.setObjectType(SocialObjectType.valueOf(d[3].toString()));
			deletedInfo.id = ((BigInteger)d[0]).longValue();
			deletedInfo.socialObjectID = ((BigInteger)d[6]).longValue();
			deletedInfo.reportedBy = d[4].toString();
			deletedInfo.reportedDate = (Date)d[5];
			DeletedInfoVM vm = new DeletedInfoVM(deletedInfo,comment);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = Comment.getAllAnswersTotal(PAGINATION_SIZE,title);
		List<Comment> answerList = Comment.findAllAnswers(currentPage, PAGINATION_SIZE, totalPages, title);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = ReportedObject.getAllCommunitiesTotal(PAGINATION_SIZE);
		long size = ReportedObject.getCommunitySize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedCommunities(currentPage, PAGINATION_SIZE, totalPages);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = DeletedInfo.getAllCommunityTotal(PAGINATION_SIZE);
		long size = DeletedInfo.getCommunitySize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedcommunities(currentPage, PAGINATION_SIZE, totalPages);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = Community.getAllCommunitiesTotal(PAGINATION_SIZE,title);
		List<Community> communityList = Community.findAllCommunities(currentPage, PAGINATION_SIZE, totalPages, title);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = ReportedObject.getAllUsersTotal(PAGINATION_SIZE);
		long size = ReportedObject.getUsersSize();
		List<ReportedObject> reportedObjects = ReportedObject.getAllReportedUsers(currentPage, PAGINATION_SIZE, totalPages);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = DeletedInfo.getAllUsersTotal(PAGINATION_SIZE);
		long size = DeletedInfo.getUserSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedUsers(currentPage, PAGINATION_SIZE, totalPages);
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
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = User.getAllUsersTotal(PAGINATION_SIZE,title);
		List<User> userList = User.findAllUsers(currentPage, PAGINATION_SIZE, totalPages, title);
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
	
	@Transactional
    public static Result getAllSubscription() {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		List<Subscription> subscriptions = Subscription.getAllSubscription();
		List<SubscriptionVM> subscriptionVMs = new ArrayList<>();
		for(Subscription s : subscriptions) {
			SubscriptionVM vm = new SubscriptionVM(s);
			subscriptionVMs.add(vm);
		}
		return ok(Json.toJson(subscriptionVMs));
	}
	
	@Transactional
	public static Result getAllSubscribedUsers(int currentPage,String title,String gender,String location,String subscription) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		long totalPages = User.getAllSubscribedUsersTotal(2, title, gender, location, subscription);
		List<Object[]> userList = User.getAllSubscribedUsers(currentPage, 2, totalPages, title, gender, location, subscription);
		List<UserVM> userVMs = new ArrayList<>();
		for (Object[] obj:userList) {
			UserVM vm = new UserVM(obj);
			userVMs.add(vm);
		}
		
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		if(totalPages == 0) {
			currentPage = 0;
		}
		
		List<Long> ids = User.getAllUsersId(title, gender, location, subscription);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("ids", ids);
		map.put("results", userVMs);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result sendEmailsToSubscribedUsers(String ids, String subscription, String subject, String body) {
		String[] userIds = ids.split(",");
		for(String userId: userIds) {
			User user = User.findById(Long.parseLong(userId));
			if(subscription.trim().equals("")) {
				List<Long> subscriptionIds = User.getSubscriptionIds(Long.parseLong(userId));
			    for(Long id : subscriptionIds) {
			        Subscription sub = Subscription.findById(id);
			        logger.underlyingLogger().info(String.format("send edm to user [u=%d|name=%s|sub=%d]", user.id, user.name, sub.id));
			        edmUtility.sendMailToUser(user,sub,subject,body);
			    }
			} else {
                Subscription sub = Subscription.findById(Long.parseLong(subscription));
                edmUtility.sendMailToUser(user,sub,subject,body);
			}
		}
		return ok();
	}
	
	@Transactional
    public static Result photoUpload() {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		
		DynamicForm form = DynamicForm.form().bindFromRequest();
		boolean fullSize = Boolean.parseBoolean(form.get("fullSize"));
		boolean thumbnail = Boolean.parseBoolean(form.get("thumbnail"));
		boolean miniThumbnail = Boolean.parseBoolean(form.get("miniThumbnail"));
		boolean mobile = Boolean.parseBoolean(form.get("mobile"));
		String category = form.get("category");
		ImageUploadUtil imageUploadUtil = new ImageUploadUtil(category);
		FilePart picture = request().body().asMultipartFormData().getFile("cover-photo");
        String fileName = picture.getFilename();
        DateTime  now = new DateTime();
        File file = picture.getFile();
        String imagePath;
        try {
            imagePath = imageUploadUtil.getImagePath(now, fileName);
            FileUtils.copyFile(file, new File(imagePath));
        } catch (IOException e) {
            return status(500);
        }
        
        if(thumbnail == true) {
        	StringBuffer sb = new StringBuffer(fileName);
        	sb.insert(fileName.indexOf("."),"_thumbnail");
        	String name = sb.toString();
        	try {
                String imagePath2 = imageUploadUtil.getImagePath(now, name);
                BufferedImage originalImage = ImageIO.read(file);
                File file2 = new File(imagePath2);
                Thumbnails.of(originalImage).size(200, 200).toFile(file2);
                
            } catch (IOException e) {
                return status(500);
            }
        }
        
        if(miniThumbnail == true) {
        	StringBuffer sb = new StringBuffer(fileName);
        	sb.insert(fileName.indexOf("."),"_mini" );
        	String name = sb.toString();
        	try {
                String imagePath2 = imageUploadUtil.getImagePath(now, name);
                BufferedImage originalImage = ImageIO.read(file);
                File file2 = new File(imagePath2);
                Thumbnails.of(originalImage).size(150, 150).toFile(file2);
            } catch (IOException e) {
                return status(500);
            }
        }
        
        if(mobile == true) {
        	StringBuffer sb = new StringBuffer(fileName);
        	sb.insert(fileName.indexOf("."),"_m" );
        	String name = sb.toString();
        	try {
                String imagePath2 = imageUploadUtil.getImagePath(now, name);
                BufferedImage originalImage = ImageIO.read(file);
                File file2 = new File(imagePath2);
                Thumbnails.of(originalImage).size(100, 100).toFile(file2);
            } catch (IOException e) {
                return status(500);
            }
        	
        	if(thumbnail == true) {
        		StringBuffer sb1 = new StringBuffer(fileName);
            	sb1.insert(fileName.indexOf("."),"_thumbnail_m" );
            	String name1 = sb1.toString();
            	try {
                    String imagePath2 = imageUploadUtil.getImagePath(now, name1);
                    BufferedImage originalImage = ImageIO.read(file);
                    File file2 = new File(imagePath2);
                    Thumbnails.of(originalImage).size(80, 80).toFile(file2);
                } catch (IOException e) {
                    return status(500);
                }
        	}
        	
        	if(miniThumbnail == true) {
        		StringBuffer sb1 = new StringBuffer(fileName);
            	sb1.insert(fileName.indexOf("."),"_mini_m" );
            	String name1 = sb1.toString();
            	try {
                    String imagePath2 = imageUploadUtil.getImagePath(now, name1);
                    BufferedImage originalImage = ImageIO.read(file);
                    File file2 = new File(imagePath2);
                    Thumbnails.of(originalImage).size(50, 50).toFile(file2);
                } catch (IOException e) {
                    return status(500);
                }
        	}
        	
        }
        
		Map<String, String> map = new HashMap<>();
        map.put("URL", imagePath);
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result getPostImageById(Long id) {
    	response().setHeader("Cache-Control", "max-age=1");
        return ok(Resource.findById(id).getRealFile());
    }
	
	@Transactional
    public static Result getGameAccountAllUsers() {
		List<GameAccount> accounts = GameAccount.getAllGameAccounts();
		List<GameAccountVM> accountVMs = new ArrayList<>();
		for(GameAccount s : accounts) {
			GameAccountVM vm = new GameAccountVM(s);
			accountVMs.add(vm);
		}
		return ok(Json.toJson(accountVMs));
	}
	
	@Transactional
	public static Result addBonus() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
	    String id = form.get("id");
	    String bonus = form.get("bonus");
	    String detail = form.get("detail");
	    String uId = form.get("uId");
	    GameAccount account = GameAccount.findByUserId(Long.parseLong(uId));
	    account.addBonusbyAdmin(bonus,detail);
	    System.out.println("id :::: "+id+"\n bonus :::: "+bonus+"\n detail ::::: "+detail+"\n uId :::::"+uId);
        return ok();
    }
	
	@Transactional
	public static Result addPenalty() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
	    String id = form.get("id");
	    String bonus = form.get("penalty");
	    String detail = form.get("detail");
	    String uId = form.get("uId");
	    GameAccount account = GameAccount.findByUserId(Long.parseLong(uId));
	    account.addPenaltybyAdmin(bonus,detail);
	    System.out.println("id :::: "+id+"\n bonus :::: "+bonus+"\n detail ::::: "+detail+"\n uId :::::"+uId);
        return ok();
    }
	
	@Transactional
    public static Result getRedemptionRequests() {
		List<GameRedemption> redemptions = GameRedemption.getAllGameRedemption();
		List<GameRedemptionVM> redemptionVMs = new ArrayList<>();
		for(GameRedemption r : redemptions) {
			GameRedemptionVM vm = new GameRedemptionVM(r);
			redemptionVMs.add(vm);
		}
		return ok(Json.toJson(redemptionVMs));
	}
	
	@Transactional
    public static Result confirmRedemptionRequests() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
	    String id = form.get("id");
	    GameRedemption.confirmRedemptionRequest(id);
		return ok();
	}
	
	
	@Transactional
	public static Result sendTestEDM() {
		final String value = session().get("NAME");

        if (value == null) {
        	return ok(views.html.login.render());
        }
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String[] userEmails = form.get("userEmails").split(",");
		for(String userEmail : userEmails) {
			User user = User.findByEmail(userEmail);
			if(form.get("subscription").trim().equals("")) {
				List<Long> subscriptionIds = User.getSubscriptionIds(user.id);
			    for(Long id : subscriptionIds) {
			        Subscription sub = Subscription.findById(id);
			        logger.underlyingLogger().info(String.format("send edm to user [u=%d|name=%s|sub=%d]", user.id, user.name, sub.id));
			        edmUtility.sendMailToUser(user,sub,form.get("EDMSubject"),form.get("EDMBody"));
			    }
			} else {
                Subscription sub = Subscription.findById(Long.parseLong(form.get("subscription")));
                edmUtility.sendMailToUser(user,sub,form.get("EDMSubject"),form.get("EDMBody"));
			}
		}
		
		
		
		logger.underlyingLogger().info(String.format("Completed EDM job [id=%s]", form.get("usersId")));
		return ok();
	}
	
	// EDM job status
	private static Long success = 0L;
	private static Long fail= 0L;
    
	@Transactional
	public static Result sendBulkEDM() {
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        
		final DynamicForm form = DynamicForm.form().bindFromRequest();
		EDMJob edmJob = new EDMJob();
		edmJob.startTime = new Date();
		edmJob.json = Json.stringify(Json.toJson(form.get()));
		edmJob.save();
		final Long id = edmJob.id;
		ActorSystem  actorSystem1 = Akka.system();
		actorSystem1.scheduler().scheduleOnce(
		        Duration.create(0, TimeUnit.MILLISECONDS), 
		        new Runnable() {
                    public void run() {
                        try {
                    	   JPA.withTransaction(new play.libs.F.Callback0() {
	           					public void invoke() {
	           						String message = "Starts";
	           						List<User> users = User.filterUsers(form);
	           						message = message + "\n Total Users :: "+users.size();
           							for(User user : users){
           								
           								try{
	           								if(user.active && 
	           								        user.emailValidated &&
	           								        user.deleted && 
	           								        !StringUtils.isEmpty(user.email) && 
	           										!StringUtils.isEmpty(user.firstName)) {
	           									sendEmailsToSubscribedUsers(user.id.toString(), form.get("subscription"), form.get("EDMSubject"), form.get("EDMBody"));
	           									ReportsController.success++;
	           									message  = message + "\nThis User successed ID :: "+user.id;
	           								} else {
	           									message  = message + "\n#ELSE CONDITION \n This User failed ID :: "+user.id;
	           									ReportsController.fail++;
	           								}
           								}catch(Exception e){
           									message  = message + "\n#EXCEPTION \n This User failed ID :: "+user.id;
           									message = message + ExceptionUtils.getFullStackTrace(e);
    	           							ReportsController.fail++;
    	           						}
           							}

                                	EDMJob edmJob = EDMJob.findById(id);
               						edmJob.successCount = ReportsController.success;
               				        edmJob.failureCount = ReportsController.fail;
               				        edmJob.endTime = new Date();
               				        edmJob.merge();
               				        message = message + "\n#RESULT \n success :: "+ReportsController.success+" \n fail :: "+ReportsController.fail;
               				        String name = EDM_LOGGING_PATH + "/" + "edm_job_"+edmJob.id+"_"+
               				        			DateUtil.formatDate(edmJob.startTime,"yyyyMMdd")+"_"+
               				        			DateUtil.formatDate(edmJob.startTime,"hhmm")+".log";
               				        try {
	               				        File file = new File(name);
		               				    if (!file.exists()) {
		               						file.createNewFile();
		               					}
		               		 
		               					FileWriter fw = new FileWriter(file.getAbsoluteFile());
									
		               					BufferedWriter bw = new BufferedWriter(fw);
		               					bw.write(message);
		               					bw.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
               				        ReportsController.success = 0L;
               				        ReportsController.fail = 0L;
               				        logger.underlyingLogger().info(String.format("Completed EDM job [id=%d]", edmJob.id));
	                    		}
                    		});
                        } catch (Exception e) {
                            logger.underlyingLogger().error(ExceptionUtils.getFullStackTrace(e));
                        }
                    }
                }, actorSystem1.dispatcher()
        );
        return ok();
	}
}
