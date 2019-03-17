package com.centennial.donateblood.utils

import java.util.*


data class User (val UID: String = ""){

    var firstName: String = ""
    var lastName: String = ""
    var emailId: String = ""
    var phoneNumber: String = ""
    var bloodGroup: Int = 0
    var birthDate: Date = Date()
    var weight: Int = 0
    var postalCode: String = ""
    var isMale: Boolean = false
    var isEligible: Boolean = false

    companion object {
        private const val TAG = "User"
    }

}