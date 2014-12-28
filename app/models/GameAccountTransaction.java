package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class GameAccountTransaction  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccountTransaction.class);

    public static enum TransactionType {
		SystemCredit,
		Redemption,
		Bonus,
		Penalty
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public Long userId;

	public Long transactedPoints;

	public Date transactedTime;

	public Long newTotalPoints;

	public TransactionType transactionType;

    public String transactionDescription;

    /**
     * Ctor
     */
	public GameAccountTransaction() {}
	
	public static GameAccountTransaction findByUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where u.userId = ?1");
	        q.setParameter(1, id);
	        return (GameAccountTransaction) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}
	
	public static GameAccountTransaction findById(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where u.id = ?1");
	        q.setParameter(1, id);
	        return (GameAccountTransaction) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}

    /**
     * @param userId
     * @param transactedPoints
     * @param type
     * @param desc
     * @param newTotalPoints
     */
    @Transactional
	public static void recordPoints(long userId, long transactedPoints, TransactionType type, String desc,
                                    long newTotalPoints) {
		GameAccountTransaction transaction = new GameAccountTransaction();
		transaction.userId = userId;
		transaction.transactedTime = new Date();
		transaction.transactedPoints = transactedPoints;
		transaction.transactionType = type;
        transaction.transactionDescription = desc;
        transaction.newTotalPoints = newTotalPoints;
		transaction.save();
	}
}
