package com.example.flagwire.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flagwire.R
import com.example.flagwire.adapter.CategoryAdapter
import com.example.flagwire.model.category.CategoryDataItem
import com.example.flagwire.model.category.CategoryResponse
import com.example.flagwire.others.SessionManagement
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_flag_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlagListActivity : AppCompatActivity() {

    private lateinit var recyclerview: RecyclerView
    private lateinit var btn_cancel: MaterialButton
    lateinit var progressDialog: ProgressDialog

    var categorylist = ArrayList<CategoryDataItem>()
    var categoryid = ArrayList<String>()
    var status_reminder = ArrayList<String>()
    lateinit var sessionManagement:SessionManagement


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flag_list)

        recyclerview = findViewById(R.id.recyclerview_category_list)
        btn_cancel = findViewById(R.id.btn_cancel)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("loading...")
        sessionManagement = SessionManagement(this)

        btn_cancel.setOnClickListener {

            finish()
        }

        GetCategoryList()

        btn_next.setOnClickListener {


            if (categorylist.isEmpty()){

                Log.d("category_list","empty")
            }else {

                for (i in 0 until categorylist.size) {
                    categoryid.add(categorylist.get(i).categoryId!!)
                }

                for (i in 0 until categoryid.size) {
                    if (categorylist.get(i).reminder_status_red.equals("true") && categorylist.get(i).reminder_status_green.equals(
                            "true"
                        )
                    ) {
                        status_reminder.add("category_id " + categoryid.get(i) + " reminder " + "2")
                    } else if (categorylist.get(i).reminder_status_red.equals("true") && categorylist.get(
                            i
                        ).reminder_status_green.equals("false")
                    ) {
                        status_reminder.add("category_id " + categoryid.get(i) + " reminder " + "1")
                    } else if (categorylist.get(i).reminder_status_red.equals("false") && categorylist.get(
                            i
                        ).reminder_status_green.equals("true")
                    ) {
                        status_reminder.add("category_id " + categoryid.get(i) + " reminder " + "0")
                    } else if (categorylist.get(i).reminder_status_red.equals("false") && categorylist.get(
                            i
                        ).reminder_status_green.equals("false")
                    ) {
                        status_reminder.add("category_id " + categoryid.get(i) + " reminder " + "3")
                    }
                }

                Log.d("get_category_id", status_reminder.toString())

                val intent = Intent(this,LocationActivity::class.java)
                intent.putStringArrayListExtra("reminder_list", status_reminder)
                startActivity(intent);

                //startActivity(Intent(this,LocationActivity::class.java))
            }
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
    }

    private fun GetCategoryList() {
        progressDialog.show()

        val call = RetrofitBuilder.instance.retrofit.category()
        call.enqueue(object : Callback<CategoryResponse> {
            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Toast.makeText(
                    this@FlagListActivity, "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(
                call: Call<CategoryResponse>,
                response: Response<CategoryResponse>
            ) {

                if (response.isSuccessful) {
                    progressDialog.dismiss()

                    if (response.body()!!.message.equals("success", false)) {

                        categorylist = response.body()!!.data as ArrayList<CategoryDataItem>
                        val adapter = CategoryAdapter(categorylist, this@FlagListActivity)
                        val linearLayoutManager =
                            LinearLayoutManager(this@FlagListActivity, LinearLayout.VERTICAL, false)
                        recyclerview.layoutManager = linearLayoutManager
                        recyclerview.adapter = adapter

                    } else if (response.body()!!.message.equals("No Data Available", false)) {
                        Toast.makeText(
                            this@FlagListActivity,
                            "No flag list found",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            this@FlagListActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@FlagListActivity,
                        response.body().toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        })
    }
}