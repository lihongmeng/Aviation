package com.jxntv.pptv.repository;

import androidx.annotation.NonNull;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.pptv.model.Channel;
import com.jxntv.pptv.model.ChannelResponse;
import com.jxntv.pptv.model.Media;
import com.jxntv.pptv.request.GetChannelListRequest;
import io.reactivex.rxjava3.core.Observable;

/**
 * Channel仓库
 *
 */
public final class ChannelRepository extends BaseDataRepository {
  /**
   * 获取Channel列表
   *
   */
  @NonNull
  public Observable<ChannelResponse> getChannelList() {
    return new NetworkData<ChannelResponse>(mEngine) {
      @Override
      protected BaseRequest<ChannelResponse> createRequest() {
        GetChannelListRequest request = new GetChannelListRequest();
        return request;
      }

      @Override protected void saveData(ChannelResponse channelResponse) {
        if (channelResponse.getPlate() != null) {
          for (Channel channel : channelResponse.getPlate()) {
            for (Media media : channel.getMedia()) {
              media.updateInteract();
            }
          }
        }
      }
    }.asObservable();
  }
}
