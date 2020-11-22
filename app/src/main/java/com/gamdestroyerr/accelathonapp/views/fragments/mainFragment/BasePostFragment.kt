package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestManager
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.viewModels.BasePostViewModel
import com.gamdestroyerr.accelathonapp.views.fragments.adapter.PostAdapter
import com.gamdestroyerr.accelathonapp.views.fragments.mainFragment.dialogs.DeletePostDialog
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
    }

    private fun subscribeToObserver() {
        basePostViewModel.upVotePostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    currentUpVoteIndex?.let { index ->
                        postAdapter.posts[index].isUpVoting = false
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
                    if (isUpVoted) {
                        upVotedBy += uid
                    } else {
                        upVotedBy -= uid
                    }
                }
                postAdapter.notifyItemChanged(index)
            }
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