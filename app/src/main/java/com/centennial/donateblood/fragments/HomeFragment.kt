package com.centennial.donateblood.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.centennial.donateblood.R
import com.centennial.donateblood.activities.MapsActivity
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    private lateinit var rootView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        rootView.btnMap.setOnClickListener {
            startActivity(Intent(activity, MapsActivity::class.java))
        }

        return rootView
    }
}// Required empty public constructor
// public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
