package com.bandesha.convocards

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bandesha.convocards.adapters.CardAdapter
import com.bandesha.convocards.adapters.CategoryAdapter
import com.bandesha.convocards.adapters.OnCategoryClicked
import com.bandesha.convocards.databinding.ActivityCategoryBinding
import com.bandesha.convocards.models.Cards
import com.bandesha.convocards.models.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoryActivity : AppCompatActivity(), OnCategoryClicked
{
    private val TAG = "CategoryActivity"
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var database1: DatabaseReference
    private var list:MutableList<Category> = mutableListOf<Category>()

    private lateinit var adapter: CategoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val window =this.window
        window.statusBarColor = ContextCompat.getColor(this,R.color.white)
        window.navigationBarColor = getColor(R.color.white)

        fetchCategory()
        clicks()

    }
    private fun clicks()
    {
        binding.addCards.setOnClickListener {
            startActivity(Intent(this,AddCardsActivity::class.java))
        }
    }
    fun fetchCategory() {


        database1 = FirebaseDatabase.getInstance().getReference("categories")

        database1.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (dataSnapshot in snapshot.children)
                {

                    val category = dataSnapshot.getValue(Category::class.java)
                    category?.let { list.add(it) }
                    loadAdapter()
                }
                Log.d(TAG, "onDataChange: "+list.size)
                loadAdapter()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadAdapter() {

        binding.circularProgress.visibility = View.GONE
        binding.layout.visibility = View.VISIBLE
        adapter = CategoryAdapter(this,list,this)
        binding.recyclerCategory.layoutManager =
            GridLayoutManager(this,2)
        binding.recyclerCategory.adapter = adapter

    }

    override fun onDataReceived(category: Category) {
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("category", category.categoryName);
        startActivity(intent);
    }


}