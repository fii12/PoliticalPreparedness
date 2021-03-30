package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.RepresentativesRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(application: Application) : ViewModel() {
    private val representativesRepository = RepresentativesRepository()
    private var _representatives = MutableLiveData<List<Representative>>()
    val representativeList: LiveData<List<Representative>>
        get() = _representatives
    private var _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    fun findRepresentatives(address: String) {
        viewModelScope.launch {
            try {
                val (offices, officials) =
                        representativesRepository.getRepresentatives(address,true, null, null)
                _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
            } catch (e: Exception) {
                Log.e("Representative", e.localizedMessage)
            }
        }
    }

    fun setAddress(address: Address) {
        _address.value = address
    }

}
