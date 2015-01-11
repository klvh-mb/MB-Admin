package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.FrontPageTopic;
import models.FrontPageTopic.TopicSubType;
import models.FrontPageTopic.TopicType;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.FrontPageTopicVM;

public class FrontPageTopicController extends Controller{
    private static final play.api.Logger logger = play.api.Logger.apply(FrontPageTopicController.class);
    
	@Transactional
	public static Result getFrontPageTopics(String title,int currentPage) {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
		long totalPages = FrontPageTopic.getAllFrontPageTopicsTotal(title, 10);
		List<FrontPageTopic> allFrontPageTopics = FrontPageTopic.getAllFrontPageTopics(title, currentPage, 10, totalPages);
			
		List<FrontPageTopicVM> listOfFrontPageTopics = new ArrayList<>();
		for (FrontPageTopic frontPageTopic:allFrontPageTopics) {
		    FrontPageTopicVM vm = new FrontPageTopicVM(frontPageTopic);
			listOfFrontPageTopics.add(vm);
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", listOfFrontPageTopics);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result saveFrontPageTopic() {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
		DynamicForm form = DynamicForm.form().bindFromRequest();
		FrontPageTopic frontPageTopic = new FrontPageTopic();
		frontPageTopic.topicType = TopicType.valueOf(form.get("topicType"));
		frontPageTopic.topicSubType = TopicSubType.valueOf(form.get("topicSubType"));
		frontPageTopic.seq = Integer.parseInt(form.get("seq"));
		frontPageTopic.name = form.get("name");
		frontPageTopic.description = form.get("description");
		frontPageTopic.image = form.get("image");
		frontPageTopic.url = form.get("url");
		frontPageTopic.attribute = form.get("attribute");
		frontPageTopic.publishedDate = new Date();
		frontPageTopic.active = true;
		frontPageTopic.save();
		return ok();
	}
	
	@Transactional
	public static Result deleteFrontPageTopic(Long id) {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        FrontPageTopic frontPageTopic = FrontPageTopic.findById(id);
        frontPageTopic.deleted = true;
        frontPageTopic.save();
		return ok();
	}
	
	@Transactional
    public static Result toggleActiveFrontPageTopic(Long id) {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        FrontPageTopic frontPageTopic = FrontPageTopic.findById(id);
        frontPageTopic.active = !frontPageTopic.active;
        frontPageTopic.save();
        return ok();
    }
	
	@Transactional
	public static Result updateFrontPageTopic() {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
		DynamicForm form = DynamicForm.form().bindFromRequest();
		FrontPageTopic frontPageTopic = FrontPageTopic.findById(Long.parseLong(form.get("id")));
		frontPageTopic.topicType = TopicType.valueOf(form.get("ty"));
		frontPageTopic.topicSubType = TopicSubType.valueOf(form.get("sty"));
		frontPageTopic.seq = Integer.parseInt(form.get("seq"));
		frontPageTopic.name = form.get("nm");
		frontPageTopic.description = form.get("ds");
		frontPageTopic.image = form.get("img");
		frontPageTopic.url = form.get("url");
		frontPageTopic.attribute = form.get("attr");
		frontPageTopic.merge();
		return ok();
	}
}
