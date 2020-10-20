package com.example.flagwire.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.flagwire.R
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var txt_signup:MaterialTextView
    private lateinit var txt_forget_pass:MaterialTextView
    private lateinit var et_login_email:TextInputEditText
    private lateinit var et_login_password:TextInputEditText
    private lateinit var btn_login: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()


    }

    private fun init() {
        txt_signup = findViewById(R.id.txt_signup)
        txt_forget_pass = findViewById(R.id.txt_forget_pass)
        et_login_email = findViewById(R.id.et_login_email)
        et_login_password = findViewById(R.id.et_login_password)
        btn_login = findViewById(R.id.btn_login)

        txt_signup.setOnClickListener {

            startActivity(Intent(this,RegistrationActivity::class.java))
        }

        txt_forget_pass.setOnClickListener {

            startActivity(Intent(this,ForgetPassActivity::class.java))
        }
        btn_login.setOnClickListener {

            if (TextUtils.isEmpty(et_login_email.text)){
                Toast.makeText(this@LoginActivity, "Enter email address", Toast.LENGTH_LONG).show()
            }else if (TextUtils.isEmpty(et_login_password.text)){
                Toast.makeText(this@LoginActivity, "enter password", Toast.LENGTH_LONG).show()
            }else{
                LoginUser()
            }

        }

    }


    private fun LoginUser() {

        val request :HashMap<String,String> =HashMap<String,String>()
        request.put("email_id",et_login_email.text.toString())
        request.put("password",et_login_password.text.toString())


        val call = RetrofitBuilder.instance.retrofit.login(request)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@LoginActivity,"Please check your internet connection",
                    Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if (response.isSuccessful){

                    val json = JSONObject(response.body()!!.string())
                    val meassge = json.getString("message")

                    if (meassge.equals("profile not verified",false)){

                        Toast.makeText(this@LoginActivity, meassge, Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this@LoginActivity, meassge, Toast.LENGTH_LONG).show()
                    }



                }else{

                    Toast.makeText(this@LoginActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                }
            }

        })



    }

}