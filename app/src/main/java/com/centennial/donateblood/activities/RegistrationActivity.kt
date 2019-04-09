package com.centennial.donateblood.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation
import com.centennial.donateblood.R
import com.centennial.donateblood.models.User
import com.centennial.donateblood.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.common.collect.Range
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_registration.*
import org.joda.time.DateTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class RegistrationActivity: BaseActivity() {

    private lateinit var myCalendar: Calendar
    private var firebaseUser: FirebaseUser? = null

    private lateinit var mValidation: AwesomeValidation

    private var isEdit = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.centennial.donateblood.R.layout.activity_registration)
        title = getString(com.centennial.donateblood.R.string.registration)
        myCalendar = Calendar.getInstance()
        mValidation = AwesomeValidation(ValidationStyle.BASIC)

        auth = FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        userDBRef = firestore.collection(Constants.Companion.USER_DATA_REF)
        firebaseUser = auth.currentUser


            if(!intent.getBooleanExtra("isEdit", true)){
                isEdit = false
                switchEditMode(isEdit)

            } else {
                Log.e(TAG, "isEdit: True")
                redirectTo(firebaseUser, userDBRef, this)

            }




        tvID.text =  getString(R.string.title_reg_id) + firebaseUser!!.email
        addValidations(this)

        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar.time)
        }

        etDOB.setOnClickListener {
            // TODO Auto-generated method stub
            if (etDOB.isEnabled)
                DatePickerDialog(
                    this@RegistrationActivity, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
        }
    }

    private fun updateLabel(date: Date) {
        val myFormat = "MM/dd/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        etDOB.setText(sdf.format(date))
    }


    fun addValidations(activity: Activity){
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etFirstname, "[a-zA-Z]+", com.centennial.donateblood.R.string.err_valid_name)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etLastname, "[a-zA-Z]+", com.centennial.donateblood.R.string.err_valid_name)
  //      mValidation.addValidation(activity, com.centennial.donateblood.R.id.etDonorNum, Patterns.EMAIL_ADDRESS, com.centennial.donateblood.R.string.err_valid_email)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etMobile, Patterns.PHONE, com.centennial.donateblood.R.string.err_valid_tel)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etPostalCode, "[A-Z][0-9][A-Z] [0-9][A-Z][0-9]", com.centennial.donateblood.R.string.err_valid_email)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etWeight, Range.atLeast(110), com.centennial.donateblood.R.string.err_not_eligible)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etDOB,
            SimpleCustomValidation { input ->
                try {
                    val calendarBirthday = Calendar.getInstance()
                    val calendarToday = Calendar.getInstance()
                    calendarBirthday.time = SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(input)
                    val yearOfToday = calendarToday.get(Calendar.YEAR)
                    val yearOfBirthday = calendarBirthday.get(Calendar.YEAR)
                    if (yearOfToday - yearOfBirthday > 17) {
                        return@SimpleCustomValidation true
                    } else if (yearOfToday - yearOfBirthday == 17) {
                        val monthOfToday = calendarToday.get(Calendar.MONTH)
                        val monthOfBirthday = calendarBirthday.get(Calendar.MONTH)
                        if (monthOfToday > monthOfBirthday) {
                            return@SimpleCustomValidation true
                        } else if (monthOfToday == monthOfBirthday) {
                            if (calendarToday.get(Calendar.DAY_OF_MONTH) >= calendarBirthday.get(Calendar.DAY_OF_MONTH)) {
                                return@SimpleCustomValidation true
                            }
                        }
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                false
            }, com.centennial.donateblood.R.string.err_valid_dob
        )
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.spBloodgroup,
            { validationHolder ->
                (validationHolder.view as Spinner).selectedItem.toString() != "BloodGroup"
            }, { validationHolder ->
                val textViewError = (validationHolder.view as Spinner).selectedView as TextView
                textViewError.error = validationHolder.errMsg
                textViewError.setTextColor(Color.RED)
            }, { validationHolder ->
                val textViewError = (validationHolder.view as Spinner).selectedView as TextView
                textViewError.error = null
                textViewError.setTextColor(Color.BLACK)
            }, com.centennial.donateblood.R.string.err_select_spinner
        )

    }

    fun submitForm() {
       if (mValidation.validate()){
           if(checkEligibility()) {
               showProgressDialog()
               if(firebaseUser != null){

                   val user = User(firebaseUser!!.uid)
                   user.birthDate = myCalendar.time
                   user.bloodGroup = spBloodgroup.selectedItemPosition
                   user.donorNumber = etDonorNum.text.toString()
                   user.firstName = etFirstname.text.toString()
                   user.lastName = etLastname.text.toString()
                   user.isMale = rb_male.isChecked
                   user.emailID = firebaseUser!!.email!!
                   user.phoneNumber = etMobile.rawText.toString()
                   user.weight = etWeight.rawText!!.toInt()
                   user.postalCode = etPostalCode.text.toString()
                   user.isEligible = true
                   user.lastDonationDate = DateTime.now().minusYears(1).toDate()

                   userDBRef.document(user.UID).set(user)
                       .addOnSuccessListener {
                           Log.d(TAG, "DocumentSnapshot successfully written!")
                           startActivity(Intent(this, MainActivity::class.java))
                           finish()
                       }
                       .addOnFailureListener {
                               e -> Log.e(TAG, "Error writing document", e)
                           Snackbar.make(llCheckbox, "Failed to store data. Make sure you're connected to internet and try again !", Snackbar.LENGTH_LONG).show()
                           hideProgressDialog()
                       }

               } else {
                   startActivity(Intent(this, LoginActivity::class.java))
                   finish()
               }
           } else {
               hideProgressDialog()
               AlertDialog.Builder(this)
                   .setTitle(getString(com.centennial.donateblood.R.string.title_check_eligibility))
                   .setMessage(getString(com.centennial.donateblood.R.string.err_not_eligible))
                   .setNeutralButton(R.string.ok){_,_ ->
                       //dismiss
                   }
                   // Specifying a listener allows you to take an action before dismissing the dialog.
                   // The dialog is automatically dismissed when a dialog button is clicked.
                   .setIcon(android.R.drawable.ic_dialog_alert)
                   .show()
           }
       }
    }


    private fun switchEditMode(enable: Boolean) {



        etDOB.isEnabled = enable
        spBloodgroup.isEnabled = enable
        etDonorNum.isEnabled = enable
        etFirstname.isEnabled = enable
        etLastname.isEnabled = enable
        rb_male.isEnabled = enable
        rg_sex.isEnabled = enable
        etMobile.isEnabled = enable
        etWeight.isEnabled = enable
        etPostalCode.isEnabled = enable


        userDBRef.document(firebaseUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    var user = document.toObject(User::class.java)
                    if(user != null) setData(user)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to fetch User data:" +it.stackTrace)
                showToast("Error: Please try again", Toast.LENGTH_SHORT)
                redirectTo(firebaseUser, userDBRef, this)
            }

        invalidateOptionsMenu()
    }


    private fun setData (user: User) {

        myCalendar.time = user.birthDate
        updateLabel(myCalendar.time)

        spBloodgroup.setSelection(user.bloodGroup)
        etDonorNum.setText(user.donorNumber)
        etFirstname.setText(user.firstName)
        etLastname.setText(user.lastName)
        rb_male.isChecked = user.isMale
        user.isMale = rb_male.isChecked
        etMobile.setText(user.phoneNumber)
        etWeight.setText(""+user.weight)
        etPostalCode.setText(user.postalCode)



        llCheckbox.visibility = View.GONE
//        cbAge.visibility = View.GONE
//        cbBaby.visibility = View.GONE
//        cbMedicine.visibility = View.GONE
//        cbSurgery.visibility = View.GONE
//        cbUK.visibility = View.GONE
//        cbUS.visibility = View.GONE
//        cbWeight.visibility = View.GONE
//        cbTattoo.visibility = View.GONE



    }

    fun checkEligibility(): Boolean {

        return if (llCheckbox.visibility == View.GONE) true
        else cbAge.isChecked && cbBaby.isChecked && cbMedicine.isChecked && cbSurgery.isChecked && cbUK.isChecked && cbUS.isChecked && cbWeight.isChecked && cbTattoo.isChecked

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(com.centennial.donateblood.R.menu.registration_menu, menu)

        var menuEdit: MenuItem =  menu.findItem(R.id.regEdit)
        var menuSubmit: MenuItem = menu.findItem(R.id.regSubmit)

        menuEdit.isVisible = !isEdit
        menuSubmit.isVisible = isEdit

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.centennial.donateblood.R.id.regSubmit -> {
                submitForm()
            }

            R.id.regEdit -> {
                isEdit = true
                switchEditMode(isEdit)
            }
        }
        return true
    }

    companion object {
        private  val TAG = this::class.java.simpleName
    }
}