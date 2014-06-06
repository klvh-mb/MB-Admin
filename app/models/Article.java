package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.NoResultException;

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Article extends domain.Entity {

	public Article() {}
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	
	@Lob
	public String description;
	
	public int noOfLikes=0;
    
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date publishedDate;
	
	public String objectType = "ARTICLE";
	
	@ManyToOne
	public ArticleCategory category;
	
	// Targeting attributes
	
    @Column(nullable=false)
    public boolean excludeFromTargeting = false;
    
    @Column(nullable=false)
    public String targetingType = "SCORE";
    
    @Column(nullable=true)
    public String targetingInfo;
    
    @Column(nullable=true)
    public int targetAgeMinMonth;
    
    @Column(nullable=true)
    public int targetAgeMaxMonth;
    
    @Column(nullable=true)
    public int targetGender;
    
    @Column(nullable=true)
    public int targetParentGender;
    
    @ManyToOne
    public Location targetLocation;
    
	@Transactional
	public static List<Article> getAllArticles() {
		Query q = JPA.em().createQuery("Select a from Article a order by publishedDate desc");
		return (List<Article>)q.getResultList();
	}
	
	public static Article findById(Long id) {
		Query q = JPA.em().createQuery("SELECT u FROM Article u where id = ?1");
		q.setParameter(1, id);
		return (Article) q.getSingleResult();
	}
	
	public static int deleteByID(Long id) {
		Query q = JPA.em().createQuery("DELETE FROM Article u where id = ?1");
		q.setParameter(1, id);
		return q.executeUpdate();
	}
	
	public static boolean checkTitleExists(String title) {
		Query q = JPA.em().createQuery("Select a from Article a where a.name = ?1");
		q.setParameter(1, title);
		Article article = null;
		try {
			article = (Article) q.getSingleResult();
		} catch(NoResultException nre) {
		}
		
		return (article == null);
	}
	
	public void updateById() {
		this.merge();
	}
	
	public void saveArticle() {
		this.save();
	}
	
}
