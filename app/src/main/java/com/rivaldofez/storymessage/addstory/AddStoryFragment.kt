package com.rivaldofez.storymessage.addstory

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.google.android.material.snackbar.Snackbar
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.Utils.MediaUtility
import com.rivaldofez.storymessage.Utils.MediaUtility.reduceFileImage
import com.rivaldofez.storymessage.Utils.MediaUtility.uriToFile
import com.rivaldofez.storymessage.databinding.FragmentAddStoryBinding
import com.rivaldofez.storymessage.extension.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@AndroidEntryPoint
class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var token: String = ""

    private val addStoryViewModel: AddStoryViewModel by viewModels()

    private val launcherCameraIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK) {
            val file = File(currentPhotoPath).also { getFile = it }
            val os: OutputStream

            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exif = ExifInterface(currentPhotoPath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED )

            val rotatedBitmap: Bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(bitmap, 270)
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }
            try {
                Dispatchers.Main.apply {
                    os = FileOutputStream(file)
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.flush()
                    os.close()
                }
                getFile = file
            } catch (e: Exception) {
                e.printStackTrace()
            }
            binding.imgStory.setImageBitmap(rotatedBitmap)
        }
    }

    private val launcherGalleryIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            uriToFile(selectedImg, requireContext()).also { getFile = it }

            binding.imgStory.setImageURI(selectedImg)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUserToken()
        setToolbarActions()
        setActions()
    }

    private fun setUserToken(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            addStoryViewModel.getAuthenticationToken().collect { token ->
                if (!token.isNullOrEmpty()){
                    this@AddStoryFragment.token = token
                }
            }
        }
    }
    private fun setToolbarActions(){
        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.toolbarAddStory)
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.title_add_story)
        }

        binding.toolbarAddStory.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_story_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.menu_save -> {
                        addStory()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setActions(){
        binding.btnCamera.setOnClickListener { startCameraIntent() }
        binding.btnGallery.setOnClickListener { startGalleryIntent() }
    }

    private fun startCameraIntent(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        MediaUtility.createTempFile(requireActivity()).also {
            val photoUri =
                FileProvider.getUriForFile(requireContext(), "com.rivaldofez.storymessage", it)

            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherCameraIntent.launch(intent)
        }
    }

    private fun startGalleryIntent(){
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.dialog_choose_an_image))
        launcherGalleryIntent.launch(chooser)
    }
    private fun showLoading(isLoading: Boolean){
        binding.apply {
            btnGallery.isEnabled = !isLoading
            btnCamera.isEnabled = !isLoading
            edtStory.isEnabled = !isLoading

            layoutLoading.root.animateVisibility(isLoading)
        }
    }

    private fun addStory(){
        var isAllFieldValid = true
        showLoading(isLoading = true)

        if (binding.edtStory.text.isNullOrBlank()){
            binding.edtStory.error = getString(R.string.field_story_error)
            isAllFieldValid = false
        }

        if (getFile == null){
            isAllFieldValid = false
        }

        if (isAllFieldValid) {
            val file = reduceFileImage(getFile as File)
            val description = binding.edtStory.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                launch {
                    addStoryViewModel.addStory(token = token, file = imageMultipart, description = description).collect{ response ->
                        response.onSuccess {
                            showLoading(isLoading = false)
                            Snackbar.make(
                                binding.root,
                                getString(R.string.success_upload_story),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }

                        response.onFailure {
                            showLoading(isLoading = false)
                            Snackbar.make(binding.root, getString(R.string.error_while_upload_story), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            showLoading(isLoading = false)
            Snackbar.make(
                binding.root,
                getString(R.string.error_field_not_valid),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}