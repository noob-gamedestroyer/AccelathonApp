package com.gamdestroyerr.accelathonapp.repositories

import android.net.Uri
import com.gamdestroyerr.accelathonapp.util.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>
}