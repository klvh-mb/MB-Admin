package viewmodel;

import java.util.Date;

import models.Campaign;

import org.codehaus.jackson.annotate.JsonProperty;

import common.utils.DateTimeUtil;
import controllers.CampaignController;

public class CampaignVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("im") public String image;
	@JsonProperty("ds") public String description;
	@JsonProperty("ct") public String campaignType;
	@JsonProperty("cs") public String campaignState;
	@JsonProperty("sd") public String startDate;
	@JsonProperty("ed") public String endDate;
	@JsonProperty("at") public String announcementType;
    @JsonProperty("an") public String announcement;
	@JsonProperty("uc") public Long joinedUsersCount;
	@JsonProperty("cd") public Date createdDate;
    @JsonProperty("cb") public String createdBy;
        
	public CampaignVM(Campaign campaign) {
	    this.id = campaign.id;
	    this.name = campaign.name;
	    this.image = campaign.image;
	    this.description = campaign.description;
		this.campaignType = campaign.campaignType.name();
		this.campaignState = campaign.campaignState.name();
		this.startDate = DateTimeUtil.toString(campaign.startDate);
		this.endDate = DateTimeUtil.toString(campaign.endDate);
		this.announcementType = campaign.announcementType.name();
        this.announcement = campaign.announcement;
		this.joinedUsersCount = CampaignController.getJoinedUsersCount(campaign.id);
		this.createdDate = campaign.getCreatedDate();
		this.createdBy = campaign.getCreatedBy();
	}
}
