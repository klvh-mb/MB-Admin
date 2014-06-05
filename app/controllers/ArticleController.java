package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Article;
import models.ArticleCategory;
import models.Location;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.ArticleCategoryVM;
import viewmodel.ArticleVM;
import viewmodel.LocationVM;

public class ArticleController extends Controller {

	@Transactional
	public static Result addArticle() {
		Form<Article> articleForm = DynamicForm.form(Article.class).bindFromRequest();
		DynamicForm form = DynamicForm.form().bindFromRequest();
		Long category_id;
		Long targetLocation_id;
		
		Article article = articleForm.get();
		try {
    		article.targetAgeMinMonth = Integer.parseInt(form.get("targetAgeMinMonth"));
    		article.targetAgeMaxMonth = Integer.parseInt(form.get("targetAgeMaxMonth"));
    	} catch(NumberFormatException ne) {
			return status(507,"PLEASE SELECT TARGET AGE");
		}
		
		if (article.targetAgeMinMonth >=article.targetAgeMaxMonth) {
			return status(508,"TargetAgeMinMonth should be less than TargetAgeMaxMonth");
		}
		
		if (!Article.checkTitleExists(article.name)) {
			return status(505, "PLEASE CHOOSE OTHER TITLE");
		}
		
		try {
		    category_id = Long.parseLong(form.get("category_id"));
		} catch(NumberFormatException e) {
			return status(506, "PLEASE CHOOSE CATEGORY");
		}
		
		try {
		    article.targetGender = Integer.parseInt(form.get("targetGender"));
		    article.targetParentGender = Integer.parseInt(form.get("targetParentGender"));
		} catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		
		try {
            targetLocation_id = Long.parseLong(form.get("targetLocation_id"));
        } catch(NumberFormatException e) {
            return status(506, "PLEASE CHOOSE TARGET LOCATION");
        }

		Location location = Location.getLocationById(targetLocation_id);
		article.targetLocation = location;
		ArticleCategory ac = ArticleCategory.getCategoryById(category_id);
		article.category = ac;
		article.publishedDate = new Date();
		
		article.saveArticle();
		return ok();
	}
	
	@Transactional
	public static Result updateArticle() {
		Form<Article> articleForm = DynamicForm.form(Article.class).bindFromRequest();
		DynamicForm form = DynamicForm.form().bindFromRequest();
		
		Long id = Long.parseLong(form.get("id"));
		Article article = Article.findById(id);
		article.name = form.get("name");
		article.targetAgeMinMonth = Integer.parseInt(form.get("targetAgeMinMonth"));
		article.targetAgeMaxMonth = Integer.parseInt(form.get("targetAgeMaxMonth"));
		if (article.targetAgeMinMonth >= article.targetAgeMaxMonth) {
			return status(509,"TargetAgeMinMonth should be less than TargetAgeMaxMonth");
		}
		article.targetGender = Integer.parseInt(form.get("targetGender"));
		article.targetParentGender = Integer.parseInt(form.get("targetParentGender"));
		Location location = Location.getLocationById(Long.parseLong(form.get("targetLocation.id")));
		article.targetLocation = location;
		ArticleCategory ac = ArticleCategory.getCategoryById(Long.parseLong(form.get("category.id")));
		article.category = ac;
		article.description = form.get("description");
		article.updateById();
		return ok();
	}

   @Transactional
    public static Result getAllDistricts() {
        List<Location> locations = Location.getHongKongDistricts();  // TODO
        
        List<LocationVM> locationVMs = new ArrayList<>();
        for(Location location : locations) {
            LocationVM vm = LocationVM.locationVM(location);
            locationVMs.add(vm);
        }
        return ok(Json.toJson(locationVMs));
    }
	   
	@Transactional
	public static Result getAllArticleCategory() {
		List<ArticleCategory> articleCategorys = ArticleCategory.getAllCategory();
		
		List<ArticleCategoryVM> articleCategoryVMs = new ArrayList<>();
		for(ArticleCategory articleCategory : articleCategorys) {
			ArticleCategoryVM vm = ArticleCategoryVM.articleCategoryVM(articleCategory);
			articleCategoryVMs.add(vm);
		}
		return ok(Json.toJson(articleCategoryVMs));
	}
	
	@Transactional
	public static Result getAllArticles() {
		List<Article> allArticles = Article.getAllArticles();
		List<ArticleVM> listOfArticles = new ArrayList<>();
		for (Article article:allArticles) {
			ArticleVM vm = new ArticleVM(article.category,
                    article.name, article.id,
                    article.targetAgeMinMonth, article.targetAgeMaxMonth,
                    article.targetGender, article.targetParentGender, article.targetLocation);
			listOfArticles.add(vm);
		}
		return ok(Json.toJson(listOfArticles));
	}
	
	@Transactional
	public static Result getDescriptionOdArticle(Long art_id) {
		Article article = Article.findById(art_id);
		Map<String, String> description = new HashMap<>();
		description.put("description", article.description);
		return ok(Json.toJson(description));
	}
	
	@Transactional
	public static Result deleteArticle(Long art_id) {
		Article.deleteByID(art_id);
		return ok();
	}
	
	@Transactional
	public static Result infoArticle(Long art_id) {
		Article article = Article.findById(art_id);
		return ok(Json.toJson(article));
	}
}
