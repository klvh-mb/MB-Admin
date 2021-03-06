package controllers;

import java.util.*;

import common.collection.Pair;
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
import viewmodel.PKViewVM;

/**
 * Controller for PKView
 */
public class PKViewController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(PKViewController.class);

    @Transactional
    public static Result getPKViews() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }
        
        List<Pair<PKViewMeta, Post>> allPKViewMeta = PKViewMeta.getAllPKViewMeta();
        List<PKViewVM> vms = new ArrayList<>();
        for (Pair<PKViewMeta, Post> pkViewMeta : allPKViewMeta) {
            PKViewVM vm = new PKViewVM(pkViewMeta.first, pkViewMeta.second);
            vms.add(vm);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("results", vms);
        return ok(Json.toJson(map));
    }
    
    /**
     * For Admin purpose (Create)
     * @return
     */
    @Transactional
    public static Result savePKView() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }

        DynamicForm form = DynamicForm.form().bindFromRequest();
        Long communityId = Long.parseLong(form.get("community_id"));
        String pkTitle = form.get("title");
        String pkText = form.get("text");
        String pkImage = form.get("image");
        int shortBodyCount = StringUtil.computePostShortBodyCount(pkText);

        String pkYesText = form.get("yes_text");
        String pkYesImage = form.get("yes_image");
        String pkNoText = form.get("no_text");
        String pkNoImage = form.get("no_image");

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
        PKViewMeta pkViewMeta = new PKViewMeta(post.id, pkImage, pkYesText, pkNoText, pkYesImage, pkNoImage);
        pkViewMeta.save();

        logger.underlyingLogger().info("[c="+communityId+"] postPKOnCommunity");

        return ok();
    }

    /**
     * For Admin purpose (Update)
     * @return
     */
    @Transactional
    public static Result updatePKView() {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }

        DynamicForm form = DynamicForm.form().bindFromRequest();
        Long id = Long.parseLong(form.get("id"));

        Pair<PKViewMeta,Post> pkViewMeta = PKViewMeta.getPKViewById(id);
        if (pkViewMeta == null) {
            logger.underlyingLogger().error("Invalid pkViewMetaId: "+id);
            return status(501);
        }

        Long communityId = Long.parseLong(form.get("cid"));

        Community community = Community.findById(communityId);
        if (community == null) {
            logger.underlyingLogger().error("Invalid communityId: "+communityId);
            return status(502);
        }

        PKViewMeta meta = pkViewMeta.getFirst();
        Post post = pkViewMeta.getSecond();

        String pkTitle = form.get("ptl");
        String pkText = form.get("pt");
        String pkImage = form.get("img");

        String pkYesText = form.get("red_ds");
        String pkNoText = form.get("blue_ds");
        String pkYesImage = form.get("red_img");
        String pkNoImage = form.get("blue_img");

        post.title = pkTitle;
        post.body = pkText;
        meta.setPostImage(pkImage);
        meta.setYesText(pkYesText);
        meta.setNoText(pkNoText);
        meta.setYesImage(pkYesImage);
        meta.setNoImage(pkNoImage);

        post.merge();
        meta.merge();

        logger.underlyingLogger().info(loggedInUser+" updated PKView ["+id+"] Post ["+post.id+"]");
        return ok();
    }

    /**
     * For Admin purpose (Delete)
     * @return
     */
    @Transactional
    public static Result deletePKView(Long id) {
        final String loggedInUser = Application.getLoggedInUser();
        if (loggedInUser == null) {
            return ok(views.html.login.render());
        }

        Pair<PKViewMeta,Post> pkViewMeta = PKViewMeta.getPKViewById(id);
        if (pkViewMeta != null) {
            pkViewMeta.getFirst().deleted = true;
            pkViewMeta.getFirst().save();
            pkViewMeta.getSecond().deleted = true;
            pkViewMeta.getSecond().save();
            logger.underlyingLogger().info(loggedInUser+" deleted PKView ["+id+"]");
        }
        else {
            logger.underlyingLogger().info("Invalid PKView ["+id+"]");
        }

        return ok();
    }
}