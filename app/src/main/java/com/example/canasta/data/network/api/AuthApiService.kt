package com.example.canasta.data.network.api

import com.example.canasta.data.network.api.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {
    @POST("auth/register")
    suspend fun register(@Body registerData: NetworkRegisterRequest): Response<NetworkAuthResponse>

    @POST("auth/login")
    suspend fun login(@Body loginData: NetworkLoginRequest): Response<NetworkAuthResponse>

    @POST("auth/verify")
    suspend fun verifyEmail(@Body verificationData: NetworkVerificationRequest): Response<NetworkAuthResponse>

    @POST("auth/resend-verification")
    suspend fun resendVerification(@Body email: Map<String, String>): Response<Unit>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<NetworkUser>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body userData: NetworkNewUser): Response<NetworkUser>
}
