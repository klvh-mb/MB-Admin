package viewmodel;

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
	
	@JsonProperty("uc") public Long joinedUsersCount;
    
	public CampaignVM(Campaign campaign) {
	    this.id = campaign.id;
	    this.name = campaign.name;
	    this.image = campaign.image;
	    this.description = campaign.description;
		this.campaignType = campaign.campaignType.name();
		this.campaignState = campaign.campaignState.name();
		this.startDate = DateTimeUtil.toString(campaign.startDate);
		this.endDate = DateTimeUtil.toString(campaign.endDate);
		
		this.joinedUsersCount = CampaignController.getJoinedUsersCount(campaign.id);
	}
}
