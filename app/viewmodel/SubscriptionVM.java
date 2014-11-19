package viewmodel;

import models.Subscription;

import org.codehaus.jackson.annotate.JsonProperty;

public class SubscriptionVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("isSub") public boolean isSubscription = false;
	
	public SubscriptionVM() {}	
	
	public SubscriptionVM(Subscription s) {
		this.id = s.id;
		this.name = s.name;
	}
}
