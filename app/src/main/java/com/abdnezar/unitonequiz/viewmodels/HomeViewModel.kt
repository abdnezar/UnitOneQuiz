package com.abdnezar.unitonequiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdnezar.unitonequiz.data.models.City
import com.abdnezar.unitonequiz.data.models.Slider
import com.abdnezar.unitonequiz.repos.MainRepo
import com.abdnezar.unitonequiz.utils.Helper.Companion.log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val TAG = this.javaClass.simpleName
    private var repo : MainRepo = MainRepo()

    // slider
    private val _sliderChannel = Channel<SliderResponseResult>()
    val sliderFlow = _sliderChannel.receiveAsFlow()

    sealed class SliderResponseResult {
        data class ErrorResponse(val error: Int?) : SliderResponseResult()
        data class CorrectResponse(val sliders: ArrayList<Slider>) : SliderResponseResult()
    }

    // cities
    private val _cityChannel = Channel<CitiesResponseResult>()
    val citiesFlow = _cityChannel.receiveAsFlow()

    sealed class CitiesResponseResult {
        data class ErrorResponse(val error: Int?) : CitiesResponseResult()
        data class CorrectResponse(val cities: ArrayList<City>) : CitiesResponseResult()
    }

    fun getHomeData() = viewModelScope.launch {
        try {
            val response = repo.getHomeData()
            log(TAG, "Response(${response}) : ${response.body()}")

            if (response.body() != null) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val slidersList = response.body()!!.data.slider
                    _sliderChannel.send(SliderResponseResult.CorrectResponse(slidersList))

                    val citiesList = response.body()!!.data.allCities
                    _cityChannel.send(CitiesResponseResult.CorrectResponse(citiesList))
                } else {
                    log(TAG, "ResponseError (${response.body()})")
                    when(response.code()){
                        404 -> {
                            _sliderChannel.send(SliderResponseResult.ErrorResponse(5))
                            _cityChannel.send(CitiesResponseResult.ErrorResponse(5))
                        }
                        500 -> {
                            _sliderChannel.send(SliderResponseResult.ErrorResponse(4))
                            _cityChannel.send(CitiesResponseResult.ErrorResponse(4))
                        }
                        else -> {
                            _sliderChannel.send(SliderResponseResult.ErrorResponse(3))
                            _cityChannel.send(CitiesResponseResult.ErrorResponse(3))
                        }
                    }
                }
            } else {
                log(TAG, "Response Body NULL (${response.body()})")
                _sliderChannel.send(SliderResponseResult.ErrorResponse(2))
                _cityChannel.send(CitiesResponseResult.ErrorResponse(2))
            }

        } catch (e: Exception) {
            log(TAG, "HomeDataVMException : ${e.message}")
            _sliderChannel.send(SliderResponseResult.ErrorResponse(1))
            _cityChannel.send(CitiesResponseResult.ErrorResponse(1))
        }
    }
}