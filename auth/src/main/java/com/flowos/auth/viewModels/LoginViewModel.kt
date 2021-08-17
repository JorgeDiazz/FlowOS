package com.flowos.auth.viewModels

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flowos.auth.R
import com.flowos.auth.data.LoginNews
import com.flowos.auth.domain.data.DriverData
import com.flowos.auth.domain.data.LoginData
import com.flowos.base.interfaces.Logger
import com.flowos.base.interfaces.SingleUseCase
import com.flowos.base.interfaces.UseCase
import com.flowos.core.BaseViewModel
import com.flowos.core.Event
import com.flowos.core.interfaces.AppResources
import com.flowos.core.qualifiers.GetDeviceId
import com.flowos.core.qualifiers.LoginUser
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class LoginViewModel @Inject constructor(
  private val logger: Logger,
  private val appResources: AppResources,
  @LoginUser private val loginUserUseCase: SingleUseCase<Pair<LoginData, String?>, DriverData>,
  @GetDeviceId private val getDeviceIdUseCase: UseCase<Unit, String>,
) : BaseViewModel(), LifecycleObserver {

  private val _news = MutableLiveData<Event<LoginNews>>()
  val news: LiveData<Event<LoginNews>> = _news

  fun loginUser(driverId: String) {
    val googleAuth2Token = ""
    val deviceId = getDeviceIdUseCase.execute(Unit)

    disposables.add(
      loginUserUseCase.execute(
        LoginData(
          driverId = driverId,
          deviceId = deviceId,
          vehicleId = "",
        ) to googleAuth2Token
      )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          _news.value = Event(LoginNews.LoginSuccessful(it))
        }) {
          logger.e("LoginViewModel loginUser", it)
          _news.value = Event(
            LoginNews.ShowErrorNews(
              it.message ?: appResources.getString(
                R.string.error_when_trying_to_login
              )
            )
          )
        }
    )
  }
}
