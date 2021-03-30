package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.*
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val application: Application) : ViewModel() {
    private val database = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionsRepository(database)

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election


    private val _voterInfoElection = MutableLiveData<Election>()
    val voterInfoElection: LiveData<Election>
        get() = _voterInfoElection

    //Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    val state : LiveData<State> =
            Transformations.map(voterInfo) { it.state?.get(0) }

    val administrationBody: LiveData<AdministrationBody> =
            Transformations.map(state) { it.electionAdministrationBody }

    private val _isFollowed = MutableLiveData<Boolean>(false)
    val isFollowed: LiveData<Boolean>
        get() = _isFollowed

    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url



    fun followElection() {
        viewModelScope.launch {
            _voterInfo.value?.let {
                if (_isFollowed.value == true) {
                    // Delete the Election and set the flag to false
                    electionsRepository.deleteElection(it.election.id)
                    _isFollowed.value = false
                } else {
                    // Save the Election and set the flag to true
                    electionsRepository.saveElection(it.election)
                    _isFollowed.value = true
                }
            }
        }
    }

    fun getVoterInfo(electionId: Int, division: Division){
        viewModelScope.launch {
            val election: Election? = database.electionDao.getElection(electionId)
            _election.value = election
            _isFollowed.value = election != null
//            Log.e("TAG", "getVoterInfo: electionId:"+electionId+", division: "+division)
            val voterInfoResponse = electionsRepository.getVoterInfo(electionId,division)
//            Log.e("TAG", "getVoterInfo: "+voterInfoResponse.toString())
            isFollowed.value?.let {
                _election.value = voterInfoResponse.election
            }
            _voterInfo.value = voterInfoResponse
        }
    }

    fun navigateUrl(url: String) {
        Log.e("TAG", "navigateUrl: "+url)
        _url.value = url
    }


}