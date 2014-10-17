package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class GameRedemption  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameRedemption.class);
    
    public static enum Redemption_state{
    	InProgress,
    	Delivered
    }
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long user_id;
	
	public Long redemption_points;
	
	public Date date;
	
	public Redemption_state redemption_state; 
	
	public GameRedemption() {}
	
	public static List<GameRedemption> getAllGameRedemption() {
		Query q = JPA.em().createQuery("Select a from GameRedemption a ");
		return (List<GameRedemption>)q.getResultList();
	}

	public static void confirmRedemptionRequest(String id) {
		GameRedemption gameRedemption = GameRedemption.findById(Long.parseLong(id));
		System.out.println(id+" :: game redemption :: " +gameRedemption.id);
		gameRedemption.redemption_state = Redemption_state.Delivered;
		gameRedemption.merge();
		
	}

	private static GameRedemption findById(Long id) {
		try { 
            Query q = JPA.em().createQuery("SELECT u FROM GameRedemption u where u.id = ?1");
            q.setParameter(1,id);
            return (GameRedemption) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in findById", e);
            return null;
        }
		
	}

	 
}
