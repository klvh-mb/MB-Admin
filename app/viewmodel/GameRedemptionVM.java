package viewmodel;

import models.GameAccount;
import models.GameRedemption;
import models.Subscription;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class GameRedemptionVM {
	@JsonProperty("id") public long id;
	@JsonProperty("uId") public long userId;
	@JsonProperty("nm") public String name;
	@JsonProperty("rs") public String redemptionState;
	@JsonProperty("rp") public long redemptionPoints;
	
	public GameRedemptionVM() {
	}	
	
	public GameRedemptionVM(GameRedemption redemption) {
		this.id = redemption.id;
		this.userId = redemption.user_id;
		this.name = User.findById(redemption.user_id).name;
		this.redemptionPoints = redemption.redemption_points;
		this.redemptionState = redemption.redemption_state.toString();
	}
}
