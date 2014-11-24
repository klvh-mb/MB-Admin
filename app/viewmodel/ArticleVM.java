package viewmodel;

import java.util.Date;

import models.ArticleCategory;
import models.Location;

import org.codehaus.jackson.annotate.JsonProperty;

public class ArticleVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("ds") public String description;
	@JsonProperty("ct") public ArticleCategory category;
	@JsonProperty("tam") public int targetAgeMinMonth;
	@JsonProperty("tamx") public int targetAgeMaxMonth;
	@JsonProperty("tg") public int targetGender;
	@JsonProperty("tpg") public int targetParentGender;
	@JsonProperty("tl") public Location targetLocation;
	@JsonProperty("pd") public Date publishedDate;
    @JsonProperty("cb") public String createdBy;
    
	public ArticleVM(ArticleCategory articleCategory,String name,long id,
	        Date publishedDate, String createdBy, 
	        int targetAgeMinMonth,int targetAgeMaxMonth,int targetGender,
	        int targetParentGender,Location targetLocation) {
		this.category = articleCategory;
		this.name = name;
		this.id = id;
		this.targetAgeMinMonth = targetAgeMinMonth;
		this.targetAgeMaxMonth = targetAgeMaxMonth;
		this.targetGender = targetGender;
		this.targetParentGender = targetParentGender;
		this.targetLocation = targetLocation;
		this.publishedDate = publishedDate;
        this.createdBy = createdBy;
	}
}
