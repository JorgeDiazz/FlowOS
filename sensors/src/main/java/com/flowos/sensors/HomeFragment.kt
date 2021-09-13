package com.flowos.sensors

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.flowos.base.interfaces.Logger
import com.flowos.components.utils.viewBinding
import com.flowos.core.EventObserver
import com.flowos.sensors.data.SensorsNews
import com.flowos.sensors.data.SensorsUiModel
import com.flowos.sensors.databinding.FragmentHomeBinding
import com.flowos.sensors.viewModels.SensorsViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Represents home fragment.
 *
 * This is the home fragment for the user.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {

  companion object {
    fun newInstance(): HomeFragment = HomeFragment()
  }

  @Inject
  lateinit var logger: Logger

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private val sensorsViewModel: SensorsViewModel by viewModels { viewModelFactory }

  private val binding by viewBinding(FragmentHomeBinding::bind)

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initializeViewModel()
    initializeObserver()
    initializeSubscription()
  }

  private fun initializeViewModel() {
    sensorsViewModel.onViewActive()
  }

  private fun initializeObserver() {
    sensorsViewModel.liveData.observe(
      viewLifecycleOwner,
      {
        observeData(it)
      }
    )
  }

  private fun initializeSubscription() {
    sensorsViewModel.news.observe(
      viewLifecycleOwner,
      EventObserver {
        handleNews(it)
      }
    )
  }

  private fun handleNews(news: SensorsNews) {
    // no-op by default
  }

  private fun observeData(uiModel: SensorsUiModel) {
    uiModel.vehicleId?.let {
      binding.textViewVehicleIdValue.text = it
    }
  }
}
