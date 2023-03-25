package com.rivaldofez.storymessage.addstory

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.Utils.MediaUtility
import com.rivaldofez.storymessage.Utils.MediaUtility.reduceFileImage
import com.rivaldofez.storymessage.Utils.MediaUtility.uriToFile
import com.rivaldofez.storymessage.databinding.FragmentAddStoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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

            // Rotate image to correct orientation
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

            // Convert rotated image to file
            try {
                os = FileOutputStream(file)
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()

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

    private fun startCameraIntent(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

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
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherGalleryIntent.launch(chooser)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWlyc25sM1h2cW9fYlZJOXAiLCJpYXQiOjE2Nzk1NjcyOTZ9.YsQQy3NJG6mfEUzJZTql0hrEjs_Hw25xH90AkTOrl9U"

        binding.btnCamera.setOnClickListener { startCameraIntent() }
        binding.btnGallery.setOnClickListener { startGalleryIntent() }

        binding.btnSave.setOnClickListener { addStory() }



    }

    private fun addStory(){
        var isAllFieldValid = true

        if (binding.edtStory.text.toString().isNullOrBlank()){
            binding.edtStory.error = "Kolom tidak boleh kosong"
            isAllFieldValid = false
        }

        if (getFile == null){
            isAllFieldValid = false
        }

        if (isAllFieldValid) {
            val file = reduceFileImage(getFile as File)
            val description = binding.edtStory.text.toString().toRequestBody("text/plain".toMediaType())
            var requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                launch {
                    addStoryViewModel.addStory(token = token, file = imageMultipart, description = description).collect{ response ->
                        response.onSuccess {

                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}