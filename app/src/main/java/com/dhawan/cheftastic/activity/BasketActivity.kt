package com.dhawan.cheftastic.activity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.dhawan.cheftastic.R
import com.dhawan.cheftastic.adapter.ViewPagerAdapter
import com.dhawan.cheftastic.fragments.CartFragment
import com.dhawan.cheftastic.fragments.IngredientFragment
import kotlinx.android.synthetic.main.activity_basket.*


class BasketActivity : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private val tabLayoutAdapter = ViewPagerAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)

        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()
        setUpTabLayout()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Shopping Basket"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpTabLayout(){
        tabLayoutAdapter.addFragment(IngredientFragment(), "Ingredients")
        tabLayoutAdapter.addFragment(CartFragment(this), "List")
        viewPager.adapter = tabLayoutAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home){
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}