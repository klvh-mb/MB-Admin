package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class FrontPageTopic extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(FrontPageTopic.class);

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public String name;
    
    @Lob
    public String description;
    
    public String image;
    
    public String url;
    
    public int seq;
    
    public int noClicks = 0;
    
    public Date publishedDate;

    public Boolean active = false;
    
    public Boolean deleted = false; 
    
    @Enumerated(EnumType.STRING)
    public TopicType topicType;
    
    public static enum TopicType {
        SLIDER,
        PROMO,
        FEATURED,
        GAME
    }
    
    @Enumerated(EnumType.STRING)
    public TopicSubType topicSubType;
    
    public static enum TopicSubType {
        NONE,
        FLASH,
        IMAGE
    }
    
    public FrontPageTopic() {}
    
    public static FrontPageTopic findById(Long id) {
        Query q = JPA.em().createQuery("SELECT f FROM FrontPageTopic f where id = ?1 and deleted = false");
        q.setParameter(1, id);
        return (FrontPageTopic) q.getSingleResult();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof FrontPageTopic) {
            final FrontPageTopic other = (FrontPageTopic) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
    
    @Override
    public String toString() {
        return "FrontPageTopic{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "clicks='" + noClicks + '\'' +
                "published='" + publishedDate + '\'' +
                '}';
    }
    
    @Transactional
    public static long getAllFrontPageTopicsTotal(String name, int rowsPerPage) {
        long totalPages = 0, size;
        if(name.trim().equals("")) {
            size = (long) JPA.em().createQuery("Select count(*) from FrontPageTopic a where deleted = false").getSingleResult();
        } else {
            Query query = JPA.em().createQuery("Select count(*) from FrontPageTopic a where deleted = false and a.name LIKE ?2");
            query.setParameter(2, "%"+name+"%");
            size= (long) query.getSingleResult();
        }
        
        totalPages = size/rowsPerPage;
        
        if(size % rowsPerPage > 0) {
            totalPages++;
        }
        return totalPages;
    }
    
    @Transactional
    public static List<FrontPageTopic> getAllFrontPageTopics(String name, int currentPage, int rowsPerPage, long totalPages) {
        int start=0;
        
        String sql="";
        if(name.trim().equals("")) {
            sql = "Select f from FrontPageTopic f where deleted = false order by topicType desc, seq";
        } else {
            sql = "Select f from FrontPageTopic f where deleted = false and f.name LIKE ?1 order by publishedDate desc";
        }
        
        if(currentPage >= 1 && currentPage <= totalPages) {
            start = (currentPage*rowsPerPage)-rowsPerPage;
        }
        if(currentPage>totalPages && totalPages!=0) {
            currentPage--;
            start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
        }
        Query q = JPA.em().createQuery(sql).setFirstResult(start).setMaxResults(rowsPerPage);
        
        if(!name.trim().equals("")) {
            q.setParameter(1, "%"+name+"%");
        }
    
        return (List<FrontPageTopic>)q.getResultList();
    }
}
