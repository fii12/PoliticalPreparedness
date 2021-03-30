package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment : Fragment() {
    //Declare ViewModel
    private val viewModel: ElectionsViewModel by lazy {
        ViewModelProvider(this, ElectionsViewModelFactory(requireActivity().application)).get(ElectionsViewModel::class.java)
    }

    private lateinit var upcomingElectionsAdapter: ElectionListAdapter
    private lateinit var savedElectionsAdapter: ElectionListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //binding values
        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.electionViewModel = viewModel

        upcomingElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onUpcomingElectionClicked(election)
        })
        binding.upcomingList.adapter = upcomingElectionsAdapter

        savedElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onSavedElectionClicked(election)
        })

        binding.savedElections.adapter = savedElectionsAdapter

        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer<List<Election>> { upcomingElections ->
            upcomingElections?.apply {
                upcomingElectionsAdapter.elections = upcomingElections
            }
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer<List<Election>> { savedElections ->
            savedElections?.apply {
                savedElectionsAdapter.elections = savedElections
            }
        })

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner) { election ->
            election?.let {
                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election.id, election.division))
                viewModel.onDoneNavigationToVoterInfo()
            }
        }

        return binding.root
    }

    // Refresh adapters when fragment loads
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUpcomingElections()
        viewModel.getSavedElections()
    }

}