package com.flowos.auth.data

import android.os.Parcelable
import com.flowos.auth.domain.data.DriverData
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriverDataArgument(
  val driverName: String,
  val boards: List<String>,
) : Parcelable

fun DriverData.parcelize(): DriverDataArgument =
  DriverDataArgument(driverName = driverName, boards = boards)
