package com.example.flagwire.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.flagwire.R
import com.example.flagwire.model.registration.RegistrationResponse
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    private lateinit var full_name: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var signUp: MaterialButton
    private lateinit var mobileNumber: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        init()


    }

    private fun init() {

        full_name = findViewById(R.id.et_register_fullname)
        email = findViewById(R.id.et_register_email_id)
        password = findViewById(R.id.et_register_password)
        signUp = findViewById(R.id.btn_sign_up)
        mobileNumber = findViewById(R.id.et_register_mobile_number)

        signUp.setOnClickListener {

            if (TextUtils.isEmpty(full_name.text)) {
                Toast.makeText(this, "Enter fullname", Toast.LENGTH_LONG).show()
            } else if (TextUtils.isEmpty(email.text)) {
                Toast.makeText(this, "Enter email address", Toast.LENGTH_LONG).show()
            } else if (TextUtils.isEmpty(password.text)) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_LONG).show()
            } else if (TextUtils.isEmpty(mobileNumber.text)) {
                Toast.makeText(this, "Enter mobile number", Toast.LENGTH_LONG).show()
            } else {
                registerUser()
            }


        }


    }

    private fun registerUser() {

        val request :HashMap<String,String> =HashMap<String,String>()
        request.put("fullname",full_name.text.toString())
        request.put("email_id",email.text.toString())
        request.put("password",password.text.toString())
        request.put("mobileno",mobileNumber.text.toString())
        request.put("platform","Email")
        request.put("device_token","")


        val call = RetrofitBuilder.instance.retrofit.registration(request)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@RegistrationActivity,"Please check your internet connection",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if (response.isSuccessful){

                    val json = JSONObject(response.body()!!.string())
                    val meassge = json.getString("message")
                    Toast.makeText(this@RegistrationActivity, meassge,Toast.LENGTH_LONG).show()

                }else{

                    Toast.makeText(this@RegistrationActivity, response.body().toString(),Toast.LENGTH_LONG).show()
                }
            }

        })



    }
}