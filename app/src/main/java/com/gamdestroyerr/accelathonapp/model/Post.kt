package com.gamdestroyerr.accelathonapp.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
        val id: String = "",
        val authorUid: String = "",
        @get:Exclude var authorUsername: String = "",
        @get:Exclude var authorProfilePicture: String = "",
        val text: String = "",
        val imageUrl: String = "",
        val date: Long = 0L,
        @get:Exclude var isUpVoted: Boolean = false,
        @get:Exclude var isUpVoting: Boolean = false,
        var upVotedBy: List<String> = listOf(),
        var apartmentName: String = "",
        var wingNo: String = "",
)