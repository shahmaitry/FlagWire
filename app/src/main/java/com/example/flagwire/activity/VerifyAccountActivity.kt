package com.example.flagwire.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.flagwire.R
import com.example.flagwire.others.SessionManagement
import com.example.flagwire.retrofit.RetrofitBuilder
import com.goodiebag.pinview.Pinview
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyAccountActivity : AppCompatActivity() {

    private lateinit var email_id:String
    private lateinit var user_id:String
    private lateinit var user_name:String
    private lateinit var pinview: Pinview
    private lateinit var sessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_account)
        pinview = findViewById(R.id.pinview)
        email_id = intent.getStringExtra("email_id").toString()
        user_id = intent.getStringExtra("user_id").toString()
        user_name = intent.getStringExtra("user_name").toString()

        sessionManagement = SessionManagement(this)

        Log.d("get_details",email_id+user_id+user_name)

        pinview.setPinViewEventListener { pinview, fromUser -> //Make api calls here or what not

            Verify(pinview.value)

        }
    }


    private fun Verify(value: String) {

        val request :HashMap<String,String> =HashMap<String,String>()
        request.put("email_id",email_id)
        request.put("otp",value)

        val call = RetrofitBuilder.instance.retrofit.verify_account(request)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@VerifyAccountActivity,"Please check your internet connection",
                    Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if (response.isSuccessful){

                    val json = JSONObject(response.body()!!.string())
                    val meassge = json.getString("message")

                    if (meassge.equals("Verification Success")){
                        sessionManagement.Save_UserDetails(user_id.toString(),user_name)
                        val intent = Intent(this@VerifyAccountActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else{
                        Toast.makeText( this@VerifyAccountActivity,meassge,Toast.LENGTH_LONG).show()
                    }

                    Toast.makeText(this@VerifyAccountActivity, meassge, Toast.LENGTH_LONG).show()

                }else{
                    Toast.makeText(this@VerifyAccountActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                }
            }

        })



    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@VerifyAccountActivity,RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }
}