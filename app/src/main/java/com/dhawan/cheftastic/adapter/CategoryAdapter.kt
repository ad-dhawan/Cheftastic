package com.dhawan.cheftastic.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.activity.CategoryActivity
import com.dhawan.cheftastic.activity.FoodList
import com.dhawan.cheftastic.model.Category
import com.squareup.picasso.Picasso

class CategoryAdapter(val context: Context, private val itemList: ArrayList<Category>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View): RecyclerView.ViewHolder(view){
        val category: TextView = view.findViewById(R.id.txtCategory)
        val image: ImageView = view.findViewById(R.id.imgCategoryImage)
        val layout: CardView = view.findViewById(R.id.cardLayoutCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_main_item, parent, false)

        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = itemList[position]
        holder.category.text = category.category
        Picasso.get().load(category.image).into(holder.image)

        holder.layout.setOnClickListener {
            val openIntent = Intent(context, FoodList::class.java)
            openIntent.putExtra("strCategory", category.category)
            context.startActivity(openIntent)
        }
    }

}
