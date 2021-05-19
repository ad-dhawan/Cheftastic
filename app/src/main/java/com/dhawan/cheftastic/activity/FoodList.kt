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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.adapter.CategoryAdapter
import com.dhawan.cheftastic.adapter.MealAdapter
import com.dhawan.cheftastic.model.Category
import com.dhawan.cheftastic.model.Meals
import com.example.bookhub.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_food_list.*
import org.json.JSONException
import org.json.JSONObject

class FoodList : AppCompatActivity() {

    lateinit var recyclerViewMeals: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: MealAdapter

    val mealList = arrayListOf<Meals>()

    private var category: String? = ""
    private var cuisine: String? = ""
    private var indian: String? = ""
    private var american: String? = ""
    private var chinese: String? = ""
    private var italian: String? = ""

    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)

        toolbar = findViewById(R.id.toolbar)

        recyclerViewMeals = findViewById(R.id.recyclerViewMeals)
        layoutManager = LinearLayoutManager(this)
        layoutManager = GridLayoutManager(this,2)

        if(intent != null){
            category = intent.getStringExtra("strCategory")
            cuisine = intent.getStringExtra("strArea")
            indian = intent.getStringExtra("Indian")
            american = intent.getStringExtra("American")
            chinese = intent.getStringExtra("Chinese")
            italian = intent.getStringExtra("Italian")
        } else {
            finish()
            Toast.makeText(this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
        }

        setUpToolbar()

        when {
            category != null -> {
                showCategory()
            }
            cuisine != null -> {
                showCuisine(cuisine!!)
            }
            indian != null -> {
                showCuisine(indian!!)
            }
            american != null -> {
                showCuisine(american!!)
            }
            chinese != null -> {
                showCuisine(chinese!!)
            }
            italian != null -> {
                showCuisine(italian!!)
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Meals"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showCategory() {

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.themealdb.com/api/json/v1/1/filter.php?c=${category}"

        if(ConnectionManager().checkConnectivity(this)){

            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

                try {
                    val meals =it.getJSONArray("meals")
                    for (i in 0 until meals.length()){
                        val mealObject = meals.getJSONObject(i)
                        val meal = Meals(
                            mealObject.getString("strMeal"),
                            mealObject.getString("strMealThumb"),
                            mealObject.getString("idMeal")
                        )
                        mealList.add(meal)
                        recyclerAdapter = MealAdapter(this, mealList)

                        recyclerViewMeals.adapter = recyclerAdapter
                        recyclerViewMeals.layoutManager = layoutManager
                    }
                } catch (e: JSONException){
                    Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener {

                Toast.makeText(this, "Server not responding!", Toast.LENGTH_SHORT).show()

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

    private fun showCuisine(type: String) {

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.themealdb.com/api/json/v1/1/filter.php?a=${type}"

        if(ConnectionManager().checkConnectivity(this)){

            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

                try {
                    val meals =it.getJSONArray("meals")
                    for (i in 0 until meals.length()){
                        val mealObject = meals.getJSONObject(i)
                        val meal = Meals(
                            mealObject.getString("strMeal"),
                            mealObject.getString("strMealThumb"),
                            mealObject.getString("idMeal")
                        )
                        mealList.add(meal)
                        recyclerAdapter = MealAdapter(this, mealList)

                        recyclerViewMeals.adapter = recyclerAdapter
                        recyclerViewMeals.layoutManager = layoutManager
                    }
                } catch (e: JSONException){
                    Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener {

                Toast.makeText(this, "Server not responding!", Toast.LENGTH_SHORT).show()

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