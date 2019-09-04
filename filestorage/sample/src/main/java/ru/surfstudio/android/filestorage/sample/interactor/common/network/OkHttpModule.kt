package ru.surfstudio.android.filestorage.sample.interactor.common.network

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.surfstudio.android.dagger.scope.PerApplication
import ru.surfstudio.android.network.cache.SimpleCacheInterceptor
import ru.surfstudio.android.network.etag.EtagInterceptor
import java.util.concurrent.TimeUnit

@Module
class OkHttpModule {

    companion object {
        private const val NETWORK_TIMEOUT = 10 //sec
    }

    @Provides
    @PerApplication
    fun provideOkHttpClient(cacheInterceptor: SimpleCacheInterceptor,
                            etagInterceptor: EtagInterceptor,
                            httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(NETWORK_TIMEOUT.toLong(), TimeUnit.SECONDS)
            readTimeout(NETWORK_TIMEOUT.toLong(), TimeUnit.SECONDS)
            writeTimeout(NETWORK_TIMEOUT.toLong(), TimeUnit.SECONDS)

            addInterceptor(cacheInterceptor)
            addInterceptor(etagInterceptor)
            addInterceptor(httpLoggingInterceptor)
        }.build()
    }
}