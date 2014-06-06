package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class Icon {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public String name;
    
    @Enumerated(EnumType.STRING)
    public IconType iconType;
    
    public String url;
    
    public static enum IconType {
        COMMUNITY_ZODIAC,
        ARTICLE_CATEGORY,
    }
    
    public Icon(){}
    
    public Icon(String name, IconType iconType, String url) {
        this.name = name;
        this.iconType = iconType;
        this.url = url;
    }
    
    public static List<Icon> getAllIcons() {
    	Query q = JPA.em().createQuery("Select i from Icon i");
    	return (List<Icon>)q.getResultList();
    }
    
    public String getName() {
    	return name;
    }
    
    public IconType getIconType() {
        return iconType;
    }
    
    public String getUrl() {
    	return url;
    }
}
