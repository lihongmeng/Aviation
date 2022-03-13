package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jxntv.base.plugin.H5EntryPlugin;
import com.jxntv.ioc.PluginManager;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.MessageInfo;
import com.tencent.qcloud.tuikit.tuichat.component.face.FaceManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MessageTextHolder extends MessageContentHolder {

    private TextView msgBodyText;

    public MessageTextHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_text;
    }

    @Override
    public void initVariableViews() {
        msgBodyText = rootView.findViewById(R.id.msg_body_tv);
    }

    @Override
    public void layoutVariableViews(MessageInfo msg, int position) {
        msgBodyText.setVisibility(View.VISIBLE);

        if (properties.getChatContextFontSize() != 0) {
            msgBodyText.setTextSize(properties.getChatContextFontSize());
        }
        if (msg.isSelf()) {
            if (properties.getRightChatContentFontColor() != 0) {
                msgBodyText.setTextColor(properties.getRightChatContentFontColor());
            }
        } else {
            if (properties.getLeftChatContentFontColor() != 0) {
                msgBodyText.setTextColor(properties.getLeftChatContentFontColor());
            }
        }

        if (msg.getExtra() != null) {
            String content = msg.getExtra().toString();
            if (content.startsWith("["+ PluginManager.get(H5EntryPlugin.class).getScheme()+"://")){
                int i = content.indexOf("]") + 1 ;
                String schemeStr = content.substring(0,i).trim();
                content = content.replace(schemeStr,"");
                schemeStr = schemeStr.replace("[","").replace("]","");
                msgBodyText.setTextColor(ContextCompat.getColor(msgBodyText.getContext(), R.color.c_4189FF));
                msgBodyText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                msgBodyText.getPaint().setAntiAlias(true);
                String finalSchemeStr = schemeStr;
                if (msgContentLinear!=null){
                    msgContentLinear.setOnClickListener(view -> click(finalSchemeStr));
                }
                msgBodyText.setOnClickListener(view -> click(finalSchemeStr));
            }

            FaceManager.handlerEmojiText(msgBodyText, content, false);
        }
    }

    private void click(String finalSchemeStr){
        Intent intent = new Intent();
        intent.setData(Uri.parse(finalSchemeStr));
        PluginManager.get(H5EntryPlugin.class).dispatch(msgBodyText.getContext(),intent);
    }

}
