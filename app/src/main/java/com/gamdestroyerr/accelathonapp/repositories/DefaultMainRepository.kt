package com.gamdestroyerr.accelathonapp.repositories

import android.net.Uri
import android.util.Log
import com.gamdestroyerr.accelathonapp.model.Post
import com.gamdestroyerr.accelathonapp.model.User
import com.gamdestroyerr.accelathonapp.util.Resource
import com.gamdestroyerr.accelathonapp.util.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@ActivityScoped
class DefaultMainRepository : MainRepository {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val users = fireStore.collection("users")
    private val posts = fireStore.collection("posts")
    private val comments = fireStore.collection("comments")

    override suspend fun createPost(imageUri: Uri, text: String) = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val postId = UUID.randomUUID().toString()
            var apartmentName = ""
            var wingNo = ""
            val docRef = users.document(auth.uid!!.toString())
            docRef.get().addOnSuccessListener {
                apartmentName = it?.getString("apartmentName").toString()
                wingNo = it?.getString("wingNo").toString()
            }.addOnFailureListener {
                Log.d("TAG-ERROR", it.message.toString())
            }
            val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
            val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await()
                    .toString()
            val post = Post(
                    id = postId,
                    authorUid = uid,
                    text = text,
                    imageUrl = imageUrl,
                    date = System.currentTimeMillis(),
                    apartmentName = apartmentName,
                    wingNo = wingNo,
            )
            posts.document(postId).set(post).await()
            Resource.Success(Any())
        }
    }

    override suspend fun getUsers(uids: List<String>) = withContext(Dispatchers.IO) {
        safeCall {
            val userList = users.whereIn("uid", uids).orderBy("username").get().await()
                    .toObjects(User::class.java)
            Resource.Success(userList)
        }
    }

    override suspend fun getUser(uid: String) = withContext(Dispatchers.IO) {

        safeCall {
            val user = users.document(uid).get().await().toObject(User::class.java)
                    ?: throw IllegalStateException()
            val currentUid = FirebaseAuth.getInstance().uid!!
            val currentUser = users.document(currentUid).get().await().toObject(User::class.java)
                    ?: throw IllegalStateException()

            user.isFollowing = uid in currentUser.follows
            Resource.Success(user)
        }
    }

    override suspend fun getPostForFollows() = withContext(Dispatchers.IO) {
        safeCall {
            val uid = FirebaseAuth.getInstance().uid!!
            val follows = getUser(uid).data!!.follows
            val allPosts = posts.whereIn("authorUid", follows)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(Post::class.java)
                    .onEach { post ->
                        val user = getUser(post.authorUid).data!!
                        post.authorProfilePicture = user.profilePicture
                        post.authorUsername = user.username
                        post.isUpVoted = uid in post.upVotedBy
                    }
            Resource.Success(allPosts)
        }
    }
}