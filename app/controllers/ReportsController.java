package controllers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Article;
import models.ArticleCategory;
import models.Comment;
import models.Community;
import models.DeletedInfo;
import models.GameAccount;
import models.GameRedemption;
import models.Location;
import models.Post;
import models.ReportedObject;
import models.Resource;
import models.Subscription;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CommentVM;
import viewmodel.CommunityVM;
import viewmodel.DeletedInfoVM;
import viewmodel.GameAccountVM;
import viewmodel.GameRedemptionVM;
import viewmodel.LocationVM;
import viewmodel.PostVM;
import viewmodel.ReportedObjectVM;
import viewmodel.SubscriptionVM;
import viewmodel.UserVM;
import domain.SocialObjectType;
import domain.categoryType;
import email.EDMUtility;

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
		long totalPages = DeletedInfo.getAllPostsTotal(3,communityId);
		long size = DeletedInfo.getSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedPosts(currentPage, 3, totalPages, communityId);
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
		Post post = Post.findById(postId);
		post.deleted = false;
		post.merge();
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
		GameAccount.deletePostByAdmin(post.owner.id);
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
		long totalPages = DeletedInfo.getAllCommentsTotal(3,communityId);
		long size = DeletedInfo.getCommentsSize();
		List<Object[]> deletedInfos = DeletedInfo.getAllDeletedComments(currentPage, 3, totalPages,communityId);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		Comment comment = new Comment();
		for (Object[] d:deletedInfos) {
			comment = Comment.findById(((BigInteger)d[6]).longValue());
			DeletedInfo deletedInfo = new DeletedInfo();
			deletedInfo.setCategory(categoryType.valueOf(d[2].toString()));
			deletedInfo.Comment = d[1].toString();
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
		long totalPages = DeletedInfo.getAllQuestionsTotal(3,communityId);
		long size = DeletedInfo.getQuestionSize();
		List<DeletedInfo> deletedInfos = DeletedInfo.getAllDeletedQuestions(currentPage, 3, totalPages, communityId);
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
    public static Result getDeletedAnswers(int currentPage,String communityId) {
		long totalPages = DeletedInfo.getAllAnswersTotal(3,communityId);
		long size = DeletedInfo.getAnswersSize();
		List<Object[]> deletedInfos = DeletedInfo.getAllDeletedAnswers(currentPage, 3, totalPages,communityId);
		List<DeletedInfoVM> deletedInfoVMs = new ArrayList<>();
		Comment comment = new Comment();
		for (Object[] d:deletedInfos) {
			comment = Comment.findById(((BigInteger)d[6]).longValue());
			DeletedInfo deletedInfo = new DeletedInfo();
			deletedInfo.setCategory(categoryType.valueOf(d[2].toString()));
			deletedInfo.Comment = d[1].toString();
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
	
	@Transactional
    public static Result getAllLocations() {
		List<Location> locations = Location.getAllLocations();
		List<LocationVM> locationVms = new ArrayList<>();
		for(Location l : locations) {
			LocationVM vm = new LocationVM(l);
			locationVms.add(vm);
		}
		return ok(Json.toJson(locationVms));
	}
	
	@Transactional
    public static Result getAllSubscription() {
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
    public static Result sendEmailsToSubscribedUsers(String ids) {
		String[] UserIds = ids.split(",");
		for(String s: UserIds) {
			User user = User.findById(Long.parseLong(s));
			List<Long> subscriptionIds = User.getSubscriptionIds(Long.parseLong(s));
				for(Long id:subscriptionIds) {
					Subscription sub = Subscription.findById(id);
					EDMUtility edmUtility = new EDMUtility();
					System.out.println("...................."+user.displayName+sub.name);
					edmUtility.sendMailToUser(user,sub);
				}
		}
		return ok();
	}
	
	@Transactional
    public static Result getPostImageById(Long id) {
    	response().setHeader("Cache-Control", "max-age=604800");
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
	
}
