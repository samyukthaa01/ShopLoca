package com.example.shoplo.fragment.Managing

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.shoplo.adapters.ViewPager2Images
import com.example.shoplo.data.Post
import com.example.shoplo.databinding.FragmentPostdetailsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PostDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPostdetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private var imageUri: Uri? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().getReference()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostdetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.INVISIBLE

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                binding.uploadImage.setImageURI(imageUri)
            } else {
                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.uploadImage.setOnClickListener {
            val photoPicker = Intent().apply {
                action = Intent.ACTION_GET_CONTENT
                type = "image/*"
            }
            activityResultLauncher.launch(photoPicker)
        }

        binding.uploadButton.setOnClickListener {
            if (imageUri != null) {
                uploadToFirebase(imageUri!!)
            } else {
                Toast.makeText(requireContext(), "Please select image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadToFirebase(uri: Uri) {
        val caption = binding.uploadCaption.text.toString()
        val imageReference = storageReference.child(System.currentTimeMillis().toString() + "." + getFileExtension(uri))

        imageReference.putFile(uri).addOnSuccessListener { taskSnapshot ->
            imageReference.downloadUrl.addOnSuccessListener { uri ->
                val sellerId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val post = Post(postTitle = caption, postImages = listOf(uri.toString()), timestamp = System.currentTimeMillis())
                firestore.collection("Posts").document(sellerId).collection("SellerPosts").add(post)
                    .addOnSuccessListener {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Uploaded", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnProgressListener {
            binding.progressBar.visibility = View.VISIBLE
        }.addOnFailureListener {
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(fileUri: Uri): String {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri)) ?: ""
    }
}
