package com.gamdestroyerr.accelathonapp.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gamdestroyerr.accelathonapp.R

class ShareToNgoFragment : Fragment() {

    companion object {
        fun newInstance() = ShareToNgoFragment()
    }

    private lateinit var viewModel: ShareToNgoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.share_to_ngo_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ShareToNgoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}