package com.gamdestroyerr.accelathonapp.repositories

import com.gamdestroyerr.accelathonapp.util.Resource
import com.google.firebase.auth.AuthResult

interface AuthRepository {

    suspend fun register(
        email: String,
        username: String,
        password: String,
        phoneNumber: String,
        apartment: String,
        wing: String,
        flat: String,
    ): Resource<AuthResult>

    suspend fun login(email: String, password: String): Resource<AuthResult>
}