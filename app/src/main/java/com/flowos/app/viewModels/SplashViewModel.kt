package com.flowos.app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flowos.app.R
import com.flowos.app.data.SplashNews
import com.flowos.base.interfaces.Logger
import com.flowos.base.interfaces.SingleUseCase
import com.flowos.core.BaseViewModel
import com.flowos.core.Event
import com.flowos.core.exceptions.NoConnectionException
import com.flowos.core.interfaces.AppResources
import com.flowos.core.network.ServerException
import com.flowos.core.qualifiers.IsUserLogged
import com.flowos.core.qualifiers.VerifyInternet
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class SplashViewModel @Inject constructor(
  private val logger: Logger,
  private val resources: AppResources,
  @VerifyInternet private val verifyInternetConnectivityUseCase: SingleUseCase<Unit, Boolean>,
  @IsUserLogged private val isUserLoggedUseCase: SingleUseCase<Unit, Boolean>,
) : BaseViewModel() {

  private val _news = MutableLiveData<Event<SplashNews>>()
  val news: LiveData<Event<SplashNews>> = _news

  fun onViewActive() {
    disposables.add(
      validateConnectivity()
        .andThen(validateUserIsLogged())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          _news.value = Event(SplashNews.AppInitialized)
        }) {
          handleError(it)
        }
    )
  }

  private fun validateConnectivity(): Completable {
    return Completable.defer {
      verifyInternetConnectivityUseCase.execute(Unit)
        .flatMapCompletable { isConnected ->
          if (isConnected) {
            Completable.complete()
          } else {
            Completable.error(NoConnectionException())
          }
        }
    }
  }

  private fun validateUserIsLogged(): Completable {
    return Completable.defer {
      isUserLoggedUseCase.execute(Unit)
        .flatMapCompletable { isUserLogged ->
          if (isUserLogged) {
            Completable.complete()
          } else {
            Completable.error(UserNotLoggedException())
          }
        }
    }
  }

  private fun handleError(throwable: Throwable) {
    when (throwable) {
      is ConnectException -> {
        _news.value =
          Event(SplashNews.ShowErrorNews(resources.getString(R.string.connection_error)))
      }
      is ServerException -> {
        _news.value = Event(SplashNews.ShowErrorNews(throwable.message))
      }
      is HttpException -> {
        val message = throwable.response()?.errorBody()?.string()
        _news.value = Event(SplashNews.ShowErrorNews(message.toString()))
      }
      is NoConnectionException -> {
        _news.value = Event(SplashNews.ShowNoConnectivityView)
      }
      is UserNotLoggedException -> {
        _news.value = Event(SplashNews.OpenLoginNews)
        logger.d("SplashViewModel InitializeException", throwable)
      }
      is UpdateRequiredException -> {
        _news.value = Event(SplashNews.FinishSplashNews)
        logger.d("SplashViewModel InitializeException", throwable)
      }
      else -> {
        _news.value = Event(SplashNews.ShowErrorNews(throwable.message.toString()))
        logger.e("SplashViewModel handle error", throwable)
      }
    }
  }

  private class UserNotLoggedException : Exception()
  private class UpdateRequiredException : Exception()
}
