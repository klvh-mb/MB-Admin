package viewmodel;

import models.ArticleCategory;

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
	@JsonProperty("td") public String targetDistrict;
	
	public ArticleVM(ArticleCategory articleCategory,String name,long id,int targetAgeMinMonth,int targetAgeMaxMonth,int targetGender,int targetParentGender,String targetDistrict) {
		this.category = articleCategory;
		this.name = name;
		this.id = id;
		this.targetAgeMinMonth = targetAgeMinMonth;
		this.targetAgeMaxMonth = targetAgeMaxMonth;
		this.targetGender = targetGender;
		this.targetParentGender = targetParentGender;
		this.targetDistrict = targetDistrict; 
	}

}
