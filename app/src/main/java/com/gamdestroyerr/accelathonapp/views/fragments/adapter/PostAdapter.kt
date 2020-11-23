package com.gamdestroyerr.accelathonapp.views.fragments.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.ItemPostBinding
import com.gamdestroyerr.accelathonapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class PostAdapter @Inject constructor(
        private val glide: RequestManager
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemPostBinding = ItemPostBinding.bind(itemView)

        val ivPostImage = itemPostBinding.ivPostImage
        val ivAuthorProfileImage = itemPostBinding.ivAuthorProfileImage
        val tvPostAuthor = itemPostBinding.tvPostAuthor
        val tvPostText = itemPostBinding.tvPostText
        val tvUpVotedBy = itemPostBinding.tvUpVotedBy
        val ibUpVote = itemPostBinding.ibUpVote
        val ibComments = itemPostBinding.ibComments
        val ibDeletePost = itemPostBinding.ibDeletePost
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Post>() {
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    var posts: List<Post>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_post,
                parent,
                false,
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.apply {
            glide.load(post.imageUrl).into(ivPostImage)
            glide.load(post.authorProfilePicture).into(ivAuthorProfileImage)
            tvPostAuthor.text = post.authorUsername
            tvPostText.text = post.text
            val upVoteCount = post.upVotedBy.size
            tvUpVotedBy.text = when {
                upVoteCount <= 0 -> "No votes"
                upVoteCount == 1 -> "Voted by 1 person"
                else -> "Voted by $upVoteCount people"
            }
            val uid = FirebaseAuth.getInstance().uid!!
            ibDeletePost.isVisible = uid == post.authorUid
            ibUpVote.setImageResource(if (post.isUpVoted) {
                R.drawable.ic_twotone_arrow_circle_up_black_24
            } else R.drawable.ic_round_arrow_drop_down_circle_24)


            tvPostAuthor.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(post.authorUid)
                }
            }
            ivAuthorProfileImage.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(post.authorUid)
                }
            }
            tvUpVotedBy.setOnClickListener {
                onUpVotedByClickListener?.let { click ->
                    click(post)
                }
            }
            ibUpVote.setOnClickListener {
                onUpVotedClickListener?.let { click ->
                    if (!post.isUpVoting)
                        click(post, holder.layoutPosition)
                }
            }
            ibComments.setOnClickListener {
                onCommentsClickListener?.let { click ->
                    click(post)
                }
            }
            ibDeletePost.setOnClickListener {
                onDeletePostClickListener?.let { click ->
                    click(post)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    //onClick for individual post items
    private var onUpVotedClickListener: ((Post, Int) -> Unit)? = null
    private var onUserClickListener: ((String) -> Unit)? = null
    private var onDeletePostClickListener: ((Post) -> Unit)? = null
    private var onUpVotedByClickListener: ((Post) -> Unit)? = null
    private var onCommentsClickListener: ((Post) -> Unit)? = null

    fun setOnUpVoteClickListener(listener: (Post, Int) -> Unit) {
        onUpVotedClickListener = listener
    }

    fun setOnUserClickListener(listener: (String) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeletePostClickListener(listener: (Post) -> Unit) {
        onDeletePostClickListener = listener
    }

    fun setOnUpVotedByClickListener(listener: (Post) -> Unit) {
        onUpVotedByClickListener = listener
    }

    fun setOnCommentsClickListener(listener: (Post) -> Unit) {
        onCommentsClickListener = listener
    }
}