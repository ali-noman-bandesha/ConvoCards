package com.bandesha.convocards


import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bandesha.convocards.databinding.ActivityAddCardsBinding
import com.bandesha.convocards.models.Cards
import com.bandesha.convocards.models.Category
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddCardsActivity : AppCompatActivity()
{
    private val PICK_AUDIO_REQUEST = 1
    private val PICK_ICON_REQUEST = 2
    private val PICK_GIF_REQUEST = 3
    private val TAG = "AddCardsActivity"
    private lateinit var ref :DatabaseReference

    private lateinit var englishAudio :String
    private lateinit var gifFile :String

    private lateinit var englishName :String
    private lateinit var gifName :String

    private lateinit var englishTitle :String

    private lateinit var dialog : BottomSheetDialog


    private lateinit var englishUri: Uri
    private lateinit var gifUri1: Uri
    private lateinit var binding :ActivityAddCardsBinding

    private var alertDialog: AlertDialog? = null

    private lateinit var storageReference: StorageReference
    private lateinit var gifStorageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: DatabaseReference
    private lateinit var database1: DatabaseReference



    private lateinit var iconUri: Uri
    private lateinit var iconName :String
    private lateinit var iconFile :String
    private lateinit var selectedSpinner :String


    private var adapter: ArrayAdapter<*>? = null
    var spinner: Spinner? = null
    private var list  = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        fetchCategory()
        clicks()


    }
    private fun init(){

        val window =this.window
        window.statusBarColor = ContextCompat.getColor(this,R.color.dull_button)
        window.navigationBarColor = getColor(R.color.clickable_button)

        Glide.with(this).asGif().load(R.drawable.add_image)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Handle load failure if needed
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Ensure the GIF drawable is not null
                    resource?.setLoopCount(1)
                    return false // Let Glide handle the onResourceReady event
                }
            })
            .into(binding.gifView)
        storageReference = FirebaseStorage.getInstance().getReference().child("audios")
        gifStorageReference = FirebaseStorage.getInstance().getReference().child("gifs")
        firestore = FirebaseFirestore.getInstance()


    }
    private fun clicks()
    {
        binding.addCategoryBtn.setOnClickListener {
            showBottomSheet()
        }
        binding.englishAudio.setOnClickListener {
            openEnglishAudioPicker()
        }
        binding.gifView.setOnClickListener {
            openGifPicker()
        }
        binding.loginButton.setOnClickListener {
            isValid()
        }
    }
    private fun isValid() {
        if (!::gifUri1.isInitialized || !::englishUri.isInitialized) {
            Toast.makeText(this, "Please select both GIF and audio files", Toast.LENGTH_SHORT).show()
        }
        else if (binding.emailEdittext.text.toString().length<3)
        {
            Toast.makeText(this, "Please enter names", Toast.LENGTH_SHORT).show()
        }
        else
        {
            showProgressDialog()
            uploadEnglishAudioFile(englishUri,englishName)
        }

    }

    private fun openIconPicker() {
        Log.d(TAG, "openGifPicker: ")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/gif"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select Gif"), PICK_ICON_REQUEST)
    }
    private fun openGifPicker() {
        Log.d(TAG, "openGifPicker: ")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/gif"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select Gif"), PICK_GIF_REQUEST)
    }
    private fun openEnglishAudioPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), PICK_AUDIO_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { engAudioUri ->
                // Handle the selected audio URI
                // You can play the audio or perform other operations with the URI
//                playSelectedAudio(audioUri)
                Log.d(TAG, "onActivityResult: english result")
                englishUri = engAudioUri
                englishName = getFileName(engAudioUri)!!
                binding.englishAudio.text = englishName
            }
        }
        else if (requestCode == PICK_ICON_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { icon->
                iconUri = icon
                iconName = getFileName(iconUri)!!
                val imgView = dialog.findViewById<ImageView>(R.id.addIcon)
                Glide.with(this).load(iconUri).into(imgView!!)

            }
        }
        else if (requestCode == PICK_GIF_REQUEST && resultCode == RESULT_OK) {

            data?.data?.let { gifUri ->
                // Handle the selected audio URI
                // You can play the audio or perform other operations with the URI
//                playSelectedAudio(audioUri)
                Toast.makeText(this, "gif", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onActivityResult: gif result")
                gifUri1 = gifUri
                gifName = getFileName(gifUri)!!

                Glide.with(this)
                    .asGif()
                    .load(gifUri1)
                    .apply(RequestOptions()) // Optional placeholder
                    .transition(DrawableTransitionOptions.withCrossFade()) // Optional crossfade effect
                    .into(binding.gifView)

            }
        }
    }
    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }
    private fun uploadEnglishAudioFile(audioUri: Uri, fileName: String) {

        val ref = storageReference.child(fileName)
        ref.putFile(audioUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    englishAudio = uri.toString()
//                    (fileName, downloadUrl)
                    Log.d(TAG, "uploadEnglishAudioFile: $englishAudio")
                    uploadGifFile(gifUri1,gifName)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun uploadGifFile(audioUri: Uri, fileName: String) {
        val ref = gifStorageReference.child(fileName)
        ref.putFile(audioUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    gifFile= uri.toString()
                    Log.d(TAG, "uploadGifFile: $gifFile")
                    getNames()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun getNames()
    {
        englishTitle = binding.emailEdittext.text.toString()
        uploadNames()
    }
    private fun uploadNames(){

        selectedSpinner = spinner!!.selectedItem.toString()

        database = FirebaseDatabase.getInstance().reference
        Log.d(TAG, "uploadNames: "+selectedSpinner)
        val card = Cards(gifFile,englishAudio,englishTitle,selectedSpinner)
        database.child("cards").push().setValue(card).addOnSuccessListener {
            Log.d(TAG, "uploadNames: ")
            Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT).show()
            hideProgressDialog()
            onBackPressed()
        }
            .addOnFailureListener {
            hideProgressDialog()
            Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "uploadNames:fail ")

        }

    }
    fun showProgressDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress_dialog_layout)
        builder.setMessage("Uploading to database")
        builder.setCancelable(false) // Prevent user from canceling the dialog
        alertDialog = builder.create()
        alertDialog!!.show()
    }
    fun hideProgressDialog() {
        alertDialog!!.dismiss()

    }

    fun fetchCategory() {


        database1 = FirebaseDatabase.getInstance().getReference("categories")

        database1.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (dataSnapshot in snapshot.children)
                {

                    val category = dataSnapshot.getValue(Category::class.java)

                    val name = category?.categoryName
                    list.add(name!!)
                }
                Log.d(TAG, "onDataChange: "+list.size)

                load(list)}

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun load(list :List<String>){

        spinner = binding.spinnerView
        if (list.isEmpty()|| list.size==0){
            binding.spinnerView.visibility= View.GONE
        }
        else {
            adapter = ArrayAdapter(applicationContext, R.layout.spinner_text_view, list)
            (adapter as ArrayAdapter<String>).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner!!.setAdapter(adapter)
        }

    }



    private fun showBottomSheet()
    {
        dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.add_category_bottom_sheet,null)
        dialog.edgeToEdgeEnabled
        dialog.onAttachedToWindow()
        dialog.setCancelable(false)
        dialog.dismissWithAnimation
        dialog.setContentView(view)
        dialog.show()
        val confirmBtn = view.findViewById<Button>(R.id.confirmBtn)
        val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)
        val name = view.findViewById<EditText>(R.id.myRemarks)
        val icon = view.findViewById<ImageView>(R.id.addIcon)

        icon.setOnClickListener {
            openIconPicker()
        }
        cancelBtn.setOnClickListener {
            dismiss()
        }
        confirmBtn.setOnClickListener {
            val category = name.text.toString()
            Log.d(TAG, "showBottomSheet: "+category)
            showProgressDialog()
            dismiss()
            uploadIconFile(iconUri,iconName,category)
            }
    }

    private fun uploadIconFile(audioUri: Uri, fileName: String,categoryName :String) {
        val ref = gifStorageReference.child(fileName)
        ref.putFile(audioUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    iconFile= uri.toString()
                    Log.d(TAG, "uploadIconFile: $iconFile")
                    upload(categoryName,iconFile)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
    }
    private fun upload(names: String,icon : String)
    {
        database1 = FirebaseDatabase.getInstance().reference
        val category = Category(icon,names)
        database1.child("categories").push().setValue(category).addOnSuccessListener {
            Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT).show()
            hideProgressDialog()

        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            hideProgressDialog()

        }

    }

    private fun dismiss(){
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }
}