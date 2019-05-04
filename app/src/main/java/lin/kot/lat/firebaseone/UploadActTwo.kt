package lin.kot.lat.firebaseone

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.upload_photo.image
import kotlinx.android.synthetic.main.upload_photo2.*

class UploadActTwo : AppCompatActivity() {


    lateinit var btnChoose : Button
    lateinit var btnUpload : Button
    lateinit var imgView : ImageView
    val PERMISSION_REQUEST_CODE = 1001
    val PICK_IMAGE_REQUEST = 71
    var value = 0.0
    lateinit var filePath : Uri
    lateinit var storage : FirebaseStorage
    lateinit var storageReference: StorageReference

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_photo2)
        btnChoose = findViewById(R.id.choose)
        btnUpload = findViewById(R.id.upload)
        imgView = findViewById(R.id.image)

            btnChoose.setOnClickListener {
            when{
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(this@UploadActTwo, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                    }else{
                        chooseImage()
                    }
                }
                else -> chooseImage()
            }
        }
        btnUpload.setOnClickListener {
            uploadFile()
        }
    }

    private fun chooseImage(){
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty()|| grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this@UploadActTwo, "Oops! Permission Denied!!", Toast.LENGTH_SHORT).show()
                else
                    chooseImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK){
            return
        }
        when (requestCode){
            PICK_IMAGE_REQUEST -> {
                filePath = data!!.getData()
                //uploadFile()
                Toast.makeText(this@UploadActTwo, "File "+filePath+" selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private  fun uploadFile(){
        val progress = ProgressDialog(this).apply {
            setTitle("Uploading Pictures....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
        val data = FirebaseStorage.getInstance()
        var value = 0.0
        var storage = data.getReference()
        storage.child("mypic.jpg")
            .putFile(filePath)
            .addOnProgressListener {
                    taskSnapshot ->
                value = (100.0 * taskSnapshot.bytesTransferred)/ taskSnapshot
                    .totalByteCount
                Log.v("value", "value=="+value)
                progress.setMessage("Upload.." + value.toInt() + "%")
            }
            .addOnSuccessListener {
                    taskSnapshot ->
                progress.dismiss()
                val uri = taskSnapshot.storage.downloadUrl
                Log.v("Download File", "File.." +uri)
                Glide.with(this@UploadActTwo).load(uri).into(image)
            }.addOnFailureListener {
                    exception -> exception
                .printStackTrace()
            }
    }
}
