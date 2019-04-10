package com.centennial.donateblood.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.centennial.donateblood.R
import com.centennial.donateblood.extensions.affectOnItemClick
import com.centennial.donateblood.models.Request
import com.centennial.donateblood.utils.Constants
import com.centennial.donateblood.utils.RecyclerItemClickListener
import com.centennial.donateblood.utils.TimeAgo
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_list.view.*


class RequestListFragment : BaseFragment() {

    private lateinit var rootView: View
    private lateinit var requestAdapter: RequestFirestoreRecyclerAdapter

    private lateinit var firestore: FirebaseFirestore
    private lateinit var query: Query


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        activity!!.title = getString(R.string.donation_requests)
        rootView = inflater.inflate(R.layout.fragment_list, container, false)
        firestore = FirebaseFirestore.getInstance()
        query = firestore.collection(Constants.REQUEST_DATA_REF)//.orderBy("timestampCreated", Query.Direction.DESCENDING)

        requestAdapter = RequestFirestoreRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Request>().setQuery(
                query,
                Request::class.java
            ).build()
        )
        rootView.recyclerView.adapter = requestAdapter
        rootView.recyclerView.layoutManager = LinearLayoutManager(context)

        rootView.recyclerView.affectOnItemClick(object : RecyclerItemClickListener.OnClickListener {
            override fun onItemClick(position: Int, view: View) {
                showToast("Item positon: "+position+"itemID: "+requestAdapter.getItem(position).requestID, Toast.LENGTH_LONG)
            }
        })
        return rootView
    }



    override fun onStart() {
        super.onStart()
        requestAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        requestAdapter.stopListening()

    }


    private inner class RequestViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.tv_main)
        var subTitle: TextView = view.findViewById(R.id.tv_other)
        var midTitle: TextView = view.findViewById(R.id.tv_mid)



    }

    private inner class RequestFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Request>) :
        FirestoreRecyclerAdapter<Request, RequestViewHolder>(options) {
        override fun onBindViewHolder(requestViewHolder: RequestViewHolder, position: Int, request: Request) {

            try {
                requestViewHolder.title.text = request.orgName
                requestViewHolder.midTitle.text = "BloodGroup Required: " + Constants.BGArray[request.bloodGroup]
                requestViewHolder.subTitle.text =  TimeAgo().getTimeAgo(request.timestampCreated)

            } catch(e: Exception) {
                Log.e (TAG, "Error while loading data into viewHolder:"+e.localizedMessage)
                showToast("Some data is missing. Please try again",Toast.LENGTH_LONG)
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_request, parent, false)



            return RequestViewHolder(view)
        }
    }


    companion object {
        private val TAG = this::class.java.simpleName
    }
}
