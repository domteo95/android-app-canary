package com.chelseatroy.canary.api

import retrofit2.http.GET

interface LoginService {
    @GET("/v3/ea2a2f23-308a-4a4f-8bb1-2fe128fb9be7")
    suspend fun authenticate(): CanarySession
}