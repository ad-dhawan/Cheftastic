package com.dhawan.cheftastic.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.adapter.IngredientsAdapter
import com.dhawan.cheftastic.database.IngredientDatabase
import com.dhawan.cheftastic.database.IngredientEntity
import com.dhawan.cheftastic.model.Ingredients
import com.example.bookhub.util.ConnectionManager
import org.json.JSONException

class CartFragment(val contextParam:Context) : Fragment() {

    lateinit var recyclerViewList: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: IngredientsAdapter

    val ingredientsList = arrayListOf<Ingredients>()

    lateinit var noListLayout: TextView

    lateinit var progressDialogCart: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerViewList = view.findViewById(R.id.recyclerViewCart)
        layoutManager = LinearLayoutManager(activity as Context)

        progressDialogCart = view.findViewById(R.id.progressDialogCart)
        progressDialogCart.visibility = View.VISIBLE

        showList()

        noListLayout = view.findViewById(R.id.noListLayout)
        noListLayout.visibility = View.INVISIBLE

        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
            progressDialogCart.visibility = View.VISIBLE
        }
    }


    private fun showList(){

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            try {
                progressDialogCart.visibility = View.GONE
                val queue = Volley.newRequestQueue(activity as Context)

                val url = "https://www.themealdb.com/api/json/v1/1/list.php?i=list"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {

                        ingredientsList.clear()


                        val ingredients =it.getJSONArray("meals")

                            for (i in 0 until ingredients.length()) {
                                val ingredientObject = ingredients.getJSONObject(i)

                                val ingredientEntity=IngredientEntity(
                                    ingredientObject.getString("idIngredient"),
                                    ingredientObject.getString("strIngredient")
                                )

                                if(DBAsynTask(contextParam,ingredientEntity,1).execute().get())
                                {

                                    val ingredient = Ingredients(
                                        ingredientObject.getString("idIngredient"),
                                        ingredientObject.getString("strIngredient")
                                    )

                                    ingredientsList.add(ingredient)

                                    recyclerAdapter = IngredientsAdapter(
                                        activity as Context,
                                        ingredientsList
                                    )

                                    recyclerViewList.adapter = recyclerAdapter

                                    recyclerViewList.layoutManager = layoutManager

                                }
                            }
                            if(ingredientsList.size==0){
                                noListLayout.visibility = View.VISIBLE
                            }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            activity as Context,
                            "Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    })

                {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

            }catch (e: JSONException){
                Toast.makeText(activity as Context,"Some Unexpected error occured!!!", Toast.LENGTH_SHORT).show()
            }

        }else
        {
            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(activity as Activity)//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()
        }

    }


    class DBAsynTask(val context: Context, private val ingredientEntity: IngredientEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, IngredientDatabase::class.java, "ingredient-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            /*
            * Mode 1->check if restaurant is in favourites
            * Mode 2->Save the restaurant into DB as favourites
            * Mode 3-> Remove the favourite restaurant*/


            when (mode) {
                1 -> {
                    val restaurant: IngredientEntity? = db.ingredientDao()
                        .getIngredientById(ingredientEntity.ingredientId)
                    db.close()
                    return restaurant != null
                }
                else->return false

            }

        }


    }


}