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
import com.gamdestroyerr.accelathonapp.databinding.ShareToNgoFragmentBinding
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.util.toast
import com.gamdestroyerr.accelathonapp.viewmodels.ShareToNgoViewModel
import com.gamdestroyerr.accelathonapp.views.activity.MainActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShareToNgoFragment : Fragment(R.layout.share_to_ngo_fragment) {

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: ShareToNgoViewModel by viewModels()
    private lateinit var ngoBinding: ShareToNgoFragmentBinding
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
        ngoBinding = ShareToNgoFragmentBinding.bind(view)
        val activity = activity as MainActivity
        activity.binding.addFab.isEnabled = false

        subscribeToObservers()

        ngoBinding.apply {
            ngoBinding.imageNgo.setOnClickListener {
                cropContent.launch("image/*")
            }
            addImageBtn.setOnClickListener {
                cropContent.launch("image/*")
            }
            confirmNgoPost.setOnClickListener {
                curImageUri?.let { uri ->
                    viewModel.createNgoPost(
                        uri,
                        etDonateList.text.toString(),
                        etNgoList.text.toString(),
                    )
                } ?: snackBar("Please provide an image")
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.curImageUri.observe(viewLifecycleOwner) {
            curImageUri = it
            glide.load(curImageUri).into(ngoBinding.imageNgo)
        }
        viewModel.createNgoPostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    ngoBinding.progressBarNgo.isVisible = false
                    snackBar(it)
                },
                onLoading = {
                    ngoBinding.progressBarNgo.isVisible = true
                },
        ) {
            ngoBinding.progressBarNgo.isVisible = false
            toast("Request sent to ${ngoBinding.etNgoList.text.toString()}")
            findNavController().popBackStack()
        })
    }

}