package com.flowos.sensors

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.flowos.components.utils.viewBinding
import com.flowos.core.EventObserver
import com.flowos.sensors.data.SensorsNews
import com.flowos.sensors.data.SensorsUiModel
import com.flowos.sensors.databinding.FragmentSensorsBinding
import com.flowos.sensors.viewModels.SensorsViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Represents sensors fragment.
 *
 * This is the orchestrator of sensors functionality (GPS, BLE, NFC).
 */
class SensorsFragment : Fragment(R.layout.fragment_sensors) {

  companion object {
    fun newInstance(): SensorsFragment = SensorsFragment()
  }

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private val viewModel: SensorsViewModel by viewModels { viewModelFactory }

  private val binding by viewBinding(FragmentSensorsBinding::bind)

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
    viewModel.onViewActive()
  }

  private fun initializeObserver() {
    viewModel.liveData.observe(
      viewLifecycleOwner,
      {
        observeData(it)
      }
    )
  }

  private fun initializeSubscription() {
    viewModel.news.observe(
      viewLifecycleOwner,
      EventObserver {
        handleNews(it)
      }
    )
  }

  private fun observeData(uiModel: SensorsUiModel) {
  }

  private fun handleNews(news: SensorsNews) {
    when (news) {
    }
  }
}
