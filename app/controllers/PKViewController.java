package controllers;

import java.util.*;

import common.utils.ImageUploadUtil;
import common.utils.StringUtil;
import domain.PostType;
import domain.SocialObjectType;
import models.Community;
import models.Post;
import models.PKViewMeta;

import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Controller for PKView
 */
public class PKViewController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(PKViewController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("pkview");

    /**
     * For Admin purpose.
     * @return
     */
    @Transactional
    public static Result postPKOnCommunity() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }

        DynamicForm form = DynamicForm.form().bindFromRequest();
        Long communityId = Long.parseLong(form.get("community_id"));
        String pkTitle = form.get("pkTitle");
        String pkText = form.get("pkText");
        String pkImage = form.get("pkImage");
        int shortBodyCount = StringUtil.computePostShortBodyCount(pkText);

        String pkYesText = form.get("pkYesText");
        String pkNoText = form.get("pkNoText");
        String pkYesImage = form.get("pkYesImage");
        String pkNoImage = form.get("pkNoImage");

        Community community = Community.findById(communityId);
        if (community == null) {
            logger.underlyingLogger().error("Invalid communityId: "+communityId);
            return status(501);
        }

        // create Post
        Post post = new Post(loggedInUser, pkTitle, pkText, community);
        post.objectType = SocialObjectType.PK_VIEW;
        post.postType = PostType.PK_VIEW;
        post.shortBodyCount = shortBodyCount;
        post.setUpdatedDate(new Date());
        post.save();
        // create PKViewMeta
        PKViewMeta pkViewMeta = new PKViewMeta(post.id, pkYesText, pkImage, pkNoText, pkYesImage, pkNoImage);
        pkViewMeta.save();

        logger.underlyingLogger().info("[c="+communityId+"] postPKOnCommunity");

        Map<String,String> map = new HashMap<>();
        map.put("id", post.id.toString());

        if (post.shortBodyCount > 0) {
            map.put("text", post.body.substring(0, post.shortBodyCount));
            map.put("showM", "true");
        } else{
            map.put("text", post.body);
            map.put("showM", "false");
        }
        return ok(Json.toJson(map));
    }
}