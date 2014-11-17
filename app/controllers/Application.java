package controllers;

import javax.persistence.NoResultException;

import models.AdminUser;
import play.Play;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(Application.class);
    
    public static final String APPLICATION_ENV = 
            Play.application().configuration().getString("application.env", "dev");
    
    public static final String APPLICATION_BASE_URL = 
            Play.application().configuration().getString("application.baseUrl");
    
    public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";

	public static Result getPost(int offset, int limit) {
        return ok();// TODO: null check need to be added
    }
    
	public static String getLoggedInUser() {
	    final String value = session().get("NAME");
        if (value != null) {
            return value;
        }
        return "";
	}
        
	@Transactional
	public static Result index() {
		final String value = session().get("NAME");
        if (value == null) {
        	return ok(views.html.login.render());
        }
		return ok(views.html.home.render());
	}

	@Transactional
	public static Result login() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String name = form.get("name");
	    String password = form.get("pass");
		try {
			AdminUser adminUser = AdminUser.doLogin(name,password);
			
			if(adminUser != null) {
				session().put("NAME", adminUser.getUserName());
				logger.underlyingLogger().info("Admin user logged in - "+adminUser.getUserName());
				return ok(views.html.home.render());
			} else {
			    logger.underlyingLogger().info("Admin user not exist in - "+name);
			}
		} catch(NoResultException e) { 
		    logger.underlyingLogger().error("Admin user failed to log in - "+name);
		}
		return ok(views.html.login.render());
	}
	
	@Transactional
	public static Result logout() {
	    logger.underlyingLogger().info("Admin user logged out - "+session().get("NAME"));
		session().clear();
		return ok(views.html.login.render());
	}	
}
