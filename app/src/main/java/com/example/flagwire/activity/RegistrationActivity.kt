package com.example.flagwire.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.example.flagwire.R
import com.example.flagwire.others.SessionManagement
import com.example.flagwire.retrofit.RetrofitBuilder
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class RegistrationActivity : AppCompatActivity() {

    private lateinit var full_name: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var signUp: MaterialButton
    private lateinit var mobileNumber: TextInputEditText

    private lateinit var loginButton: LoginButton
    lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"
    private lateinit var iv_fb_login:AppCompatImageView
    private val RC_SIGN_IN = 7
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var signIn_button: SignInButton
    private lateinit var auth: FirebaseAuth
    private lateinit var send_gmail_id :String
    private lateinit var send_facebook_id :String
    private lateinit var send_facebook_id_name :String
    private lateinit var send_gmail_id_name :String
    private lateinit var iv_gmail_login:AppCompatImageView
    private lateinit var sessionManagement: SessionManagement
    private lateinit var txt_login:MaterialTextView

    lateinit var progressDialog: ProgressDialog


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
        loginButton = findViewById(R.id.login_button);
        iv_fb_login = findViewById(R.id.iv_fb_login)
        iv_gmail_login = findViewById(R.id.iv_gmail_login)
        callbackManager = CallbackManager.Factory.create()
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        signIn_button = findViewById(R.id.google_button)
        auth = FirebaseAuth.getInstance()

        sessionManagement = SessionManagement(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("loading...")
        txt_login = findViewById(R.id.txt_login)


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
                registerUser("email")
            }


        }


        txt_login.setOnClickListener {

            startActivity(Intent(this,LoginActivity::class.java))
            finish()

        }
        iv_gmail_login.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        iv_fb_login.setOnClickListener({
            loginButton.performClick()
        })

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {

                val accessToken = loginResult!!.accessToken
                useLoginInformation(accessToken)

            }

            override fun onCancel() {
                Toast.makeText(this@RegistrationActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(this@RegistrationActivity, exception.message, Toast.LENGTH_LONG).show()
                Log.d("TAG-ERROR", exception.localizedMessage+" "+exception.message)
            }
        })



        setupGoogleLogin()

        signIn_button.setOnClickListener({
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        })



    }

    private fun useLoginInformation(accessToken: AccessToken) {

        val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
            try {

                val name = `object`.getString("name")
                val email = `object`.getString("email")
                send_facebook_id = email
                send_facebook_id_name = name
                registerUser("facebook")

            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Your facebook account is not associated with email-id please use another facebook account",
                    Toast.LENGTH_LONG
                ).show()

                LoginManager.getInstance().logOut()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture.width(200)")
        request.parameters = parameters
        request.executeAsync()
    }
    private fun setupGoogleLogin() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    private fun registerUser(type: String) {

        progressDialog.show()

        if (type.equals("email")){

            val request :HashMap<String,String> =HashMap<String,String>()
            request.put("fullname",full_name.text.toString())
            request.put("email_id",email.text.toString())
            request.put("password",password.text.toString())
            request.put("mobileno",mobileNumber.text.toString())
            request.put("platform",type)
            request.put("device_token","")

            val call = RetrofitBuilder.instance.retrofit.registration(request)
            call.enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(this@RegistrationActivity,"Please check your internet connection",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful){
                        progressDialog.dismiss()
                        val json = JSONObject(response.body()!!.string())
                        val meassge = json.getString("message")
                        if (meassge.equals("Error Registering or User with Same Details already Exists")){
                            Toast.makeText(this@RegistrationActivity, meassge,Toast.LENGTH_LONG).show()
                        } else if (meassge.equals("We have sent you an email for verification")){
                            val data: JSONArray = json.getJSONArray("data")
                            val jsonObject: JSONObject = data.getJSONObject(0)
                            val email_id= jsonObject.get("email_id")
                            val user_id= jsonObject.get("user_id")
                            val intent  = Intent(this@RegistrationActivity,VerifyAccountActivity::class.java)
                            intent.putExtra("email_id",email_id.toString())
                            intent.putExtra("user_id",user_id.toString())
                            intent.putExtra("user_name",full_name.text.toString())
                            startActivity(intent)
                            finish()
                        }else if (meassge.equals("success")){
                            val intent  = Intent(this@RegistrationActivity,VerifyAccountActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this@RegistrationActivity, meassge,Toast.LENGTH_LONG).show()
                        }

                    }else{
                        progressDialog.dismiss()
                        Toast.makeText(this@RegistrationActivity, response.body().toString(),Toast.LENGTH_LONG).show()
                    }
                }

            })


        }else if (type.equals("facebook")){

            val request :HashMap<String,String> =HashMap<String,String>()
            request.put("fullname",send_facebook_id_name)
            request.put("email_id",send_facebook_id)
            request.put("password","")
            request.put("mobileno","")
            request.put("platform",type)
            request.put("device_token","")

            val call = RetrofitBuilder.instance.retrofit.registration(request)
            call.enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(this@RegistrationActivity,"Please check your internet connection",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful){
                        progressDialog.dismiss()
                        val json = JSONObject(response.body()!!.string())
                        val meassge = json.getString("message")

                        if (meassge.equals("Error Registering or User with Same Details already Exists")){
                            Toast.makeText(this@RegistrationActivity, meassge,Toast.LENGTH_LONG).show()
                        } else if (meassge.equals("success")){
                            val data: JSONArray = json.getJSONArray("data")
                            val jsonObject: JSONObject = data.getJSONObject(0)
                            val user_id= jsonObject.get("user_id")
                            sessionManagement.Save_UserDetails(user_id.toString(),send_facebook_id_name)
                            val intent  = Intent(this@RegistrationActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else if (meassge.equals("Welcome Again")){
                            val data: JSONArray = json.getJSONArray("data")
                            val jsonObject: JSONObject = data.getJSONObject(0)
                            val user_id= jsonObject.get("user_id")
                            sessionManagement.Save_UserDetails(user_id.toString(),send_facebook_id_name)
                            val intent  = Intent(this@RegistrationActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Log.d("Registration_Facebook",meassge)
                            Toast.makeText(this@RegistrationActivity, meassge,Toast.LENGTH_LONG).show()
                        }

                    }else{
                        progressDialog.dismiss()
                        Toast.makeText(this@RegistrationActivity, response.body().toString(),Toast.LENGTH_LONG).show()
                    }
                }

            })


        }else if (type.equals("google")){

            val request :HashMap<String,String> =HashMap<String,String>()
            request.put("fullname",send_gmail_id_name)
            request.put("email_id",send_gmail_id)
            request.put("password","")
            request.put("mobileno","")
            request.put("platform",type)
            request.put("device_token","")

            val call = RetrofitBuilder.instance.retrofit.registration(request)
            call.enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(this@RegistrationActivity,"Please check your internet connection",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful){
                        progressDialog.dismiss()
                        val json = JSONObject(response.body()!!.string())
                        val meassge = json.getString("message")



                        if (meassge.equals("Error Registering or User with Same Details already Exists")){
                            Toast.makeText(this@RegistrationActivity, meassge,Toast.LENGTH_LONG).show()
                        } else if (meassge.equals("success")){
                            val data: JSONArray = json.getJSONArray("data")
                            val jsonObject: JSONObject = data.getJSONObject(0)

                            val user_id= jsonObject.get("user_id")
                            sessionManagement.Save_UserDetails(user_id as String,send_gmail_id_name)
                            val intent  = Intent(this@RegistrationActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else if (meassge.equals("Welcome Again")){
                            val data: JSONArray = json.getJSONArray("data")
                            val jsonObject: JSONObject = data.getJSONObject(0)
                            val user_id= jsonObject.get("user_id")
                            sessionManagement.Save_UserDetails(user_id.toString(),send_gmail_id_name)
                            val intent  = Intent(this@RegistrationActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this@RegistrationActivity, meassge,Toast.LENGTH_LONG).show()
                        }

                    }else{
                        progressDialog.dismiss()
                        Toast.makeText(this@RegistrationActivity, response.body().toString(),Toast.LENGTH_LONG).show()
                    }
                }

            })

        }




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Login_Gmail", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("Login_Gmail", "Google sign in failed"+ e.message+" "+e.localizedMessage)

            }
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("Login", "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "signInWithCredential:success")
                    val user = auth.currentUser
                    send_gmail_id = user!!.email!!;
                    send_gmail_id_name = user!!.displayName!!
                    registerUser("google")
                } else {
                    Log.w("Login", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,"Auth Failed",Toast.LENGTH_LONG).show()
                }

            }
    }

}