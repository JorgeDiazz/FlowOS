package com.flowos.auth.repositories

import com.flowos.base.interfaces.Cache
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

private const val AUTH_TOKEN = "AUTH_TOKEN"
private const val REFRESH_TOKEN = "REFRESH_TOKEN"
private const val TEMPORAL_TOKEN = "TEMPORAL_TOKEN"

class CredentialsRepositoryImpl constructor(private val cache: Cache) : CredentialsRepository {

  private val loggedInSubject = BehaviorSubject.create<Boolean>()

  init {
    loggedInSubject.onNext(cache.readString(AUTH_TOKEN) != null)
  }

  override fun isLoggedIn(): Observable<Boolean> {
    return loggedInSubject.hide()
  }

  override fun saveToken(token: String) {
    cache.saveString(AUTH_TOKEN, token)
    loggedInSubject.onNext(cache.readString(AUTH_TOKEN) != null)
  }

  override fun saveTemporalToken(token: String) {
    cache.saveString(TEMPORAL_TOKEN, token)
  }

  override fun saveRefreshToken(refreshToken: String) {
    cache.saveString(REFRESH_TOKEN, refreshToken)
  }

  override fun getToken(): String? {
    return cache.readString(AUTH_TOKEN)
  }

  override fun getRefreshToken(): String? {
    return cache.readString(REFRESH_TOKEN)
  }

  override fun getTemporalToken(): String? {
    return cache.readString(TEMPORAL_TOKEN)
  }

  override fun clearTemporalToken() {
    return cache.removeValue(TEMPORAL_TOKEN)
  }

  override fun clearAll() {
    loggedInSubject.onNext(false)
    cache.clearAll()
  }
}
