package com.edadursun.otorentacar.core.session

import com.edadursun.otorentacar.data.remote.model.ConnectResponse
import com.edadursun.otorentacar.data.repository.AuthRepository

class SessionManager(
    private val authRepository: AuthRepository
) {

    suspend fun initializeSession(
        username: String,
        password: String
    ): Result<ConnectResponse> {
        val result = authRepository.connect(username, password)

        //istek başarılıysa gelen tokeni Tokenstorea yaz , başarısızsa temizle
        return result.onSuccess { connectResponse ->
            TokenStore.saveToken(connectResponse.token)
        }.onFailure {
            TokenStore.clearToken()
        }
    }
}