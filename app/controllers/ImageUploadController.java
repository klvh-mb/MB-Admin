package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import common.utils.ImageUploadUtil;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class ImageUploadController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(ImageUploadController.class);

    protected static ImageUploadUtil imageUploadUtil;
    
    protected static void setImageUploadUtil(ImageUploadUtil util) {
    	imageUploadUtil = util;
    }
    
    @Transactional
    public static Result uploadPhoto() {
	    final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
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
        String imageUrl;
        String imagePath;
        try {
            imageUrl = imageUploadUtil.getImageUrl(now, fileName);
            imagePath = imageUploadUtil.getImagePath(now, fileName);
            FileUtils.copyFile(file, new File(imagePath));
        } catch (IOException e) {
            return status(500);
        }
        
        if(thumbnail == true) {
        	StringBuffer sb = new StringBuffer(fileName);
        	sb.insert(fileName.indexOf("."),"_thumb");
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
        	sb.insert(fileName.indexOf("."),"_minithumb" );
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
            	sb1.insert(fileName.indexOf("."),"_thumb_m" );
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
            	sb1.insert(fileName.indexOf("."),"_minithumb_m" );
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
        map.put("URL", Application.APPLICATION_BASE_URL + imageUrl);
		return ok(Json.toJson(map));
	}
    
    @Transactional
    public static Result uploadImage() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
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
            logger.underlyingLogger().error(loggedInUser+" failed to upload photo", e);
            return status(500);
        }

        String imageUrl = imageUploadUtil.getImageUrl(now, fileName);
        logger.underlyingLogger().info("uploadImage. imageUrl=" + imageUrl);

        Map<String, String> map = new HashMap<>();
        map.put("URL", imageUrl);
        logger.underlyingLogger().info(loggedInUser+" uploaded photo - "+imageUrl);
        return ok(Json.toJson(map));
    }

    @Transactional
    public static Result getImage(Long year, Long month, Long date, String name) {
    	response().setHeader("Cache-Control", "max-age=604800");
        String path = imageUploadUtil.getImagePath(year, month, date, name);
        return ok(new File(path));
    }
}