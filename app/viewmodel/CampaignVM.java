package viewmodel;

import java.util.Date;

import models.Campaign.CampaignType;

import org.codehaus.jackson.annotate.JsonProperty;

public class CampaignVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("ds") public String description;
	@JsonProperty("ct") public String campaignType;
	@JsonProperty("sd") public Date startDate;
	@JsonProperty("ed") public Date endDate;
	
	public CampaignVM(CampaignType campaignType,String name,long id,Date startDate,Date endDate) {
		this.campaignType = campaignType.name();
		this.name = name;
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
