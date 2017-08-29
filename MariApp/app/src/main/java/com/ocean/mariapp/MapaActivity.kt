package com.ocean.mariapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class MapaActivity : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    var pref: SharedPreferences? = null

    val root = FirebaseDatabase.getInstance().reference

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.activity_mapa, container, false)

        pref = this.activity.getSharedPreferences("urlinf", 0)

        getIdParentes(activity)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    fun getIdParentes(context: Context){

        val URL = pref!!.getString("url", "")

        var json: JSONArray

        val str_req = object : StringRequest(Request.Method.POST,  URL + "/mariapp/default/lista_parentes",
                Response.Listener<String> { response ->
                    try {

                        json = JSONArray(response.trim())

                        for( i in 0..json.length()-1){

                            val id = json.getJSONObject(i).getString("id")

                            root.child(id).addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    try {
                                        val latLon = LatLng(dataSnapshot.child("lat").getValue<Double>(Double::class.java) , dataSnapshot.child("lon").getValue<Double>(Double::class.java))
                                        mMap!!.addMarker(MarkerOptions().position(latLon).title(dataSnapshot.child("nome").value.toString()))
                                    }catch (e: Exception){

                                    }


                                }

                                override fun onCancelled(databaseError: DatabaseError) {

                                }
                            })

                        }

                    } catch (e: JSONException) {

                    }
                },
                Response.ErrorListener { error ->
                    try {
                        Toast.makeText(context, resources.getString(R.string.algo_errado) , Toast.LENGTH_LONG).show()
                    }catch (e: Exception){

                    }

                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(str_req)
    }
}
