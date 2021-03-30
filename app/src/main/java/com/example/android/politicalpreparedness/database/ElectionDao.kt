package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(elections: Election)

    //select all election query
    @Query("SELECT * FROM election_table")
    fun getElections(): List<Election>

    //single election query
    @Query("SELECT * FROM election_table WHERE id = :id")
    suspend fun getElection(id: Int): Election

    //delete query
    @Query("DELETE FROM election_table WHERE id = :id")
    fun deleteElection(id: Int)

    //clear query
    @Query("DELETE FROM election_table")
    fun clear()
}