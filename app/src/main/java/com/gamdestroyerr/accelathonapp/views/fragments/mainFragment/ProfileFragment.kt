package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentProfileBinding
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.viewModels.BasePostViewModel
import com.gamdestroyerr.accelathonapp.viewModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment(R.layout.fragment_profile) {

    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var navController: NavController

    override val progressBarPost: ProgressBar
        get() {
            profileBinding = FragmentProfileBinding.bind(requireView())
            return profileBinding.profilePostsProgressBar
        }
    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: ProfileViewModel by viewModels()
            return vm
        }

    protected val viewModel: ProfileViewModel
        get() = basePostViewModel as ProfileViewModel

    protected open val uid: String
        get() = FirebaseAuth.getInstance().uid!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileBinding = FragmentProfileBinding.bind(view)
        navController = Navigation.findNavController(view)
        profileBinding.profileMetaProgressBar.isVisible = false

        setUpRecyclerView(profileBinding)
        subscribeToObservers()

        profileBinding.btnToggleFollow.isVisible = false
        viewModel.loadProfile(uid)
    }

    private fun setUpRecyclerView(binding: FragmentProfileBinding) = binding.rvPosts.apply {
        adapter = postAdapter
        itemAnimator = null
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    profileBinding.profileMetaProgressBar.isVisible = false
                    snackBar(it)
                },
                onLoading = {
                    profileBinding.profileMetaProgressBar.isVisible = true
                },
        ) { user ->
            profileBinding.profileMetaProgressBar.isVisible = false
            profileBinding.tvUsername.text = user.username
            profileBinding.tvProfileDescription.text = if (user.description.isEmpty()) {
                requireContext().getString(R.string.no_description)
            } else user.description
            glide.load(user.profilePicture).into(profileBinding.ivProfileImage)
        })
    }
}