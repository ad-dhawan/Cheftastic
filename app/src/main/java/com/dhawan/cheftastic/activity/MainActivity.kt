package com.dhawan.cheftastic.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
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
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    lateinit var recyclerViewCategories: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CategoryAdapter

    val categoryList = arrayListOf<Category>()

    lateinit var recyclerViewTopPicks: RecyclerView
    lateinit var layoutManagerTopPicks: RecyclerView.LayoutManager
    lateinit var recyclerAdapterTopPicks: MealAdapter

    val TopPicksList = arrayListOf<Meals>()

    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    lateinit var progressDialogCategoryMain: RelativeLayout

    lateinit var progressDialogTopPicksMain: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)

        recyclerViewCategories = findViewById(R.id.recyclerViewCategories)
        layoutManager = LinearLayoutManager(this)
        layoutManager = GridLayoutManager(this, 3)
        recyclerViewCategories.isNestedScrollingEnabled = false

        recyclerViewTopPicks = findViewById(R.id.recyclerViewTopPicks)
        layoutManagerTopPicks = LinearLayoutManager(this)
        (layoutManagerTopPicks as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL

        progressDialogCategoryMain = findViewById(R.id.progressDialogCategoryMain)
        progressDialogCategoryMain.visibility = View.VISIBLE

        progressDialogTopPicksMain = findViewById(R.id.progressDialogTopPicksMain)
        progressDialogTopPicksMain.visibility = View.VISIBLE

        setUpToolbar()

        showCategories()

        showTopPicks()

        txtViewAllCategories.setOnClickListener { openCategoryActivity() }

        txtViewAllCuisines.setOnClickListener { openCuisineActivity() }

        layoutIndianCuisine.setOnClickListener { openIndianCuisine() }

        layoutAmericanCuisine.setOnClickListener { openAmericanCuisine() }

        layoutChineseCuisine.setOnClickListener { openChineseCuisine() }

        layoutItalianCuisine.setOnClickListener { openItalianCuisine() }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cheftastic"
    }

    private fun openCategoryActivity(){
        startActivity(Intent(this, CategoryActivity::class.java))
    }

    private fun openCuisineActivity(){
        startActivity(Intent(this, CuisinesActivity::class.java))
    }

    private fun openIndianCuisine(){
        val openIntent = Intent(this, FoodList::class.java)
        openIntent.putExtra("Indian", txtIndianCuisine.text)
        this.startActivity(openIntent)
    }

    private fun openAmericanCuisine(){
        val openIntent = Intent(this, FoodList::class.java)
        openIntent.putExtra("American", txtAmericanCuisine.text)
        this.startActivity(openIntent)
    }

    private fun openChineseCuisine(){
        val openIntent = Intent(this, FoodList::class.java)
        openIntent.putExtra("Chinese", txtChineseCuisine.text)
        this.startActivity(openIntent)
    }

    private fun openItalianCuisine(){
        val openIntent = Intent(this, FoodList::class.java)
        openIntent.putExtra("Italian", txtItalianCuisine.text)
        this.startActivity(openIntent)
    }

    private fun showCategories() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.themealdb.com/api/json/v1/1/categories.php"

        if(ConnectionManager().checkConnectivity(this)){

            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

                try {
                    progressDialogCategoryMain.visibility = View.GONE
                    val categories =it.getJSONArray("categories")
                    for (i in 0..5){
                        val categoryObject = categories.getJSONObject(i)
                        val category = Category(
                            categoryObject.getString("strCategory"),
                            categoryObject.getString("strCategoryThumb")
                        )
                        categoryList.add(category)
                        recyclerAdapter = CategoryAdapter(this, categoryList)

                        recyclerViewCategories.adapter = recyclerAdapter
                        recyclerViewCategories.layoutManager = layoutManager
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



    private fun showTopPicks() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.themealdb.com/api/json/v1/1/random.php"

        for (i in 0..4){

            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

                try {
                    progressDialogTopPicksMain.visibility = View.GONE

                    val meals =it.getJSONArray("meals")
                        val mealObject = meals.getJSONObject(0)
                        val meal = Meals(
                            mealObject.getString("strMeal"),
                            mealObject.getString("strMealThumb"),
                            mealObject.getString("idMeal")
                        )
                        TopPicksList.add(meal)
                        recyclerAdapterTopPicks = MealAdapter(this, TopPicksList)

                        recyclerViewTopPicks.adapter = recyclerAdapterTopPicks
                        recyclerViewTopPicks.layoutManager = layoutManagerTopPicks
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

        }


    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id =item.itemId

        if(id == R.id.action_search)
            startActivity(Intent(this, SearchMealActivity::class.java))
        if(id == R.id.action_basket)
            startActivity(Intent(this, BasketActivity::class.java))

        return super.onOptionsItemSelected(item)
    }
}