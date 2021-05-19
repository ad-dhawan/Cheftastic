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
import com.dhawan.cheftastic.adapter.CategoryListAdapter
import com.dhawan.cheftastic.model.Category
import com.example.bookhub.util.ConnectionManager
import org.json.JSONException

class CategoryActivity : AppCompatActivity() {

    lateinit var recyclerViewCategory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CategoryListAdapter

    val categoryList = arrayListOf<Category>()

    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    lateinit var progressDialogCategory: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        toolbar = findViewById(R.id.toolbar)

        recyclerViewCategory = findViewById(R.id.recyclerViewCategory)
        layoutManager = LinearLayoutManager(this)
        layoutManager = GridLayoutManager(this, 2)

        progressDialogCategory = findViewById(R.id.progressDialogCategory)
        progressDialogCategory.visibility = View.VISIBLE

        setUpToolbar()

        showCategories()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Categories"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showCategories() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.themealdb.com/api/json/v1/1/categories.php"

        if(ConnectionManager().checkConnectivity(this)){

            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {

                try {
                    progressDialogCategory.visibility = View.GONE
                    val categories =it.getJSONArray("categories")
                    for (i in 0 until categories.length()){
                        val categoryObject = categories.getJSONObject(i)
                        val category = Category(
                            categoryObject.getString("strCategory"),
                            categoryObject.getString("strCategoryThumb")
                        )
                        categoryList.add(category)
                        recyclerAdapter = CategoryListAdapter(this, categoryList)

                        recyclerViewCategory.adapter = recyclerAdapter
                        recyclerViewCategory.layoutManager = layoutManager
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