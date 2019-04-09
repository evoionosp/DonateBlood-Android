package com.centennial.donateblood.utils

class Constants {

    companion object {

        //Firestore Ref Paths
        const val USER_DATA_REF: String = "USER_DB"
        const val HOSPITAL_DATA_REF: String = "HOSPITAL_DB"
        const val REQUEST_DATA_REF: String = "REQUEST_DB"

        const val FCM_DEFAULT: String = "GLOBAL"

        //Blood groups constants
        const val BG_A_POSITIVE: Int = 1
        const val BG_A_NEGATIVE: Int = 2
        const val BG_B_POSITIVE: Int = 3
        const val BG_B_NEGATIVE: Int = 4
        const val BG_O_POSITIVE: Int = 5
        const val BG_O_NEGATIVE: Int = 6
        const val BG_AB_POSITIVE: Int = 7
        const val BG_AB_NEGATIVE: Int = 8
        const val RARE_TYPE: Int = 9

        val BGArray = arrayListOf<String>("NOT_SELECTED","A+","A-","B+","B-","O+","O-","AB+","AB-","RARE TYPE")



    }
}