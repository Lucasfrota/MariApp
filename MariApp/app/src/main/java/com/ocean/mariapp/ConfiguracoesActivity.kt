package com.ocean.mariapp

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ConfiguracoesActivity : Fragment() {

    var endereco: TextView? = null
    var salvar: Button? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.activity_configuracoes, container, false)

        endereco = view.findViewById(R.id.endereco) as TextView
        salvar = view.findViewById(R.id.salvar) as Button

        val pref = this.activity.getSharedPreferences("urlinf", 0)

        endereco!!.text = pref.getString("url", "")

        salvar!!.setOnClickListener {
            val editor = pref!!.edit()
            editor.putString("url", endereco!!.text.toString())
            editor.apply()
            Toast.makeText(activity, resources.getString(R.string.endereco_salvo) , Toast.LENGTH_LONG).show()
        }

        return view
    }
}
