package models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import domain.SocialObjectType;

/**
 * A resource can be a file or an external url, is contained always in a Folder
 * 
 */

@Entity
public class Resource extends SocialObject {

    public static final String STORAGE_PATH = 
            Play.application().configuration().getString("storage.path");
    public static final int STORAGE_PARTITION_DIR_MAX = 
            Play.application().configuration().getInt("storage.partition.dir.max", 20000);
    
	public Resource() {
	}
	
	@JsonIgnore
	@Required
	@ManyToOne
	public Folder folder;

	@Required
	public String resourceName;

	@Lob
	public String description;

	@Required
	public Integer priority = 0;

	@OneToMany(cascade = CascadeType.REMOVE)
	public Set<Comment> comments;
	
	public SocialObjectType objectType;

	public Resource(SocialObjectType objectType) {
		this.objectType = objectType;
	}

	public Boolean isImage() {
		return com.mnt.utils.FileUtils.isImage(resourceName);
	}

	public Boolean isExternal() {
		return com.mnt.utils.FileUtils.isExternal(resourceName);
	}

	@Override
	public String toString() {
		return super.toString() + " " + resourceName;
	}

	public String getPath() {
		if (isExternal()) {
			return resourceName;
		} else {
			return STORAGE_PATH + getStoragePartition() + "/"
					+ owner.id + "/" + folder.id + "/" + id + "/" + resourceName;
		}
	}
	
	@Transactional
	public String getThumbnail() {
		if (isExternal()) {
			return resourceName;
		} else {
			return STORAGE_PATH + getStoragePartition() + "/"
					+ owner.id + "/" + folder.id + "/" + id + "/thumbnail." + resourceName;
		}
	}
	
	public java.io.File getThumbnailFile() {
		java.io.File f = new java.io.File(getThumbnail());
		if (f.exists()) {
			return f;
		}
		return null;
	}
	
	@Transactional
	public String getMini() {
		if (isExternal()) {
			return resourceName;
		} else {
			return STORAGE_PATH + getStoragePartition() + "/"
					+ owner.id + "/" + folder.id + "/" + id + "/mini." + resourceName;
		}
	}
	
	@Transactional
	public String getMiniComment() {
		if (isExternal()) {
			return resourceName;
		} else {
			return STORAGE_PATH + getStoragePartition() + "/"
					+ owner.id + "/" + folder.id + "/" + id + "/miniComment." + resourceName;
		}
	}

	public java.io.File getRealFile() {
		java.io.File f = new java.io.File(getPath());
		if (f.exists()) {
			return f;
		}
		return null;
	}

	public Long getSize() {
		if (isExternal()) {
			return null;
		} else {
			return FileUtils.sizeOf(getRealFile());
		}
	}

	/*
	@Override
	public SocialObject onComment(User user, String body, CommentType type)
			throws SocialObjectNotCommentableException {
		Comment comment = new Comment(this, user, body);

		if (type == CommentType.ANSWER) {
			comment.commentType = type;
		}

		if (type == CommentType.SIMPLE) {
			comment.commentType = type;
		}

		if (comments == null) {
			comments = new HashSet<Comment>();
		}
		comment.save();
		this.comments.add(comment);
		JPA.em().merge(this);
		recordCommentOnCommunityPost(user);
		return comment;
    }
	*/
	
    private String getStoragePartition() {
        return "part"+(owner.id / STORAGE_PARTITION_DIR_MAX);
    }
	
	public static Resource findById(Long id) {
		Query q = JPA.em().createQuery("SELECT r FROM Resource r where id = ?1");
		q.setParameter(1, id);
		return (Resource) q.getSingleResult();
	}
	
	public static Resource findAllResourceOfFolder(Long id) {
		Query q = JPA.em().createQuery("SELECT r FROM Resource r where folder.id = ?1");
		q.setParameter(1, id);
		return (Resource) q.getSingleResult();
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}
}
