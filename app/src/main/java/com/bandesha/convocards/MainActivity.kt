package com.bandesha.convocards

import android.app.AlertDialog
import android.app.Service.STOP_FOREGROUND_DETACH
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bandesha.convocards.adapters.CardAdapter
import com.bandesha.convocards.adapters.OnClickListener
import com.bandesha.convocards.databinding.ActivityAddCardsBinding
import com.bandesha.convocards.databinding.ActivityMainBinding
import com.bandesha.convocards.models.Cards
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import kotlin.math.log


class MainActivity : AppCompatActivity()
{
    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: ")
        clicks()
    }

    private fun clicks() {

        binding.start.setOnClickListener {
            startRecording()
        }
        binding.stop.setOnClickListener {
            upload("/storage/emulated/0/Music/AudioRecords/1737649797191audiorecordtest.aac")
        }

    }

    private fun getAppStorageDir(): File {
        return File(applicationContext.cacheDir, "AudioRecords").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    fun startRecording() {
        try {

            val timestamp = System.currentTimeMillis()

            fileName = "${getAppStorageDir().absolutePath}/${timestamp}audiorecordtest.aac"

            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                try {
                    prepare()
                    start()
                    Toast.makeText(applicationContext, "started", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Recording started")
                } catch (e: IOException) {
                    Log.e(TAG, "prepare() failed   $e")
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in startRecording   $e")

            throw e
        }
    }
    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null


            Log.d(TAG, "stopRecording: $fileName")
            Toast.makeText(applicationContext, "stoped", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
//                upload("/data/user/0/com.drhaydar.time/cache/AudioRecords/1737646582226audiorecordtest.aac")
            }, 30)

            Log.d(TAG, "Recording stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error in stopRecording"+e)
        }
    }
    private fun upload(filePath: String) {
        Log.d(TAG, "upload: called")
        val storageReference = FirebaseStorage.getInstance().reference
        val audioRef = storageReference.child("Recording/${System.currentTimeMillis()}_record.aac")
        val file = Uri.fromFile(File(filePath))

        val uploadTask = audioRef.putFile(file)
        uploadTask.addOnSuccessListener {
            audioRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d(TAG, "File uploaded successfully: $uri")
                Toast.makeText(this, "File uploaded successfully!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "File upload failed: ${exception.message}")
            Toast.makeText(this, "File upload failed!", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            Log.d(TAG, "Upload is $progress% done")
        }
    }
    companion object{
        private const val TAG = "MainActivity"
    }


}