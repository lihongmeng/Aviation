package com.hzlz.aviation.kernel.base.model.circle;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

public class FindCircleContent implements Parcelable {

    /**
     * 作者名称
     */
    public String authorName;

    /**
     * 媒体id
     */
    public String mediaId;

    /**
     * 资源大类
     * {@link com.hzlz.aviation.kernel.base.model.anotation.MediaType}
     */
    public int mediaType;

    /**
     * 显示标题
     */
    public String title;

    /**
     * 话题对象
     */
    public TopicDetail topic;

    /**
     * {@link Constant.FindCircleContentType}
     */
    public int type;

    /**
     * 问答id ,
     */
    public int answerSquareId;
    /**
     * 问答类型  1 提问  2 回答
     */
    public int answerSquareType;

    public FindCircleContent() {

    }


    protected FindCircleContent(Parcel in) {
        authorName = in.readString();
        mediaId = in.readString();
        mediaType = in.readInt();
        title = in.readString();
        topic = in.readParcelable(TopicDetail.class.getClassLoader());
        type = in.readInt();
        answerSquareId = in.readInt();
        answerSquareType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authorName);
        dest.writeString(mediaId);
        dest.writeInt(mediaType);
        dest.writeString(title);
        dest.writeParcelable(topic, flags);
        dest.writeInt(type);
        dest.writeInt(answerSquareId);
        dest.writeInt(answerSquareType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FindCircleContent> CREATOR = new Creator<FindCircleContent>() {
        @Override
        public FindCircleContent createFromParcel(Parcel in) {
            return new FindCircleContent(in);
        }

        @Override
        public FindCircleContent[] newArray(int size) {
            return new FindCircleContent[size];
        }
    };

    public static SpannableString getLiveContent(@NonNull FindCircleContent findCircleContent) {
        SpannableString result = new SpannableString(findCircleContent.authorName + " 正在直播");
        result.setSpan(
                new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_333333)),
                findCircleContent.authorName.length(),
                result.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        );
        return result;
    }

    public static void getLiveMomentContent(
            @NonNull final FindCircleContent findCircleContent,
            GVideoTextView gVideoTextView
    ) {
        if (findCircleContent.type == Constant.FindCircleContentType.LIVE) {
            SpannableString result = new SpannableString(findCircleContent.authorName + " 正在直播");
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_333333)),
                    findCircleContent.authorName.length(),
                    result.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
            gVideoTextView.setText(result);
            return;
        }

        String header;
        SpannableString result;

        if (findCircleContent.type == Constant.FindCircleContentType.TOPIC) {
            if (TextUtils.isEmpty(findCircleContent.topic.authName)
                    && TextUtils.isEmpty(findCircleContent.topic.content)) {
                gVideoTextView.setText("");
                return;
            }

            if (TextUtils.isEmpty(findCircleContent.topic.authName)) {
                result = new SpannableString("# " + findCircleContent.topic.content + " #");
                result.setSpan(
                        new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_006fbb)),
                        0,
                        result.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                );
                gVideoTextView.setText(result);
                return;
            } else {
                // String authName = AuthUgcReply.getName(findCircleContent.topic.authName, 4);
                String authName = findCircleContent.topic.authName;

                header = authName + " 参与讨论 ";
                result = new SpannableString(header + "# " + findCircleContent.topic.content + " #");
                result.setSpan(
                        new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_191919)),
                        0,
                        authName.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                );
                result.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        0,
                        authName.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                );
                result.setSpan(
                        new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_006fbb)),
                        header.length(),
                        result.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                );
            }
            gVideoTextView.setText(result);
            return;
        }

        // 内容
        if (TextUtils.isEmpty(findCircleContent.authorName)
                && TextUtils.isEmpty(findCircleContent.title)) {
            gVideoTextView.setText("");
            return;
        }

        if (TextUtils.isEmpty(findCircleContent.authorName)) {
            result = new SpannableString(findCircleContent.title);
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_333333)),
                    0,
                    result.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        } else {
            // String authName = AuthUgcReply.getName(findCircleContent.authorName, 4);
            String authName = findCircleContent.authorName;
            header = authName + "：";
            result = new SpannableString(header + findCircleContent.title);
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_191919)),
                    0,
                    authName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
            result.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0,
                    authName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_333333)),
                    header.length(),
                    result.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
        gVideoTextView.setText(result);
    }

    public SpannableString getLiveSpannableString() {
        if (TextUtils.isEmpty(authorName)) {
            authorName = "";
        }
        SpannableString result = new SpannableString(authorName + " 正在直播");
        result.setSpan(
                new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_333333)),
                authorName.length(),
                result.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        );
        return result;
    }

    public SpannableString getTopicSpannableString() {
        String header;
        SpannableString result;

        if (TextUtils.isEmpty(topic.authName)
                && TextUtils.isEmpty(topic.content)) {
            result = new SpannableString("");
            return result;
        }

        if (TextUtils.isEmpty(topic.authName)) {
            result = new SpannableString("# " + topic.content + " #");
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_006fbb)),
                    0,
                    result.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
            return result;
        } else {
            String authName = topic.authName;

            header = authName + " 参与讨论 ";
            result = new SpannableString(header + "# " + topic.content + " #");
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_191919)),
                    0,
                    authName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
            result.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0,
                    authName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_006fbb)),
                    header.length(),
                    result.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
        return result;
    }

    public SpannableString getMomentSpannableString() {
        String header;
        SpannableString result;

        if (TextUtils.isEmpty(authorName)
                && TextUtils.isEmpty(title)) {
            result = new SpannableString("");
            return result;
        }

        if (TextUtils.isEmpty(authorName)) {
            result = new SpannableString(title);
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_333333)),
                    0,
                    result.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        } else {
            String authName = authorName;
            header = authName + "：";
            result = new SpannableString(header + title);
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_191919)),
                    0,
                    authName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
            result.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0,
                    authName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
            result.setSpan(
                    new ForegroundColorSpan(ResourcesUtils.getColor(R.color.color_333333)),
                    header.length(),
                    result.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
        return result;
    }

}
