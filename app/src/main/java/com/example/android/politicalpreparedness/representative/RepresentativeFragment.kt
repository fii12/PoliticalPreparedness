package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        //Constant for Location request
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 10
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this, RepresentativeViewModelFactory(requireActivity().application)).get(RepresentativeViewModel::class.java)
    }
    private lateinit var representativeListAdapter: RepresentativeListAdapter


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Establish bindings
        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        representativeListAdapter = RepresentativeListAdapter(RepresentativeListener {
        })


        binding.listRepresentatives.adapter = representativeListAdapter

        //Done: Populate Representative adapter
        viewModel.representativeList.observe(viewLifecycleOwner) {
            Log.e("TAG", "onCreateView: it:"+it.size)
            representativeListAdapter.representatives = it

        }
        binding.buttonSearch.setOnClickListener {
            val representativeAddress1 = binding.addressLine1.text
            val representativeAddress2 = binding.addressLine2.text
            val representativeState = binding.state.getItemAtPosition(binding.state.selectedItemPosition)
            val representativeCity = binding.city.text
            val representativeZip = binding.zip.text
            viewModel.findRepresentatives("$representativeAddress2 $representativeAddress1, $representativeCity, $representativeState, $representativeZip")
            hideKeyboard()
        }

        binding.buttonLocation.setOnClickListener {
//            if (checkLocationPermissions()) {
//                getLocation()
//            }
            getRepresentativesByLocation()
        }


        return binding.root
    }


    private fun getRepresentativesByLocation() {
        return if (isPermissionGranted()) {
            getLocation()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(requireView(), "error_location_services_required", Snackbar.LENGTH_LONG).show()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocation()
                }
            }
        }
    }



    private fun isPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)

        //Done: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            try {
                val address = geoCodeLocation(location)
                viewModel.setAddress(address)
            } catch (e: Exception) {
                Log.e("RepresentativeFragment", "getLocation: exception:" + e.toString())
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}