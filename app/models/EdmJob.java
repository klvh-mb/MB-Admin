package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class EdmJob extends domain.Entity {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Lob
 	public String json;
    
    public Date startTime;
    
    public Date endTime;
    
    public Long successCount;
    
    public Long failureCount;
    
    public EdmJob(){}
    
    public static EdmJob findById(Long id) {
    	Query query = JPA.em().createQuery("Select u from EdmJob u where u.id = ?1");
		query.setParameter(1, id);
    	return (EdmJob) query.getSingleResult();
    }
	    
}
