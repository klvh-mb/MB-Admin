package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Article;
import models.ArticleCategory;
import models.Location;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import common.utils.ImageUploadUtil;

import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import viewmodel.ArticleCategoryVM;
import viewmodel.ArticleVM;
import viewmodel.LocationVM;

public class ArticleController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(ArticleController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("article");
    
    @Transactional
    public static Result addArticle() {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        
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
        
        if (article.targetAgeMinMonth >= article.targetAgeMaxMonth) {
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
            //return status(509, "PLEASE CHOOSE TARGET LOCATION");
            targetLocation_id = Location.getHongKongCity().id;
        }
        
        Location location = Location.getLocationById(targetLocation_id);
        article.targetLocation = location;
        ArticleCategory ac = ArticleCategory.getCategoryById(category_id);
        article.category = ac;
        article.publishedDate = new Date();
        
        article.save();
        logger.underlyingLogger().info(value+" saved article ["+article.id+"|"+article.name+"]");
        return ok();
    }
    
    @Transactional
    public static Result updateArticle() {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        
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
        article.update();
        
        logger.underlyingLogger().info(value+" updated article ["+article.id+"|"+article.name+"]");
        return ok();
    }

    @Transactional
    public static Result getAllDistricts() {
	   final String value = session().get("NAME");
       if (value == null) {
       	return ok(views.html.login.render());
       }
        //List<Location> locations = Location.getHongKongDistricts();  // TODO
        List<Location> locations = Location.getHongKongCityRegionsDistricts();
        
        List<LocationVM> locationVMs = new ArrayList<>();
        for(Location location : locations) {
            LocationVM vm = LocationVM.locationVM(location);
            locationVMs.add(vm);
        }
        return ok(Json.toJson(locationVMs));
    }
   
    @Transactional
    public static Result getAllArticleCategory() {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        List<ArticleCategory> articleCategorys = ArticleCategory.getAllCategory();
        
        List<ArticleCategoryVM> articleCategoryVMs = new ArrayList<>();
        for(ArticleCategory articleCategory : articleCategorys) {
            ArticleCategoryVM vm = ArticleCategoryVM.articleCategoryVM(articleCategory);
            articleCategoryVMs.add(vm);
        }
        return ok(Json.toJson(articleCategoryVMs));
    }
    
    @Transactional
    public static Result getLatestArticles() {
        List<Article> allArticles = Article.getLatestArticles();
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
    public static Result getArticles(String id,String name) {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        List<Article> allArticles = Article.getArticles(id,name);
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
    public static Result getDescriptionOfArticle(Long art_id) {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        Article article = Article.findById(art_id);
        Map<String, String> description = new HashMap<>();
        description.put("description", article.description);
        return ok(Json.toJson(description));
    }
    
    @Transactional
    public static Result deleteArticle(Long art_id) {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        Article article = Article.findById(art_id);
        if (article != null) {
            article.delete();
        }
        logger.underlyingLogger().info(value+" deleted article ["+art_id+"]");
        return ok();
    }
    
    @Transactional
    public static Result infoArticle(Long art_id) {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        Article article = Article.findById(art_id);
        return ok(Json.toJson(article));
    }
    
    @Transactional
    public static Result uploadImage() {
    	final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
        FilePart picture = request().body().asMultipartFormData().getFile("url-photo0");
        String fileName = picture.getFilename();
        logger.underlyingLogger().info("uploadImage. fileName=" + fileName);

        DateTime  now = new DateTime();
        File file = picture.getFile();
        try {
            String imagePath = imageUploadUtil.getImagePath(now, fileName);
            FileUtils.copyFile(file, new File(imagePath));
        } catch (IOException e) {
            logger.underlyingLogger().error(value+" failed to upload photo", e);
            return status(500);
        }

        String imageUrl = imageUploadUtil.getImageUrl(now, fileName);
        logger.underlyingLogger().info("uploadImage. imageUrl=" + imageUrl);

        Map<String, String> map = new HashMap<>();
        map.put("URL", imageUrl);
        logger.underlyingLogger().info(value+" uploaded photo - "+imageUrl);
        return ok(Json.toJson(map));
    }

    @Transactional
    public static Result getImage(Long year, Long month, Long date, String name) {
        String path = imageUploadUtil.getImagePath(year, month, date, name);
        return ok(new File(path));
    }
}