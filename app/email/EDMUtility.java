package email;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.exception.ExceptionUtils;

import models.Subscription;
import models.User;
import play.Configuration;
import play.Logger;
import play.Play;
import akka.actor.Cancellable;

import com.feth.play.module.mail.Mailer;
import com.feth.play.module.mail.Mailer.Mail;
import com.feth.play.module.mail.Mailer.Mail.Body;

import controllers.Application;

public class EDMUtility {
    private static play.api.Logger logger = play.api.Logger.apply(EDMUtility.class);
    
    public static final String EDM_TEST_RECIPIENT = 
            Play.application().configuration().getString("edm.test.recipient", "minibean.dev@gmail.com");
    
    protected static final String EDM_UNSUBSCRIBE_URL_PREFIX = 
            Application.APPLICATION_BASE_URL+'/'+"unsubscribe"+"/";
    
	protected static final String PROVIDER_KEY = "password";

	protected static final String SETTING_KEY_MAIL = "mail";
	
	protected static Configuration config = 
            Play.application().configuration().getConfig("mail");
	
	protected static Mailer mailer = Mailer.getCustomMailer(config);
	
	public void sendMailToUser(final User user,Subscription subscription,String bodyContent) {

		//final boolean isSecure = getConfiguration().getBoolean(SETTING_KEY_VERIFICATION_LINK_SECURE);
	    //final String url = routes.Signup.verify(token).absoluteURL(ctx.request(), isSecure);
        
		String url = EDM_UNSUBSCRIBE_URL_PREFIX;
		try {
			url = url.concat(EDMHelper.doEncryption("userid:"+user.id+",subid:"+subscription.id));
			logger.underlyingLogger().debug(String.format("Unsubscribe url generated [u=%d|url=%s]", user.id, url));
		} catch (Exception e) {
		    logger.underlyingLogger().error(String.format("Failed to generate unsubscribe url [u=%d|sub=%d]", user.id, subscription.id));
		    logger.underlyingLogger().error(ExceptionUtils.getFullStackTrace(e));
		}
		
		final String text = getEmailTemplate(
				subscription.HTMLtemplate,
				user.name, user.email,url,bodyContent);
		
		final String html = getEmailTemplate(
				subscription.TXTtemplate,
				user.name, user.email,url,bodyContent);

		Body body =  new Body(html, text);
		sendMail(subscription.name, body, user.email);
		logger.underlyingLogger().debug(String.format("sendMailToUser [u=%d|%s|%s]", user.id, user.name, user.email));
	}

	protected Cancellable sendMail(String subject, Body body, String recipient) {
	    if ("dev".equals(Application.APPLICATION_ENV)) {
	        recipient = EDM_TEST_RECIPIENT;
        }
		return sendMail(new Mail(subject, body, new String[] { recipient }));
	}
	
    protected Cancellable sendMail(final Mail mail) {
        if(mailer == null) {
            logger.underlyingLogger().error("EDM mailer is null. Check mail config in smtp.conf");
            return null;
        }
        return mailer.sendMail(mail);
    }
	   
	private String getEmailName(final User user) {
		return getEmailName(user.email, user.name);
	}
	
	protected String getEmailName(final String email, final String name) {
		return Mailer.getEmailName(email, name);
	}
	
	/*protected Configuration getConfiguration() {
		return getConfiguration1().getConfig(getKey());
	}*/
	
	protected String getEmailTemplate(final String template,
			final String name, final String email,final String url,final String bodyContent) {
		Class<?> cls = null;
		String ret = null;
		try {
			cls = Class.forName(template);
		} catch (ClassNotFoundException e) {
			Logger.warn("Template: '"
					+ template
					+ "' was not found! Trying to use English fallback template instead.");
		}
		if (cls == null) {
			try {
				cls = Class.forName(template);
			} catch (ClassNotFoundException e) {
				Logger.error("Fallback template: '" + template 
						+ "' was not found either!");
			}
		}
		if (cls != null) {
			Method htmlRender = null;
			try {
				htmlRender = cls.getMethod("render", String.class, String.class,String.class,String.class);
				ret = htmlRender.invoke(null, name, email, url, bodyContent)
						.toString();

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}
