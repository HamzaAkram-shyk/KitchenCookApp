package com.example.apnakitchen.cookdashboard.ui.extrameal_module

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.imageupload.Cloud
import com.example.apnakitchen.model.cookModel.ExtraMeal
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.add_extra_meal_fragment.*
import kotlinx.android.synthetic.main.add_extra_meal_fragment.view.*
import java.util.*

class AddExtraMeal : Fragment() {

    companion object {
        fun newInstance() = AddExtraMeal()
    }

    private lateinit var viewModel: AddaExtraMealViewModel
    private lateinit var mainView: View
    private val GalleryCode = 1010
    private var imageUri: Uri? = null
    private var extraDish: ExtraMeal? = null
    private lateinit var user: FirebaseUser
    private var limitHourTime: Int? = null
    private var limitMinute: Int? = null

    var calendar: Calendar = Calendar.getInstance()
    var hour24hrs: Int = calendar.get(Calendar.HOUR_OF_DAY)
    var seconds: Int = calendar.get(Calendar.SECOND)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.add_extra_meal_fragment, container, false)
        user = AuthRepository.getCurrentUser()
        mainView.imageCard.setOnClickListener {
            selectImage(GalleryCode)
        }
        mainView.sheduleBtn.setOnClickListener {
            uploadDish()

        }
        mainView.clockTextView.setOnClickListener {
            val hour12hrs: Int = calendar.get(Calendar.HOUR)
            val minutes: Int = calendar.get(Calendar.MINUTE)
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(hour12hrs)
                    .setMinute(minutes)
                    .setTitleText("Select till you want to received the orders")
                    .build()
            picker.show(childFragmentManager, "tag")
            picker.addOnPositiveButtonClickListener {
                // call back code
                Toast.makeText(
                    context,
                    "${picker.hour} ${picker.minute} ${picker.inputMode} ",
                    Toast.LENGTH_SHORT
                ).show()

                if (picker.hour - hour12hrs in 1..3) {
                    picker.hour!!.also { limitHourTime = it }
                    picker.minute!!.also { limitMinute = it }
                } else {
                    Toast.makeText(context, "Select Appropriate Time", Toast.LENGTH_SHORT).show()
                }

            }
            picker.addOnNegativeButtonClickListener {
                // call back code

            }
            picker.addOnCancelListener {
                // call back code
            }
            picker.addOnDismissListener {
                // call back code
            }
        }

        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddaExtraMealViewModel::class.java)

    }

    private fun selectImage(code: Int) {
        var i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Choose Picture"), code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GalleryCode && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!
            Glide
                .with(requireContext())
                .load(imageUri)
                .into(mainView.dishImageView)

        }
    }


    private fun getIsAm(hour: Int): Boolean {
        return hour <= 12
    }


    private fun uploadDish() {
        Cloud.sendImageData(imageUri!!, "${Reuse.getTimeStamp()}") { response ->
            if (response != "=") {
                extraDish = ExtraMeal()
                extraDish!!.imageUrl = response
                extraDish?.name = nameField.text.toString()
                extraDish?.price = Integer.parseInt(price_field.text.toString())
                extraDish?.noOfMeal = Integer.parseInt(noOfMeal_field.text.toString())
                extraDish?.detail = mainView.detailField.text.toString()
                extraDish?.cookId = user.uid
                extraDish?.dishId = Reuse.getUniqueId(user.uid)
                extraDish?.CookTime = Integer.parseInt(cookTime_field.text.toString())
                extraDish?.preOrderTime = limitHourTime!!
                extraDish?.minute = if (limitMinute!! >= 20) limitMinute!! else 0
                viewModel.addDish(extraDish!!).observe(viewLifecycleOwner, {
                    when (it.status) {
                        Status.SUCCESS -> {
                            Reuse.stopLoading(mainView.loadingView)
                            Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                        }
                        Status.ERROR -> {
                            Reuse.stopLoading(mainView.loadingView)
                            Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                        }

                        Status.LOADING -> {
                            Reuse.startLoading(mainView.loadingView)
                        }
                    }
                })
            } else {
                Toast.makeText(context, "Image doesn't Upload Successfully", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

}