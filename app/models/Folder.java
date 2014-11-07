package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

/**
 * Represent a folder contains a set of Resources
 * 
 */
@Entity

public class Folder extends SocialObject{
    private static play.api.Logger logger = play.api.Logger.apply(Folder.class);

    @Lob
    public String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "folder")
    public List<Resource> resources = new ArrayList<Resource>();
    
	public Folder() {}
	
	public Folder(String name) {
		this.name = name;
	}

    public Resource getHighPriorityFile() {
        int max = Integer.MIN_VALUE;
        Resource highest = null;
        for (Resource file : resources) {
            if (file.priority > max) {
                highest = file;
                max = file.priority;
            }
        }
        return highest;
    }
	
}
