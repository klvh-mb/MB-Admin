package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import models.GameAccountTransaction.TransactionType;

import play.db.jpa.JPA;

@Entity
public class GameAccount extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccount.class);

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public Long user_id;

	private Long game_points = 0L;      // total capped points redeemable
    private Long activity_points = 0L;  // total points from activity

    // Points multiplier
    private Double firstPersonMultiplier = 1d;

    // Redemption
	public Long redeemed_points;
	public Date last_redemption_time;

    // Contact information
    public String realName;
	public String phone;
    public String email;
    public String address_1;
	public String address_2;
	public String city;

	public Boolean has_upload_profile_pic = false;

    public String promoCode;
	public Long number_of_referral_signups = 0L;


    /**
     * Ctor
     */
	public GameAccount() {}
	
	public static GameAccount findByUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where user_id = ?1");
	        q.setParameter(1, id);
	        return (GameAccount) q.getSingleResult();
	    } catch (NoResultException e) {
	    	GameAccount account = new GameAccount();
	    	account.user_id = id;
	    	account.save();
	        return account;
	    } 
	}

	public static List<GameAccount> getAllGameAccounts() {
		Query q = JPA.em().createQuery("Select a from GameAccount a ");
		return (List<GameAccount>)q.getResultList();		
	}

	public void addBonusByAdmin(String bonus, String detail) {
		this.auditFields.setUpdatedDate(new Date());
        Long bonusL = Long.parseLong(bonus);
        addPointsAcross(bonusL);
		this.merge();

        GameAccountTransaction.recordPoints(user_id, bonusL, TransactionType.Bonus, "小豆豆獎賞 -"+detail, getGame_points());
	}
	
	public void addPenaltyByAdmin(String penalty, String detail) {
		this.auditFields.setUpdatedDate(new Date());
        Long penaltyL = Long.parseLong(penalty);
        if (penaltyL > 0) {
            penaltyL *= -1;
        }
        addPointsAcross(penaltyL);
		this.merge();

        GameAccountTransaction.recordPoints(user_id, penaltyL, TransactionType.Penalty, detail, getGame_points());
	}

    public void addPointsAcross(long newPoints) {
        game_points += newPoints;
        activity_points += newPoints;
    }

    public void subtractPointsAcross(long points) {
        game_points -= points;
        activity_points -= points;
    }

    public static void deletePostByAdmin(Long id) {
		// TODO
	}

	public static void deleteCommentByAdmin(Long id) {
		// TODO
	}

    public Long getGame_points() {
        return game_points;
    }
}
