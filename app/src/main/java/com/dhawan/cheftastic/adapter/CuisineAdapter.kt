package com.dhawan.cheftastic.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.activity.FoodList
import com.dhawan.cheftastic.model.Cuisine

class CuisineAdapter(val context: Context, private val itemList: ArrayList<Cuisine>): RecyclerView.Adapter<CuisineAdapter.CuisineViewHolder>() {

    class CuisineViewHolder(view: View): RecyclerView.ViewHolder(view){
        val cuisine: TextView = view.findViewById(R.id.txtCuisine)
        val layout: LinearLayout = view.findViewById(R.id.layoutCuisine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cuisine_item, parent, false)

        return CuisineViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CuisineViewHolder, position: Int) {
        val cuisine = itemList[position]
        holder.cuisine.text = cuisine.cuisine

        holder.layout.setOnClickListener {
            val openIntent = Intent(context, FoodList::class.java)
            openIntent.putExtra("strArea", cuisine.cuisine)
            context.startActivity(openIntent)
        }
    }

}