package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Query;
import javax.persistence.NoResultException;

import domain.DefaultValues;
import domain.SocialObjectType;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Campaign extends SocialObject {

	public String image;
	
	@Lob
	public String description;
	
	public int noOfLikes = 0;
	
	public int noOfViews = 0;
    
	public Date startDate;
	
    public Date endDate;
    
	@Enumerated(EnumType.STRING)
	public SocialObjectType objectType = SocialObjectType.CAMPAIGN;
	
	@Enumerated(EnumType.STRING)
    public CampaignState campaignState = CampaignState.NEW;
    
    public static enum CampaignState {
        NEW,
        PUBLISHED,
        STARTED,
        ENDED,
        ANNOUNCED,
        CLOSED
    }
    
    @Enumerated(EnumType.STRING)
    public CampaignType campaignType;
    
    public static enum CampaignType {
    	INFO,
        ACTIONS,
        QUESTIONS,
        VOTING
    }
	
    @Lob
    public String announcement;
    
    @Enumerated(EnumType.STRING)
    public AnnouncementType announcementType;
    
    public static enum AnnouncementType {
        WINNERS,
        CUSTOM
    }
    
    public Campaign() {}
    
    public static List<Campaign> getLatestCampaigns() {
        Query q = JPA.em().createQuery("Select c from Campaign c where c.deleted = false order by startDate desc");
        q.setMaxResults(DefaultValues.MAX_CAMPAIGN_COUNT);
        return (List<Campaign>)q.getResultList();
    }
    
	@Transactional
	public static List<Campaign> getCampaigns(String id,String name) {
		String sql= "";
		if(id.trim().equals("") && name.trim().equals("")) {
			return getLatestCampaigns();
		}
		
		if(id.trim().equals("") && !name.trim().equals("")) {
			sql="Select c from Campaign c where c.deleted = false and c.name LIKE ?1 order by startDate desc";
		}
		
		if(!id.trim().equals("") && name.trim().equals("")) {
			sql="Select c from Campaign c where c.deleted = false and c.id = ?2 order by startDate desc";
		}
		
		if(!id.trim().equals("") && !name.trim().equals("")) {
			sql="Select c from Campaign c where c.deleted = false and c.name LIKE ?1 and c.id = ?2 order by startDate desc";
		}
		
		Query q = JPA.em().createQuery(sql);
		if(!name.trim().equals("")) {
			q.setParameter(1, "%"+name+"%");
		}
		if(!id.trim().equals("")) {
			q.setParameter(2, Long.parseLong(id));
		}
		return (List<Campaign>)q.getResultList();
	}
	
	public static Campaign findById(Long id) {
		Query q = JPA.em().createQuery("SELECT c FROM Campaign c where c.id = ?1 and c.deleted = false");
		q.setParameter(1, id);
		try {
		    return (Campaign) q.getSingleResult();
		} catch(NoResultException e) {
            return null;
        }
	}
	
    public static int deleteById(Long id) {
        Query q = JPA.em().createQuery("DELETE FROM Campaign c where c.id = ?1");
        q.setParameter(1, id);
        return q.executeUpdate();
    }
	   
	public static boolean checkTitleExists(String title) {
		Query q = JPA.em().createQuery("Select c from Campaign c where c.name = ?1 and c.deleted = false");
		q.setParameter(1, title);
		Campaign campaign = null;
		try {
		    campaign = (Campaign) q.getSingleResult();
		} catch(NoResultException nre) {
		}
		
		return (campaign == null);
	}
	
    public void delete() {
        this.deleted = true;
        save();
    }
    
	public void update() {
		this.merge();
	}
	
	public void save() {
		super.save();
	}
}
