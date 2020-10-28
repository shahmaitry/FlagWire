package com.example.flagwire.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


class LoginActivity : AppCompatActivity() {

    private lateinit var txt_signup:MaterialTextView
    private lateinit var txt_forget_pass:MaterialTextView
    private lateinit var et_login_email:TextInputEditText
    private lateinit var et_login_password:TextInputEditText
    private lateinit var btn_login: MaterialButton
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

    lateinit var progressDialog: ProgressDialog

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

        txt_signup.setOnClickListener {

            startActivity(Intent(this,RegistrationActivity::class.java))
        }

        txt_forget_pass.setOnClickListener {

            startActivity(Intent(this,ForgetPassActivity::class.java))
        }
        btn_login.setOnClickListener {

            LoginUser("email")

        }

        iv_fb_login.setOnClickListener {

            loginButton.performClick()

        }

        iv_gmail_login.setOnClickListener {

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }
        setupGoogleLogin()

        signIn_button.setOnClickListener({
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        })

        iv_fb_login.setOnClickListener({
            loginButton.performClick()
        })

        //Key hash generate code
      /*  try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }*/

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {

                val accessToken = loginResult!!.accessToken
                useLoginInformation(accessToken)

            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(this@LoginActivity, exception.message, Toast.LENGTH_LONG).show()
                Log.d("TAG-ERROR", exception.localizedMessage+" "+exception.message)
            }
        })

    }

    private fun useLoginInformation(accessToken: AccessToken) {

        val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
            try {

                val name = `object`.getString("name")
                val email = `object`.getString("email")
                send_facebook_id = email
                send_facebook_id_name = name
                LoginUser("facebook")

            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(
                    this@LoginActivity,
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


    private fun LoginUser(type: String) {
        progressDialog.show()
        if (type.equals("email")){
            if (TextUtils.isEmpty(et_login_email.text)){
                Toast.makeText(this@LoginActivity, "Enter email address", Toast.LENGTH_LONG).show()
            }else if (TextUtils.isEmpty(et_login_password.text)){
                Toast.makeText(this@LoginActivity, "enter password", Toast.LENGTH_LONG).show()
            }else{

                val request :HashMap<String,String> =HashMap<String,String>()
                request.put("email_id",et_login_email.text.toString())
                request.put("password",et_login_password.text.toString())

                val call = RetrofitBuilder.instance.retrofit.login(request)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        progressDialog.dismiss()
                        Toast.makeText(this@LoginActivity,"Please check your internet connection",
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

                            if (meassge.equals("profile not verified",false)){
                                Toast.makeText(this@LoginActivity, meassge, Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@LoginActivity,VerifyAccountActivity::class.java))
                                finish()
                            }else if (meassge.equals("login success",false)){
                                val data: JSONArray = json.getJSONArray("data")
                                val jsonObject: JSONObject = data.getJSONObject(0)
                                val user_id= jsonObject.get("user_id")
                                val user_name= jsonObject.get("fullname")
                                sessionManagement.Save_UserDetails(user_id.toString(), user_name.toString())
                                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                                finish()
                                Toast.makeText(this@LoginActivity, meassge, Toast.LENGTH_LONG).show()
                            }

                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this@LoginActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                })
            }
        }else if (type.equals("google")){

            val request :HashMap<String,String> =HashMap<String,String>()
            request.put("email_id",send_gmail_id)
            request.put("password","")

            val call = RetrofitBuilder.instance.retrofit.login(request)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(this@LoginActivity,"Please check your internet connection",
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



                        if (meassge.equals("profile not verified",false)){
                            Toast.makeText(this@LoginActivity, meassge, Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@LoginActivity,VerifyAccountActivity::class.java))
                            finish()
                        }else if (meassge.equals("login success",false)){
                            val data: JSONArray = json.getJSONArray("data")
                            val jsonObject: JSONObject = data.getJSONObject(0)
                            val user_id= jsonObject.get("user_id")
                            val user_name= jsonObject.get("fullname")
                            sessionManagement.Save_UserDetails(user_id.toString(), user_name.toString())

                            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                            finish()
                            Toast.makeText(this@LoginActivity, meassge, Toast.LENGTH_LONG).show()
                        }else if (meassge.equals("Welcome Again",false)){
                            val data: JSONArray = json.getJSONArray("data")
                            val jsonObject: JSONObject = data.getJSONObject(0)
                            val user_id= jsonObject.get("user_id")
                            val user_name= jsonObject.get("fullname")
                            sessionManagement.Save_UserDetails(user_id.toString(), user_name.toString())
                            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                            finish()
                            Toast.makeText(this@LoginActivity, meassge, Toast.LENGTH_LONG).show()
                        }else if (meassge.equals("login fail")){
                            registerUser("google",send_gmail_id,send_gmail_id_name)

                        }

                    }else{
                        progressDialog.dismiss()
                        Toast.makeText(this@LoginActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                    }
                }

            })

        }else{

            val request :HashMap<String,String> =HashMap<String,String>()
            request.put("email_id",send_facebook_id)
            request.put("password","")

            val call = RetrofitBuilder.instance.retrofit.login(request)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(this@LoginActivity,"Please check your internet connection",
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
                        if (meassge.equals("profile not verified",false)){
                            Toast.makeText(this@LoginActivity, meassge, Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@LoginActivity,VerifyAccountActivity::class.java))
                            finish()
                        }else if (meassge.equals("login success",false)){
                            val data: JSONArray = json.getJSONArray("data")
                            val jsonObject: JSONObject = data.getJSONObject(0)
                            val user_id= jsonObject.get("user_id")
                            val user_name= jsonObject.get("fullname")
                            sessionManagement.Save_UserDetails(user_id.toString(), user_name.toString())
                            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                            finish()
                            Toast.makeText(this@LoginActivity, meassge, Toast.LENGTH_LONG).show()
                        }else if (meassge.equals("login fail")){
                            registerUser("facebook",send_facebook_id,send_facebook_id_name)

                        }

                    }else{
                        progressDialog.dismiss()
                        Toast.makeText(this@LoginActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                    }
                }

            })
        }


    }


    private fun registerUser(
        type: String,
        email_id: String,
        sendName: String
    ) {

        progressDialog.show()
        val request :HashMap<String,String> =HashMap<String,String>()
        request.put("fullname",sendName)
        request.put("email_id",email_id)
        request.put("password","")
        request.put("mobileno","")
        request.put("platform",type)
        request.put("device_token","")

        val call = RetrofitBuilder.instance.retrofit.registration(request)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@LoginActivity,"Please check your internet connection",Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this@LoginActivity, meassge,Toast.LENGTH_LONG).show()
                    }else if (meassge.equals("success")){
                        val data: JSONArray = json.getJSONArray("data")
                        val jsonObject: JSONObject = data.getJSONObject(0)
                        val user_id= jsonObject.get("user_id")
                        val user_name= jsonObject.get("fullname")
                        sessionManagement.Save_UserDetails(user_id.toString(), user_name.toString())
                        val intent  = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, meassge,Toast.LENGTH_LONG).show()
                    }

                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this@LoginActivity, response.body().toString(),Toast.LENGTH_LONG).show()
                }
            }

        })



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
                    LoginUser("google")
                } else {
                    Log.w("Login", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,"Auth Failed",Toast.LENGTH_LONG).show()
                }

            }
    }

}