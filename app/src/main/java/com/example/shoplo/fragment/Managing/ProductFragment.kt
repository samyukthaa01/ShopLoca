package com.example.shoplo.fragment.Managing

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shoplo.R
import com.example.shoplo.data.Product
import com.example.shoplo.databinding.FragmentProductdetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.ColorPickerDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*

class ProductFragment : Fragment(R.layout.fragment_productdetails) {

    private lateinit var binding: FragmentProductdetailsBinding
    private var selectedImages = mutableListOf<Uri>()
    private val selectedColors = mutableListOf<Int>()
    private val productsStorage = Firebase.storage.reference
    private val firestore = Firebase.firestore

    // Define the ORANGE constant
    private lateinit var categorySpinner: Spinner
    private val ORANGE = android.graphics.Color.rgb(235, 134, 12)
    private val LIGHT_BLUE = android.graphics.Color.rgb(184, 217, 250)
    private val GREEN = android.graphics.Color.rgb(119, 189, 136)
    private val BLUE = android.graphics.Color.rgb(0, 13, 174)
    private val PINK = android.graphics.Color.rgb(255, 153, 153)
    private val PURPLE = android.graphics.Color.rgb(196, 122, 204)
    private val COLOR_OTHERS = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductdetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorOptions()
        setupSaveButton()

        categorySpinner = view.findViewById(R.id.spinnerCategory)
        setupCategorySpinner()
    }

    private fun setupCategorySpinner() {
        // Get the array of categories from resources
        val categoryOptions = resources.getStringArray(R.array.category_options)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryOptions)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        categorySpinner.adapter = adapter
    }

    private fun setupColorOptions() {
        binding.colorOption1.setOnClickListener {
            onColorOptionSelected(android.graphics.Color.RED, it as Button)
        }
        binding.colorOption2.setOnClickListener {
            onColorOptionSelected(ORANGE, it as Button)
        }
        binding.colorOption3.setOnClickListener {
            onColorOptionSelected(GREEN, it as Button)
        }

        binding.colorOption5.setOnClickListener {
            onColorOptionSelected(BLUE, it as Button)
        }
        binding.colorOption6.setOnClickListener {
            onColorOptionSelected(LIGHT_BLUE, it as Button)
        }
        binding.colorOption7.setOnClickListener {
            onColorOptionSelected(PINK, it as Button)
        }
        binding.colorOption8.setOnClickListener {
            onColorOptionSelected(PURPLE, it as Button)
        }

        binding.colorOption10.setOnClickListener {
            onColorOptionSelected(COLOR_OTHERS, it as Button)
        }
        // Add more color option buttons and listeners as needed

        val selectedImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data

                    //Mutliple images selected
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
                            val imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let {
                                selectedImages.add(it)
                            }
                        }
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let { selectedImages.add(it) }
                    }
                    updatesImages()
                }
            }
        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectedImagesActivityResult.launch(intent)
        }
    }


    private fun updatesImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    private fun onColorOptionSelected(color: Int, selectedButton: Button) {
        if (selectedColors.contains(color)) {
            // Color is already selected, so remove it
            selectedColors.remove(color)
            // selectedButton.isEnabled = true // Re-enable the button
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                0,
                0
            ) // Remove the check icon
        } else {
            // Color is not selected, so add it
            selectedColors.add(color)
            //selectedButton.isEnabled = false // Disable the button to prevent selecting the same color again
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_picked,
                0
            ) // Set the check icon
            // Adjust drawable padding to center the check mark
            val drawable = resources.getDrawable(R.drawable.ic_picked)
            val drawableWidth = drawable.intrinsicWidth
            val buttonWidth = selectedButton.width
            selectedButton.compoundDrawablePadding = (buttonWidth - drawableWidth) / 2
        }
        updateColors()
    }


    private fun updateColors() {
        val colorNames = selectedColors.map { colorIntToName(it) }
        val colorsString = colorNames.joinToString(", ")
        binding.tvSelectedColors.text = colorsString
    }

    private fun colorIntToName(color: Int): String {
        return when (color) {
            android.graphics.Color.RED -> "Red"
            ORANGE -> "Orange"
            GREEN -> "Green"
            BLUE -> "Navy Blue"
            LIGHT_BLUE -> "Light Blue"
            PINK -> "Pink"
            PURPLE -> "Purple"

            COLOR_OTHERS -> "Others"
            else -> "Unknown"
        }
    }


    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            if (validateInformation()) {
                // Start the button animation
                binding.buttonSave.startAnimation()
                saveProduct()
            } else {
                Toast.makeText(requireContext(), "Check your inputs", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProduct() {

        val productName = binding.edName.text.toString().trim()
        val productCategory = categorySpinner.selectedItem.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.offerPercentage.text.toString().trim()
        val productDescription = binding.edDescription.text.toString().trim()
        val sizesRadioGroup = binding.edSizes
        val selectedSizeId = sizesRadioGroup.checkedRadioButtonId
        val selectedSize: String

        if (selectedSizeId != -1) {
            val selectedSizeRadioButton = sizesRadioGroup.findViewById<RadioButton>(selectedSizeId)
            selectedSize = selectedSizeRadioButton.text.toString()
        } else {
            selectedSize = ""  // or any default value
        }

        // Get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Get the sellerID
        val sellerID = currentUser?.uid ?: ""

        // Fetch the sellerName (fullName) from Firestore
        firestore.collection("shop").document(sellerID).get().addOnSuccessListener { shopDocument ->
            val sellerName = shopDocument.getString("fullName") ?: ""

            // Fetch the shopName from Firestore
            firestore.collection("seller").document(sellerID).get()
                .addOnSuccessListener { shopDocument ->
                    val shopName = shopDocument.getString("shopName") ?: ""

                    val sizes = getSizesList(selectedSize)
                    val imagesByteArrays = getImagesByteArrays()
                    val productImages = mutableListOf<String>()

                    lifecycleScope.launch(Dispatchers.IO) {
                        withContext(Dispatchers.Main) {

                        }

                        try {
                            async {
                                imagesByteArrays.forEach {
                                    val id = UUID.randomUUID().toString()
                                    launch {
                                        val imageStorage =
                                            productsStorage.child("products/images/$id")
                                        val result = imageStorage.putBytes(it).await()
                                        val downloadUrl =
                                            result.storage.downloadUrl.await().toString()
                                        productImages.add(downloadUrl)
                                    }
                                }
                            }.await()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main) {

                            }
                        }
                        val product = Product(
                            UUID.randomUUID().toString(),
                            productName,
                            productCategory,
                            price.toFloat(),
                            if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                            if (productDescription.isEmpty()) null else productDescription,
                            if (selectedColors.isEmpty()) null else selectedColors,
                            sizes,
                            productImages,
                            System.currentTimeMillis(),
                            shopName, // new field
                            sellerID, // new field
                            sellerName // new field
                        )
                        firestore.collection("Products").add(product).addOnSuccessListener {
                            binding.buttonSave.revertAnimation()
                        }.addOnFailureListener {
                            binding.buttonSave.revertAnimation()
                            Log.e("Error", it.message.toString())
                        }
                    }
                } // This closing bracket was missing
        }
    }


    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()

        selectedImages.forEach { uri ->
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                imagesByteArray.add(stream.toByteArray())
            }
        }

        return imagesByteArray
    }


    private fun getSizesList(size: String): List<String> {
        if (size.isEmpty()) {
            return emptyList()
        }
        return listOf(size)
    }


    private fun validateInformation(): Boolean {
        return binding.edPrice.text.toString().trim().isNotEmpty() &&
                binding.edName.text.toString().trim().isNotEmpty() &&
                categorySpinner.selectedItem != null &&
                !selectedImages.isEmpty()
    }
}