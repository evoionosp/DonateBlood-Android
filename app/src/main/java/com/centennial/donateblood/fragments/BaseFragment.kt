package com.centennial.donateblood.fragments


import android.app.ProgressDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import com.centennial.donateblood.R

open class BaseFragment : Fragment() {



    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(activity)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun showToast(txt: String, duration: Int){
        Toast.makeText(activity, txt, duration).show()
    }

    fun hideKeyboard(view: View) {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }



    companion object {
        private val TAG = this::class.java.simpleName
    }
}