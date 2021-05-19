package com.dhawan.cheftastic.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.adapter.CategoryAdapter
import com.dhawan.cheftastic.adapter.CuisineAdapter
import com.dhawan.cheftastic.model.Category
import com.dhawan.cheftastic.model.Cuisine
import com.example.bookhub.util.ConnectionManager
import org.json.JSONException

class CuisinesActivity : AppCompatActivity() {

    lateinit var recyclerViewCuisines: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CuisineAdapter

    val cuisineList = arrayListOf<Cuisine>()

    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    lateinit var progressDialogCuisines: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuisines)

        toolbar = findViewById(R.id.toolbar)

        recyclerViewCuisines = findViewById(R.id.recyclerViewCuisines)
        layoutManager = LinearLayoutManager(this)

        progressDialogCuisines = findViewById(R.id.progressDialogCuisines)
        progressDialogCuisines.visibility = View.VISIBLE

        setUpToolbar()

        showCuisines()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cuisines"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showCuisines() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.themealdb.com/api/json/v1/1/list.php?a=list"

        if(ConnectionManager().checkConnectivity(this)){

            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

                try {
                    progressDialogCuisines.visibility = View.GONE
                    val cuisines =it.getJSONArray("meals")
                    for (i in 0 until cuisines.length()){
                        val cuisineObject = cuisines.getJSONObject(i)
                        val cuisine = Cuisine(
                            cuisineObject.getString("strArea")
                        )
                        cuisineList.add(cuisine)
                        recyclerAdapter = CuisineAdapter(this, cuisineList)

                        recyclerViewCuisines.adapter = recyclerAdapter
                        recyclerViewCuisines.layoutManager = layoutManager
                    }
                } catch (e: JSONException){
                    Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener {

                Toast.makeText(this, "Unexpected error occurred!", Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)

        } else{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Failure!")
            dialog.setMessage("Internet not available")
            dialog.setPositiveButton("Open settings"){text, listner ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home){
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}