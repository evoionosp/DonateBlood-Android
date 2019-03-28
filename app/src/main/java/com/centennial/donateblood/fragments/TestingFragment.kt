package com.centennial.donateblood.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.centennial.donateblood.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_testing.*


class TestingFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var requestDB: FirebaseFirestore
    private lateinit var requestDBRef: CollectionReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_testing, container, false)
        setHasOptionsMenu(true)

        btnSendReq.setOnClickListener {
            var bgId = spBloodgroup.selectedItemPosition
            var units =  testUnits.text.toString().toInt()
            var postalCode = testPostal.text.toString()
        }

        return rootView
    }

    private fun sendRequest(bg: Int, units: Int, postalCode: String){

    }

    companion object {
        private  val TAG = this::class.java.simpleName
    }
}


