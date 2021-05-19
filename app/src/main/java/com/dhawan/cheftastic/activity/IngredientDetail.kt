package com.dhawan.cheftastic.activity

import android.app.AlertDialog
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.adapter.MealAdapter
import com.dhawan.cheftastic.model.Meals
import com.example.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_ingredient_detail.*
import org.json.JSONException

class IngredientDetail : AppCompatActivity() {

    lateinit var recyclerViewMeals: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: MealAdapter

    val mealList = arrayListOf<Meals>()

    lateinit var noMealLayout: TextView

    private var ingredient: String? = ""
    private lateinit var ingredientImage: ImageView

    lateinit var progressDialogIngredientDetail: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_detail)

        recyclerViewMeals = findViewById(R.id.recyclerViewIngredientMeals)
        layoutManager = LinearLayoutManager(this)
        layoutManager = GridLayoutManager(this,2)

        progressDialogIngredientDetail = findViewById(R.id.progressDialogIngredientDetail)
        progressDialogIngredientDetail.visibility = View.VISIBLE

        ingredientImage = findViewById(R.id.imgIngredient)

        noMealLayout = findViewById(R.id.noMealLayout)
        noMealLayout.visibility = View.GONE

        if(intent != null){
            ingredient = intent.getStringExtra("strIngredient")
        } else {
            finish()
            Toast.makeText(this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
        }

        textIngredient.text = ingredient

        showImage()

        showMeals()

    }

    private fun showImage(){
        val imageUrl = "https://www.themealdb.com/images/ingredients/${ingredient}.png"
        Picasso.get().load(imageUrl).into(ingredientImage)
    }


    private fun showMeals() {

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.themealdb.com/api/json/v1/1/filter.php?i=${ingredient}"

        if(ConnectionManager().checkConnectivity(this)){

            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

                try {
                    progressDialogIngredientDetail.visibility = View.GONE
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
                    noMealLayout.visibility = View.VISIBLE
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

}