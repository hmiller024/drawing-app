package com.example.drawingapp

//Import firebase singleton
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.example.drawingapp.db.CanvasImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import java.io.ByteArrayOutputStream
import java.util.Date

class FirebaseManager {
    var user: FirebaseUser? = null

    //Method to sign in using email and password
    fun signIn(email: String, password: String) {
        //Sign in with email and password
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    this.user = Firebase.auth.currentUser
                } else {
                    // Sign in failed
                    this.user = null
                    Log.e("sign-in failure", "signInWithEmailAndPassword:failure", task.exception)

                }
            }
    }

    //Method to sign out
    fun signOut() {
        //Sign out
        Firebase.auth.signOut()
        this.user = null
    }

    //Method to check if user is signed in
    fun isSignedIn(): Boolean {
        //Check if user is signed in
        return Firebase.auth.currentUser != null
    }

    //Method to create a new user with email and password
    fun createUser(email: String, password: String) {
        //Create user with email and password
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign up success
                    this.user = Firebase.auth.currentUser
                } else {
                    // Sign up failed
                    this.user = null
                }
            }
    }

    //Method to get the current user
    fun getCurrentUser(): FirebaseUser? {
        //Get current user
        return Firebase.auth.currentUser
    }

    //Store document to collection
    fun saveDrawing(canvasImage: CanvasImage, private: Boolean = true) {
        //Check if user is signed in
        if (this.user == null) {
            Log.e("SaveDrawing", "saveDrawing: ", )
            return
        }

        val document = mapOf(
            "name" to canvasImage.getFileName(),
            "timestamp" to canvasImage.timestamp,
            "uploadedDate" to Date()
        )

        //Get the database
        val db = Firebase.firestore

        //Store the document to the user-private collection
        if (private) {
            db.collection("user").document(this.user!!.uid).collection("drawings")
                .add(document)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "FirebaseManager",
                        "DocumentSnapshot added with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w("FirebaseManager", "Error adding document", e)
                }
        } else {
            //Store the document to the public/drawings collection without user id
            db.collection("public").document("drawings").collection("drawings")
                .add(document)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "FirebaseManager",
                        "DocumentSnapshot added with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w("FirebaseManager", "Error adding document", e)
                }
        }

        //Store the image to the storage
        val baos = ByteArrayOutputStream()
        val bitmap = canvasImage.getBitmap()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        val ref = Firebase.storage.reference
        var fileRef = ref.child("public/drawings/${canvasImage.getFileName()}")
        if (private) {
            fileRef = ref.child("user/${this.user!!.uid}/${canvasImage.getFileName()}")
        }

        var uploadTask = fileRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.e("FirebaseManager", "Error uploading image", it)
        }.addOnSuccessListener {
            Log.d("FirebaseManager", "Image uploaded successfully")
        }
    }

    //Method to get all drawings
    fun getDrawings(private: Boolean = true): MutableList<CanvasImage> {
        //Check if user is signed in
        if (this.user == null) {
            return mutableListOf()
        }

        //Get the database
        val db = Firebase.firestore

        //Get the collection
        val collection = if (private) {
            db.collection("user").document(this.user!!.uid).collection("drawings")
        } else {
            db.collection("public").document("drawings").collection("drawings")
        }

        //Get the documents
        var canvasList = mutableListOf<CanvasImage>()
        val documents = collection.get()
            .addOnSuccessListener{ result ->
                for (document in result) {
                    Log.d("FirebaseManager", "${document.id} => ${document.data}")
                    val canvasImage = CanvasImage(document.data["name"].toString(), document.data["timestamp"] as Long)
                    canvasList.add(canvasImage)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FirebaseManager", "Error getting documents.", exception)
            }

        return canvasList
    }
}