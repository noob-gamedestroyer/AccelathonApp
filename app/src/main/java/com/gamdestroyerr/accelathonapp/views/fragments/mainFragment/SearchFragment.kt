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
import com.gamdestroyerr.accelathonapp.databinding.FragmentSearchBinding
import com.gamdestroyerr.accelathonapp.util.Constants.SEARCH_TIME_DELAY
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.viewModels.SearchViewModel
import com.gamdestroyerr.accelathonapp.views.fragments.adapter.UserAdapter
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
    private lateinit var navController: NavController
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchBinding = FragmentSearchBinding.bind(view)
        navController = Navigation.findNavController(view)
        searchBinding.progressBarSearch.isVisible = false

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