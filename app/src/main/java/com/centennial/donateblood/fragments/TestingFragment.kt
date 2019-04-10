package com.centennial.donateblood.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.centennial.donateblood.R
import com.centennial.donateblood.models.Request
import com.centennial.donateblood.utils.Constants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_testing.view.*


class TestingFragment : BaseFragment() {

    private lateinit var rootView: View
    private lateinit var firestore: FirebaseFirestore
    private lateinit var requestDBRef: CollectionReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_testing, container, false)
        firestore= FirebaseFirestore.getInstance()
        requestDBRef = firestore.collection(Constants.REQUEST_DATA_REF)
        setHasOptionsMenu(true)


        rootView.btnSendReq.setOnClickListener {
            val bgId = rootView.spBloodgroup.selectedItemPosition
            val units =  rootView.testUnits.text.toString().toInt()
            val postalCode = rootView.testPostal.text.toString()

            sendRequest(bgId, units, postalCode)
        }

        return rootView
    }

    private fun sendRequest(bg: Int, units: Int, postalCode: String){







        val request = Request(requestDBRef.document().id)
        request.orgName = postalCode
        request.bloodGroup = bg
        request.orgAddress = "tmp address"
        request.orgPostalCode = "M1G 3S8"
        request.contactNumber = "6478046665"
        request.personName = "Shubh Patel"
        request.units = units
        request.responds.put("user_${request.responds.size}", "shubh@gmail.com")
        request.responds.put("user_${request.responds.size}", "diego@gmail.com")


        requestDBRef.document(request.requestID).set(request)
            .addOnSuccessListener {
                Log.d(TAG, "Request DocumentSnapshot successfully written!")
                showToast("Request: data stored",Toast.LENGTH_LONG)
            }
            .addOnFailureListener {
                    e -> Log.e(TAG, "Error writing request document", e)
                showToast("Request: Failed to store data",Toast.LENGTH_LONG)

            }

    }

    companion object {
        private  val TAG = this::class.java.simpleName
    }
}


