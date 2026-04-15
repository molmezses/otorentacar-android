package com.edadursun.otorentacar.core.session

import android.util.Log
import com.edadursun.otorentacar.data.remote.model.ConnectResponse
import com.edadursun.otorentacar.data.repository.AuthRepository

//Token alma ve başlatma akışını yöneten yer, tokenle ne yapılacağını yönetir
//Uygulama oturmunu başlatan yönetici. Auth isteğini başlatır başarılıysa tokeni saklar değilse temizler
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
            Log.d("TOKEN_TEST", "Token başarıyla alındı")
            TokenStore.saveToken(connectResponse.token)
        }.onFailure {
            TokenStore.clearToken()
        }
    }
}