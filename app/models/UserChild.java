package models;

import java.util.List;

import javax.persistence.*;

import play.db.jpa.JPA;

@Entity
public class UserChild {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

    public String birthYear;
    
    public String birthMonth;
    
    public String birthDay;
    
    public String gender;
    
    @ManyToOne(cascade = CascadeType.REMOVE)
  	public User user;
    
    public void merge(UserInfo userInfo) {
        // TODO - keith
    }
    
    public static UserChild findById(Long id) {
    	Query query = JPA.em().createQuery("Select u from UserChild u where u.id = ?1");
		query.setParameter(1, id);
    	return (UserChild) query.getSingleResult();
    }
    
    public static List<UserChild> findListById(Long id) {
    	Query query = JPA.em().createQuery("Select u from UserChild u where u.user.id = ?1");
		query.setParameter(1, id);
    	return (List<UserChild>) query.getResultList();
    }
    
    public void save() {
		JPA.em().persist(this);
		JPA.em().flush();
	}
}
