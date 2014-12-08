package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.FeaturedTopic.FeaturedType;
import models.FeaturedTopic;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.FeaturedTopicVM;

public class FeaturedTopicController extends Controller{
    private static final play.api.Logger logger = play.api.Logger.apply(FeaturedTopicController.class);
    
	@Transactional
	public static Result getFeaturedTopics(String title,int currentPage) {
		
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		long totalPages = FeaturedTopic.getAllFeaturedTopicsTotal(title, 10);
		List<FeaturedTopic> allFeaturedTopics = FeaturedTopic.getAllFeaturedTopics(title, currentPage, 10, totalPages);
			
		List<FeaturedTopicVM> listOfFeaturedTopics = new ArrayList<>();
		for (FeaturedTopic featuredTopic:allFeaturedTopics) {
		    FeaturedTopicVM vm = new FeaturedTopicVM(featuredTopic);
			listOfFeaturedTopics.add(vm);
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPages", totalPages);
		map.put("currentPage", currentPage);
		map.put("results", listOfFeaturedTopics);
		return ok(Json.toJson(map));
	}
	
	@Transactional
	public static Result saveFeaturedTopic() {
		final String value = session().get("NAME");

        if (value == null) {
        	return ok(views.html.login.render());
        }
		DynamicForm form = DynamicForm.form().bindFromRequest();
		FeaturedTopic featuredTopic = new FeaturedTopic();
		featuredTopic.featuredType = FeaturedType.valueOf(form.get("featuredType"));
		featuredTopic.name = form.get("name");
		featuredTopic.description = form.get("description");
		featuredTopic.image = form.get("image");
		featuredTopic.url = form.get("url");
		featuredTopic.publishedDate = new Date();
		featuredTopic.active = true;
		featuredTopic.save();
		return ok();
	}
	
	@Transactional
	public static Result deleteFeaturedTopic(Long id) {
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        FeaturedTopic featuredTopic = FeaturedTopic.findById(id);
        featuredTopic.deleted = true;
        featuredTopic.save();
		return ok();
	}
	
	@Transactional
    public static Result toggleActiveFeaturedTopic(Long id) {
        final String value = session().get("NAME");
        if (value == null) {
            return ok(views.html.login.render());
        }
        FeaturedTopic featuredTopic = FeaturedTopic.findById(id);
        featuredTopic.active = !featuredTopic.active;
        featuredTopic.save();
        return ok();
    }
	
	@Transactional
	public static Result updateFeaturedTopic() {
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		DynamicForm form = DynamicForm.form().bindFromRequest();
		FeaturedTopic featuredTopic = FeaturedTopic.findById(Long.parseLong(form.get("id")));
		featuredTopic.featuredType = FeaturedType.valueOf(form.get("ty"));
		featuredTopic.name = form.get("nm");
		featuredTopic.description = form.get("ds");
		featuredTopic.image = form.get("img");
		featuredTopic.url = form.get("url");
		featuredTopic.merge();
		return ok();
	}
}
