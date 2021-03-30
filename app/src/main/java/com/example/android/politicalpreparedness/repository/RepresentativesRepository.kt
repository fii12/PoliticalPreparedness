package com.example.android.politicalpreparedness.repository

import android.util.Log
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativesRepository {
    suspend fun getRepresentatives(address: String, includeOffices: Boolean, levels: String?, roles: String?): RepresentativeResponse {
        Log.e("RepresentativesRepository", "getRepresentatives: address:$address")
        return CivicsApi.retrofitService.getRepresentatives(address, includeOffices, levels, roles)
    }
}