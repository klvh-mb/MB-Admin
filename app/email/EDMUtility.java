package email;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import models.Subscription;
import models.User;
import play.Configuration;
import play.Logger;
import play.Play;
import akka.actor.Cancellable;

import com.feth.play.module.mail.Mailer;
import com.feth.play.module.mail.Mailer.Mail;
import com.feth.play.module.mail.Mailer.Mail.Body;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public class EDMUtility {
	
	protected static final String PROVIDER_KEY = "password";

	protected static final String SETTING_KEY_MAIL = "mail";
	
	public void sendMailToUser(final User user,Subscription subscription) {

		//final boolean isSecure = getConfiguration().getBoolean(SETTING_KEY_VERIFICATION_LINK_SECURE);
	    //final String url = routes.Signup.verify(token).absoluteURL(ctx.request(), isSecure);
        
		System.out.println("User ::: "+user.name+" ::: "+user.email);
		
		final String text = getEmailTemplate(
				subscription.HTMLtemplate,
				user.name, user.email);
		
		final String html = getEmailTemplate(
				subscription.TXTtemplate,
				user.name, user.email);

		Body body =  new Body(text, html);
		
		sendMail(subscription.name, body, user.email);
	}

	protected Cancellable sendMail(final String subject, final Body body,
			final String recipient) {
		return sendMail(new Mail(subject, body, new String[] { recipient }));
	}
	
	private String getEmailName(final User user) {
		return getEmailName(user.email, user.name);
	}
	
	protected String getEmailName(final String email, final String name) {
		return Mailer.getEmailName(email, name);
	}
	
	protected Cancellable sendMail(final Mail mail) {
		//Mailer mailer = Mailer.getDefaultMailer();
		Configuration config = Play.application().configuration()
				.getConfig("mail");
		Mailer mailer = Mailer.getCustomMailer(config);
		
		
		if(mailer == null) return null;
		return mailer.sendMail(mail);
	}
	
	/*protected Configuration getConfiguration() {
		return getConfiguration1().getConfig(getKey());
	}*/
	
	protected String getEmailTemplate(final String template,
			final String name, final String email) {
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
				htmlRender = cls.getMethod("render", String.class, String.class);
				ret = htmlRender.invoke(null, name, email)
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
