package viewmodel;

import models.ArticleCategory;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.mail.search.StringTerm;

public class ArticleVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("ds") public String description;
	@JsonProperty("ct") public ArticleCategory category;
	@JsonProperty("tamin") public int targetAgeMinMonth;
    @JsonProperty("tamax") public int targetAgeMaxMonth;
    @JsonProperty("tg") public String targetGenderStr;
    @JsonProperty("tpg") public String targetParentGenderStr;
    @JsonProperty("td") public String targetDistrict;
	public ArticleVM(ArticleCategory articleCategory,
                     String name,
                     Long id,
                     int targetAgeMinMonth,
                     int targetAgeMaxMonth,
                     int targetGender,
                     int targetParentGender,
                     String targetDistrict) {
		this.category = articleCategory;
		this.name = name;
		this.id = id;
        this.targetAgeMinMonth = targetAgeMinMonth;
        this.targetAgeMaxMonth = targetAgeMaxMonth;
        switch(targetGender) {
            case 1: targetGenderStr = "Boy"; break;
            case 2: targetGenderStr = "Girl"; break;
            default: targetGenderStr = "Both"; break;
        }
        switch(targetParentGender) {
            case 1: targetParentGenderStr = "Father"; break;
            case 2: targetParentGenderStr = "Mother"; break;
            default: targetParentGenderStr = "Both"; break;
        }
        this.targetDistrict = targetDistrict;
	}

}
