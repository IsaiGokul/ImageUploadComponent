package com.isaigokul.imageuploader.utils


import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isaigokul.imageuploader.R
import java.io.File

/**This class is created as a custom-image-upload view with preview ,
 *  submit options and has gallery access to fetch image from storage created by isaigokul on 19-02-2023**/
class ImageUploadView : LinearLayout {
    private var imageView: ImageView? = null
    private var fileTypeTextView: TextView? = null
    private var previewButton: Button? = null
    private var submitButton: Button? = null
    private var fileSelectionManager: FileSelectionManager? = null
    private var imageActionListener: OnImageActionListener? = null
    private var currentFile: File? = null
    private var currentURI: Uri? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        // Inflate the layout for the component
        LayoutInflater.from(context).inflate(R.layout.image_preview_layout, this, true)

        // Initialize UI elements
        imageView = findViewById<ImageView>(R.id.iv_preview)
        fileTypeTextView = findViewById<TextView>(R.id.tv_file_type)
        previewButton = findViewById<Button>(R.id.btn_preview)
        submitButton = findViewById<Button>(R.id.btn_submit)

        // Set click listeners for UI elements
        setListeners()
        //file selected listener
        fileSelectionManager = FileSelectionManager(context as ComponentActivity) { uri, file ->
            imageView?.setImageURI(uri)
            fileTypeTextView?.text = "${file?.name}"
            currentFile = file
            currentURI = uri
            setSelectedState(this.currentFile != null)
        }
    }

    fun reset() {
        currentURI = null
        currentFile = null
        setSelectedState(false)
        fileTypeTextView?.text = "Tap to select the file"
        imageView?.setImageResource(R.drawable.ic_upload_icon)
    }

    private fun setListeners() {
        setOnClickListener { v: View? ->
            fileSelectionManager?.let {
                CustomBottomSheetDialog(it).show(
                    (context as FragmentActivity).supportFragmentManager,
                    "BottomSheetDialogFragment"
                )
            }
        }
        // Set onClickListener for the "Preview" button to display the selected image
        previewButton?.setOnClickListener { v: View? ->
            val uri = Uri.fromFile(currentFile)
            if (uri != null) {
                ImageExpansionDialog(context, uri).show()
            } else {
                showMessage(context, "Something went wrong,Try again")
            }

        }

        // Set onClickListener for the "Submit" button to simulate image upload
        submitButton?.setOnClickListener { v: View? ->

            if (currentFile != null && currentURI != null) {
                imageActionListener?.onSubmit(currentFile!!, currentURI!!)
            }
        }

    }

    private fun setSelectedState(state: Boolean) {
        val alpha = if (state) 1f else .5f
        previewButton?.isEnabled = state
        submitButton?.isEnabled = state
        previewButton?.alpha = alpha
        submitButton?.alpha = alpha
    }

    fun onSubmitListener(imageActionListener: OnImageActionListener) {
        this.imageActionListener = imageActionListener
    }

    interface OnImageActionListener {
        fun onSubmit(selectedFile: File, uri: Uri)
    }

    class CustomBottomSheetDialog(private val fileSelectionManager: FileSelectionManager) :
        BottomSheetDialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val cvCamera: CardView = view.findViewById(R.id.cv_camera)
            val cvGallery: CardView = view.findViewById(R.id.cv_gallery)
            view.setOnClickListener {
                dismiss()
            }
            cvCamera.setOnClickListener {
                dismiss()
                fileSelectionManager.requestCameraPermission()
            }
            cvGallery.setOnClickListener {
                dismiss()
                fileSelectionManager.requestReadExternalStoragePermission()
            }

        }
    }

    class ImageExpansionDialog(context: Context, private val imageURi: Uri) :
        Dialog(context) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_image_expansion)

            val expandedImageView: ImageView = findViewById(R.id.iv_expand)

            // Set a click listener to dismiss the dialog on tap
            expandedImageView.setOnClickListener {
                dismiss()
            }
            expandedImageView.setImageURI(imageURi)
            // Apply the scale animation to the ImageView
            val scaleAnimation = ScaleAnimation(
                0.0f, 1.0f, // Start and end scale X
                0.0f, 1.0f, // Start and end scale Y
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Pivot X (center)
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f  // Pivot Y (center)
            )
            scaleAnimation.duration = 300 // Animation duration in milliseconds
            expandedImageView.startAnimation(scaleAnimation)
        }
    }

    private fun showMessage(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    }
}

