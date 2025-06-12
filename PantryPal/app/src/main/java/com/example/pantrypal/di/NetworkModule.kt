package com.example.pantrypal.di

import com.example.pantrypal.network.OpenRouterApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Application-level scope for network components
object NetworkModule {

    private const val BASE_URL = "https://openrouter.ai/api/v1/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        // For development: log network request/response bodies and headers
        // For release builds, you might want to set Level.NONE or Level.BASIC
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) // Or Level.BASIC for less verbose logs
        return logging
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Add logging interceptor
            // You can add other interceptors here if needed (e.g., for auth, timeouts)
            // .connectTimeout(30, TimeUnit.SECONDS)
            // .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        // Configure Gson if needed, e.g., setLenient() or register custom type adapters
        val gson = GsonBuilder().create()
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Use the configured OkHttpClient
            .addConverterFactory(gsonConverterFactory) // Use Gson for JSON serialization/deserialization
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenRouterApiService(retrofit: Retrofit): OpenRouterApiService {
        // Create and provide an implementation of the OpenRouterApiService interface
        return retrofit.create(OpenRouterApiService::class.java)
    }
}
