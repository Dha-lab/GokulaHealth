package com.gokula.health.utils

import android.net.Uri
import android.util.Log
import com.gokula.health.models.Cattle
import com.gokula.health.models.MilkEntry
import com.gokula.health.models.Vaccination
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object FirebaseSync {
    private val TAG = "FirebaseSync"
    private val db get() = FirebaseFirestore.getInstance()
    private val storage get() = FirebaseStorage.getInstance()
    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    suspend fun uploadCattle(cattle: Cattle, photoUri: Uri? = null): Boolean {
        return try {
            var photoUrl = cattle.photoUrl
            if (photoUri != null && uid.isNotEmpty()) {
                val ref = storage.reference
                    .child("users/$uid/cattle/${cattle.earTagId}.jpg")
                ref.putFile(photoUri).await()
                photoUrl = ref.downloadUrl.await().toString()
            }
            if (uid.isNotEmpty()) {
                db.collection("users").document(uid)
                    .collection("cattle").document(cattle.earTagId)
                    .set(cattle.copy(photoUrl = photoUrl, ownerId = uid))
                    .await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Upload cattle failed: ${e.message}")
            false
        }
    }

    suspend fun uploadMilkEntry(entry: MilkEntry): Boolean {
        return try {
            if (uid.isNotEmpty()) {
                db.collection("users").document(uid)
                    .collection("milk_entries").document(entry.id.toString())
                    .set(entry).await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Upload milk failed: ${e.message}")
            false
        }
    }

    suspend fun uploadVaccination(v: Vaccination): Boolean {
        return try {
            if (uid.isNotEmpty()) {
                db.collection("users").document(uid)
                    .collection("vaccinations").document(v.id.toString())
                    .set(v).await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Upload vaccination failed: ${e.message}")
            false
        }
    }

    suspend fun syncVaccinationUpdate(v: Vaccination): Boolean {
        return try {
            if (uid.isNotEmpty()) {
                db.collection("users").document(uid)
                    .collection("vaccinations").document(v.id.toString())
                    .set(v).await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Sync vaccination failed: ${e.message}")
            false
        }
    }
}