package com.dhawan.cheftastic.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.adapter.IngredientsAdapter
import com.dhawan.cheftastic.model.Ingredients
import kotlinx.android.synthetic.main.fragment_ingredient.*
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class IngredientFragment : Fragment() {

    lateinit var recyclerViewIngredient: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: IngredientsAdapter

    val ingredientsList = arrayListOf<Ingredients>()

    private lateinit var noIngredientsLayout: TextView

    lateinit var progressDialogIngredients: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ingredient, container, false)

        recyclerViewIngredient = view.findViewById(R.id.recyclerViewIngredient)
        layoutManager = LinearLayoutManager(activity as Context)

        progressDialogIngredients = view.findViewById(R.id.progressDialogIngredients)
        progressDialogIngredients.visibility = View.VISIBLE

        showIngredients()

        val etSearchIngredient: EditText = view.findViewById(R.id.etSearchIngredient)

        noIngredientsLayout = view.findViewById(R.id.noIngredientsFoundLayout)
        noIngredientsLayout.visibility = View.INVISIBLE

        etSearchIngredient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })

        return view
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//            fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
//        }
//    }

    private fun showIngredients() {
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "https://www.themealdb.com/api/json/v1/1/list.php?i=list"


            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

                try {
                    progressDialogIngredients.visibility = View.GONE
                    val ingredients =it.getJSONArray("meals")
                    for (i in 0 until ingredients.length()){
                        val ingredientObject = ingredients.getJSONObject(i)
                        val ingredient = Ingredients(
                            ingredientObject.getString("idIngredient"),
                            ingredientObject.getString("strIngredient")
                        )
                        ingredientsList.add(ingredient)
                        recyclerAdapter = IngredientsAdapter(activity as Context, ingredientsList)

                        recyclerViewIngredient.adapter = recyclerAdapter
                        recyclerViewIngredient.layoutManager = layoutManager
                    }

                } catch (e: JSONException){
                    Toast.makeText(activity as Context, "Error Occurred!", Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener {

                Toast.makeText(activity as Context, "Unexpected error occurred!", Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)

    }

    private fun filter(text: String) {
        val filteredList = arrayListOf<Ingredients>()
        for (item in ingredientsList) {
            if (item.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(item)
            }
        }

        if(filteredList.size==0){
            noIngredientsLayout.visibility=View.VISIBLE
        }
        else{
            noIngredientsLayout.visibility=View.INVISIBLE
        }

        recyclerAdapter.filterList(filteredList)
    }

}