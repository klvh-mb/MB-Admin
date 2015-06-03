package viewmodel;

import java.util.Date;

import models.GameGift;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import common.utils.DateTimeUtil;
import controllers.GameController;

public class GameGiftVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("im") public String image;
	@JsonProperty("imt") public String imageThumb;
	@JsonProperty("ds") public String description;
	@JsonProperty("ri") public String redeemInfo;
	@JsonProperty("ei") public String expirationInfo;
	@JsonProperty("si") public String shippingInfo;
	@JsonProperty("ci") public String customerCareInfo;
	@JsonProperty("mi") public String moreInfo;
	@JsonProperty("ft") public String featureType;
	@JsonProperty("rt") public String redeemType;
	@JsonProperty("gt") public String giftType;
	@JsonProperty("gs") public String giftState;
	@JsonProperty("sd") public String startDate;
	@JsonProperty("ed") public String endDate;
	@JsonProperty("rp") public Long requiredPoints;
	@JsonProperty("rl") public Long requiredLevel;
	@JsonProperty("qt") public Long quantityTotal;
	@JsonProperty("qa") public Long quantityAvailable;
	@JsonProperty("rc") public Long redeemedUsersCount;
	@JsonProperty("cd") public Date createdDate;
    @JsonProperty("cb") public String createdBy;
        
	public GameGiftVM(GameGift gameGift) {
	    this.id = gameGift.id;
	    this.name = gameGift.name;
	    this.image = gameGift.image;
	    this.imageThumb = StringUtils.isEmpty(gameGift.imageThumb)? this.image : this.imageThumb;
	    this.description = gameGift.description;
	    this.redeemInfo = gameGift.redeemInfo;
	    this.expirationInfo = gameGift.expirationInfo;
	    this.shippingInfo = gameGift.shippingInfo;
	    this.customerCareInfo = gameGift.customerCareInfo;
	    this.moreInfo = gameGift.moreInfo;
	    this.featureType = gameGift.featureType.name();
	    this.redeemType = gameGift.redeemType.name();
		this.giftType = gameGift.giftType.name();
		this.giftState = gameGift.giftState.name();
		this.startDate = DateTimeUtil.toString(gameGift.startDate);
		this.endDate = DateTimeUtil.toString(gameGift.endDate);
		this.requiredPoints = gameGift.requiredPoints;
		this.requiredLevel = gameGift.requiredLevel;
		this.quantityTotal = gameGift.quantityTotal;
		this.quantityAvailable = gameGift.quantityAvailable;
		this.redeemedUsersCount = GameController.getRedeemedUsersCount(gameGift.id);
		this.createdDate = gameGift.getCreatedDate();
		this.createdBy = gameGift.getCreatedBy();
	}
}
