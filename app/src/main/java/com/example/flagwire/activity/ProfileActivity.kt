package com.example.flagwire.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.example.flagwire.R
import com.example.flagwire.others.SessionManagement
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileActivity : AppCompatActivity() {

    val GALLERY_REQUEST_CODE = 101
    private lateinit var back_button:AppCompatImageView
    private lateinit var sessionManagement: SessionManagement
    private lateinit var progressDialog: ProgressDialog
    private lateinit var user_name:TextInputEditText
    private lateinit var user_email:TextInputEditText
    private lateinit var profile_image:CircleImageView
    private  var add_photo = "no"
    private  lateinit var image: Image
    private lateinit var edit_profile:AppCompatImageView
    private lateinit var btn_save_edit_profile:MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        back_button = findViewById(R.id.iv_back_profile)
        sessionManagement = SessionManagement(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        user_name = findViewById(R.id.et_edit_fullname)
        user_email = findViewById(R.id.et_edit_profile_email)
        profile_image = findViewById(R.id.profile_image)
        edit_profile = findViewById(R.id.iv_profile_edit)
        btn_save_edit_profile = findViewById(R.id.btn_save_edit_profile)

        back_button.setOnClickListener {

            finish()
        }

        val user = sessionManagement.GetUserDetails()
        val user_id = user[SessionManagement.KEY_ID].toString()


        edit_profile.setOnClickListener {

            ImagePicker.with(this)
                .setRequestCode(GALLERY_REQUEST_CODE)
                .setMultipleMode(true)              //  Select multiple images or single image
                .setFolderMode(true)                //  Folder mode
                .setShowCamera(true)
                .setMaxSize(1)
                .start();
        }

        btn_save_edit_profile.setOnClickListener {

            if (add_photo.equals("no")) {
                progressDialog.show()

                val user_id = RequestBody.create(MultipartBody.FORM, user_id)
                val user_name = RequestBody.create(MultipartBody.FORM, user_name.text.toString())

                val call = RetrofitBuilder.instance.retrofit.updateProfile(null,user_id,user_name)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                        Toast.makeText(this@ProfileActivity,t.message,Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful){
                            progressDialog.dismiss()
                            Toast.makeText(this@ProfileActivity,"Successfully updated",Toast.LENGTH_LONG).show()
                            GetProfile(user_id.toString())
                        }else{
                            progressDialog.dismiss()
                        }
                    }

                })

            }else{


                progressDialog.show()
                val file = File(image.path)
                val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val body = MultipartBody.Part.createFormData("profile_pic", file.name, reqFile)

                Log.d("image_url",file.name+" "+file.path)
                val user_id = RequestBody.create(MultipartBody.FORM, user_id)
                val user_name = RequestBody.create(MultipartBody.FORM, user_name.text.toString())

                val call = RetrofitBuilder.instance.retrofit.updateProfile(body,user_id,user_name)
                call.enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                        Toast.makeText(this@ProfileActivity,t.localizedMessage,Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful){
                            progressDialog.dismiss()
                            Toast.makeText(this@ProfileActivity,"Successfully updated",Toast.LENGTH_LONG).show()
                            GetProfile(user_id.toString())
                        }else{
                            progressDialog.dismiss()
                        }
                    }

                })

            }
        }

        GetProfile(user_id)
    }

    private fun GetProfile(userId: String) {

        progressDialog.show()

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
                        user_name.setText( data.getString("fullname"))
                        user_email.setText(data.getString("email_id"))

                        val image_url = data.getString("profile_pic")
                        if (image_url.isEmpty() || image_url==null){
                            Log.d("edit_profile","No image found")
                        }else {
                            val uri =  Uri.parse("https://konnecthost.in/Flagwire/"+image_url)
                            Log.d("edit_profile","show image uri "+uri)
                            Picasso.get().load(uri).into(profile_image)

                        }
                        //sessionManagement.Save_location(get_dialog_city, get_state,get_dialog_address)

                        Log.d("edit_profile", response.body()!!.string())
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
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
                    this@ProfileActivity,
                    t.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("edit_profile", t.message + " " + t.localizedMessage)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode ==GALLERY_REQUEST_CODE){

            val images = data?.getParcelableArrayListExtra<Parcelable>(Config.EXTRA_IMAGES);

            if (images != null) {
                add_photo = "yes"
                image = images[0] as Image
                profile_image.setImageURI(Uri.parse(image.path))

            }
        }
    }
}