package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class ElectionsViewModel(application: Application) : ViewModel() {
    private val database = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionsRepository(database)

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>> get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>> get() = _savedElections

    init {
        getUpcomingElections()
        getSavedElections()
    }

    fun getUpcomingElections() {
        viewModelScope.launch {
            try {
                val elections = electionsRepository.getUpcomingElections()
                _upcomingElections.value = elections
            } catch (e: Exception) {
                Log.e("ElectionsViewModel", "getUpcomingElections: exception:$e")
            }
        }
    }


    fun getSavedElections() {
        viewModelScope.launch {
            try {
//                val savedElectionsList = database.electionDao.getElections()
                val savedElectionsList = electionsRepository.getSavedElections()
                if (savedElectionsList != null) {
                    _savedElections.value = savedElectionsList
                }
            } catch (e: Exception) {
                Log.e("ElectionsViewModel", "getSavedElections: exception:$e")
            }
        }
    }

    fun onUpcomingElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun onSavedElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun onDoneNavigationToVoterInfo() {
        _navigateToVoterInfo.value = null
    }
}