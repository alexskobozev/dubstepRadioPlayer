package com.wishnewjam.dubstepfm.common

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
	class Success<T>(data: T) : Resource<T>(data)
	class Loading<T>(message: String? = null, data: T? = null) : Resource<T>(data, message)
	class Error<T>(message: String) : Resource<T>(message = message)
}
