package com.hzlz.aviation.feature.record.recorder.repository;

import com.hzlz.aviation.feature.record.recorder.model.VodVideoCreateModel;
import com.hzlz.aviation.feature.record.recorder.request.VodVideoCreateRequest;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;

import io.reactivex.rxjava3.core.Observable;

/**
 * 视频录制仓库类
 */
public class VideoRecordRepository extends BaseDataRepository {

    /**
     * 生成发布video模型
     *
     * @param fileName 待生成video对应的文件名
     */
    public Observable<VodVideoCreateModel> createVodVideoModel(String fileName) {
        return new NetworkData<VodVideoCreateModel>(mEngine) {
            @Override
            protected BaseRequest<VodVideoCreateModel> createRequest() {
                VodVideoCreateRequest request = new VodVideoCreateRequest();
                request.setFileName(fileName);
                return request;
            }

            @Override
            protected void saveData(VodVideoCreateModel vodVideoCreateModel) {

            }
        }.asObservable(GVideoSchedulers.IO_PRIORITY_USER);
    }

}
