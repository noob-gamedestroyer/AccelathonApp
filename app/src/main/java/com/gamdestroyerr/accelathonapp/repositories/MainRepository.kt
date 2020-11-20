package com.gamdestroyerr.accelathonapp.repositories

import android.net.Uri
import com.gamdestroyerr.accelathonapp.model.Post
import com.gamdestroyerr.accelathonapp.model.User
import com.gamdestroyerr.accelathonapp.util.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>

    suspend fun getPostForFollows(): Resource<List<Post>>
}