package com.dhawan.cheftastic.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.adapter.IngredientsAdapter
import com.dhawan.cheftastic.adapter.SearchMealAdapter
import com.dhawan.cheftastic.model.Ingredients
import com.dhawan.cheftastic.model.Meals
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONException

class SearchMealActivity : AppCompatActivity() {

    lateinit var recyclerViewSearch: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: SearchMealAdapter

    val mealList = arrayListOf<Meals>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerViewSearch = findViewById(R.id.recyclerViewSearch)
        layoutManager = LinearLayoutManager(this)

        btnSearchMeal.setOnClickListener { showMeals() }

    }

    private fun showMeals() {
        mealList.clear()

        val queue = Volley.newRequestQueue(this)
        val item = etSearchMeal.text
        val url = "https://www.themealdb.com/api/json/v1/1/search.php?s=${item}"


        val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

            try {
                val meals =it.getJSONArray("meals")
                for (i in 0 until meals.length()){
                    val mealObject = meals.getJSONObject(i)
                    val ingredient = Meals(
                        mealObject.getString("strMeal"),
                        mealObject.getString("strMealThumb"),
                        mealObject.getString("idMeal")
                    )
                    mealList.add(ingredient)
                    recyclerAdapter = SearchMealAdapter(this, mealList)

                    recyclerViewSearch.adapter = recyclerAdapter
                    recyclerViewSearch.layoutManager = layoutManager
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

    }

}