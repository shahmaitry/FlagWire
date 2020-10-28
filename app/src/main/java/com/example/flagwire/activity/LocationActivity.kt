package com.example.flagwire.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import com.example.flagwire.R
import com.example.flagwire.others.SessionManagement
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.textview.MaterialTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_location.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class LocationActivity : AppCompatActivity(), MultiplePermissionsListener {

    private lateinit var mSettingsClient: SettingsClient
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private val REQUEST_CHECK_SETTINGS = 123
    private val REQUEST_ENABLE_GPS = 321
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var addresses = ArrayList<Address>()
    lateinit var progressDialog: ProgressDialog
    private lateinit var country:MaterialTextView
    private lateinit var txt_postal_code_area:MaterialTextView
    private lateinit var ll_in_app_only:LinearLayout
    private lateinit var ll_in_email_only:LinearLayout
    private lateinit var ll_in_app_both:LinearLayout
    private var send_via ="1"
    private lateinit var sessionManagement: SessionManagement
    var reminder_list = ArrayList<String>()
    private lateinit var user_id :String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        btn_next.setOnClickListener {

            Purchase()

        }


        iv_back.setOnClickListener {

            finish()
        }

        val pop_up_menu = findViewById(R.id.iv_popup_menu) as AppCompatImageView
        pop_up_menu.setOnClickListener {

            val popup = PopupMenu(this,it)
            popup.inflate(R.menu.header_menu)

            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.menu_profile -> {

                        startActivity(Intent(this,ProfileActivity::class.java))
                    }

                    R.id.menu_about_us ->{
                        startActivity(Intent(this,AboutUsActivity::class.java))
                    }

                    R.id.menu_contact -> {
                        startActivity(Intent(this,ContactUsActivity::class.java))
                    }

                    R.id.menu_terms_condition ->{
                        startActivity(Intent(this,TermsConditionActivity::class.java))
                    }

                    R.id.menu_logout ->{
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

        val i = intent //This should be getIntent();
        reminder_list = i.getStringArrayListExtra("reminder_list") as ArrayList<String>
        Log.d("reminder_list",reminder_list.toString())


        sessionManagement = SessionManagement(this)
        val user = sessionManagement.GetUserDetails()
        user_id = user[SessionManagement.KEY_ID].toString()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("fetching your location")

        country = findViewById(R.id.txt_country)
        txt_postal_code_area = findViewById(R.id.txt_postal_code_area)

        ll_in_app_both = findViewById(R.id.ll_in_app_both)
        ll_in_app_only = findViewById(R.id.ll_in_app_only)
        ll_in_email_only = findViewById(R.id.ll_in_email_only)


        ll_in_app_both.setOnClickListener {
            send_via = "2"
            ll_in_app_both.setBackgroundColor(Color.parseColor("#E57373"))
            ll_in_app_only.setBackgroundColor(Color.parseColor("#0b4060"))
            ll_in_email_only.setBackgroundColor(Color.parseColor("#0b4060"))
        }

        ll_in_app_only.setOnClickListener {
            send_via = "0"
            ll_in_app_both.setBackgroundColor(Color.parseColor("#0b4060"))
            ll_in_app_only.setBackgroundColor(Color.parseColor("#E57373"))
            ll_in_email_only.setBackgroundColor(Color.parseColor("#0b4060"))
        }

        ll_in_email_only.setOnClickListener {
            send_via = "1"
            ll_in_app_both.setBackgroundColor(Color.parseColor("#0b4060"))
            ll_in_app_only.setBackgroundColor(Color.parseColor("#0b4060"))
            ll_in_email_only.setBackgroundColor(Color.parseColor("#E57373"))
        }



        val permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)

        Dexter.withActivity(this)
            .withPermissions(permissions)
            .withListener(this)
            .check()
    }

    private fun Purchase() {

        if (reminder_list.isEmpty()){

            Toast.makeText(this,"No category select for reminder...",Toast.LENGTH_LONG).show()

        }else{

            progressDialog.setMessage("Loading")
            progressDialog.show()

            for (i in 0 until reminder_list.size){
                val concate_string = reminder_list.get(i)
                if (concate_string.split("\\s".toRegex())[3].equals("3")){
                    Log.d("list","no entry found")
                }else{
                    Log.d("sub_string",concate_string.split("\\s".toRegex())[1] +concate_string.split("\\s".toRegex())[3])
                    val request :HashMap<String,String> =HashMap<String,String>()
                    request.put("customer_id",user_id)
                    request.put("country",country.text.toString())
                    request.put("postcode",txt_postal_code_area.text.toString())
                    request.put("send_via",send_via)
                    request.put("reminder",concate_string.split("\\s".toRegex())[3])
                    request.put("category_id",concate_string.split("\\s".toRegex())[1])

                    val call = RetrofitBuilder.instance.retrofit.purchase(request)
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            progressDialog.dismiss()
                            Toast.makeText(this@LocationActivity,"Please check your internet connection",
                                Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {

                            if (response.isSuccessful){
                                progressDialog.dismiss()
                                val json = JSONObject(response.body()!!.string())
                                val meassge = json.getString("message")
                                if (meassge.equals("success")) {
                                    Log.d("purchase Api","Done")
                                }else{
                                    Toast.makeText(this@LocationActivity, meassge, Toast.LENGTH_LONG).show()
                                }
                            }else{
                                progressDialog.dismiss()
                                Toast.makeText(this@LocationActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                            }
                        }

                    })

                }

                if (i == reminder_list.size-1){
                    startActivity(Intent(this@LocationActivity, FinalActivity::class.java))
                }

            }


        }

    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
        if (report != null) {
            if (report.areAllPermissionsGranted()) {
                progressDialog.show()
                mFusedLocationClient = FusedLocationProviderClient(this)
                Check_Location_Status()
            }else{
                progressDialog.dismiss()
                Toast.makeText(this,"give permission to fetch your current location....",Toast.LENGTH_LONG).show()
            }

            if (report.isAnyPermissionPermanentlyDenied){
                progressDialog.dismiss()
                val intent  = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null))
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
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }


    public fun Check_Location_Status() {
        if (isLocationEnabled()){
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
                    addresses = geocoder.getFromLocation(location.latitude,   location.longitude, 1) as ArrayList<Address>;

                    if (addresses.size >0) {
                        progressDialog.dismiss()

                        country.text = addresses.get(0).countryName
                        txt_postal_code_area.text = addresses.get(0).postalCode
                        Log.d("address", "Address -"+addresses.get(0).getAddressLine(0)+" city - "+addresses.get(0).getLocality()+" State - "+ addresses.get(0).getAdminArea())
                    }

                }
            }
        }else{
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
            Toast.makeText(this,"Turn On Location Successfully",Toast.LENGTH_LONG).show()
        }
            .addOnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val rae = e as ResolvableApiException
                        rae.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (sie: IntentSender.SendIntentException) {
                        Toast.makeText(this,"Unable to execute request.",Toast.LENGTH_LONG).show()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                        Toast.makeText(this,"Location settings are inadequate, and cannot be fixed here. Fix in Settings.",Toast.LENGTH_LONG).show()
                }
            }
            .addOnCanceledListener {
                Toast.makeText(this,"cancel",Toast.LENGTH_LONG).show()
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

            val geocoder = Geocoder(this@LocationActivity, Locale.getDefault())

            try {
                addresses = geocoder.getFromLocation(mLastLocation.latitude,   mLastLocation.longitude, 1) as ArrayList<Address>;
                if (addresses.size >0) {
                    progressDialog.dismiss()

                    country.text = addresses.get(0).countryName
                    txt_postal_code_area.text = addresses.get(0).postalCode
                    Log.d("address", "Address -"+addresses.get(0).getAddressLine(0)+" city - "+addresses.get(0).getLocality()+" State - "+ addresses.get(0).getAdminArea())
                }

            }catch (e: IOException)
            {
                Log.d("address","Location not found")
            }

        }
    }

    private fun openGpsEnableSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, REQUEST_ENABLE_GPS)
    }
}