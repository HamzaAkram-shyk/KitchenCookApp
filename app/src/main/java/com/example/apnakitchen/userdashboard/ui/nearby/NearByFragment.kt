package com.example.apnakitchen.userdashboard.ui.nearby

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.Utils.Status
import com.example.apnakitchen.Utils.TAG
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.adapter.cookAdapter.DishAdapter
import com.example.apnakitchen.commonModule.detailActivity.DetailedActivity
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.pushNotify.Notification
import com.example.apnakitchen.pushNotify.PushNotification
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.near_by_fragment.*
import kotlinx.android.synthetic.main.near_by_fragment.view.*

class NearByFragment : Fragment(), MyResponse<Dish> {

    private lateinit var viewModel: NearByViewModel
    private lateinit var mainView: View
    val PERMISSION_ID = 512
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var islock = false
    private lateinit var adapter: DishAdapter

    override fun success(data: Dish, msg: String) {
        activity?.let {
            val intent = Intent(requireContext(), DetailedActivity::class.java)
            intent.putExtra(DetailedActivity.dataKey, data)
            intent.putExtra(DetailedActivity.typeKey, false)
            startActivity(intent)
        }

    }

    override fun onError(data: Dish, msg: String) {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(NearByViewModel::class.java)
        mainView = inflater.inflate(R.layout.near_by_fragment, container, false)
        adapter = DishAdapter(arrayListOf<Dish>(), requireContext(), this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mainView.recyclerView.hasFixedSize()
        mainView.recyclerView.layoutManager = LinearLayoutManager(context)
        mainView.recyclerView.adapter = adapter
        getLastLocation()
        mainView.search_bar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    val list = viewModel.getFilterList(s.toString())
                    adapter.addList(list as ArrayList<Dish>)
                } else {
                    adapter.addList(viewModel.dishList)
                }
            }

        })
        return mainView
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationClient!!.lastLocation!!.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location != null) {
                        getNearByList(location)

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
                islock = true
                getNearByList(p0.lastLocation)
            }

        }
    }

    private fun getNearByList(location: Location) {
        viewModel.getNearByCookIds(location).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Reuse.startLoading(loading)
                }
                Status.ERROR -> {
                    Reuse.stopLoading(loading)
                    if (it.msg == "0") {
                        mainView.alert_label.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            context,
                            "${it.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, it.msg!!)
                    }

                }

                Status.SUCCESS -> {
                    fetchNearByDishes(it.data!!)
                }
            }
        })
    }

    private fun fetchNearByDishes(listOfCook: List<String>) {
        for (cookId in listOfCook) {
            viewModel.getNearByDishes(cookId).observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        Reuse.stopLoading(loading)
                        if (it.msg == "1") {
                            viewModel.appendList(it.data!!)
                            adapter.addList(viewModel.dishList)
                        }

                    }
                    Status.ERROR -> {
                        Reuse.stopLoading(loading)
                        Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        Reuse.startLoading(loading)
                    }
                }
            })
        }
    }


}