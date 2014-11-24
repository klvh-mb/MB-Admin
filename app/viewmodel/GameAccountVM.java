package viewmodel;

import models.GameAccount;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class GameAccountVM {
	@JsonProperty("id") public long id;
	@JsonProperty("uId") public long userId;
	@JsonProperty("nm") public String name;
	@JsonProperty("tp") public long totalPoints;
	
	public GameAccountVM() {
	}	
	
	public GameAccountVM(GameAccount account) {
		this.id = account.id;
		this.userId = account.User_id;
		this.name = User.findById(account.User_id).name;
		this.totalPoints = account.total_points;
	}
}
