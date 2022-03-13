package com.jxntv.account.repository;

import androidx.annotation.NonNull;
import com.jxntv.account.request.AuthVisitRequest;
import com.jxntv.base.plugin.IAuthRepository;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.OneTimeNetworkData;
import com.jxntv.network.request.BaseRequest;
import io.reactivex.rxjava3.core.Observable;

public final class AuthVisitRepository extends BaseDataRepository implements
    IAuthRepository {

  @NonNull @Override public Observable<Object> authVisit(Integer... authProtocols) {
    return new OneTimeNetworkData<Object>(mEngine) {

      @Override
      protected BaseRequest<Object> createRequest() {
        AuthVisitRequest request = new AuthVisitRequest();
        for (Integer i : authProtocols) {
          request.authProtocol(i);
        }
        return request;
      }

    }.asObservable();
  }

}
