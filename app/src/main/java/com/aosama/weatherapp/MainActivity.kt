package com.aosama.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aosama.weatherapp.ViewModels.MainViewModel
import com.aosama.weatherapp.ViewModels.ViewModelFactory
import com.aosama.weatherapp.api.ApiClient
import com.aosama.weatherapp.api.ApiService
import com.aosama.weatherapp.models.WeatherResponseModel
import com.aosama.weatherapp.utils.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var locationRequest: LocationRequest
    private var requestingLocationUpdates: Boolean = false
    private val REQUEST_CHECK_SETTINGS: Int = 2
    private val REQUEST_CODE_LOCATION_PERMISSION: Int = 1
    private var searchView: SearchView? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: Dialog
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        dialog = MyUtils.getDlgProgress(this)!!
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//init location result
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
        locationRequest.interval = 500 * 900000
        locationRequest.fastestInterval = 100000000 / 2
//init location callback
        locationCallback = object : LocationCallback() {
            @SuppressLint("TimberArgCount")
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                locationResult.lastLocation
                getTempAPi(
                    lat = locationResult.lastLocation.latitude.toString(),
                    lon = locationResult.lastLocation.longitude.toString()
                )
                for (location in locationResult.locations) {
                    Timber.i("New Last Location", location.toString())
                }
            }
        }

//        requestPermission()

        getTempAPi()
        model.foo2().observe(this, Observer {
            tvTemp2.text = it.toString()
        })

        val map = HashMap<String, String>()
        map["appid"] = MyConstants.ConfigApi.WEATHER_API_KEY
        map["lat"] = ""
        map["lon"] = ""
        map["q"] = "cairo"
        map["units"] = "metric"

        GlobalScope.launch {
            viewModel.getCurrentWeatherFlow(map).collect { value ->
                withContext(Dispatchers.Main) {
                    when (value.status) {
                        Status.SUCCESS -> {
                            showProgress(false)
                            updateUi(value.data)
                        }
                        Status.LOADING -> {
                            showProgress(true)
                        }
                        Status.ERROR -> Toast.makeText(
                            this@MainActivity,
                            value.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient.apiClient().create(ApiService::class.java))
        ).get(MainViewModel::class.java)
    }

    @SuppressLint("LogNotTimber")
    private fun getTempAPi(q: String = "cairo", lat: String = "", lon: String = "") {

        val map = HashMap<String, String>()
        map["appid"] = MyConstants.ConfigApi.WEATHER_API_KEY
        map["lat"] = lat
        map["lon"] = lon
        map["q"] = q
        map["units"] = "metric"

        viewModel.getCurrentWeather(map).observe(this, {
            when (it) {
                is Success<*> -> {
                    showProgress(false)
                    updateUi(it.data as WeatherResponseModel)
                }
                is Loading -> {
                    showProgress(true)
                }
                is Failer -> {
                    showProgress(false)
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(it: WeatherResponseModel?) {
        tvTemp.text = it?.main?.temp.toString() + " \u00B0C"
        Picasso.get().load(
            MyConstants.ConfigApi.ICON_URL +
                    it?.weather?.get(0)?.icon + "@4x.png"
        ).into(ivIocn)
        searchView?.setQuery(it?.name, false)
    }

    private fun showProgress(status: Boolean) {
        if (status)
            dialog.show()
        else
            dialog.dismiss()
    }

    @SuppressLint("MissingPermission", "TimberArgCount")
    private fun getCurrentLocation() {

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            requestingLocationUpdates = true
            startLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    Toast.makeText(
                        this, "Failed to show", Toast.LENGTH_LONG
                    ).show()
                    exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED ||

            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // show Ui dialog,explain why need this permission
                Toast.makeText(this, "Show Ui Dialog", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_CODE_LOCATION_PERMISSION
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_CODE_LOCATION_PERMISSION
                )
            }
        } else {
            // Permission has already been granted
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_LOCATION_PERMISSION -> {

                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        getCurrentLocation()
                    }
                } else {

                    Toast.makeText(
                        this,
                        "Permission is needed to detect you current location automatically",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val menuItem = menu?.findItem(R.id.action_search)
        searchView = menuItem?.actionView as SearchView
        searchView?.isIconified = false
        searchView?.queryHint = getString(R.string.city_name)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getTempAPi(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        menuItem.setOnActionExpandListener(object : OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                item?.setShowAsAction(SHOW_AS_ACTION_IF_ROOM or SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                item.expandActionView()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}