package com.bentrengrove.projectexplorer.login

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginAPI {
    @POST("login/device/code")
    @Headers("Accept: application/json")
    suspend fun requestCode(@Body request: LoginDeviceCodeRequest): LoginDeviceCodeResponse

    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun pollCode(@Body request: LoginDevicePollRequest): LoginDevicePollResponse
}

data class LoginDeviceCodeRequest(
    @field:Json(name="client_id") val clientId: String,
    @field:Json(name="scope") val scope: String
)

data class LoginDeviceCodeResponse(
    @field:Json(name="device_code") val deviceCode: String,
    @field:Json(name="user_code") val userCode: String,
    @field:Json(name="verification_uri") val verificationUri: String,
    @field:Json(name="expires_in") val expiresIn: Long,
    @field:Json(name="interval") val interval: Long
)

data class LoginDevicePollRequest(
    @field:Json(name="client_id") val clientId: String,
    @field:Json(name="device_code") val deviceCode: String,
    @field:Json(name="grant_type") val grantType: String = "urn:ietf:params:oauth:grant-type:device_code"
)

data class LoginDevicePollResponse(
    @field:Json(name="access_token") val accessToken: String?,
    @field:Json(name="token_type") val tokenType: String?,
    @field:Json(name="scope") val scope: String?
)