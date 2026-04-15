package com.edadursun.otorentacar.core.session

//Tokeni tutan yer, uygulama açıkken kullandığımız tokeni hafızada tutar, tokeni saklar
//Her yerde ayrı token değişkeni olmasın tek merkezden erişilsin diye object olarak tanımladık
object TokenStore {

    //Neden private set ? herkes gidip direkt yazamasın
    var token: String? = null
        private set

    //connect başarılı olursa token buraya yazılacak
    fun saveToken(newToken: String) {
        token = newToken
    }

    //logout ya da hata anında silmek için
    fun clearToken() {
        token = null
    }

    //token var mı yok mu kontrol ediyor
    fun hasToken(): Boolean {
        return !token.isNullOrBlank()
    }
}