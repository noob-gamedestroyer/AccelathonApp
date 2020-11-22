package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment.dialogFragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentCommentBinding
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.viewmodels.CommentViewModel
import com.gamdestroyerr.accelathonapp.views.activity.MainActivity
import com.gamdestroyerr.accelathonapp.views.fragments.adapter.CommentAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentDialog : DialogFragment() {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var commentAdapter: CommentAdapter

    private val args: CommentDialogArgs by navArgs()

    private val viewModel: CommentViewModel by viewModels()

    private lateinit var commentBinding: FragmentCommentBinding

    private lateinit var dialogView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return dialogView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        dialogView = LayoutInflater.from(requireContext()).inflate(
                R.layout.fragment_comment,
                null,
        )
        return MaterialAlertDialogBuilder(requireContext())
                .setTitle("Comments")
                .setView(dialogView)
                .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentBinding = FragmentCommentBinding.bind(view)
        val activity by lazy {
            requireActivity() as MainActivity
        }

        setUpRecyclerView()
        subscribeToObservers()
        viewModel.getCommentsForPost(args.postId)

        commentBinding.btnComment.setOnClickListener {
            val commentText = commentBinding.etComment.text.toString()
            viewModel.createComment(commentText, args.postId)
            commentBinding.etComment.text?.clear()
        }

        commentAdapter.setOnDeleteCommentClickListener { comment ->
            viewModel.deleteComment(comment)
        }

        commentAdapter.setOnUserClickListener { comment ->
            if (FirebaseAuth.getInstance().uid!! == comment.uid) {
                activity.binding.bottomNavBar.selectedItemId = R.id.profileFragment
                return@setOnUserClickListener
            }
            findNavController().navigate(
                    CommentDialogDirections.globalActionToOthersProfileFragment(comment.uid)
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.commentsForPost.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    commentBinding.commentProgressBar.isVisible = false
                    snackBar(it)
                    Log.d("TAG", it)
                },
                onLoading = { commentBinding.commentProgressBar.isVisible = true },
        ) { commentsList ->
            commentBinding.commentProgressBar.isVisible = false
            commentAdapter.comments = commentsList
        })

        viewModel.createCommentStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    commentBinding.commentProgressBar.isVisible = false
                    snackBar(it)
                    commentBinding.btnComment.isEnabled
                },
                onLoading = {
                    commentBinding.apply {
                        commentProgressBar.isVisible = true
                        btnComment.isEnabled = false
                    }
                },
        ) {
            commentBinding.apply {
                commentProgressBar.isVisible = false
                btnComment.isEnabled = true
            }
            commentAdapter.comments += it
        })

        viewModel.deleteCommentStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    commentBinding.commentProgressBar.isVisible = false
                    snackBar(it)
                },
                onLoading = { commentBinding.commentProgressBar.isVisible = true },
        ) { comment ->
            commentBinding.commentProgressBar.isVisible = false
            commentAdapter.comments -= comment
        })
    }

    private fun setUpRecyclerView() = commentBinding.rvComments.apply {
        adapter = commentAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

}