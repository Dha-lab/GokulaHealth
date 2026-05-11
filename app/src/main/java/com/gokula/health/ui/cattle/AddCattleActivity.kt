package com.gokula.health.ui.cattle

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gokula.health.database.AppDatabase
import com.gokula.health.databinding.ActivityAddCattleBinding
import com.gokula.health.models.Cattle
import com.gokula.health.ui.scan.TagScannerActivity
import com.gokula.health.utils.FirebaseSync
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AddCattleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCattleBinding
    private var photoUri: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri = result.data?.data
            Glide.with(this).load(photoUri).circleCrop().into(binding.ivCattlePhoto)
        }
    }

    private val scanTag = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedTag = result.data?.getStringExtra("scanned_tag") ?: ""
            if (scannedTag.isNotEmpty()) {
                binding.etEarTag.setText(scannedTag)
                Toast.makeText(this, "Tag scanned: $scannedTag", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCattleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pre-fill if coming from scanner
        intent.getStringExtra("scanned_tag")?.let {
            binding.etEarTag.setText(it)
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnPickPhoto.setOnClickListener {
            pickImage.launch(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            )
        }

        binding.btnScanTagInForm.setOnClickListener {
            scanTag.launch(Intent(this, TagScannerActivity::class.java))
        }

        binding.btnSaveCattle.setOnClickListener { saveCattle() }
    }

    private fun saveCattle() {
        val tag = binding.etEarTag.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        val breed = binding.etBreed.text.toString().trim()

        if (tag.isEmpty()) {
            binding.tilEarTag.error = "Ear Tag ID is required"
            return
        }
        binding.tilEarTag.error = null
        if (name.isEmpty()) {
            binding.tilName.error = "Cattle name is required"
            return
        }
        binding.tilName.error = null

        val cattle = Cattle(
            earTagId = tag,
            name = name,
            breed = breed.ifEmpty { "Unknown" },
            age = binding.etAge.text.toString().toIntOrNull() ?: 0,
            weight = binding.etWeight.text.toString().toFloatOrNull() ?: 0f,
            photoPath = photoUri?.toString() ?: "",
            ownerId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        )

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveCattle.isEnabled = false

        lifecycleScope.launch {
            AppDatabase.getInstance(this@AddCattleActivity).cattleDao().insert(cattle)
            FirebaseSync.uploadCattle(cattle, photoUri)
            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AddCattleActivity, "✅ ${name} saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}