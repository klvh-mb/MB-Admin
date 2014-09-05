package models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;
import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.i18n.Messages;

import domain.PostType;
import domain.SocialObjectType;

@Entity
public class Community extends TargetingSocialObject {
    private static play.api.Logger logger = play.api.Logger.apply(Community.class);
    
    private static final String STORAGE_COMMUNITY_COVER_THUMBNAIL_NOIMAGE = 
            Play.application().configuration().getString("storage.community.cover.thumbnail.noimage");

    private static final String STORAGE_COMMUNITY_COVER_MINI_NOIMAGE = 
            Play.application().configuration().getString("storage.community.cover.mini.noimage");

    private static final String STORAGE_COMMUNITY_COVER_NOIMAGE = 
            Play.application().configuration().getString("storage.community.cover.noimage");
    
    public static enum CommunityType {
		OPEN,
		CLOSE,
		BUSINESS
	}

	@OneToMany(cascade=CascadeType.REMOVE, fetch = FetchType.LAZY)
	public List<Post> posts = new ArrayList<Post>();
	
	@Enumerated(EnumType.ORDINAL)
	public CommunityType communityType;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	public Folder albumPhotoProfile;
	
	@Column(length=2000)
	public String description;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date createDate;

	public boolean adminPostOnly = false;
	
	public boolean excludeFromNewsfeed = false;

	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Folder> folders;
	
	public String icon;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType objectType;

	public Community() {
		this.objectType = SocialObjectType.COMMUNITY;
	}
	
	public Community(String name, String description, User owner, CommunityType type) {
		this();
		this.name = name;
		this.owner = owner;
		this.description = description;
		this.communityType =type;
	}
	
		public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public CommunityType getCommunityType() {
		return communityType;
	}

	public void setCommunityType(CommunityType communityType) {
		this.communityType = communityType;
	}

	public Folder getAlbumPhotoProfile() {
		return albumPhotoProfile;
	}

	public void setAlbumPhotoProfile(Folder albumPhotoProfile) {
		this.albumPhotoProfile = albumPhotoProfile;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

    public boolean isExcludeFromNewsfeed() {
        return excludeFromNewsfeed;
    }

    public void setExcludeFromNewsfeed(boolean excludeFromNewsfeed) {
        this.excludeFromNewsfeed = excludeFromNewsfeed;
    }
    
    @Transactional
    public static long getAllCommunitiesTotal(int rowsPerPage,String title) {
		long totalPages = 0, size;
		if(title.trim().equals("")) {
			size = (long) JPA.em().createQuery("Select count(*) from Community c where c.objectType = 'COMMUNITY'").getSingleResult();
		}
		else
		{
			Query q = JPA.em().createQuery("SELECT count(*) FROM Community c where c.id = ?1 AND c.objectType = 'COMMUNITY'");
			q.setParameter(1, Long.parseLong(title));
			size = q.getMaxResults();
			if(size == 0) {
				totalPages = 0;
			}
			else {
				totalPages = 1;
			}
			return totalPages;
		}
			
		totalPages = size/rowsPerPage;
		
    	if(size % rowsPerPage > 0) {
			totalPages++;
		}
    	System.out.println("total pages ::"+totalPages);
    	return totalPages;
	}
    
    public static List<Community> findAllCommunities(int currentPage, int rowsPerPage, long totalPages, String title) {
		int  start=0;
		String sql="";
    	if(title.trim().equals("")) {
    		sql = "SELECT c FROM Community c where c.objectType = 'COMMUNITY'";
    	} else {
    		sql = "SELECT c FROM Community c where c.id = ?1 AND c.objectType = 'COMMUNITY'";
    	}
		
		if(currentPage >= 1 && currentPage <= totalPages) {
			start = (currentPage*rowsPerPage)-rowsPerPage;
		}
		if(currentPage>totalPages && totalPages!=0) {
			currentPage--;
			start = (int) ((totalPages*rowsPerPage)-rowsPerPage); 
		}
        try {
            Query q = JPA.em().createQuery(sql).setFirstResult(start).setMaxResults(rowsPerPage);
            if(!title.trim().equals("")) {
    			q.setParameter(1, Long.parseLong(title));
    		}
            return (List<Community>) q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    
    public static Community findById(Long id) {
        try {
            Query q = JPA.em().createQuery("SELECT c FROM Community c where id = ?1");
            q.setParameter(1, id);
            return (Community) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    
}
