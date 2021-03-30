package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Division

class VoterInfoFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(this, VoterInfoViewModelFactory(requireActivity().application)).get(VoterInfoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val voterInfoArgs = arguments?.let { VoterInfoFragmentArgs.fromBundle(it) }
        val electionId: Int = voterInfoArgs?.argElectionId ?: 0
        val division: Division = voterInfoArgs?.argDivision!!

        viewModel.getVoterInfo(electionId, division)

        viewModel.url.observe(viewLifecycleOwner, Observer {
            loadUrl(it)
        })


        return binding.root

    }

    //method to load URL intents
    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}