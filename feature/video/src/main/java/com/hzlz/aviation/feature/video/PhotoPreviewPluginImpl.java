package com.hzlz.aviation.feature.video;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.hzlz.aviation.feature.video.ui.PhotoPreviewActivity;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.PhotoPreviewPlugin;

import java.util.ArrayList;


public class PhotoPreviewPluginImpl implements PhotoPreviewPlugin {

    @Override
    public void startPhotoViewActivity(@NonNull Context context,@NonNull View view, String imageUrl) {
        ArrayList<String> stringList = new ArrayList<>();
        stringList.add(imageUrl);
        startPhotoViewActivity(context,view,stringList,0);
    }


    @Override
    public void startPhotoViewActivity(@NonNull Context context, @NonNull View view, ArrayList<String> imageUrls,int selectPosition) {

        if (imageUrls!=null && imageUrls.size()>0 && imageUrls.get(0).startsWith("http")){
            //使用网络图片不需要权限
            startPreview(context,view,imageUrls,selectPosition);
        }else {
            PermissionManager
                    .requestPermissions(context, new PermissionCallback() {
                        @Override
                        public void onPermissionGranted(@NonNull Context context) {
                            startPreview(context,view,imageUrls,selectPosition);

                        }

                        @Override
                        public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                            Toast.makeText(context, R.string.refuse_permission_write_read, Toast.LENGTH_SHORT).show();
                        }
                    },Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

    }

    private void startPreview(@NonNull Context context, @NonNull View view, ArrayList<String> imageUrls,int selectPosition){
        Intent intent = new Intent(context, PhotoPreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_IMG_URL, imageUrls);
        intent.putExtra(Constants.EXTRA_IMG_SELECT_POSITION, selectPosition);
        intent.putExtras(bundle);
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, view, context.getString(R.string.transition_image));
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, rect.left, rect.top, view.getWidth(), view.getHeight());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(context, intent, options.toBundle());
    }
}
