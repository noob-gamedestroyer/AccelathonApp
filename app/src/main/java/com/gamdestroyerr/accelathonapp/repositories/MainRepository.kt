package com.gamdestroyerr.accelathonapp.repositories

import android.net.Uri
import com.gamdestroyerr.accelathonapp.model.Comment
import com.gamdestroyerr.accelathonapp.model.Post
import com.gamdestroyerr.accelathonapp.model.ProfileUpdate
import com.gamdestroyerr.accelathonapp.model.User
import com.gamdestroyerr.accelathonapp.util.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>

    suspend fun createNgoPost(imageUri: Uri, text: String, ngo: String): Resource<Any>

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>

    suspend fun getPostForApartment(): Resource<List<Post>>

    suspend fun toggleUpVoteForPost(post: Post): Resource<Boolean>

    suspend fun deletePost(post: Post): Resource<Post>

    suspend fun getPostForProfile(uid: String): Resource<List<Post>>

    suspend fun toggleFollowForUser(uid: String): Resource<Boolean>

    suspend fun searchUser(query: String): Resource<List<User>>

    suspend fun getCommentsForPost(postId: String): Resource<List<Comment>>

    suspend fun createComment(commentText: String, postId: String): Resource<Comment>

    suspend fun deleteComment(comment: Comment): Resource<Comment>

    suspend fun updateProfile(profileUpdate: ProfileUpdate): Resource<Any>

    suspend fun updateProfilePicture(uid: String, imageUri: Uri): Uri?
}