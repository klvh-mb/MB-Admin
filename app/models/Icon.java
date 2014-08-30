package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Icon {

	 @Id @GeneratedValue(strategy = GenerationType.AUTO)
	    public Long id;
	 	
	 	public String iconType;
	    
	    public String name;
	    
	    public String url;
	    
	    @Transactional
	    public static List<Icon> getAllIcons() {
	    	Query q = JPA.em().createQuery("select i from Icon i where i.iconType = 'ANNOUNCEMENT'");
	    	return (List<Icon>)q.getResultList();
	    }
	    
	    @Transactional
	    public static Icon findById(Long id) {
	    	Query q = JPA.em().createQuery("select i from Icon i where i.id = ?1");
	    	q.setParameter(1, id);
	    	return (Icon)q.getSingleResult();
	    }
}
