package com.bandesha.convocards

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bandesha.convocards.databinding.ActivityAddCardsBinding
import com.bandesha.convocards.databinding.ActivitySplash1Binding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Splash1Activity : AppCompatActivity() {
    private val TAG = "Splash1Activity"
    private lateinit var binding: ActivitySplash1Binding




    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivitySplash1Binding.inflate(layoutInflater)
        setContentView(binding.root)


        val window =this.window
        window.statusBarColor = ContextCompat.getColor(this,R.color.white)

        window.navigationBarColor = getColor(R.color.white)
        Handler().postDelayed(
            {
                startActivity(Intent(this,CategoryActivity::class.java))
                finish()
            },1000)

    }

}