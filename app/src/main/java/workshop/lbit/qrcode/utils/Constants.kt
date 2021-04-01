package workshop.lbit.qrcode.utils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import workshop.lbit.qrcode.retrofitservice.retrofit_qrcode
import java.util.concurrent.TimeUnit


object Constants {


//    val QRCODE_URL = "https://multibrand.lbit.co.in/"
//    val QRCODE_URL = "http://13.233.31.10:9057/"

//    val QRCODE_URL_WS = "https://multibrand.lbit.co.in"
//    val QRCODE_URL_WH = "https://multibrand.lbit.co.in"

    val QRCODE_URL_WS = "http://13.233.31.10:9065"
    val QRCODE_URL_WH = "http://13.233.31.10:9065"

//    val QRCODE_URL_WH = "http://13.233.31.10:9057"

    val PREOWNED_PROD_URL = "http://13.233.31.10:9020/"
    val PHP_URL = "http://13.127.220.54:1213/"
    val CAMPAIGN_URL = "http://lbit.letzbank.com/"

    val PREFS_NAME = "LOGIN DETAILS"
    val WH_User = "WSAPIUSER"
    val WH_pwd = "WSEMPTYPWD"

    val LOGINUSER_MOBILE = "login_mobile"

    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(80000, TimeUnit.SECONDS)
        .connectTimeout(80000, TimeUnit.SECONDS)
        .build()


    val qrCode_uat = Retrofit.Builder()
        .baseUrl(QRCODE_URL_WS)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create<retrofit_qrcode>(retrofit_qrcode::class.java)

    val qrCode_uat_wh = Retrofit.Builder()
        .baseUrl(QRCODE_URL_WH)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create<retrofit_qrcode>(retrofit_qrcode::class.java)



    /*val retrofit_Campaign = Retrofit.Builder()
        .baseUrl(CAMPAIGN_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create<retrofit_qrcode>(retrofit_qrcode::class.java!!)*/

    /*val valuation_car_prod = Retrofit.Builder()
        .baseUrl(PHP_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create<retrofit_qrcode>(retrofit_qrcode::class.java!!)*/


    /* val retrofit_9025 = Retrofit.Builder()
             .baseUrl(BASE_URL)
             .client(okHttpClient)
             .addConverterFactory(GsonConverterFactory.create())
             .build().create<retrofit_preowned>(retrofit_preowned::class.java!!)*/


}