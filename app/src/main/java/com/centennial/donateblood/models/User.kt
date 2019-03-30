package com.centennial.donateblood.models

import java.util.*


data class User (val UID: String = ""){



    var firstName: String = ""
    var lastName: String = ""
    var donorNumber: String = ""
    var emailID: String = ""
    var phoneNumber: String = ""
    var bloodGroup: Int = 0
    var birthDate: Date = Date()
    var weight: Int = 0
    var postalCode: String = ""
    var isMale: Boolean = false
    var isEligible: Boolean = false
    var lastDonationDate: Date = Date()

    companion object {
        private const val TAG = "User"
    }

}