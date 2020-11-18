package com.gamdestroyerr.accelathonapp.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gamdestroyerr.accelathonapp.MakeRequestViewModel
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.views.activity.MainActivity

class MakeRequestFragment : Fragment(R.layout.make_request_fragment) {

    private lateinit var viewModel: MakeRequestViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        activity.binding.addFab.hide()
        viewModel = ViewModelProvider(this).get(MakeRequestViewModel::class.java)
    }
}