package com.gamdestroyerr.accelathonapp.model

import com.gamdestroyerr.accelathonapp.util.Constants.DEFAULT_PROFILE_PICTURE_URL
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        val uid: String = "",
        val username: String = "",
        val profilePicture: String = DEFAULT_PROFILE_PICTURE_URL,
        val description: String = "",
        val phoneNumber: String = "",
        val apartmentName: String = "",
        val wingNo: String = "",
        val flatNo: String = "",
        val follows: List<String> = listOf(),
        @Exclude
        var isFollowing: Boolean = false,
)