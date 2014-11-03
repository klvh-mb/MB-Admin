package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class AdminUser extends domain.Entity {
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String userName;
	public String passWord;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	public static AdminUser doLogin(String name,String pass) {
		Query q = JPA.em().createQuery("SELECT a FROM AdminUser a where a.userName = ?1 AND a.passWord = ?2");
		q.setParameter(1, name);
		q.setParameter(2, pass);
		
		return (AdminUser) q.getSingleResult();
	}
}
