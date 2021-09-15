package my.tarc.mobileapp.facility

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentEditFacilityBinding

class EditFacilityFragment : Fragment() {

    //Binding fragment
    private var _binding: FragmentEditFacilityBinding? = null
    private val binding get() = _binding!!

    //Store uris of picked Images
    private var images: ArrayList<Uri?>? = null

    // Current position of selected image
    private var position = 0

    // Current position of deleted image
    private var deletePosition = 0

    //request code to pick image
    private val PICK_IMAGES_CODE = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditFacilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init list
        images = ArrayList()

        //Setup Image Switcher
        binding.imageViewEditFacilityImage.setFactory { ImageView(activity?.applicationContext) }

        //Pick Image
        binding.btnPickFacilityImage.setOnClickListener{
            pickImagesIntent()
        }

        //Next Image
        binding.btnNextImg.setOnClickListener {
            if(position < images!!.size-1){
                position++
                binding.imageViewEditFacilityImage.setImageURI(images!![position])
            }
            else
            {
                //Display no more image
                Toast.makeText(activity, "No more images...", Toast.LENGTH_SHORT).show()
            }
        }

        //Previous Image
        binding.btnPreviousImg.setOnClickListener {
            if(position > 0){
                position--
                binding.imageViewEditFacilityImage.setImageURI(images!![position])
            }
            else
            {
                //Display no more image
                Toast.makeText(activity, "No more images...", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete Image
        binding.imageViewEditFacilityImage.setOnClickListener {
            // Apply confirmation dialog here
        }

        // Navigate to Admin Facility List
        binding.btnEditFacilityCancel.setOnClickListener {
            findNavController().navigate(R.id.action_editFacilityFragment_to_adminAllFacilityFragment)
        }

        // Navigate to Admin Facility List
        binding.btnEditFacilitySave.setOnClickListener {

            Toast.makeText(activity, "Facility Details Update Successful", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_editFacilityFragment_to_adminAllFacilityFragment)
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun pickImagesIntent (){
        val intent = Intent()
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        // Deprecated issues
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"), PICK_IMAGES_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Deprecated issues
        super.onActivityResult(requestCode, resultCode, data)

        //if(requestCode == PICK_IMAGES_CODE){

        if(data!!.clipData != null){
            //picked multiple images
            //get number of picked images
            val count = data.clipData!!.itemCount
            for(i in 0 until count){
                val imageUri = data.clipData!!.getItemAt(i).uri
                //add image to list
                images!!.add(imageUri)
            }
            // set first image from array list to image switcher
            binding.imageViewEditFacilityImage.setImageURI(images!![0])
            position = 0
        }
        else{
            //picked single image
            val imageUri = data.data
            //set image to image switcher
            binding.imageViewEditFacilityImage.setImageURI(imageUri)
            position = 0
        }
        //}
    }
}