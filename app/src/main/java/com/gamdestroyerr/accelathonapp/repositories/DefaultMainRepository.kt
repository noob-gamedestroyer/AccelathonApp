package com.gamdestroyerr.accelathonapp.repositories

import android.net.Uri
import com.gamdestroyerr.accelathonapp.model.Post
import com.gamdestroyerr.accelathonapp.util.Resource
import com.gamdestroyerr.accelathonapp.util.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
//            val apartmentDoc = users.document(auth.uid!!)
//            val apartment = apartmentDoc.get().addOnSuccessListener {
//                it?.getDate("apartmentName")
//            }.addOnFailureListener {
//                Log.d("TAG", it.message.toString())
//            }
//            Log.d("TAG", apartment.await().toString())
            val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
            val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            val post = Post(
                id = postId,
                authorUid = uid,
                text = text,
                imageUrl = imageUrl,
                date = System.currentTimeMillis()
            )
            posts.document(postId).set(post).await()
            Resource.Success(Any())
        }
    }
}