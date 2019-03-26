package com.centennial.donateblood.models

import com.google.firebase.firestore.FieldValue


data class Request (val RequestID: String = ""){

    var orgName: String = ""
    var orgAddress: String = ""
    var orgPostalCode: String = ""
    var personName: String = ""
    var personContact: String = ""
    var bloodGroup: Int = 0
    var timestampCreated = FieldValue.serverTimestamp()


    companion object {
        private const val TAG = "Request"
    }

}

