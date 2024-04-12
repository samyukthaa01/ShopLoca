package com.example.shoplo.fragment.Managing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.shoplo.R
import com.example.shoplo.data.DataClass
import com.example.shoplo.databinding.GuideContentBinding

class GuideDetailsFragment : Fragment() {

    private lateinit var detailDesc: TextView
    private lateinit var detailTitle: TextView
    private lateinit var detailImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.guide_content, container, false)
        detailDesc = view.findViewById(R.id.detailDesc)
        detailTitle = view.findViewById(R.id.detailTitle)
        detailImage = view.findViewById(R.id.detailImage)

        val bundle = arguments
        if (bundle != null) {
            detailDesc.text = getString(bundle.getInt("Desc"))
            detailImage.setImageResource(bundle.getInt("Image"))
            detailTitle.text = bundle.getString("Title")
        }

        return view
    }
}