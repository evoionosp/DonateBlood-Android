package com.centennial.donateblood.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.centennial.donateblood.R
import com.centennial.donateblood.models.Request
import com.centennial.donateblood.utils.Constants
import com.centennial.donateblood.utils.RecyclerItemClickListener
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
        rootView = inflater.inflate(R.layout.fragment_list, container, false)
        firestore = FirebaseFirestore.getInstance()
        query = firestore.collection(Constants.REQUEST_DATA_REF).orderBy("timestampCreated", Query.Direction.DESCENDING)

        requestAdapter = RequestFirestoreRecyclerAdapter(
            FirestoreRecyclerOptions.Builder<Request>().setQuery(
                query,
                Request::class.java
            ).build()
        )
        rootView.recyclerView.adapter = requestAdapter
        rootView.recyclerView.layoutManager = LinearLayoutManager(context)



        rootView.recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(context!!, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {


//                    Log.e("MainActivity", "RecyclerView Pos: $position")
//                    val intent = Intent(getActivity(), RequestDetailsActivity::class.java)
//                    intent.putExtra("id", requestList[position].getId())
//                    startActivity(intent)
//                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }
            })
        )
        return rootView
    }


    /*   override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
           inflater.inflate(R.menu.search, menu)
           val search = menu.findItem(R.id.search).actionView as SearchView
           search.setOnQueryTextListener(object : SearchView.OnQueryTextListener() {
               fun onQueryTextSubmit(query: String): Boolean {
                   //search(query);
                   return false
               }

               fun onQueryTextChange(newText: String): Boolean {
                   search(newText)
                   return true
               }
           })
       } */

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

            requestViewHolder.title.text = request.orgName
            requestViewHolder.midTitle.text = "BloodGroup Required: " + Constants.BGArray[request.bloodGroup]
            requestViewHolder.subTitle.text = request.orgAddress + ", " + request.orgPostalCode
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
