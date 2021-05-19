package com.dhawan.cheftastic.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
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
import com.dhawan.cheftastic.adapter.IngredientsRecipeAdapter
import com.dhawan.cheftastic.model.IngredientsRecipe
import com.example.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_recipe.*
import org.json.JSONException

class RecipeActivity : AppCompatActivity() {

    lateinit var recyclerViewIngredients: RecyclerView
    lateinit var recyclerViewMeasurement: RecyclerView
    lateinit var layoutManagerIngredients: RecyclerView.LayoutManager
    lateinit var layoutManagerMeasurements: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: IngredientsRecipeAdapter

    val ingredientsList = arrayListOf<IngredientsRecipe>()
    val measurementsList = arrayListOf<IngredientsRecipe>()

    lateinit var progressDialogRecipe: RelativeLayout

    var mealId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        recyclerViewIngredients = findViewById(R.id.recyclerViewIngredients)
        recyclerViewMeasurement = findViewById(R.id.recyclerViewMeasurement)
        layoutManagerIngredients = LinearLayoutManager(this)
        layoutManagerMeasurements = LinearLayoutManager(this)

        recyclerViewIngredients.isNestedScrollingEnabled = false
        recyclerViewMeasurement.isNestedScrollingEnabled = false

        progressDialogRecipe = findViewById(R.id.progressDialogRecipe)
        progressDialogRecipe.visibility = View.VISIBLE

        if (intent != null){
            mealId = intent.getStringExtra("idMeal")
        } else {
            finish()
            Toast.makeText(this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
        }

        if (mealId != null){
            showRecipe()
        }
    }

    private fun showRecipe() {

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=${mealId}"

        if(ConnectionManager().checkConnectivity(this)){

            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

                try {
                    progressDialogRecipe.visibility = View.GONE

                    val ingredients =it.getJSONArray("meals")
                    val ingredientObject = ingredients.getJSONObject(0)
                    Picasso.get().load(ingredientObject.getString("strMealThumb")).into(imgMealImage)
                    txtMealName.text = ingredientObject.getString("strMeal")
                    txtMealTags.text = ingredientObject.getString("strTags")
                    txtMealCategory.text = ingredientObject.getString("strCategory")
                    txtMealCuisine.text = ingredientObject.getString("strArea")
                    txtMealRecipe.text = ingredientObject.getString("strInstructions")


                    for (i in 1..20){
                        val ingredient = IngredientsRecipe(
                            ingredientObject.getString("strIngredient$i")
                        )
                        ingredientsList.add(ingredient)

                        if (ingredientObject.getString("strIngredient$i").isNullOrEmpty()
                            || ingredientObject.getString("strIngredient$i").isNullOrBlank()){
                            ingredientsList.remove(ingredient)
                        }

                        recyclerAdapter = IngredientsRecipeAdapter(this, ingredientsList)
                        recyclerViewIngredients.adapter = recyclerAdapter
                        recyclerViewIngredients.layoutManager = layoutManagerIngredients
                    }


                    for (i in 1..20){
                        val measurement = IngredientsRecipe(
                            ingredientObject.getString("strMeasure$i")
                        )
                        measurementsList.add(measurement)

                        if (ingredientObject.getString("strMeasure$i").isNullOrEmpty()
                            || ingredientObject.getString("strMeasure$i").isNullOrBlank()){
                            measurementsList.remove(measurement)
                        }

                        recyclerAdapter = IngredientsRecipeAdapter(this, measurementsList)
                        recyclerViewMeasurement.adapter = recyclerAdapter
                        recyclerViewMeasurement.layoutManager = layoutManagerMeasurements
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

}