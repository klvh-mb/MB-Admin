package models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.Creatable;
import domain.SocialObjectType;
import domain.Updatable;
import play.libs.Json;

@Entity
public class Notification extends domain.Entity implements Serializable, Creatable, Updatable  {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	/*To whom this notification is intended for*/
	@Required
	public Long recipient;
	 
	@Required
	public String message;
	
	public String URLs;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType targetType;
	
	public Long target;
	
	public long socialActionID;
	
	public String usersName;
	
	public Long count = 0L;

    // 0=unread, 1=read, 2=ignored, 3=accepted
	public int status = 0;
	
	@Enumerated(EnumType.STRING)
	public NotificationType notificationType;
	
	public static enum NotificationType {
        FRD_REQUEST,
        FRD_ACCEPTED,
		NEW_MESSAGE,
        COMM_JOIN_REQUEST,
        COMM_JOIN_APPROVED,
        COMM_INVITE_REQUEST,
		COMMENT,
		POSTED,
		LIKED,
		ANSWERED,
        QUESTIONED,
        WANTED_ANS,
        CAMPAIGN
	}


	public void addToList(User addUser) {
        String addUserName = addUser.displayName;
        if (addUserName == null) {
            addUserName = "User";
        }

		if(this.usersName == null) {
            this.usersName = addUserName;
            this.count++;
		}
        else {
			if(this.usersName.toLowerCase().contains(addUserName.toLowerCase())){
				return;
			}

            this.count++;

            int lastDelimIdx = this.usersName != null ? this.usersName.lastIndexOf(",") : -1;
			if(count >= 3 && lastDelimIdx != -1){
                long othersCount = count - 2;
				this.usersName = addUserName+", "+this.usersName.substring(0,lastDelimIdx)+" 與另外 "+othersCount+"人都";
			} else {
				this.usersName = addUserName+", "+this.usersName;
            }
		}
	}

	public void changeStatus(int status) {
		this.status = status;
	    save();
	}

	public String getUsersName() {
		return usersName;
	}

	public void setUsersName(String usersName) {
		this.usersName = usersName;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getRecipient() {
		return recipient;
	}

	public void setRecipient(Long recipient) {
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}
	
    public static Notification findById(Long id) {
        String sql = "SELECT n FROM Notification n WHERE id=?1";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, id);
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }

    /**
     * Create a 1-to-1 notification for campaign winning.
     * @param recipientUserIds
     * @param campaign
     * @return
     */
    public static void createCampaignNotification(List<Long> recipientUserIds, Campaign campaign) {
        final String FRONTPAGE_PREFIX = "/frontpage#";
        final String landingUrl = FRONTPAGE_PREFIX + "/campaign/"+campaign.getId();
        final String msg = "恭喜你成為「"+campaign.name+"」的得獎者! 請到獎賞頁了解領獎詳情。";

        for (Long recipientUserId : recipientUserIds) {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("actor", recipientUserId);
            jsonMap.put("target", campaign.getId());

            Notification notification = new Notification();
            notification.notificationType = NotificationType.CAMPAIGN;
            notification.target = campaign.getId();
            notification.targetType = SocialObjectType.CAMPAIGN;
            notification.recipient = recipientUserId;
            notification.socialActionID = campaign.getId();
            jsonMap.put("photo", campaign.image);
            jsonMap.put("onClick", landingUrl);
            notification.URLs = Json.stringify(Json.toJson(jsonMap));
            notification.status = 0;
            notification.message = msg;
            notification.setUpdatedDate(new Date());
            notification.save();
        }
    }
}
