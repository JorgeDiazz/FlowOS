package com.flowos.auth.repositories

import io.reactivex.rxjava3.core.Observable

interface CredentialsRepository {

  fun isLoggedIn(): Observable<Boolean>
  fun saveToken(token: String)
  fun saveRefreshToken(refreshToken: String)
  fun saveTemporalToken(token: String)
  fun getToken(): String?
  fun getTemporalToken(): String?
  fun clearTemporalToken()
  fun getRefreshToken(): String?
  fun clearAll()
}
