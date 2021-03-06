package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import domain.DefaultValues;
import domain.SocialObjectType;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class GameGift extends SocialObject {
    private static play.api.Logger logger = play.api.Logger.apply(GameGift.class);
    
    public String image;
    
    public String imageThumb;
    
    @Column(length = 1024)
	public String description;
	
    @Column(length = 1024)
    public String redeemInfo;
    
    @Column(length = 1024)
    public String expirationInfo;
    
    @Column(length = 1024)
    public String shippingInfo;
    
    @Column(length = 1024)
    public String customerCareInfo;
    
    @Column(length = 1024)
    public String moreInfo;
    
    public long requiredPoints = 0L;
    
    public long quantityTotal = 0L;
    
    public long quantityAvailable = 0L;
    
    public long limitPerUser = 0L;
    
	public Date startDate;

    public Date endDate;

    @Enumerated(EnumType.STRING)
	public SocialObjectType objectType = SocialObjectType.GAME_GIFT;
	
    @Enumerated(EnumType.STRING)
	public FeatureType featureType;
	
    @Enumerated(EnumType.STRING)
    public RedeemType redeemType;
	
    @Enumerated(EnumType.STRING)
	public GiftType giftType;
	
    @Enumerated(EnumType.STRING)
    public GiftState giftState = GiftState.NEW;
	
    public static enum FeatureType {
        NONE,
        RECOMMEND,
        SPONSORED
    }
    
	public static enum RedeemType {
        POINTS
    }
    
    public static enum GiftType {
    	EXPIRATION,
        QUANTITY
    }
    
	public static enum GiftState {
        NEW,
        PUBLISHED,
        STARTED,
        ENDED,
        CLOSED
    }
	
    public int noOfLikes = 0;
    
	public int noOfViews = 0;
	
	public int noOfRedeems = 0;
	
    public GameGift() {}
    
    public static List<GameGift> getLatestGameGifts() {
        Query q = JPA.em().createQuery("Select g from GameGift g where g.deleted = false order by startDate desc");
        q.setMaxResults(DefaultValues.MAX_GAME_GIFT_COUNT);
        return (List<GameGift>)q.getResultList();
    }
    
	@Transactional
	public static List<GameGift> getGameGifts(String id,String name) {
		String sql= "";
		if(id.trim().equals("") && name.trim().equals("")) {
			return getLatestGameGifts();
		}
		
		if(id.trim().equals("") && !name.trim().equals("")) {
			sql="Select g from GameGift g where g.deleted = false and g.name LIKE ?1 order by startDate desc";
		}
		
		if(!id.trim().equals("") && name.trim().equals("")) {
			sql="Select g from GameGift g where g.deleted = false and g.id = ?2 order by startDate desc";
		}
		
		if(!id.trim().equals("") && !name.trim().equals("")) {
			sql="Select g from GameGift g where g.deleted = false and g.name LIKE ?1 and g.id = ?2 order by startDate desc";
		}
		
		Query q = JPA.em().createQuery(sql);
		if(!name.trim().equals("")) {
			q.setParameter(1, "%"+name+"%");
		}
		if(!id.trim().equals("")) {
			q.setParameter(2, Long.parseLong(id));
		}
		return (List<GameGift>)q.getResultList();
	}
	
	public static GameGift findById(Long id) {
		Query q = JPA.em().createQuery("SELECT g FROM GameGift g where g.id = ?1 and g.deleted = false");
		q.setParameter(1, id);
		try {
		    return (GameGift) q.getSingleResult();
		} catch(NoResultException e) {
            return null;
        }
	}
	
    public static int deleteById(Long id) {
        Query q = JPA.em().createQuery("DELETE FROM GameGift g where g.id = ?1");
        q.setParameter(1, id);
        return q.executeUpdate();
    }

	public static boolean checkTitleExists(String title) {
		Query q = JPA.em().createQuery("Select g from GameGift g where g.name = ?1 and g.deleted = false");
		q.setParameter(1, title);
		GameGift gameGift = null;
		try {
		    gameGift = (GameGift) q.getSingleResult();
		} catch(NoResultException nre) {
		}
		
		return (gameGift == null);
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