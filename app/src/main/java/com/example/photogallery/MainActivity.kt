package com.example.photogallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PhotoAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadPhotos()
        } else {
            Toast.makeText(this, "Permission denied. Cannot load photos.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = PhotoAdapter(this)
        recyclerView.adapter = adapter

        photoViewModel = ViewModelProvider(this)[PhotoViewModel::class.java]
        photoViewModel.photos.observe(this) { photos ->
            adapter.submitList(photos)
        }

        checkPermissionAndLoadPhotos()
    }

    private fun checkPermissionAndLoadPhotos() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadPhotos()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                Toast.makeText(this, "Storage permission is needed to display photos.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun loadPhotos() {
        val photos = mutableListOf<String>()
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_TAKEN + " DESC"
        )
        cursor?.use {
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                photos.add(cursor.getString(dataIndex))
            }
        }
        photoViewModel.setPhotos(photos)
    }
}
