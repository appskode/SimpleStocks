package com.tastytrade.stock.di


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.tastytrade.stock.api.NetworkInterceptor
import com.tastytrade.stock.api.SymbolSearchApi
import com.tastytrade.stock.api.WatchListAPI
import com.tastytrade.stock.model.NoConnectivityException
import com.tastytrade.stock.model.NoInternetException
import com.tastytrade.stock.utils.Constants.BASE_URL
import com.tastytrade.stock.utils.Constants.BASE_URL_SEARCH
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Named("default")
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    @Named("search")
    fun provideRetrofitSearchBuilder(): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(BASE_URL_SEARCH)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    fun provideOkHttpClient(
        networkInterceptor: NetworkInterceptor, @ApplicationContext context: Context
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder().addInterceptor(NoConnectionInterceptor(context))
            .addInterceptor(loggingInterceptor).addInterceptor(networkInterceptor).build()
    }

    @Provides
    fun provideStocksAPI(
        @Named("default") retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient
    ): WatchListAPI {
        return retrofitBuilder.client(okHttpClient).build().create(WatchListAPI::class.java)
    }

    @Provides
    fun provideSearchAPI(
        @Named("search") retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient
    ): SymbolSearchApi {
        return retrofitBuilder.client(okHttpClient).build().create(SymbolSearchApi::class.java)
    }

    class NoConnectionInterceptor(
        private val context: Context
    ) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            return if (!isConnectionOn()) {
                throw NoConnectivityException()
            } else if (!isInternetAvailable()) {
                throw NoInternetException()
            } else {
                chain.proceed(chain.request())
            }
        }

        private fun isInternetAvailable(): Boolean {
            return try {
                val timeoutMs = 1500
                val sock = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)

                sock.connect(socketAddress, timeoutMs)
                sock.close()

                true
            } catch (e: IOException) {
                false
            }
        }

        private fun isConnectionOn(): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return postAndroidMInternetCheck(connectivityManager)
        }

        private fun postAndroidMInternetCheck(
            connectivityManager: ConnectivityManager
        ): Boolean {
            val network = connectivityManager.activeNetwork
            val connection = connectivityManager.getNetworkCapabilities(network)

            return connection != null && (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || connection.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ))
        }
    }
}
