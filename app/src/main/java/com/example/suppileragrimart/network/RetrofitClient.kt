package com.example.suppileragrimart.network

import android.content.Context
import android.os.Build
import com.example.suppileragrimart.R
import com.example.suppileragrimart.utils.Constants.LOCALHOST
import com.example.suppileragrimart.utils.Utils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class RetrofitClient {
    private val BASE_URL: String = LOCALHOST

    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val okBuilder = OkHttpClient.Builder()
            .readTimeout(30 , TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            addCustomTrustManager(okBuilder)
        }

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okBuilder.build())
            .build()
    }

    fun getApi(): Api {
        return retrofit.create(Api::class.java)
    }

    companion object {
        private var mInstance: RetrofitClient? = null

        @Synchronized
        fun getInstance(): RetrofitClient {
            if (mInstance == null) {
                mInstance = RetrofitClient()
            }
            return mInstance!!
        }
    }

    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        IOException::class,
        NoSuchAlgorithmException::class,
        KeyManagementException::class
    )
    fun addCustomTrustManager(builder: OkHttpClient.Builder) {
//        val caFileInputStream: InputStream = context.resources.openRawResource(R.raw.server)
        val caFileInputStream: InputStream = Utils.certsInputStream
        val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
        val yourCertificate: X509Certificate = certificateFactory.generateCertificate(caFileInputStream) as X509Certificate

        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("springboot", yourCertificate)

        // Create a TrustManager that trusts the server certificate
        val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val trustManagers: Array<TrustManager> = arrayOf(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    // No client verification needed
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    // Check if the server's certificate matches your trusted certificate
                    for (cert in chain) {
                        if (cert == yourCertificate) {
                            return // The certificate is trusted
                        }
                    }
                    throw CertificateException("Server certificate does not match the expected certificate.")
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

        builder.sslSocketFactory(sslSocketFactory, trustManagers[0] as X509TrustManager)
        Utils.certsInputStream.close()
    }
}