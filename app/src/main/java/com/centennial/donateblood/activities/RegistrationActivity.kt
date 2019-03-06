package com.centennial.donateblood.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.Spinner
import android.widget.TextView
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation
import com.centennial.donateblood.R
import com.google.common.collect.Range
import kotlinx.android.synthetic.main.activity_registration.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class RegistrationActivity: BaseActivity() {

    private lateinit var myCalendar: Calendar

    private lateinit var mValidation: AwesomeValidation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        title = getString(com.centennial.donateblood.R.string.registration)
        myCalendar = Calendar.getInstance()
        mValidation = AwesomeValidation(ValidationStyle.BASIC)
        addValidations(this)

        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
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

    private fun updateLabel() {
        val myFormat = "MM/dd/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        etDOB.setText(sdf.format(myCalendar.time))
    }


    fun addValidations(activity: Activity){
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etFirstname, "[a-zA-Z]+", com.centennial.donateblood.R.string.err_valid_name)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etLastname, "[a-zA-Z]+", com.centennial.donateblood.R.string.err_valid_name)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etEmail, Patterns.EMAIL_ADDRESS, com.centennial.donateblood.R.string.err_valid_email)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etMobile, Patterns.PHONE, com.centennial.donateblood.R.string.err_valid_tel)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etPostalCode, "[A-Z][0-9][A-Z] [0-9][A-Z][0-9]", com.centennial.donateblood.R.string.err_valid_email)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etWeight, Range.atLeast(110), com.centennial.donateblood.R.string.err_not_eligible)
        mValidation.addValidation(activity, com.centennial.donateblood.R.id.etDOB,
            SimpleCustomValidation { input ->
                try {
                    val calendarBirthday = Calendar.getInstance()
                    val calendarToday = Calendar.getInstance()
                    calendarBirthday.time = SimpleDateFormat("mm/dd/yyyy", Locale.US).parse(input)
                    val yearOfToday = calendarToday.get(Calendar.YEAR)
                    val yearOfBirthday = calendarBirthday.get(Calendar.YEAR)
                    if (yearOfToday - yearOfBirthday > 18) {
                        return@SimpleCustomValidation true
                    } else if (yearOfToday - yearOfBirthday == 18) {
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
            }, R.string.err_valid_dob
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
           // TODO: Submit data to firebase
       }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(com.centennial.donateblood.R.menu.registration_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.regSubmit -> {
                submitForm()
            }
        }
        return true
    }

    companion object {
        private const val TAG = "RegistrationActivity"
    }
}