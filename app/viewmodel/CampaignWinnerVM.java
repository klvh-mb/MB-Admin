package viewmodel;

import models.CampaignWinner;
import models.GameAccount;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class CampaignWinnerVM {
	@JsonProperty("id") public long id;
	@JsonProperty("name") public String name;
	@JsonProperty("winnerState") public String winnerState;
	@JsonProperty("userId") public long userId;
	@JsonProperty("campaignId") public long campaignId;
	@JsonProperty("note") public String note;
	@JsonProperty("email") public String email;
	
	public CampaignWinnerVM(CampaignWinner winner) {
	    this.id = winner.id;
	    this.name = User.findById(winner.userId).name;
	    this.userId = winner.userId;
	    this.campaignId = winner.campaignId;
	    this.winnerState = winner.winnerState.name();
	    this.note = winner.note;
	    this.email = GameAccount.findByUserId(winner.userId).email;
	}
	
	public CampaignWinnerVM(Long userId, Long campaignId) {
        this.id = -1L;
        this.name = User.findById(userId).name;
        this.userId = userId;
        this.campaignId = campaignId;
        this.winnerState = "";
        this.note = "";
        this.email = GameAccount.findByUserId(userId).email;
    }
}
