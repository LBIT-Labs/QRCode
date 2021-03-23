package workshop.lbit.qrcode.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId

import org.json.JSONObject
import workshop.lbit.qrcode.MainActivity
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.utils.Constants
import java.io.IOException

class Splashscreen : AppCompatActivity(), View.OnClickListener {
    internal var mMobileNumber: String? = null
    private val dict_dat: JSONObject? = null
    private val mUserDetails: String? = null
    internal var SPLASH_TIME_OUT = 3000

    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    lateinit var newToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getSupportActionBar()!!.hide();
        initView()


        /*Shared*/
        sharedpreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()


        //        mNotification = sharedpreferences.getString(Constants.LOGINUSER_NOTIFY, "");
        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")

        if (mMobileNumber!!.length > 0) {
            val i = Intent(this@Splashscreen, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
            finish()
        } else {
            val i = Intent(this@Splashscreen, LoginActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
            finish()
        }


    }

    private fun initView() {

        Thread(Runnable {
            try {
                newToken =
                    FirebaseInstanceId.getInstance().getToken(getString(R.string.SENDER_ID), "FCM")
                        .toString()
                UserSession(this@Splashscreen).setToken(newToken)

                Log.e("TAG", "TOKEN**** " + newToken)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }

    override fun onClick(view: View) {
        val i = view.id


    }

}
