package com.gamdestroyerr.accelathonapp.repositories

import com.gamdestroyerr.accelathonapp.model.User
import com.gamdestroyerr.accelathonapp.util.Resource
import com.gamdestroyerr.accelathonapp.util.safeCall
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultAuthRepository : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")

    override suspend fun register(
        email: String,
        username: String,
        password: String,
        phoneNumber: String,
        apartment: String,
        wing: String,
        flat: String,
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid!!
                val user = User(
                    uid,
                    username,
                    phoneNumber = phoneNumber,
                    apartmentName = apartment,
                    wingNo = wing,
                    flatNo = flat,
                )
                users.document(uid).set(user).await()
                Resource.Success(result)
            }
        }
    }

    override suspend fun login(
            email: String,
            password: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                Resource.Success(result)
            }
        }
    }

}