package models;

import javax.persistence.Entity;
import javax.persistence.Lob;


/**
 * Represent a folder contains a set of Resources
 * 
 */
@Entity

public class Folder extends SocialObject{
    private static play.api.Logger logger = play.api.Logger.apply(Folder.class);


	public Folder() {}
	
	public Folder(String name) {
		this.name = name;
	}

	@Lob
	public String description;

	

}
