package com.example.apnakitchen.imageupload

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apnakitchen.Utils.Image_Folder
import com.example.apnakitchen.Utils.LOADING
import com.example.apnakitchen.Utils.Resource
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.okhttp.Dispatcher
import io.grpc.internal.SharedResourceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Cloud {
    private val cloudStorage = FirebaseStorage.getInstance().reference.child(Image_Folder)


    fun uploadImage(
        data: Uri,
        imageName: String,
        response: CloudResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileRef = cloudStorage!!.child("$imageName.jpg")
            var uploadTask: StorageTask<*>

            uploadTask = fileRef.putFile(data!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                        response.onError(task.exception.toString())
                    }

                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    response.onSuccess(url)


                } else {
                    response.onError(task.exception.toString())
                }

            }

        }

    }


    interface CloudResponse {
        fun onSuccess(url: String)
        fun onError(error: String)
    }


}