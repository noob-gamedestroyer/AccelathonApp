package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentProfileBinding
import com.gamdestroyerr.accelathonapp.model.User
import com.gamdestroyerr.accelathonapp.util.EventObserver

class OthersProfileFragment : ProfileFragment() {

    private lateinit var profileBinding: FragmentProfileBinding
    private val args: OthersProfileFragmentArgs by navArgs()

    override val uid: String
        get() = args.uid

    private var curUser: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileBinding = FragmentProfileBinding.bind(view)
        subscribeToObservers()

        profileBinding.btnToggleFollow.setOnClickListener {
            viewModel.toggleFollowForUser(uid)
        }
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver {
            profileBinding.btnToggleFollow.isVisible = true
            setupToggleFollowButton(it)
            curUser = it
        })

        viewModel.followStatus.observe(viewLifecycleOwner, EventObserver {
            curUser?.isFollowing = it
            setupToggleFollowButton(curUser ?: return@EventObserver)
        })
    }

    private fun setupToggleFollowButton(user: User) {
        profileBinding.btnToggleFollow.apply {
            val changeBounds = ChangeBounds().apply {
                duration = 300L
                interpolator = OvershootInterpolator()
            }

            val set1 = ConstraintSet()
            val set2 = ConstraintSet()
            set1.clone(requireContext(), R.layout.fragment_profile)
            set2.clone(requireContext(), R.layout.fragment_profile_anim)
            TransitionManager.beginDelayedTransition(profileBinding.clProfile, changeBounds)
            if (user.isFollowing) {
                text = requireContext().getString(R.string.unfollow)
                setBackgroundColor(Color.RED)
                setTextColor(Color.WHITE)
                set1.applyTo(profileBinding.clProfile)
            } else {
                text = requireContext().getString(R.string.follow)
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                set2.applyTo(profileBinding.clProfile)
            }
        }
    }
}