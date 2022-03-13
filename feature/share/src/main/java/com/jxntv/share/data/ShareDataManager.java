package com.jxntv.share.data;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.sensordata.utils.InteractType;
import com.jxntv.share.R;
import com.jxntv.share.ShareRuntime;
import com.jxntv.share.adapter.ShareSlideAdapter;
import com.jxntv.share.dialog.CreateBillDialog;
import com.jxntv.share.model.ShareItemModel;
import com.jxntv.share.strategy.CopyShareStrategy;
import com.jxntv.share.strategy.DeleteShareStrategy;
import com.jxntv.share.strategy.FavoriteShareStrategy;
import com.jxntv.share.strategy.FollowShareStrategy;
import com.jxntv.share.strategy.FontSettingStrategy;
import com.jxntv.share.strategy.QQShareStrategy;
import com.jxntv.share.strategy.ReportShareStrategy;
import com.jxntv.share.strategy.WeChatShareStrategy;
import com.jxntv.share.strategy.WeiBoShareStrategy;
import com.jxntv.stat.GVideoStatManager;
import com.jxntv.stat.StatConstants;
import com.jxntv.stat.StatPid;
import com.jxntv.stat.db.entity.StatEntity;
import com.jxntv.utils.ResourcesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分享数据Manager
 */
public class ShareDataManager {

    private static boolean isCanShareQQ, isCanShareWeiXin, isCanShareWeibo;

    public static void setCanShareConfig(
            boolean shareWeiXin,
            boolean shareQQ,
            boolean shareWeibo
    ) {
        isCanShareWeiXin = shareWeiXin;
        isCanShareQQ = shareQQ;
        isCanShareWeibo = shareWeibo;
    }

    public static boolean isCanShare() {
        return isCanShareQQ || isCanShareWeiXin || isCanShareWeibo;
    }

    /**
     * 获取默认分享数据（分享模块内部功能）
     *
     * @return 分享数据
     */
    @NonNull
    public static List<ShareItemModel> getDefaultShareModel(
            Context context,
            ShareDataModel model,
            StatFromModel stat
    ) {
        List<ShareItemModel> list = new ArrayList<>();
        if (isCanShareWeiXin) {
            list.add(makeWeixinFriendItem(context, model, stat));
            list.add(makeWeixinFriendCircleItem(context, model, stat));
        }
        if (isCanShareQQ) {
            list.add(makeQQFriendCircleItem(context, model, stat));
            list.add(makeQQSpaceCircleItem(context, model, stat));
        }
        if (isCanShareWeibo) {
            list.add(makeWeiBoItem(context, model, stat));
        }
        if (isCanShareQQ || isCanShareWeiXin || isCanShareWeibo) {
            list.add(makeCopyLinkItem(context, model, stat));
        }
        return list;
    }

    /**
     * 生成默认其他数据（需转接其他模块功能）
     *
     * @return 其他数据列表
     */
    @NonNull
    public static List<ShareItemModel> getDefaultOtherModel(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        List<ShareItemModel> list = new ArrayList<>();
        if (dataModel.isShowFavorite()) {
            list.add(makeShareOtherFavoriteItem(context, dataModel, stat));
        }
        if (dataModel.isShowFollow()) {
            list.add(makeShareOtherFollowItem(context, dataModel, stat));
        }
        if (dataModel.isShowReport()) {
            list.add(makeShareOtherReportItem(context, dataModel, stat));
        }

        if (dataModel.isShowFontSetting()) {
            list.add(makeShareOtherFontSettingItem(context, dataModel, stat));
        }
        if (dataModel.isShowDelete()) {
            list.add(makeShareOtherDeleteItem(context, dataModel, stat));
        }
        // if (dataModel.isShowCreateBill()) {
        //     list.add(makeShareOtherCreateBillItem(context, dataModel, stat));
        // }
        return list;
    }

    /**
     * 生成微信朋友圈数据
     *
     * @return 微信朋友圈数据
     */
    private static ShareItemModel makeWeixinFriendCircleItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_weixin_friends_circle);
        model.drawableRes = R.drawable.share_item_weixin_friends_circle;
        model.shareStrategy = new WeChatShareStrategy(context, WXSceneTimeline);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxFriend(dataModel, stat, model.title);
            }
        };
        return model;
    }

    /**
     * 生成微信朋友数据
     *
     * @return 微信朋友数据
     */
    private static ShareItemModel makeWeixinFriendItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_weixin_friend);
        model.drawableRes = R.drawable.share_item_weixin_friends;
        model.shareStrategy = new WeChatShareStrategy(context, WXSceneSession);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxMoment(dataModel, stat, model.title);
            }
        };
        return model;
    }

    public static void startWXShare(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        new WeChatShareStrategy(context, WXSceneSession).share(dataModel);
        statWxMoment(
                dataModel,
                stat,
                ResourcesUtils.getString(R.string.share_weixin_friend)
        );
    }

    public static void startWXCircleShare(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        new WeChatShareStrategy(context, WXSceneTimeline).share(dataModel);
        statWxFriend(
                dataModel,
                stat,
                ResourcesUtils.getString(R.string.share_weixin_friends_circle)
        );
    }


    /**
     * 生成微博数据
     *
     * @return 微博数据
     */
    private static ShareItemModel makeWeiBoItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share_weibo);
        model.drawableRes = R.drawable.share_item_weibo;
        model.shareStrategy = new WeiBoShareStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxMoment(dataModel, stat, model.title);
            }
        };
        return model;
    }

    /**
     * 生成QQ好友数据
     *
     * @return QQ好友数据
     */
    private static ShareItemModel makeQQFriendCircleItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_qq_friend);
        model.drawableRes = R.drawable.share_item_qq_friend;
        model.shareStrategy = new QQShareStrategy(context, 0);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxMoment(dataModel, stat, model.title);
            }
        };
        return model;
    }

    /**
     * 生成QQ空间数据
     *
     * @return QQ空间数据
     */
    private static ShareItemModel makeQQSpaceCircleItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_qq_space);
        model.drawableRes = R.drawable.share_item_qq_space;
        model.shareStrategy = new QQShareStrategy(context, 1);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxMoment(dataModel, stat, model.title);
            }
        };
        return model;
    }

    /**
     * 生成复制粘贴数据
     *
     * @return 复制粘贴数据
     */
    private static ShareItemModel makeCopyLinkItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_copy_link);
        model.drawableRes = R.drawable.share_item_copy_link;
        model.shareStrategy = new CopyShareStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statCopy(dataModel, stat);
            }
        };
        return model;
    }

    /**
     * 生成收藏item数据
     *
     * @return 收藏item数据
     */
    private static ShareItemModel makeShareOtherFavoriteItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = dataModel.isFavorite() ?
                ShareRuntime.getAppContext().getResources().getString(R.string.share_other_favorited) :
                ShareRuntime.getAppContext().getResources().getString(R.string.share_other_collect);
        model.drawableRes = dataModel.isFavorite() ?
                R.drawable.share_other_favorited :
                R.drawable.share_other_favorite;
        model.shareStrategy = new FavoriteShareStrategy(context);
        model.clickListener = view -> {
            Context viewContext = view.getContext();
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (TextUtils.isEmpty(accountPlugin.getToken())) {
                accountPlugin.startLoginActivity(viewContext);
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(dataModel.getPid()),
                        ResourcesUtils.getString(R.string.like)
                );
                return;
            }
            if (!NetworkTipUtils.checkNetworkOrTip(viewContext)) {
                return;
            }
            if (stat != null) {
                ((FavoriteShareStrategy) model.shareStrategy).share(dataModel, stat.pid);
            } else {
                model.shareStrategy.share(dataModel);
            }
            statFavorite(dataModel, stat);
        };
        return model;
    }

    /**
     * 生成关注数据
     *
     * @return 生成关注item数据
     */
    private static ShareItemModel makeShareOtherFollowItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = dataModel.isFollow() ?
                ShareRuntime.getAppContext().getResources().getString(R.string.share_other_followed) :
                ShareRuntime.getAppContext().getResources().getString(R.string.share_other_attention);
        model.drawableRes = dataModel.isFollow() ?
                R.drawable.share_other_followed :
                R.drawable.share_other_follow;
        model.shareStrategy = new FollowShareStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context viewContext = view.getContext();
                AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
                if (TextUtils.isEmpty(accountPlugin.getToken())) {
                    accountPlugin.startLoginActivity(viewContext);
                    GVideoSensorDataManager.getInstance().enterRegister(
                            StatPid.getPageName(dataModel.getPid()),
                            ResourcesUtils.getString(R.string.follow)
                    );
                    return;
                }
                if (!NetworkTipUtils.checkNetworkOrTip(viewContext)) {
                    return;
                }

                model.shareStrategy.share(dataModel);
            }
        };
        return model;
    }

    /**
     * 生成删除数据
     *
     * @return 生成删除item数据
     */
    private static ShareItemModel makeShareOtherDeleteItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share_other_delete);
        model.drawableRes = R.drawable.share_other_delete;
        model.shareStrategy = new DeleteShareStrategy(context);
        model.clickListener = view -> {
            Context viewContext = view.getContext();
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (TextUtils.isEmpty(accountPlugin.getToken())) {
                accountPlugin.startLoginActivity(viewContext);
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(dataModel.getPid()),
                        ResourcesUtils.getString(R.string.delete)
                );
                return;
            }
            if (!NetworkTipUtils.checkNetworkOrTip(viewContext)) {
                return;
            }
            model.shareStrategy.share(dataModel);
        };
        return model;
    }

    /**
     * 生成保存海报
     *
     * @return 生成保存海报
     */
    private static ShareItemModel makeShareOtherCreateBillItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share);
        model.drawableRes = R.drawable.share_other_save_pic;
        model.clickListener = v -> {
            new CreateBillDialog(context).show();
        };
        return model;
    }

    /**
     * 生成举报数据
     *
     * @return 生成举报item数据
     */
    private static ShareItemModel makeShareOtherReportItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share_other_report);
        model.drawableRes = R.drawable.share_other_report;
        model.shareStrategy = new ReportShareStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkTipUtils.checkNetworkOrTip(v.getContext())) {
                    return;
                }
                model.shareStrategy.share(dataModel);
            }
        };
        return model;
    }

    /**
     * 生成字体调整数据
     *
     * @return 生成生成字体调整item数据
     */
    private static ShareItemModel makeShareOtherFontSettingItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share_other_font_setting);
        model.drawableRes = R.drawable.share_other_font;
        model.shareStrategy = new FontSettingStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
            }
        };
        return model;
    }

    private static void statWxFriend(
            ShareDataModel shareDataModel,
            StatFromModel stat,
            String title
    ) {
        if (stat == null) return;
        String method = "1";
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withDs(createShareDs(shareDataModel, stat, method).toString())
                .withEv(StatConstants.EV_SHARE)
                .withPid(stat.pid)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
        if (shareDataModel.getVideoModel() != null) {
            GVideoSensorDataManager.getInstance().clickContent(shareDataModel.getVideoModel(), stat.pid,
                    InteractType.SHARE, title, null);
        }
    }

    private static void statWxMoment(
            ShareDataModel shareDataModel,
            StatFromModel stat,
            String title
    ) {
        if (stat == null) return;
        String method = "2";
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withDs(createShareDs(shareDataModel, stat, method).toString())
                .withEv(StatConstants.EV_SHARE)
                .withPid(stat.pid)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
        VideoModel videoModel;
        if (shareDataModel.getVideoModel() == null) {
            videoModel = new VideoModel();
            videoModel.setId(shareDataModel.getMediaId());
            videoModel.setTitle(shareDataModel.getTitle());
        } else {
            videoModel = shareDataModel.getVideoModel();
        }
        GVideoSensorDataManager.getInstance().clickContent(videoModel, stat.pid,
                InteractType.SHARE, title, null);
    }

    private static void statCopy(ShareDataModel shareDataModel, StatFromModel stat) {
        if (stat == null) return;
        String method = "100";
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withDs(createShareDs(shareDataModel, stat, method).toString())
                .withEv(StatConstants.EV_SHARE)
                .withPid(stat.pid)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

    private static void statFavorite(ShareDataModel shareDataModel, StatFromModel stat) {
        if (stat == null) return;
        String fav = shareDataModel.isFavorite() ? "0" : "1";
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(stat);
        ds.addProperty(StatConstants.DS_KEY_FAVORITE, fav);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withDs(ds.toString())
                .withEv(StatConstants.EV_FAVORITE)
                .withPid(stat.pid)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

    private static JsonObject createShareDs(
            ShareDataModel shareDataModel,
            StatFromModel stat,
            String method
    ) {
        String module = "";
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(stat);
        ds.addProperty(StatConstants.DS_KEY_METHOD, method);
        ds.addProperty(StatConstants.DS_KEY_MODULE, module);
        return ds;
    }

    public static View getShareView(
            Context context,
            ShareDataModel model,
            StatFromModel stat
    ) {
        if (!isCanShare()) {
            return null;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.layout_share_recyclerview, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.share_recycler);
        recyclerView.setFocusable(false);
        recyclerView.setFocusableInTouchMode(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        ShareSlideAdapter adapter = new ShareSlideAdapter(context, false, false, null);
        recyclerView.setAdapter(adapter);

        List<ShareItemModel> list = ShareDataManager.getDefaultShareModel(context, model, stat);
        if (list != null && list.size() > 0) {
            list.remove(list.size() - 1);
            adapter.refreshData(list);
        }
        return view;
    }

}
