package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.viewmodels.BasePostViewModel
import com.gamdestroyerr.accelathonapp.views.fragments.adapter.PostAdapter
import com.gamdestroyerr.accelathonapp.views.fragments.adapter.UserAdapter
import com.gamdestroyerr.accelathonapp.views.fragments.mainFragment.dialogFragment.DeletePostDialog
import com.gamdestroyerr.accelathonapp.views.fragments.mainFragment.dialogFragment.UpVotedByDialog
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

abstract class BasePostFragment(
        layoutId: Int
) : Fragment(layoutId) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postAdapter: PostAdapter

    protected abstract val progressBarPost: ProgressBar

    protected abstract val basePostViewModel: BasePostViewModel

    private var currentUpVoteIndex: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserver()

        postAdapter.setOnUpVoteClickListener { post, i ->
            currentUpVoteIndex = i
            post.isUpVoted = !post.isUpVoted
            basePostViewModel.toggleUpVoteForPost(post)
        }

        postAdapter.setOnDeletePostClickListener { post ->
            DeletePostDialog().apply {
                setPositiveListener {
                    basePostViewModel.deletePost(post)
                }
            }.show(childFragmentManager, null)
        }

        postAdapter.setOnUpVotedByClickListener { post ->
            basePostViewModel.getUsers(post.upVotedBy)
        }

        postAdapter.setOnCommentsClickListener { post ->
            findNavController().navigate(
                    R.id.globalActionToCommentDialog,
                    Bundle().apply { putString("postId", post.id) }
            )
        }
    }

    private fun subscribeToObserver() {
        basePostViewModel.upVotePostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    currentUpVoteIndex?.let { index ->
                        postAdapter.posts[index].isUpVoting = true
                        postAdapter.notifyItemChanged(index)
                    }
                    snackBar(it)
                },
                onLoading = {
                    currentUpVoteIndex?.let {
                        postAdapter.posts[it].isUpVoting = true
                        postAdapter.notifyItemChanged(it)
                    }
                }
        ) { isUpVoted ->
            currentUpVoteIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                postAdapter.posts[index].apply {
                    this.isUpVoted = isUpVoted
                    isUpVoting = false
                    if (isUpVoted) {
                        upVotedBy += uid
                    } else {
                        upVotedBy -= uid
                    }
                }
                postAdapter.notifyItemChanged(index)
            }
        })

        basePostViewModel.upVotedByUserStatus.observe(viewLifecycleOwner, EventObserver(
                onError = { snackBar(it) },
        ) { userList ->
            val userAdapter = UserAdapter(glide)
            userAdapter.users = userList
            UpVotedByDialog(userAdapter).show(childFragmentManager, null)
        })

        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    snackBar(it)
                },
        ) { deletedPost ->
            postAdapter.posts -= deletedPost
        })


        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    progressBarPost.isVisible = false
                    snackBar(it)
                },
                onLoading = {
                    progressBarPost.isVisible = true
                },
        ) { postList ->
            progressBarPost.isVisible = false
            postAdapter.posts = postList
        })
    }
}