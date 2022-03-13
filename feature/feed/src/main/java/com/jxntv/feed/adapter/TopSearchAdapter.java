package com.jxntv.feed.adapter;

import static com.jxntv.base.Constant.THEME_COLOR_SWITCH_TOP.MEET;
import static com.jxntv.base.Constant.THEME_COLOR_SWITCH_TOP.NORMAL;
import static com.jxntv.base.Constant.THEME_COLOR_SWITCH_TOP.SPRING_FESTIVAL;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jxntv.base.datamanager.ThemeDataManager;
import com.jxntv.feed.R;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * <p>文件描述：</p>
 * <p>作者：hanxw</p>
 * <p>创建时间：2022/2/17</p>
 * <p>更改时间：2022/2/17</p>
 * <p>版本号：1</p>
 */
public class TopSearchAdapter extends BannerAdapter<String, RecyclerView.ViewHolder> {
    public TopSearchAdapter(List<String> datas) {
        super(datas);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
//        updateTextTheme(textView);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, parent.getResources().getDimensionPixelSize(R.dimen.sp_13));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        return new RecyclerView.ViewHolder(textView) {
        };
    }

    private void updateTextTheme(TextView textView) {
        int themeColorSwitchInteger = ThemeDataManager.getInstance().getThemeColorSwitchInteger();
        switch (themeColorSwitchInteger) {
            case MEET:
            case SPRING_FESTIVAL:
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_ffffff_70));
                break;
            case NORMAL:
            default:
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_999999));
                break;
        }
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, String data, int position, int size) {
        ((TextView) holder.itemView).setText(data);
        updateTextTheme((TextView) holder.itemView);
    }
}
