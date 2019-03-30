package com.centennial.donateblood.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class Request(val RequestID: String = "") {

    var orgName: String = ""
    var orgAddress: String = ""
    var orgPostalCode: String = ""
    var personName: String = ""
    var personContact: String = ""
    var bloodGroup: Int = 0
    var units: Int = 0
    @ServerTimestamp
    var timestampCreated: Date = Date()


    companion object {
        private const val TAG = "Request"
    }

}

