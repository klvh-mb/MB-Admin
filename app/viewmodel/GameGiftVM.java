package viewmodel;

import java.util.Date;

import models.GameGift;

import org.codehaus.jackson.annotate.JsonProperty;

import common.utils.DateTimeUtil;
import controllers.GameController;

public class GameGiftVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("im") public String image;
	@JsonProperty("ds") public String description;
	@JsonProperty("ri") public String redeemInfo;
	@JsonProperty("furl") public String formUrl;
	@JsonProperty("ft") public String featureType;
	@JsonProperty("rt") public String redeemType;
	@JsonProperty("gt") public String giftType;
	@JsonProperty("gs") public String giftState;
	@JsonProperty("sd") public String startDate;
	@JsonProperty("ed") public String endDate;
	@JsonProperty("qt") public Long quantity;
	@JsonProperty("rc") public Long redeemedUsersCount;
	@JsonProperty("cd") public Date createdDate;
    @JsonProperty("cb") public String createdBy;
        
	public GameGiftVM(GameGift gameGift) {
	    this.id = gameGift.id;
	    this.name = gameGift.name;
	    this.image = gameGift.image;
	    this.description = gameGift.description;
	    this.redeemInfo = gameGift.redeemInfo;
	    this.formUrl = gameGift.formUrl;
	    this.featureType = gameGift.featureType.name();
	    this.redeemType = gameGift.redeemType.name();
		this.giftType = gameGift.giftType.name();
		this.giftState = gameGift.giftState.name();
		this.startDate = DateTimeUtil.toString(gameGift.startDate);
		this.endDate = DateTimeUtil.toString(gameGift.endDate);
		this.quantity = gameGift.quantity;
		this.redeemedUsersCount = GameController.getRedeemedUsersCount(gameGift.id);
		this.createdDate = gameGift.getCreatedDate();
		this.createdBy = gameGift.getCreatedBy();
	}
}
