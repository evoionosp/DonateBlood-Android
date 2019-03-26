package com.centennial.donateblood.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.centennial.donateblood.R
import com.centennial.donateblood.adapters.RequestAdapter
import com.centennial.donateblood.models.Request
import com.centennial.donateblood.utils.Constants
import com.centennial.donateblood.utils.RecyclerItemClickListener
import java.util.*


class RequestListFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter
    private lateinit var requestList: ArrayList<Request>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        rootView = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = rootView.findViewById(R.id.recycler_view)

        requestList = ArrayList()

        requestList.add(Request("123"))

        for (i in 1..10){
            var request = Request("IDisRequest"+i)
            request.orgName = "Name"+i
            request.bloodGroup = Constants.BG_A_POSITIVE+i-1
            request.orgAddress = "tmpaddress+1"
            request.orgPostalCode = "M"+i+"G"+(i+1)+"J"+(i+2)
            request.personContact = "6478046665"
            request.personName = "Shubh Patel"

            requestList.add(request)
        }








        requestAdapter = RequestAdapter(context!!, requestList)
        recyclerView.adapter = requestAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        Log.i(TAG, "Size of request list: "+requestList.size)

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(context!!, object : RecyclerItemClickListener.OnItemClickListener{
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


    companion object {
        private  val TAG = this::class.java.simpleName
    }
}
