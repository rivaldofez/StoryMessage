package com.rivaldofez.storymessage.page.maps

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.data.local.entity.StoryEntity
import com.rivaldofez.storymessage.data.remote.response.StoryResponse
import com.rivaldofez.storymessage.databinding.FragmentMapsBinding
import com.rivaldofez.storymessage.databinding.ItemMapWindowBinding
import com.rivaldofez.storymessage.extension.setLocaleDateFormat
import com.rivaldofez.storymessage.util.LocationUtility
import com.rivaldofez.storymessage.util.MediaUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.StringBuilder

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

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        getCurrentLocation()
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

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mapsViewModel.getAuthenticationToken().collect { token ->
                if (!token.isNullOrEmpty()){
                    this@MapsFragment.token = token

                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
                    setStoryMarker()
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
                    Toast.makeText(requireContext(), "Aktifkan Lokasi", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun setStoryMarker() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                mapsViewModel.getStories(token).collect { result ->
                    result.onSuccess { response ->
                        response.stories.forEach { story ->

                            // Verify that latitude and longitude field not null
                            // Create marker on the map
                            if (story.lat != null && story.lon != null) {
                                val latLng = LatLng(story.lat, story.lon)

                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(story.name)
                                        .snippet("Lat: ${story.lat}, Lon: ${story.lon}")
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
        bindingItemMapWindow.tvName.text = StringBuilder("Story by ").append(data.name)
        bindingItemMapWindow.tvDescription.text = data.description
        bindingItemMapWindow.tvDate.setLocaleDateFormat(timestamp = data.createdAt)
        bindingItemMapWindow.imgStory.setImageBitmap(MediaUtility.bitmapFromURL(requireContext(), data.photoUrl))
        return bindingItemMapWindow.root
    }

}