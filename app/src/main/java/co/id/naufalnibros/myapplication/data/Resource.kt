package co.id.naufalnibros.myapplication.data

data class Resource<T>(val status: Status, val data: T? = null, val message: String? = null)