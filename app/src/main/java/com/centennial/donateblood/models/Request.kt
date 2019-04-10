package com.centennial.donateblood.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.HashMap


data class Request(var requestID: String = "") {

    var orgName: String = ""
    var orgAddress: String = ""
    var orgPostalCode: String = ""
    var personName: String = ""
    var contactNumber: String = ""
    var contactEmail: String = ""
    var hospitalID: String = ""
    var bloodGroup: Int = 0
    var units: Int = 0
    var responds: HashMap<String, String> = HashMap()
    @ServerTimestamp
    var timestampCreated: Date = Date()


    companion object {

        private const val TAG = "Request"
    }
}

