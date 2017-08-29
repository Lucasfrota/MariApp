package com.ocean.mariapp

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu

class MainActivity : AppCompatActivity() {


    var pref: SharedPreferences? = null

    var homeactivity: HomeActivity? = null

    internal var menu: Menu? = null

    var estaEmHome = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeactivity = HomeActivity()
        var manager = getFragmentManager()
        manager.beginTransaction().replace(R.id.content,
                homeactivity).commit()

        estaEmHome = true


        pref = this.getSharedPreferences("urlinf", 0)


        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
            }

        }
        if (ActivityCompat.checkSelfPermission(application, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)



        }

        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                homeactivity = HomeActivity()
                var manager = getFragmentManager()
                manager.beginTransaction().replace(R.id.content,
                        homeactivity).commit()
                estaEmHome = true
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                var mapa = MapaActivity()
                var manager = getSupportFragmentManager()
                manager.beginTransaction().replace(R.id.content, mapa).commit()
                estaEmHome = false
                return@OnNavigationItemSelectedListener true
            }
            R.id.config -> {
                var config = ConfiguracoesActivity()
                var manager = getFragmentManager()
                manager.beginTransaction().replace(R.id.content,
                        config).commit()
                estaEmHome = false
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

}
