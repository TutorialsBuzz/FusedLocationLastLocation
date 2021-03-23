package com.tutorialsbuzz.mylocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback

    val PERMISSION_ID = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fetchMyLocation.setOnClickListener({
            getMyCurrenLocation()
        })

        //CallBack for location update Request
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // Update UI With Lat Lng
                updateUIWithLatLng(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude
                )
            }
        }

    }

    fun updateUIWithLatLng(latitude: Double, longitude: Double) {
        val textLabel = " Latitude: $latitude \n Longitude: $longitude "
        textView.text = textLabel
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun getMyCurrenLocation() {

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //request for location permission
            requestPermissions()

        } else {

            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->

                val location: Location? = task.result

                if (location == null) {

                    // Create Location Request
                    val locationRequest = LocationRequest.create().apply {
                        interval = 10000
                        fastestInterval = 5000
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    }

                    // Request for location update
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    mFusedLocationClient.requestLocationUpdates(
                        locationRequest, mLocationCallback,
                        Looper.myLooper()
                    )

                } else {
                    // Update Ui With Lat Lng
                    updateUIWithLatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //when granted
                getMyCurrenLocation()
                showToast("Permission Granted:")
            } else {
                showToast("Permission Denied:")
            }
        }
    }

    fun showToast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
    }
}