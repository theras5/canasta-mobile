package ar.edu.itba.example.api.data.network.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface NetworkPagedData<T : @Serializable Any> {
    val data: List<T>
}

@Serializable
class NetworkPagedCategories(override val data: List<NetworkCategory>) : NetworkPagedData<NetworkCategory>
