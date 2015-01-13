package viewmodel;

import models.PKViewMeta;
import models.Post;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * VM Class for PKView
 */
public class PKViewVM {
	
    @JsonProperty("id") public Long id;
    @JsonProperty("pid") public Long postId;
    @JsonProperty("img") public String postImage;

    @JsonProperty("oid") public Long ownerId;
    @JsonProperty("p") public String postedBy;
    @JsonProperty("t") public long postedOn;
    @JsonProperty("ut") public long updatedOn;
    @JsonProperty("ptl") public String postedTitle;
    @JsonProperty("pt") public String postedText;
    @JsonProperty("type") public String postType;
    @JsonProperty("ctyp") public String communityType;
    @JsonProperty("cn") public String communityName;
    @JsonProperty("ci") public String communityIcon;
    @JsonProperty("cid") public Long communityId;
    
	@JsonProperty("red_ds") public String redDescription;
    @JsonProperty("red_img") public String redImage;
	@JsonProperty("n_rv") public long noOfRedVotes;
	
	@JsonProperty("blue_ds") public String blueDescription;
    @JsonProperty("blue_img") public String blueImage;
	@JsonProperty("n_bv") public long noOfBlueVotes;
	
	public static final long MIN_BAR_WIDTH = 12;
	
	public PKViewVM(PKViewMeta pkViewMeta, Post post) {
        this.id = pkViewMeta.id;
        this.postId = post.id;
        this.postImage = pkViewMeta.getPostImage();
        
        this.postId = post.id;
        this.ownerId = post.owner.id;
        this.postedBy = post.owner.displayName;
        this.postedOn = post.getCreatedDate().getTime();
        this.updatedOn = post.getSocialUpdatedDate().getTime();
        this.postedTitle = post.title;
        this.postedText = post.body;
        this.postType = post.postType.name();
        this.communityType = post.community.communityType.name();
        this.communityName = post.community.name;
        this.communityIcon = post.community.icon;
        this.communityId = post.community.id;
        
		this.redImage = pkViewMeta.getYesImage();
        this.blueImage = pkViewMeta.getNoImage();
        this.redDescription = pkViewMeta.getYesText();
        this.noOfRedVotes = pkViewMeta.getYesVoteCount();
        this.blueDescription = pkViewMeta.getNoText();
        this.noOfBlueVotes = pkViewMeta.getNoVoteCount();
	}
}
