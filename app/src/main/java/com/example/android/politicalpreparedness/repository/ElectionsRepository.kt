package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepository(private val database: ElectionDatabase) {

    suspend fun getUpcomingElections(): List<Election> {
        return CivicsApi.retrofitService.getElections().elections
    }

    suspend fun getSavedElections() : List<Election>? {

        var savedElections:List<Election>? = null

        withContext(Dispatchers.IO) {
            savedElections = database.electionDao.getElections()
        }

        return savedElections
    }

    suspend fun saveElection(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.insert(election)
        }
    }
    suspend fun deleteElection(electionId: Int) {
        withContext(Dispatchers.IO) {
            database.electionDao.deleteElection(electionId)
        }
    }

    suspend fun getVoterInfo(electionId: Int, division: Division): VoterInfoResponse{
        val address = """${division.country}/${division.state}"""
        return CivicsApi.retrofitService.getVoterInformation(address,electionId,false)
    }


}