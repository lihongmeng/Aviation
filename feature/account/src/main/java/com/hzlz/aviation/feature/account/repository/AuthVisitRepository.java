package com.hzlz.aviation.feature.account.repository;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.request.AuthVisitRequest;
import com.hzlz.aviation.kernel.base.plugin.IAuthRepository;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

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
