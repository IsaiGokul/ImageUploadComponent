# ImageUploadComponent


## Image Upload View
# Overview

    * ImageUploadView is a custom view designed to simplify image selection and upload functionality within Android applications. 

    * This component provides a user-friendly interface with the ability to preview selected images, submit them, and access the gallery for image retrieval.

# Features

    * Image preview with file type display.
    * Support for selecting images from the camera and gallery.
    * Simple integration into your UI layout.
    * Preview and submit options for selected images.
    * A customizable bottom sheet for camera and gallery access.

# Usage

* # in xml

  <com.isaigokul.imageuploader.utils.ImageUploadView
  android:id="@+id/imageUploadView"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"/>

* # activity

  val imageUploadView: ImageUploadView = findViewById(R.id.imageUploadView)

  * Set up an OnImageActionListener to handle image submission.
  
   imageUploadView.onSubmitListener(object : ImageUploadView.OnImageActionListener {
   override fun onSubmit(selectedFile: File, uri: Uri) {
   // Handle the submitted image

   }
   })
  * to reset the view use
    imageUploadView.reset()


## FileSelectionManager

# Overview

    * This utility class "FileSelectionManager" facilitates the management of permission requests and
    access to image files from both the gallery and the camera in Android applications.
    * It simplifies the process of handling permissions, launching intents for image selection, and
     providing callbacks for selected images.

# Usage

val fileSelectionManager = FileSelectionManager(activity, fileSelectedCallback)

val fileSelectedCallback: (Uri, File) -> Unit = { uri, file ->
// Handle the selected image and its file
// Your implementation here
}

val fileSelectionManager = FileSelectionManager(activity, fileSelectedCallback)

// Request external storage permission
fileSelectionManager.requestReadExternalStoragePermission()

// Request camera permission
fileSelectionManager.requestCameraPermission()







