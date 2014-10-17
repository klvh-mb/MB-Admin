package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import models.GameAccountTransaction.Transaction_type;

import play.db.jpa.JPA;

@Entity
public class GameAccount  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccount.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long User_id;
	
	public Long total_points = 0L;
	
	public Long redeemed_points;	
	 
	public Date last_redemption_time;
	
	public Date previous_day_accumulated_points;
	
	public String address_1;
	
	public String address_2;
	
	public String city;
	
	public Long phone;
	
	public Boolean has_upload_profile_pic = true;
	
	public Long number_of_referral_signups;
	
	public GameAccount() {}
	
	public static GameAccount findByUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where user_id = ?1");
	        q.setParameter(1, id);
	        return (GameAccount) q.getSingleResult();
	    } catch (NoResultException e) {
	    	GameAccount account = new GameAccount();
	    	account.User_id = id;
	    	account.save();
	        return account;
	    } 
	}
	
	public static void deletePostByAdmin(Long id) {
		GameAccount account = GameAccount.findByUserId(id);
		account.auditFields.setUpdatedDate(new Date());
		account.total_points = account.total_points - 5L;
		account.merge();
	}

	public static void deleteCommentByAdmin(Long id) {
		GameAccount account = GameAccount.findByUserId(id);
		account.auditFields.setUpdatedDate(new Date());
		account.total_points = account.total_points - 5L;
		account.merge();
		
	}

	public static List<GameAccount> getAllGameAccounts() {
		Query q = JPA.em().createQuery("Select a from GameAccount a ");
		return (List<GameAccount>)q.getResultList();		
	}

	public void addBonusbyAdmin(String bonus, String detail) {
		this.auditFields.setUpdatedDate(new Date());
		this.total_points = this.total_points + Long.parseLong(bonus);
		this.merge();
		GameAccountTransaction.recordPoints(this.User_id, this, Long.parseLong(bonus), Transaction_type.Bonus, detail);
	}
	
	public void addPenaltybyAdmin(String penalty, String detail) {
		this.auditFields.setUpdatedDate(new Date());
		this.total_points = this.total_points + Long.parseLong(penalty);
		this.merge();
		GameAccountTransaction.recordPoints(this.User_id, this, Long.parseLong(penalty), Transaction_type.Bonus, detail);
	}
}
