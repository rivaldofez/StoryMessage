package com.rivaldofez.storymessage.page.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.data.local.entity.StoryEntity
import com.rivaldofez.storymessage.data.remote.response.StoryResponse
import com.rivaldofez.storymessage.databinding.FragmentMapsBinding
import com.rivaldofez.storymessage.databinding.ItemMapWindowBinding
import com.rivaldofez.storymessage.extension.setLocaleDateFormat
import com.rivaldofez.storymessage.util.LocationUtility
import com.rivaldofez.storymessage.util.MediaUtility
import com.rivaldofez.storymessage.util.wrapEspressoIdlingResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class MapsFragment : Fragment(), GoogleMap.InfoWindowAdapter {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var token: String = ""
    private val mapsViewModel: MapsViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getCurrentLocation()
            }
        }

    private fun setMapStyle(styleResource: Int) {
        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), styleResource))
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        getCurrentLocation()
        setMapStyle(R.raw.style_map_light)
        mMap.setInfoWindowAdapter(this)

        mMap.setOnInfoWindowClickListener { marker ->
            val data: StoryResponse = marker.tag as StoryResponse

            val goToDetailStory = MapsFragmentDirections.actionMapsFragmentToDetailStoryFragment()
            goToDetailStory.story = StoryEntity(
                id = data.id,
                name = data.name,
                description = data.description,
                createdAt = data.createdAt,
                photoUrl = data.photoUrl,
                lon = data.lon,
                lat = data.lat
            )

            findNavController().navigate(goToDetailStory)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mapsViewModel.getAuthenticationToken().collect { token ->
                wrapEspressoIdlingResource {
                if (!token.isNullOrEmpty()){
                    this@MapsFragment.token = token
                    setStoryMarker()
                    }
                }
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f))
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.activate_location),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun setStoryMarker() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                mapsViewModel.getStoriesWithLocation(token).collect { result ->
                    result.onSuccess { response ->
                        response.stories.forEach { story ->

                            if (story.lat != null && story.lon != null) {
                                val latLng = LatLng(story.lat, story.lon)

                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                )?.tag = story
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val bindingItemMapWindow = ItemMapWindowBinding.inflate(LayoutInflater.from(requireContext()))
        val data: StoryResponse = marker.tag as StoryResponse

        bindingItemMapWindow.tvLocation.text = LocationUtility.parseAddressLocation(requireContext(), marker.position.latitude, marker.position.longitude)
        bindingItemMapWindow.tvName.text = getString(R.string.detail_toolbar_title, data.name.lowercase().replaceFirstChar { it.titlecase() })
        bindingItemMapWindow.tvDescription.text = data.description
        bindingItemMapWindow.tvDate.setLocaleDateFormat(timestamp = data.createdAt)
        bindingItemMapWindow.imgStory.setImageBitmap(MediaUtility.bitmapFromURL(requireContext(), data.photoUrl))
        return bindingItemMapWindow.root
    }

}