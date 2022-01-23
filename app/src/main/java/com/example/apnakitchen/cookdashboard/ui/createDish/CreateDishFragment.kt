package com.example.apnakitchen.cookdashboard.ui.createDish

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.cookdashboard.CookDashboard
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.create_dish_fragment.view.*
import kotlinx.coroutines.launch
import javax.annotation.meta.When

class CreateDishFragment : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {
    private var defaultSetting: Boolean = true

    companion object {
        const val modeTypeKey = "modekey"
        const val EditType = "editType"
        const val CreateType = "createType"
        const val userIdKey = "userId"
        const val dishTypeKey = "dishType"
        const val dishIdKey = "dishId"
        const val dishNameKey = "dishName"
        const val dishDescKey = "desc"
        const val cookTimeKey = "cookTime"
        const val priceKey = "price"
        const val firstImageKey = "first"
        const val secondImageKey = "second"
        const val firstImageNameKey = "first"
        const val secondImageNameKey = "second"
    }

    private lateinit var viewModel: CreateDishViewModel
    private lateinit var mainView: View
    lateinit var imagePath: ArrayList<Uri>
    private val FIRSTCODE: Int = 111
    private val SECONDCODE: Int = 222
    private var counter: Int = 0
    private lateinit var uri: Uri
    private lateinit var user: FirebaseUser
    private lateinit var imagehash: HashMap<String, Uri>
    private lateinit var dishHash: HashMap<String, String>
    private lateinit var dish: Dish

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(CreateDishViewModel::class.java)
        mainView = inflater.inflate(R.layout.create_dish_fragment, container, false)
        arguments?.let {
            dish = it.getParcelable<Dish>(modeTypeKey)!!
            defaultSetting = false
            counter = 0
            mainView.labelType.text = "Update Your Dish"
            setEditValues()
        }
        imagePath = ArrayList()
        imagehash = HashMap<String, Uri>()
        dishHash = HashMap<String, String>()
        user = AuthRepository.getCurrentUser()
        setSpinner()
        mainView.closeBtn.setOnClickListener {
            dismiss()
        }
        mainView.firstCard.setOnClickListener {
            selectImage(FIRSTCODE)
        }

        mainView.secondCard.setOnClickListener {
            selectImage(SECONDCODE)
        }

        mainView.uploadDish.setOnClickListener {
            when (defaultSetting) {
                true -> {
                    createDish()
                }
                false -> {
                    updateDish()

                }
            }
        }



        return mainView
    }

    private fun setSpinner() {
        val spinner = mainView.foodList
        val adapter =
            context?.let {
                ArrayAdapter.createFromResource(
                    it,
                    R.array.FoodCategories,
                    android.R.layout.simple_spinner_item
                )
            }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
        if (!defaultSetting) {
            var currentIndex = -1
            val array = resources.getStringArray(R.array.FoodCategories).toMutableList()
            for ((index, value) in array.withIndex()) {
                if (value.equals(dish.foodType)) {
                    currentIndex = index
                    break
                }
            }
            spinner.setSelection(currentIndex, true)

        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = parent?.getItemAtPosition(position).toString()
        dishHash[dishTypeKey] = text
        Toast.makeText(context, "Selected $text", Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(context, "Nothing..", Toast.LENGTH_SHORT).show()
    }


    private fun selectImage(code: Int) {
        var i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Choose Picture"), code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // counter variable counts how many time user upload picture from his/her
        // gallery to make sure user upload two pictures

        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == FIRSTCODE || requestCode == SECONDCODE) && resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == FIRSTCODE) {
                imagehash[firstImageKey] = data.data!!
                Glide
                    .with(requireContext())
                    .load(data.data!!)
                    .into(mainView.firstImage)
                Log.d(TAG, Reuse.getUniqueId(FIRST_IMAGE + "${user.uid}"))
                counter++
            } else {
                imagehash[secondImageKey] = data.data!!
                Glide
                    .with(requireContext())
                    .load(data.data!!)
                    .into(mainView.secondImage)

                Log.d(TAG, Reuse.getUniqueId(SECOND_IMAGE + "${user.uid}"))
                counter++
            }


        }
    }


    private fun createDish() {
        when {
            counter < 2 -> {
                Toast.makeText(context, "Please Select Food Image First", Toast.LENGTH_SHORT)
                    .show()
            }
            validFields() -> {
                Reuse.startLoading(mainView.loading)
                setHashValues()

                viewModel.uploadDish(dishHash, imagehash).observe(this, Observer {

                    when (it.status) {

                        Status.LOADING -> {
                            Reuse.startLoading(mainView.loading)
                        }
                        Status.ERROR -> {
                            Reuse.stopLoading(mainView.loading)
                            Log.d(TAG, "Error=${it.msg}")

                            Toast.makeText(context, "Error=${it.msg}", Toast.LENGTH_SHORT)
                                .show()
                        }

                        Status.SUCCESS -> {
                            Reuse.stopLoading(mainView.loading)
                            Toast.makeText(
                                context,
                                "Your Dish is Uploaded Successfully...",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            dismiss()
                            if (!defaultSetting) {
                                startActivity(Intent(requireContext(), CookDashboard::class.java))
                            }
                        }

                    }
                })

            }
            else -> {
                Toast.makeText(context, "Fields are Mandatory ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDish() {
        when {
            counter < 1 -> {
                val hasSame = hasSameValue()
                if (validFields() && !hasSame) {
                    val title = mainView.name_field.text.toString()
                    val price = mainView.price_field.text.toString()
                    val cookTime = mainView.cookTime_field.text.toString()
                    val detail = mainView.detail_field.text.toString()
                    val foodType = dishHash[dishTypeKey]!!
                    Log.d(TAG, "2")
                    viewModel.updateDish(
                        title,
                        Integer.parseInt(price),
                        Integer.parseInt(cookTime),
                        detail,
                        foodType,
                        dish.dishId
                    ).observe(this, Observer {
                        when (it.status) {
                            Status.LOADING -> {
                                Reuse.startLoading(mainView.loading)
                            }
                            Status.ERROR -> {
                                Reuse.stopLoading(mainView.loading)
                                Toast.makeText(
                                    context,
                                    "Cannot Update Dish",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d(TAG, it.msg!!)
                            }

                            Status.SUCCESS -> {
                                Reuse.stopLoading(mainView.loading)
                                Toast.makeText(
                                    context,
                                    "Update Dish Successfully.... $hasSame",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        CookDashboard::class.java
                                    )
                                )
                            }
                        }


                    })

                } else if (hasSame) {
                    Toast.makeText(
                        context,
                        "It Seems You Change Nothing Please try Some Change",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "All the Fields are Mandatory to Fill",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            counter == 2 -> {
                createDish()
            }
            counter == 1 -> {
                Toast.makeText(context, "You cannot Change only one image", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun validFields(): Boolean {
        when {
            mainView.name_field.text.trim().isNullOrEmpty() -> {
                return false
            }
            mainView.detail_field.text.trim().isNullOrEmpty() -> {
                return false
            }
            mainView.cookTime_field.text.trim().isNullOrEmpty() -> {
                return false
            }
            mainView.price_field.text.trim().isNullOrEmpty() -> {
                return false
            }
            else -> return true
        }


    }

    private fun setHashValues() {
        dishHash.put(dishNameKey, mainView.name_field.text.toString())
        dishHash.put(cookTimeKey, mainView.cookTime_field.text.toString())
        dishHash.put(dishDescKey, mainView.detail_field.text.toString())
        dishHash.put(priceKey, mainView.price_field.text.toString())
        if (defaultSetting) {
            dishHash.put(firstImageNameKey, Reuse.getUniqueId(FIRST_IMAGE + "${user.uid}"))
            dishHash.put(secondImageNameKey, Reuse.getUniqueId(SECOND_IMAGE + "${user.uid}"))
            dishHash.put(userIdKey, user.uid)
            dishHash.put(dishIdKey, Reuse.getUniqueId(user.uid))
        } else {
            dishHash.put(firstImageNameKey, dish.firstImageName)
            dishHash.put(secondImageNameKey, dish.secondImageName)
            dishHash.put(userIdKey, dish.cookId)
            dishHash.put(dishIdKey, dish.dishId)
        }


    }

    private fun setEditValues() {
        mainView.price_field.setText("${dish.price}")
        mainView.name_field.setText(dish.name)
        mainView.cookTime_field.setText("${dish.CookTime}")
        mainView.detail_field.setText(dish.detail)
        Glide
            .with(requireContext())
            .load(dish.firstImageUrl)
            .into(mainView.firstImage)
        Glide
            .with(requireContext())
            .load(dish.secondImageUrl)
            .into(mainView.secondImage)


    }

    private fun hasSameValue(): Boolean {
        if (mainView.name_field.text.trim()
                .toString() == dish.name && mainView.detail_field.text.trim()
                .toString() == dish.detail
            && mainView.cookTime_field.text.trim()
                .toString() == "${dish.CookTime}" && mainView.price_field.text.trim()
                .toString() == "${dish.price}"
        ) {
            return true
        }

        Log.d(
            TAG, "${
                mainView.name_field.text.trim()
                    .toString() == dish.name
            }"
        )
        Log.d(
            TAG, "${
                mainView.detail_field.text.trim()
                    .toString().equals(dish.detail)
            }"
        )
        Log.d(
            TAG, "${
                mainView.cookTime_field.text.trim()
                    .toString() == "${dish.CookTime}"
            }"
        )

        Log.d(
            TAG, "${
                mainView.price_field.text.trim()
                    .toString() == "${dish.price}"
            }"
        )


        return false
    }


}