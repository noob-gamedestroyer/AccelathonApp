package com.gamdestroyerr.accelathonapp.repositories

import android.net.Uri
import android.util.Log
import com.gamdestroyerr.accelathonapp.model.*
import com.gamdestroyerr.accelathonapp.util.Constants.DEFAULT_PROFILE_PICTURE_URL
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
    private val ngoPosts = fireStore.collection("ngoPost")

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
                Log.d("TAG", "ID:TAG\t" + it.message.toString())
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

    override suspend fun createNgoPost(imageUri: Uri, text: String, ngo: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val uid = auth.uid!!
                val postId = UUID.randomUUID().toString()
                var apartmentName = ""
                var wingNo = ""
                var flatNo = ""
                var phoneNumber = ""
                val docRef = users.document(auth.uid!!.toString())
                docRef.get().addOnSuccessListener {
                    apartmentName = it?.getString("apartmentName").toString()
                    wingNo = it?.getString("wingNo").toString()
                    flatNo = it?.getString("flatNo").toString()
                    phoneNumber = it?.getString("phoneNumber").toString()
                }.addOnFailureListener {
                    Log.d("TAG", "ID:TAG\t" + it.message.toString())
                }
                val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
                val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await()
                    .toString()
                val ngoPost = NgoPost(
                    id = postId,
                    authorUid = uid,
                    ngo = ngo,
                    text = text,
                    imageUrl = imageUrl,
                    date = System.currentTimeMillis(),
                    apartmentName = apartmentName,
                    wingNo = wingNo,
                    flatNo = flatNo,
                    phoneNumber = phoneNumber,
                )
                ngoPosts.document(postId).set(ngoPost).await()
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

    override suspend fun getPostForProfile(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val profilePosts = posts.whereEqualTo("authorUid", uid)
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
            Resource.Success(profilePosts)
        }
    }

    override suspend fun searchUser(query: String) = withContext(Dispatchers.IO) {
        safeCall {
            val userResults = users.whereGreaterThanOrEqualTo(
                "username",
                query.toUpperCase(Locale.ROOT)
            )
                .get().await().toObjects(User::class.java)
            Resource.Success(userResults)
        }
    }

    override suspend fun toggleFollowForUser(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val currentUid = auth.uid!!
            var isFollowing = false
            fireStore.runTransaction { transaction ->
                val currentUser =
                    transaction.get(users.document(currentUid)).toObject(User::class.java)!!
                isFollowing = uid in currentUser.follows
                val newFollows = if (isFollowing)
                    currentUser.follows - uid
                else currentUser.follows + uid
                transaction.update(users.document(currentUid), "follows", newFollows)

            }.await()
            Resource.Success(!isFollowing)
        }
    }

    override suspend fun toggleUpVoteForPost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            var isUpVoted = false
            fireStore.runTransaction { transaction ->
                val uid = FirebaseAuth.getInstance().uid!!
                val postResult = transaction.get(posts.document(post.id))
                val currentUpVotes = postResult.toObject(Post::class.java)?.upVotedBy ?: listOf()
                transaction.update(
                    posts.document(post.id),
                    "upVotedBy",
                    if (uid in currentUpVotes) currentUpVotes - uid
                    else {
                        isUpVoted = true
                        currentUpVotes + uid
                    }
                )
            }.await()
            Resource.Success(isUpVoted)
        }
    }

    override suspend fun deletePost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            posts.document(post.id).delete().await()
            storage.getReferenceFromUrl(post.imageUrl).delete().await()
            Resource.Success(post)
        }
    }

    override suspend fun createComment(commentText: String, postId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val uid = auth.uid!!
                val commentId = UUID.randomUUID().toString()
                val user = getUser(uid).data!!
                val comment = Comment(
                    commentId,
                    postId,
                    uid,
                    user.username,
                    user.profilePicture,
                    commentText,
                )
                comments.document(commentId).set(comment).await()
                Resource.Success(comment)
            }
        }

    override suspend fun getCommentsForPost(postId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val commentForPost = comments
                .whereEqualTo("postId", postId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Comment::class.java)
                .onEach { comment ->
                    val user = getUser(comment.uid).data!!
                    comment.username = user.username
                    comment.profilePictureUrl = user.profilePicture
                }
            Resource.Success(commentForPost)
        }
    }

    override suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO) {
        safeCall {
            comments.document(comment.commentId).delete().await()
            Resource.Success(comment)
        }
    }

    override suspend fun updateProfilePicture(uid: String, imageUri: Uri) =
        withContext(Dispatchers.IO) {
            val storageRef = storage.getReference(uid)
            val user = getUser(uid).data!!
            if (user.profilePicture != DEFAULT_PROFILE_PICTURE_URL) {
                storage.getReferenceFromUrl(user.profilePicture).delete().await()
            }
            storageRef.putFile(imageUri).await().metadata?.reference?.downloadUrl?.await()
        }

    override suspend fun updateProfile(profileUpdate: ProfileUpdate) = withContext(Dispatchers.IO) {
        safeCall {
            val imageUrl = profileUpdate.profilePictureUri?.let { uri ->
                updateProfilePicture(profileUpdate.uidToUpdate, uri).toString()
            }
            val map = mutableMapOf(
                "username" to profileUpdate.username,
                "description" to profileUpdate.description
            )
            imageUrl?.let { url ->
                map["profilePictureUrl"] = url
            }
            users.document(profileUpdate.uidToUpdate).update(map.toMap()).await()
            Resource.Success(Any())
        }
    }

}