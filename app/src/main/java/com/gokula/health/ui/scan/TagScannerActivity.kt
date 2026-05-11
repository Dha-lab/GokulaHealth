package com.gokula.health.ui.scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gokula.health.databinding.ActivityTagScannerBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import android.view.animation.Animation
import android.view.animation.TranslateAnimation

class TagScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTagScannerBinding
    private lateinit var cameraExecutor: ExecutorService
    private var lastScannedText = ""
    private var isDetected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startScanAnimation()

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnUseTag.setOnClickListener {
            val result = Intent()
            result.putExtra("scanned_tag", lastScannedText)
            setResult(Activity.RESULT_OK, result)
            finish()
        }

        binding.btnRetry.setOnClickListener {
            isDetected = false
            lastScannedText = ""
            binding.tvScannedText.text = "Scanning..."
            binding.btnUseTag.visibility = View.GONE
            startCamera()
        }
    }

    private fun startScanAnimation() {
        val animation = TranslateAnimation(
            0f, 0f,
            0f, 500f
        )
        animation.duration = 2000
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE
        binding.scanLine.startAnimation(animation)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, TagAnalyzer { text ->
                        if (!isDetected && text.isNotEmpty()) {
                            isDetected = true
                            lastScannedText = text
                            runOnUiThread {
                                binding.tvScannedText.text = text
                                binding.btnUseTag.visibility = View.VISIBLE
                                Toast.makeText(this, "Tag detected!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e(TAG, "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private inner class TagAnalyzer(val onResult: (String) -> Unit) : ImageAnalysis.Analyzer {
        private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        @androidx.camera.core.ExperimentalGetImage
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val rawText = visionText.text.trim()
                    if (rawText.isNotEmpty()) {
                        // Extract tag-like patterns (alphanumeric, uppercase)
                        val tagPattern = Regex("[A-Z0-9]{3,12}")
                        val found = tagPattern.findAll(rawText)
                            .map { it.value }
                            .filter { it.length in 3..12 }
                            .firstOrNull()
                        if (found != null) {
                            onResult(found)
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { imageProxy.close() }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) startCamera()
            else {
                Toast.makeText(this, "Camera permission is required for tag scanning", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "TagScanner"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}