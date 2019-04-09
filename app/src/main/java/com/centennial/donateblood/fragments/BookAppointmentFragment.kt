package com.centennial.donateblood.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.centennial.donateblood.R


class BookAppointmentFragment : BaseFragment() {

    private lateinit var rootView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_testing, container, false)
        setHasOptionsMenu(true)

        return rootView
    }


    companion object {
        private  val TAG = this::class.java.simpleName
    }
}


