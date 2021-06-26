package com.example.apnakitchen.cookdashboard.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.authentication.SigIn_SignUp
import com.example.apnakitchen.cookdashboard.BaseFragment
import com.example.apnakitchen.cookdashboard.CookDashboard
import com.example.apnakitchen.imageupload.Cloud
import com.example.apnakitchen.model.User
import com.example.apnakitchen.model.cookModel.Cook
import com.example.apnakitchen.repository.authRepository.AuthRepository
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.cook_profile.*
import kotlinx.android.synthetic.main.cook_profile.view.*
import kotlinx.android.synthetic.main.update_dailog.*
import kotlinx.coroutines.launch
import java.util.*

class ProfileFragment : BaseFragment() {


    private lateinit var viewModel: ProfileViewModel
    private lateinit var mainView: View
    override var navigationVisibility: Int = View.GONE
    private lateinit var dataStore: DataStoreRepository
    private lateinit var currentCook: Cook
    private val galleryCode = 111
    val PERMISSION_ID = 512
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var islock = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainView = inflater.inflate(R.layout.cook_profile, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        dataStore = DataStoreRepository.getInstance(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getCurrentCook()

        mainView.editBtn.setOnClickListener {
            popupMenu(it)
        }
        mainView.imageBtn.setOnClickListener {
            selectImage()
        }

        mainView.locationBtn.setOnClickListener {
            getLastLocation()
        }

        return mainView
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationClient!!.lastLocation!!.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location != null) {
                        setKitchenAddress(location.latitude, location.longitude)
                        // Log.d(TAG, "One ${setKitchenAddress(location.latitude, location.longitude)}")
                    } else {
                        getNewLocation()
                    }
                }
            } else {
                Toast.makeText(context, "Enable Location Service", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermission()
        }
    }

    private fun setProfileImage(url: String) {
        mainView.profileImage.visibility = View.VISIBLE
        shimmerEffect.visibility = View.GONE
        Glide.with(requireContext())
            .load(url)
            .into(mainView.profileImage)
    }

    override fun onResume() {
        super.onResume()
        if (activity is CookDashboard) {
            var activity = activity as CookDashboard
            activity.setVisibilityBottomAppBar(navigationVisibility)
        }

    }


    private fun popupMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Edit Name")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Add Introduction")
        if (currentCook.kitchenStatus == KitchenStatus.ONLINE) popupMenu.menu.add(
            Menu.NONE,
            3,
            3,
            "Kitchen Off"
        ) else popupMenu.menu.add(Menu.NONE, 3, 3, "Kitchen On")
        popupMenu.menu.add(Menu.NONE, 4, 4, "Logout")
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    updateDialog("Name", true, currentCook.user.name!!)
                    popupMenu.dismiss()

                }


                2 -> {
                    updateDialog("Introduction", false, currentCook.about!!)
                    popupMenu.dismiss()
                }
                3 -> {
                  changeKitchenStatus()
                }

                4 -> {
                    Reuse.startLoading(cookLoading)
                    AuthRepository.signOut().observe(viewLifecycleOwner, Observer {
                        when (it) {
                            true -> {
                                logout()
                                Reuse.stopLoading(cookLoading)
                                startActivity(Intent(requireContext(), SigIn_SignUp::class.java))
                                activity?.finish()
                            }
                            false -> {
                                Reuse.stopLoading(cookLoading)
                                Toast.makeText(context, "Failed In Logout", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    })
                    popupMenu.dismiss()
                }


            }
            true
        })
        popupMenu.show()

    }

    private fun getCurrentCook() {
        viewModel.getCurrentCook().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    shimmerEffect.startShimmer()
                    Reuse.startLoading(cookLoading)
                }
                Status.SUCCESS -> {
                    Reuse.stopLoading(cookLoading)
                    shimmerEffect.stopShimmer()
                    currentCook = it.data!!
                    setProfile()

                }
                Status.ERROR -> {
                    if (it.msg.equals("0")) {
                        getUser()
                    } else {
                        Reuse.stopLoading(cookLoading)
                        Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun addCook(cook: Cook) {
        viewModel.updateCook(cook).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(cookLoading)
                }
                Status.ERROR -> {
                    Reuse.stopLoading(cookLoading)
                    Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    Reuse.stopLoading(cookLoading)
                    getCurrentCook()
                }
            }
        })
    }

    private fun getUser() {
        viewModel.getUser().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    Reuse.stopLoading(cookLoading)
                    Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    val user: User = it.data!!
                    var cook = Cook(user)
                    addCook(cook)
                }
            }
        })
    }

    private fun setProfile(

    ) {
        cookNameTitle.text = currentCook.user.name
        cookRating.rating = currentCook.rating.toFloat()
        cookName.text = currentCook.user.name
        cookId.text = "cookId: ${currentCook.user.userId}"
        if (currentCook.user.photoUrl != NULL) {
            setProfileImage(currentCook.user.photoUrl!!)
        }

        if (currentCook.about != NULL) {
            cookIntroduction.text = currentCook.about
        }

        if (currentCook.kitchenAddress == NULL) {
            Reuse.showAlertDialog(requireContext(), getString(R.string.LocationNote))
        } else {
            kitchenAddress.text = "Kitchen Address: ${currentCook.kitchenAddress}"
        }
        if (currentCook.kitchenStatus == KitchenStatus.ONLINE) {
            kitchenStatus.text = "Kitchen Status: Online"
        } else {
            kitchenStatus.text = "Kitchen Status: Offline"
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            dataStore.setUserLogin(false)

        }
    }

    private fun uploadProfileImage(uri: Uri) {
        Cloud.uploadImage(uri, currentCook.user.userId!!, object : Cloud.CloudResponse {
            override fun onSuccess(url: String) {
                currentCook.user.photoUrl = url
                viewModel.updateCook(currentCook).observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Status.SUCCESS -> {
                            setProfileImage(url)
                            shimmerEffect.stopShimmer()
                            Toast.makeText(
                                context,
                                "Picture Uploaded Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Status.ERROR -> {
                            shimmerEffect.stopShimmer()
                            Toast.makeText(
                                context,
                                "Profile Picture don't upload Successfully please Try Again",
                                Toast.LENGTH_SHORT
                            ).show()
                            currentCook.user.photoUrl = NULL
                        }
                    }
                })


            }

            override fun onError(error: String) {
                Toast.makeText(
                    context,
                    "Profile Picture don't upload Successfully please Try Again",
                    Toast.LENGTH_SHORT
                ).show()
                shimmerEffect.stopShimmer()
                Log.d(TAG, "Error= $error")
            }

        })
    }


    private fun selectImage() {
        var i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Choose Picture"), galleryCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // counter variable counts how many time user upload picture from his/her
        // gallery to make sure user upload two pictures

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == galleryCode && resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == galleryCode) {
                shimmerEffect.visibility = View.VISIBLE
                profileImage.visibility = View.GONE
                shimmerEffect.startShimmer()
                uploadProfileImage(data.data!!)

            }


        }
    }

    private fun updateDialog(title: String, updateName: Boolean, currentText: String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.update_dailog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.dialogTitle.text = "Edit your $title"
        dialog.inputField.setText(currentText)
        dialog.show()
        dialog.cancel_button.setOnClickListener {
            dialog.cancel()
        }
        dialog.action_button.setOnClickListener {
            if (!dialog.inputField.text.trim().isNullOrEmpty()) {
                if (dialog.inputField.text.toString() == currentText) {
                    dialog.inputField.error = "New $title should be different than previous one"

                } else {
                    val text = dialog.inputField.text.toString()
                    if (updateName) currentCook.user.name = text else currentCook.about = text
                    viewModel.updateCook(currentCook).observe(viewLifecycleOwner, Observer {
                        when (it.status) {
                            Status.LOADING -> {
                                dialog.cancel()
                                Reuse.startLoading(cookLoading)
                            }
                            Status.ERROR -> {
                                Reuse.stopLoading(cookLoading)
                                if (updateName) currentCook.user.name =
                                    currentText else currentCook.about = NULL
                                Toast.makeText(
                                    context,
                                    "your $title can't Change Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            Status.SUCCESS -> {
                                Reuse.stopLoading(cookLoading)
                                if (updateName) {
                                    cookName.text = currentCook.user.name
                                    cookNameTitle.text = currentCook.user.name
                                } else {
                                    cookIntroduction.text = currentCook.about
                                }

                                Toast.makeText(
                                    context,
                                    "your $title Change Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })

                }

            } else {
                dialog.inputField.error = "$title can't be null"
            }


        }


    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            return true
        }


        return false

    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationClient!!.requestLocationUpdates(locationRequest, callback, Looper.myLooper())


    }

    private val callback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            if (!islock) {
                var lastLocation = p0.lastLocation
                islock = true
                setKitchenAddress(lastLocation.latitude, lastLocation.longitude)
            }

        }
    }

    private fun setKitchenAddress(lat: Double, lon: Double) {
        var geoCoder = Geocoder(context, Locale.getDefault())
        var address = geoCoder.getFromLocation(lat, lon, 1)
        val location=Loc(lat,lon)
        viewModel.addAddress(address[0].getAddressLine(0), location).observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    Status.LOADING -> {
                        Reuse.startLoading(mainView.cookLoading)
                    }

                    Status.ERROR -> {
                        Reuse.stopLoading(mainView.cookLoading)
                        Toast.makeText(context, "Error ${it.msg}", Toast.LENGTH_SHORT).show()
                    }

                    Status.SUCCESS -> {
                        Reuse.stopLoading(mainView.cookLoading)
                        currentCook.kitchenAddress = it.data!!
                        currentCook.location=location
                        kitchenAddress.text = "Kitchen Address: ${it.data}"
                    }
                }


            })

    }

    private fun changeKitchenStatus() {
        val status: KitchenStatus =
            if (currentCook.kitchenStatus == KitchenStatus.ONLINE) KitchenStatus.OFFLINE else KitchenStatus.ONLINE
        viewModel.changeStatus(status).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(mainView.cookLoading)
                }
                Status.ERROR -> {
                    Reuse.stopLoading(mainView.cookLoading)
                    Toast.makeText(context, "Error ${it.msg}", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    Reuse.stopLoading(mainView.cookLoading)
                    currentCook.kitchenStatus = status
                    if (currentCook.kitchenStatus == KitchenStatus.ONLINE) {
                        kitchenStatus.text = "Kitchen Status: Online"
                    } else {
                        kitchenStatus.text = "Kitchen Status: Offline"
                    }
                }
            }
        })
    }


}