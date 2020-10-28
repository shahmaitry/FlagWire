package com.example.flagwire.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flagwire.R
import com.example.flagwire.adapter.TodaysFlagListAdapter
import com.example.flagwire.model.todays_list.TodaysDataItem
import com.example.flagwire.model.todays_list.TodaysListResponse
import com.example.flagwire.others.SessionManagement
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), MultiplePermissionsListener {
    private lateinit var todays_flag_recyclerview: RecyclerView
    private lateinit var upcoming_flag_recyclerview: RecyclerView
    private lateinit var category_icon: AppCompatImageView
    private lateinit var user_name: MaterialTextView
    private lateinit var profile_image_home_page: CircleImageView
    private lateinit var sessionManagement: SessionManagement
    private lateinit var txt_clear: MaterialTextView
    private lateinit var day: MaterialTextView
    private var get_user_city = "Ahmedabad"

    private lateinit var mSettingsClient: SettingsClient
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private val REQUEST_CHECK_SETTINGS = 123
    private val REQUEST_ENABLE_GPS = 321
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var addresses = ArrayList<Address>()
    private lateinit var notification: AppCompatImageView
    lateinit var progressDialog: ProgressDialog
    lateinit var txt_no_found_today: MaterialTextView
    lateinit var txt_no_found_upcoming: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todays_flag_recyclerview = findViewById(R.id.recyclerview_todays_flag)
        upcoming_flag_recyclerview = findViewById(R.id.recyclerview_upcoming_flag)
        category_icon = findViewById(R.id.iv_category_list)
        user_name = findViewById(R.id.user_name)
        txt_clear = findViewById(R.id.txt_clear)
        day = findViewById(R.id.day)
        sessionManagement = SessionManagement(this)
        notification = findViewById(R.id.iv_notification)
        profile_image_home_page = findViewById(R.id.profile_image_home_page)
        txt_no_found_today = findViewById(R.id.txt_no_found_today)
        txt_no_found_upcoming = findViewById(R.id.txt_no_found_upcoming)

        category_icon.setOnClickListener {

            val intent = Intent(this, FlagListActivity::class.java)
            startActivity(intent)

        }

        notification.setOnClickListener {

            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("fetching your location")

        val user = sessionManagement.GetUserDetails()
        val get_user_name = user[SessionManagement.KEY_USER_NAME].toString()
        val get_user_id = user[SessionManagement.KEY_ID].toString()
        user_name.text = "Hello " + get_user_name

        TodaysFlag()
        UpcomingFlag()
        GetProfile(get_user_id)

        val pop_up_menu = findViewById(R.id.iv_popup_menu) as AppCompatImageView
        pop_up_menu.setOnClickListener {

            val popup = PopupMenu(this, it)
            popup.inflate(R.menu.header_menu)

            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.menu_profile -> {

                        startActivity(Intent(this, ProfileActivity::class.java))
                    }

                    R.id.menu_about_us -> {
                        startActivity(Intent(this, AboutUsActivity::class.java))
                    }

                    R.id.menu_contact -> {
                        startActivity(Intent(this, ContactUsActivity::class.java))
                    }

                    R.id.menu_terms_condition -> {
                        startActivity(Intent(this, TermsConditionActivity::class.java))
                    }

                    R.id.menu_logout -> {
                        sessionManagement.LogoutUser()
                        val i = Intent(this, LoginActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                        finish()
                    }

                }

                true
            })

            popup.show()

        }


        val permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        Dexter.withActivity(this)
            .withPermissions(permissions)
            .withListener(this)
            .check()
    }

    private fun GetProfile(userId: String) {

        val call = RetrofitBuilder.instance.retrofit.get_profile_data(userId)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            @SuppressLint("WrongConstant")
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if (response.isSuccessful) {
                    progressDialog.dismiss()
                    val json = JSONObject(response.body()!!.string())
                    val meassge = json.getString("message")
                    val status = json.getInt("status")

                    if (meassge.equals("success") && status == 1) {

                        val data: JSONObject = json.getJSONObject("data")
                        val image_url = data.getString("profile_pic")
                        if (image_url.isEmpty() || image_url == null) {
                            Log.d("edit_profile", "No image found")
                        } else {
                            val uri = Uri.parse("https://konnecthost.in/Flagwire/" + image_url)
                            Log.d("edit_profile", "show image uri " + uri)
                            Picasso.get().load(uri).into(profile_image_home_page)
                        }
                        //sessionManagement.Save_location(get_dialog_city, get_state,get_dialog_address)

                        Log.d("edit_profile", response.body()!!.string())
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            meassge,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    progressDialog.dismiss()
                    Log.d("edit_profile", "response not get success")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(
                    this@MainActivity,
                    t.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("edit_profile", t.message + " " + t.localizedMessage)
            }
        })
    }

    private fun TodaysFlag() {

        val call = RetrofitBuilder.instance.retrofit.todays_flag_list()
        call.enqueue(object : Callback<TodaysListResponse> {
            override fun onFailure(call: Call<TodaysListResponse>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity, "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(
                call: Call<TodaysListResponse>,
                response: Response<TodaysListResponse>
            ) {

                if (response.isSuccessful) {


                    if (response.body()!!.message.equals("success", false)) {


                        if (response.body()!!.data!!.isEmpty()) {
                            txt_no_found_today.visibility = View.VISIBLE
                        } else {
                            txt_no_found_today.visibility = View.GONE
                            val adapter = TodaysFlagListAdapter(
                                response.body()!!.data as ArrayList<TodaysDataItem>,
                                this@MainActivity
                            )
                            val linearLayoutManager =
                                LinearLayoutManager(this@MainActivity, LinearLayout.VERTICAL, false)
                            recyclerview_todays_flag.layoutManager = linearLayoutManager
                            recyclerview_todays_flag.adapter = adapter

                        }


                    } else if (response.body()!!.message.equals("No Data Available", false)) {
                        txt_no_found_today.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }

        })
    }

    private fun UpcomingFlag() {

        val call = RetrofitBuilder.instance.retrofit.upcoming_flag_list()
        call.enqueue(object : Callback<TodaysListResponse> {
            override fun onFailure(call: Call<TodaysListResponse>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity, "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(
                call: Call<TodaysListResponse>,
                response: Response<TodaysListResponse>
            ) {

                if (response.isSuccessful) {


                    if (response.body()!!.message.equals("success", false)) {

                        if (response.body()!!.data!!.isEmpty()) {
                            txt_no_found_upcoming.visibility = View.VISIBLE
                        } else {
                            val adapter = TodaysFlagListAdapter(
                                response.body()!!.data as ArrayList<TodaysDataItem>,
                                this@MainActivity
                            )
                            val linearLayoutManager =
                                LinearLayoutManager(this@MainActivity, LinearLayout.VERTICAL, false)
                            upcoming_flag_recyclerview.layoutManager = linearLayoutManager
                            upcoming_flag_recyclerview.adapter = adapter
                        }

                    } else if (response.body()!!.message.equals("No Data Available", false)) {
                        txt_no_found_upcoming.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }

        })
    }


    private fun openwether() {

        val call = RetrofitBuilder.instance.retrofit_open_wether.get_open_wether_data(
            get_user_city,
            "a8f512279613639e195bf9ef4b2cc25a"
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    t.localizedMessage+" "+t.message,
                    Toast.LENGTH_LONG
                ).show()
                Log.d("error_wether",t.localizedMessage+" "+t.message)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if (response.isSuccessful) {

                    val json = JSONObject(response.body()!!.string())
                    val wether_description = json.getJSONArray("weather")

                    val jsonObject: JSONObject = wether_description.getJSONObject(0)
                    txt_clear.text = jsonObject.get("description").toString()

                    val wind_speed: JSONObject = json.getJSONObject("wind")
                    wind.text = wind_speed.getString("speed") + " km/h"
                    day.text = LocalDate.now().dayOfWeek.name
                    val city_namee = json.getString("name")
                    val country_name = json.getJSONObject("sys")
                    city_name.text = city_namee + " "+country_name.getString("country")

                   /* day.text = LocalDate.now().dayOfWeek.name
                    city_name.text = response.body()!!.name + " " + response.body()!!.sys!!.country
                    wind.text = response.body()!!.wind!!.speed.toString() + " km/h"
                    txt_clear.text = response.body()!!.weather!!.get(0)!!.description*/

                } else {

                    Toast.makeText(this@MainActivity, response.message(), Toast.LENGTH_LONG).show()
                }
            }

        })


    }


    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
        if (report != null) {
            if (report.areAllPermissionsGranted()) {
                progressDialog.show()
                mFusedLocationClient = FusedLocationProviderClient(this)
                Check_Location_Status()
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "give permission to fetch your current location....",
                    Toast.LENGTH_LONG
                ).show()
            }

            if (report.isAnyPermissionPermanentlyDenied) {
                progressDialog.dismiss()
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onPermissionRationaleShouldBeShown(
        permissions: MutableList<PermissionRequest>?,
        token: PermissionToken?
    ) {
        if (token != null) {
            token.continuePermissionRequest()
        }
    }

    public fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    public fun Check_Location_Status() {
        if (isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                var location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {

                    val geocoder = Geocoder(this, Locale.getDefault())
                    addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) as ArrayList<Address>;

                    if (addresses.size > 0) {
                        progressDialog.dismiss()

                        get_user_city = addresses.get(0).locality
                        openwether()
                        Log.d(
                            "address",
                            "Address -" + addresses.get(0)
                                .getAddressLine(0) + " city - " + addresses.get(0)
                                .getLocality() + " State - " + addresses.get(0).getAdminArea()
                        )
                    }

                }
            }
        } else {
            EnableGPS()
        }
    }

    public fun EnableGPS() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        builder.setAlwaysShow(true)
        mLocationSettingsRequest = builder.build()
        mSettingsClient = LocationServices.getSettingsClient(this)
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener {
            Toast.makeText(this, "Turn On Location Successfully", Toast.LENGTH_LONG).show()
        }
            .addOnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val rae = e as ResolvableApiException
                        rae.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (sie: IntentSender.SendIntentException) {
                        Toast.makeText(this, "Unable to execute request.", Toast.LENGTH_LONG).show()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                        Toast.makeText(
                            this,
                            "Location settings are inadequate, and cannot be fixed here. Fix in Settings.",
                            Toast.LENGTH_LONG
                        ).show()
                }
            }
            .addOnCanceledListener {
                Toast.makeText(this, "cancel", Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Check_Location_Status()
                }
                Activity.RESULT_CANCELED -> {
                    Log.e("GPS", "User denied to access location")
                    openGpsEnableSetting()
                }
            }
        } else if (requestCode === REQUEST_ENABLE_GPS) {
            val locationManager: LocationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled: Boolean =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isGpsEnabled) {
                openGpsEnableSetting()
            } else {
                Check_Location_Status()
            }
        }
    }


    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation

            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())

            try {
                addresses = geocoder.getFromLocation(
                    mLastLocation.latitude,
                    mLastLocation.longitude,
                    1
                ) as ArrayList<Address>;
                if (addresses.size > 0) {
                    progressDialog.dismiss()

                    get_user_city = addresses.get(0).locality
                    openwether()
                    Log.d(
                        "address",
                        "Address -" + addresses.get(0)
                            .getAddressLine(0) + " city - " + addresses.get(0)
                            .getLocality() + " State - " + addresses.get(0).getAdminArea()
                    )
                }

            } catch (e: IOException) {
                Log.d("address", "Location not found")
            }

        }
    }

    private fun openGpsEnableSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, REQUEST_ENABLE_GPS)
    }

}