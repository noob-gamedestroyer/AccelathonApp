package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentAddOptionsModalDialogBinding
import com.gamdestroyerr.accelathonapp.databinding.FragmentSearchBinding
import com.gamdestroyerr.accelathonapp.util.Constants.SEARCH_TIME_DELAY
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.viewmodels.SearchViewModel
import com.gamdestroyerr.accelathonapp.views.activity.MainActivity
import com.gamdestroyerr.accelathonapp.views.fragments.adapter.UserAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    @Inject
    lateinit var userAdapter: UserAdapter

    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var bindingDialog: FragmentAddOptionsModalDialogBinding
    private lateinit var navController: NavController
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchBinding = FragmentSearchBinding.bind(view)
        bindingDialog = FragmentAddOptionsModalDialogBinding.inflate(layoutInflater)
        navController = Navigation.findNavController(view)
        searchBinding.progressBarSearch.isVisible = false

        val activity = activity as MainActivity

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
                        navController.navigate(SearchFragmentDirections.actionSearchFragmentToMakeRequestFragment())
                        bottomSheetDialog.dismiss()
                    }
                    bindingDialog.share.setOnClickListener {
                        navController.navigate(R.id.action_searchFragment_to_shareFragment)
                        bottomSheetDialog.dismiss()
                    }
                }
            }
        }

        setUpRecyclerView()
        subscribeToObservers()

        var job: Job? = null
        searchBinding.searchEdt.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    viewModel.searchUser(it.toString())
                }
            }
        }

        userAdapter.setOnUserClickListener {
            navController.navigate(
                    SearchFragmentDirections.globalActionToOthersProfileFragment(it.uid)
            )
        }
    }

    private fun setUpRecyclerView() = searchBinding.searchRecyclerView.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = userAdapter
        itemAnimator = null
    }

    private fun subscribeToObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    searchBinding.progressBarSearch.isVisible = false
                    snackBar(it)
                },
                onLoading = {
                    searchBinding.progressBarSearch.isVisible = true
                },
        ) { userList ->
            searchBinding.progressBarSearch.isVisible = false
            userAdapter.users = userList
        })
    }
}