package com.flowos.app.di.modules

import com.flowos.app.useCases.VerifyInternetConnectivityUseCase
import com.flowos.base.interfaces.SingleUseCase
import com.flowos.base.interfaces.UseCase
import com.flowos.core.qualifiers.GetIsDeviceInMovement
import com.flowos.core.qualifiers.LongDateToTimestamp
import com.flowos.core.qualifiers.VerifyInternet
import com.flowos.sensors.entities.SensorMeasure
import com.flowos.sensors.useCases.GetIsDeviceInMovementUseCase
import com.flowos.sensors.useCases.LongDateToTimestampUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class AppUseCasesModule {

  @Binds
  @VerifyInternet
  abstract fun bindVerifyInternetConnectivityUseCase(useCase: VerifyInternetConnectivityUseCase): SingleUseCase<Unit, Boolean>

  @Binds
  @LongDateToTimestamp
  abstract fun bindsLongDateToTimestampUseCase(longDateToTimestampUseCase: LongDateToTimestampUseCase): UseCase<Long, String>

  @Binds
  @GetIsDeviceInMovement
  abstract fun bindsGetIsDeviceInMovementUseCase(getIsDeviceInMovement: GetIsDeviceInMovementUseCase): UseCase<SensorMeasure, Boolean>
}
