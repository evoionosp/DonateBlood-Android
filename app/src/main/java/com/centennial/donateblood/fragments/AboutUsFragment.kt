package com.centennial.donateblood.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.centennial.donateblood.R
import kotlinx.android.synthetic.main.fragment_about.view.*


class AboutUsFragment : BaseFragment() {

    private lateinit var rootView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_about, container, false)
        setHasOptionsMenu(true)
        activity!!.title = getString(R.string.about_us)

        rootView.cardShubh.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+getString(R.string.shubh_email)))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Blood Donate")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Sent from Blood Donate App")
                startActivity(Intent.createChooser(emailIntent, "Send Email to Developer"))
            }

        rootView.cardDiego.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+getString(R.string.diego_email)))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Blood Donate")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Sent from Blood Donate App")
            startActivity(Intent.createChooser(emailIntent, "Send Email to Developer"))
        }

        rootView.cardForum.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+getString(R.string.forum_email)))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Blood Donate")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Sent from Blood Donate App")
            startActivity(Intent.createChooser(emailIntent, "Send Email to Developer"))
        }

        return rootView
    }


    companion object {
        private  val TAG = this::class.java.simpleName
    }
}


