package com.isaigokul.imageuploader.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**This class is created to manage permission request and
 * image file access from gallery & camera created by isaigokul on 19-02-2023**/

class FileSelectionManager(
    private val activity: ComponentActivity,
    private val fileSelectedCallback: (Uri, File) -> Unit
) {
    private var isStorage = false
    private val preferences = activity.getPreferences(Context.MODE_PRIVATE)

    fun requestReadExternalStoragePermission() {
        if (!isGranted(STORAGE_PERMISSION)) {
            isStorage = true
            if (isRational(STORAGE_PERMISSION)) {
                // Display a rationale to the user before launching the permission request
                showRationaleDialog(isStorage)
            } else {
                if (hasAskedForPermission(STORAGE_PERMISSION)) {
                    showPermissionSettingsDialog()
                } else {
                    saveAskedForPermission(STORAGE_PERMISSION)
                    requestPermissionsLauncher.launch(STORAGE_PERMISSION)
                }
            }
        } else {
            launchGalleryIntent()
        }
    }

    fun requestCameraPermission() {
        if (isGranted(CAMERA_PERMISSION)) {
            launchCameraIntent()
        } else {
            isStorage = false
            if (isRational(CAMERA_PERMISSION)) {
                // Display a rationale to the user before launching the permission request
                showRationaleDialog(isStorage)
            } else {
                if (hasAskedForPermission(CAMERA_PERMISSION)) {
                    showPermissionSettingsDialog()
                } else {
                    saveAskedForPermission(CAMERA_PERMISSION)
                    requestPermissionsLauncher.launch(CAMERA_PERMISSION)
                }
            }
        }
    }

    //saving the asked permission request in pref to handle rational permission request

    fun hasAskedForPermission(permission: String): Boolean {
        return preferences.getBoolean(permission, false)
    }

    fun saveAskedForPermission(permission: String): Boolean {
        return preferences.edit().putBoolean(permission, true).commit()
    }
//check to show permission rational dialog
    fun isRational(permission: String): Boolean =
        shouldShowRequestPermissionRationale(activity, permission)

    fun isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED

    private fun launchGalleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(galleryIntent)
    }

    private fun launchCameraIntent() {
        cameraPicture.launch(null)
    }
//gallery images selection result is handled here
    private var galleryActivityResultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { results ->
            if (results.resultCode == AppCompatActivity.RESULT_OK) {
                val image = results?.data?.data
                if (image != null) {
                    val file = uriToFile(image)
                    if (file != null) {
                        fileSelectedCallback.invoke(image, file)
                    }
                } else {
                    showMessage("Something went worng,Please try again")
                }

            }
        }

    //launcher for camera to take picture with preview option
    private var cameraPicture =
        activity.registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                val file = bitmapToFile(activity, bitmap)
                val uri = Uri.fromFile(file)
                if (uri != null) {
                    if (file != null) {
                        fileSelectedCallback.invoke(uri, file)
                    }
                }
            } else {
                showMessage("Something went worng,Please try again")
            }
        }
//launcher for permission result is handled here
    private val requestPermissionsLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                if (isStorage) {
                    requestReadExternalStoragePermission()
                } else {
                    requestCameraPermission()
                }
            } else {
                showMessage("${if (isStorage) "Storage" else "Camera"} permission must required")
            }
        }

    private fun showPermissionSettingsDialog() {
        // Implement your UI logic to show a dialog or other UI element explaining that the user needs to go to settings
        // Provide a positive button in the dialog to open the app settings
        AlertDialog.Builder(activity)
            .setTitle("Permission Required")
            .setMessage("This app requires the permission. Please go to app settings to enable it.")
            .setPositiveButton("Open Settings") { _, _ ->
                // Open app settings
                openAppSettingsLauncher.launch(null)
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle the case where the user decides not to grant the permission
                showMessage("Permission not granted")
            }
            .show()
    }


    private fun showRationaleDialog(isStorage: Boolean) {
        // Implement your UI logic to show a dialog or other UI element explaining the need for the permission
        // Provide a positive button in the dialog to launch the permission request again
        // and a negative button if the user decides not to grant the permission
        AlertDialog.Builder(activity)
            .setTitle("Permission Required")
            .setMessage("This app requires ${if (isStorage) "Storage" else "Camera"} permission.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionsLauncher.launch(
                    if (isStorage) STORAGE_PERMISSION
                    else CAMERA_PERMISSION
                )
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle the case where the user decides not to grant the permission
                showMessage("Permission not granted")
            }
            .show()
    }

    class OpenAppSettingsContract : ActivityResultContract<Unit?, Boolean>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            // Check if the user made changes in the settings
            return resultCode == Activity.RESULT_OK
        }
    }

    val openAppSettingsLauncher =
        activity.registerForActivityResult(OpenAppSettingsContract()) { settingsChanged ->
            //setting result state has to be handled here

        }

    fun uriToFile(uri: Uri?): File? {
        if (uri == null)
            return null
        val contentResolver = activity.contentResolver
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()

        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = cursor?.getString(columnIndex ?: -1)
        cursor?.close()

        return if (filePath != null) {
            File(filePath)
        } else {
            copyUriToFile(uri)
        }
    }

    private fun copyUriToFile(uri: Uri): File? {
        val contentResolver = activity.contentResolver

        val fileName = "file_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
            Date()
        )
        val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        }

        val file = File(storageDir, "$fileName.jpg")

        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = file.outputStream()

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun bitmapToFile(context: Context, bitmap: Bitmap): File? {
        // Create a temporary file to store the bitmap
        val file = File(context.filesDir, "camera.jpg")
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        // Return the Uri of the temporary file
        return file
    }

    private fun showMessage(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()

    }

    companion object {
        val STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
    }

}

