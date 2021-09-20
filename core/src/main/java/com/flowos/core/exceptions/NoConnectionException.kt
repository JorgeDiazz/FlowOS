package com.flowos.core.exceptions

import java.io.IOException

class NoConnectionException(override val message: String = "No connection exception") : IOException()
