package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.MakeRequestFragmentBinding
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.viewmodels.MakeRequestViewModel
import com.gamdestroyerr.accelathonapp.views.activity.MainActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MakeRequestFragment : Fragment(R.layout.make_request_fragment) {

    private val viewModel: MakeRequestViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    private lateinit var requestBinding: MakeRequestFragmentBinding
    private lateinit var cropContent: ActivityResultLauncher<String>

    private val cropActivityResultContract = object : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String?): Intent {
            return CropImage.activity()
                    .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }

    private var curImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                viewModel.setCurImageUri(uri)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestBinding = MakeRequestFragmentBinding.bind(view)
        val activity = activity as MainActivity
        activity.binding.addFab.isEnabled = false
        subscribeToObservers()

        requestBinding.apply {
            addImageBtn.setOnClickListener {
                cropContent.launch("image/*")
            }
            imageViewRequest.setOnClickListener {
                cropContent.launch("image/*")
            }
            sendBtn.setOnClickListener {
                curImageUri?.let { uri ->
                    viewModel.createPost(uri, requestEditText.text.toString())
                } ?: snackBar("Please provide an image")
            }
        }


    }

    private fun subscribeToObservers() {
        viewModel.curImageUri.observe(viewLifecycleOwner) {
            curImageUri = it
            glide.load(curImageUri).into(requestBinding.imageViewRequest)
        }
        viewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                requestBinding.progressBarCreate.isVisible = false
                snackBar(it)
            },
            onLoading = { requestBinding.progressBarCreate.isVisible = true }
        ) {
            requestBinding.progressBarCreate.isVisible = false
            findNavController().popBackStack()
        })
    }
}