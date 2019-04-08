package com.centennial.donateblood.models

data class Hospital(val userID: String = "") {

    var name: String = ""
    var address: String = ""
    var postalCode: String = ""
    var personName: String = ""
    var contactNumber: String = ""
    var contactEmail: String = ""
    var isBloodBank: Boolean = false


    companion object {
        private const val TAG = "Hospital"
    }

}