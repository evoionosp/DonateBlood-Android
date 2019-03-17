package com.centennial.donateblood.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.centennial.donateblood.R


class HomeFragment : Fragment() {

    private lateinit var rootView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        return rootView
    }

    companion object {
        private  val TAG = this::class.java.simpleName
    }
}


