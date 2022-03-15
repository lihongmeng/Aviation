package com.hzlz.aviation.feature.watchtv.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.watchtv.api.WatchTvAPI;
import com.hzlz.aviation.feature.watchtv.entity.ColumnDetail;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取栏目详情信息
 */
public class GetColumnDetailRequest extends BaseGVideoRequest<ColumnDetail> {

    private String columnId;

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return WatchTvAPI.Instance.get().getColumnDetail(columnId);
    }
}
