package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentAddOptionsModalDialogBinding
import com.gamdestroyerr.accelathonapp.databinding.FragmentHomeBinding
import com.gamdestroyerr.accelathonapp.viewmodels.BasePostViewModel
import com.gamdestroyerr.accelathonapp.viewmodels.HomeViewModel
import com.gamdestroyerr.accelathonapp.views.activity.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BasePostFragment(R.layout.fragment_home) {

    private lateinit var navController: NavController
    private lateinit var bindingDialog: FragmentAddOptionsModalDialogBinding
    private lateinit var homeBinding: FragmentHomeBinding

    override val progressBarPost: ProgressBar
        get() {
            homeBinding = FragmentHomeBinding.bind(requireView())
            return homeBinding.allPostProgressBar
        }
    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: HomeViewModel by viewModels()
            return vm
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        bindingDialog = FragmentAddOptionsModalDialogBinding.inflate(layoutInflater)
        val activity = activity as MainActivity
        homeBinding = FragmentHomeBinding.bind(view)

        setUpRecyclerView()

        activity.binding.addFab.apply {
            isEnabled = true
            setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(
                        requireContext(),
                        R.style.BottomSheetDialogTheme,
                )
                val bottomSheetView: View = bindingDialog.root

                with(bottomSheetDialog) {
                    setContentView(bottomSheetView)
                    show()
                }
                bottomSheetView.post {
                    bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                bottomSheetView.apply {
                    bindingDialog.ask.setOnClickListener {
                        navController.navigate(HomeFragmentDirections.actionHomeFragmentToMakeRequestFragment())
                        bottomSheetDialog.dismiss()
                    }
                    bindingDialog.share.setOnClickListener {
                        navController.navigate(R.id.action_homeFragment_to_shareFragment)
                        bottomSheetDialog.dismiss()
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() = homeBinding.feedRecyclerView.apply {
        adapter = postAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}



