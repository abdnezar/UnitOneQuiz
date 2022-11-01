package com.abdnezar.unitonequiz.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.abdnezar.unitonequiz.R
import com.abdnezar.unitonequiz.data.models.City
import com.abdnezar.unitonequiz.data.models.Slider
import com.abdnezar.unitonequiz.databinding.FragmentHomeBinding
import com.abdnezar.unitonequiz.ui.adapters.CitiesAdapter
import com.abdnezar.unitonequiz.ui.adapters.SliderAdapter
import com.abdnezar.unitonequiz.utils.Helper
import com.abdnezar.unitonequiz.utils.Helper.Companion.toast
import com.abdnezar.unitonequiz.utils.Loading
import com.abdnezar.unitonequiz.viewmodels.HomeViewModel
import com.abdnezar.unitonequiz.viewmodels.HomeViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.coroutines.flow.collectLatest


class HomeFragment : Fragment(R.layout.fragment_home), CitiesAdapter.OnClick {
    val TAG = this.javaClass.simpleName
    private lateinit var binding : FragmentHomeBinding
    private var _binding : FragmentHomeBinding? = null
    private lateinit var vm : HomeViewModel
    private lateinit var vmFactory : HomeViewModelFactory
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var citiesAdapter: CitiesAdapter
    private lateinit var userAuth : FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        _binding = binding

        userAuth = FirebaseAuth.getInstance()

        // vm & factory
        vmFactory = HomeViewModelFactory()
        vm = ViewModelProvider(this, vmFactory)[HomeViewModel::class.java]

        citiesAdapter = CitiesAdapter(requireContext(), this)
        binding.rvCities.adapter = citiesAdapter

        sliderAdapter = SliderAdapter(requireContext())
        binding.imageSlider.setSliderAdapter(sliderAdapter)
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageSlider.startAutoCycle()
    }

    override fun onStart() {
        super.onStart()

        if (userAuth.currentUser == null) {
            binding.ivCenter.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.signup_placeholder)
        } else {
            binding.ivCenter.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.our_servies_placeholder)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.ivCenter.setOnClickListener {
            if (userAuth.currentUser != null) {
                toast("already login as ${userAuth.currentUser?.phoneNumber}")
                return@setOnClickListener
            }

            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        getDate()
    }

    private fun getDate() {
        // get home data
        vm.getHomeData()

        lifecycleScope.launchWhenResumed {
            vm.sliderFlow.collectLatest {
                when (it) {
                    is HomeViewModel.SliderResponseResult.CorrectResponse -> {
                        val sliders = it.sliders
                        sliderAdapter.setData(sliders)
                        updateUI(Loading.SUCCESS)
                    }
                    is HomeViewModel.SliderResponseResult.ErrorResponse -> {
                        Helper.snack(binding.root, getString(R.string.can_not_get_sliders) + " ${it.error}")
                        updateUI(Loading.ERROR)
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            vm.citiesFlow.collectLatest {
                when (it) {
                    is HomeViewModel.CitiesResponseResult.CorrectResponse -> {
                        val cities = it.cities
                        citiesAdapter.setData(cities)
                        updateUI(Loading.SUCCESS)
                    }
                    is HomeViewModel.CitiesResponseResult.ErrorResponse -> {
                        Helper.snack(binding.root, getString(R.string.can_not_get_cities) + " ${it.error}")
                        updateUI(Loading.ERROR)
                    }
                }
            }
        }
    }

    private fun updateUI(state: Loading) {
        // update ui
        when (state) {
            Loading.LOADING -> {
                binding.pb.visibility = View.VISIBLE
            }
            Loading.SUCCESS -> {
                binding.pb.visibility = View.GONE
            }
            Loading.ERROR -> {
                binding.pb.visibility = View.GONE
            }
        }
    }

    override fun onClickCity(city: City) {
        toast(city.name)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}