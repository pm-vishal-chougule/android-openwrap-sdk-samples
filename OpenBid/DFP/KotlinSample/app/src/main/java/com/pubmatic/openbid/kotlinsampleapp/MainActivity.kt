package com.pubmatic.openbid.kotlinsampleapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_home.*


class MainActivity : AppCompatActivity()  {

    var recycler: RecyclerView ? = null
    var list: ArrayList<AdType> ? = null

    companion object {

        private val TAG = "MainActivity"
        private val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
            }
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        recycler = findViewById(R.id.recycler_view)
        recycler?.layoutManager = LinearLayoutManager(this)
        recycler?.setHasFixedSize(true)

        list = java.util.ArrayList()
        list?.addAll(AdType.values())

        val recyclerAdapter = RecyclerAdapter(list, RecyclerItemListener())
        recycler?.adapter = recyclerAdapter

        // Ask permission from user for location and write external storage
        if (!hasPermissions(this@MainActivity, *PERMISSIONS)) {
            val MULTIPLE_PERMISSIONS_REQUEST_CODE = 123
            ActivityCompat.requestPermissions(this@MainActivity, PERMISSIONS, MULTIPLE_PERMISSIONS_REQUEST_CODE)
        }

    }

    /**
     * Navigates respective Activity from list info
     */
    fun displayActivity(position: Int){
        if(null != list?.get(position)?.activity){
            val intent = Intent(this, list?.get(position)?.activity)
            startActivity(intent)
        }
    }


    inner class RecyclerItemListener : RecyclerAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            displayActivity(position)
        }

    }

    /**
     * Constant to represents AdType
     */
    enum class AdType constructor(val activity: Class<*>?, val displayName: String) {
        BANNER(DFPBannerActivity::class.java, "Banner"),
        INTERSTITIAL(DFPInterstitialActivity::class.java, "Interstitial"),
        VIDEO_INTERSTITIAL(VideoInterstitialActivity::class.java, "Video Interstitial"),
    }


}
