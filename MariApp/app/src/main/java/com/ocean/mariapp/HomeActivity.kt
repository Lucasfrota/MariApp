package com.ocean.mariapp

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ocean.mariapp.Adapter.CustomAdapter
import com.ocean.mariapp.Adapter.Item
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class HomeActivity : Fragment() {

    var pref: SharedPreferences? = null

    private var recyclerView: RecyclerView? = null
    private var fab: FloatingActionButton? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_home, container, false)

        pref = this.activity.getSharedPreferences("urlinf", 0)

        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        fab = view.findViewById(R.id.fab) as FloatingActionButton


        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(activity)

        getParentes(activity)

        fab!!.setOnClickListener {
            val it = Intent(activity, AdicionarActivity::class.java)
            startActivity(it)
        }

        return view
    }

    fun getParentes(context: Context){

        val URL = pref!!.getString("url", "")

        var items = ArrayList<Item>()

        var json: JSONArray

        val str_req = object : StringRequest(Request.Method.POST,  URL + "/mariapp/default/lista_parentes",
                Response.Listener<String> { response ->
                    try {

                        json = JSONArray(response.trim())

                        for( i in 0..json.length()-1){

                            val nome = json.getJSONObject(i).getString("nome_parente")
                            val dataNascimento = getData(json.getJSONObject(i).getString("data_nascimento"))
                            val geracao = json.getJSONObject(i).getString("geracao")
                            val id = json.getJSONObject(i).getString("id")

                            items.add(Item(nome, dataNascimento , geracao, id))
                        }

                        var adapter = CustomAdapter(items, activity)

                        recyclerView!!.adapter = adapter



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

    fun getData(data: String): String{

        var saida = data.substring(8,10) + "/" + data.substring(5,7) + "/" + data.substring(0,4)

        return saida
    }
}
