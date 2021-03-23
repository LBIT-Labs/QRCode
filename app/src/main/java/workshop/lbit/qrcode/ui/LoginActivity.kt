package workshop.lbit.qrcode.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.iid.FirebaseInstanceId

import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.MainActivity
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.utils.Utilities
import java.io.IOException


class LoginActivity : AppCompatActivity() {

    private lateinit var etMobile: EditText
    private var et_Otp: EditText? = null
    private val TAG = LoginActivity::class.java!!.getName()

    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var mPhoneNumber: String? = null
    private var mOTP: String? = null
    internal lateinit var rlHomeLayout: RelativeLayout

    private var tv_testotp: TextView? = null
    private val PHONE_NUMBER_HINT = 100
    internal var mTokenId = ""
    internal var mRole = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        UserSession(this@LoginActivity).getToken(mTokenId)
        if (mTokenId.isEmpty()) {
            getToken()
        }
        getSupportActionBar()!!.hide();

        pref = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        editor = pref!!.edit()

        etMobile = findViewById(R.id.et_Phone)
        tv_testotp = findViewById(R.id.tv_testotp)
//        rlHomeLayout = findViewById(R.id.rlHomeLayout)
        et_Otp = findViewById(R.id.et_Otp)



        etMobile.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(textVal: Editable) {
                mPhoneNumber = textVal.toString()
                if (mPhoneNumber!!.length == 10) {

                    getLogin(mPhoneNumber)
                    et_Otp!!.requestFocus()

                    //                    Toast.makeText(LoginActivity.this,"Search",Toast.LENGTH_SHORT).show();
                    //                    textView.setText(inputText);
                }
            }
        })


        et_Otp!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(textVal: Editable) {
                mOTP = textVal.toString()
                if (mOTP!!.length == 4) {

                    if (!etMobile.text.toString().equals("")) {
                        getOTPLogin(mOTP, mPhoneNumber)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please Enter Mobile Number",
                            Toast.LENGTH_SHORT
                        ).show();

                    }
                }
            }
        })

        getPhoneNumberByTelephony()


    }


    private fun getPhoneNumberByTelephony() {
        try {
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val imei = tm.deviceId
            val tel = tm.line1Number
            if (tel != null) {
                Log.d("TAG", "MobileNumber By Telephony: $tel")
                setMobileNumber(tel)
            }


        } catch (e: SecurityException) {
            getPhoneNumberByGoogleAPI()
        }
    }

    private fun getPhoneNumberByGoogleAPI() {
        val hintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
        try {
            val googleApiClient =
                GoogleApiClient.Builder(this@LoginActivity).addApi(Auth.CREDENTIALS_API).build()
            val pendingIntent =
                Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest)
            startIntentSenderForResult(pendingIntent.intentSender, PHONE_NUMBER_HINT, null, 0, 0, 0)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Could not start hint picker Intent", e);
        }
    }

    private fun setMobileNumber(number: String) {
        var updatedNumber = number
        if (number.length > 10) {
            updatedNumber = number.replace("+91", "")
            updatedNumber = updatedNumber.replace(" ", "")
        }
//        presenter.setMobileNumber(updatedNumber, true)
        Log.e("TAG", "Selected_no : = [" + updatedNumber)
        etMobile.setText(updatedNumber)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHONE_NUMBER_HINT && resultCode == Activity.RESULT_OK) {
            val credential = data!!.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
            val phoneNumber = credential.id
            Log.d("TAG", "MobileNumber By Play store: $phoneNumber")
            setMobileNumber(phoneNumber)
        }
    }


    private fun getLogin(inputText: String?) {
        val mProgressDialog = ProgressDialog(this@LoginActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        if (inputText != null) {
            Constants.qrCode_uat.getUserLogin(inputText, "No", "No", "", "")
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {

                        try {

                            val string = response.body()!!.string()
                            Log.e("TAG", "Login" + " " + string)

                            if (string.equals("[\"User not found\"]")) {

                                val dialogBuilder = AlertDialog.Builder(this@LoginActivity)
                                dialogBuilder.setMessage("Do you want to Register ?")

                                    .setPositiveButton(
                                        "Proceed",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            val i = Intent(
                                                this@LoginActivity,
                                                RegistrationActivity::class.java
                                            )
                                            i.putExtra("inputText", inputText)
                                            startActivity(i)
                                            overridePendingTransition(
                                                R.anim.move_left_enter,
                                                R.anim.move_left_exit
                                            )
                                            finish()
                                            Utilities.hideSoftKeyboard(this@LoginActivity)

                                        })

                                    .setNegativeButton(
                                        "Cancel",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            dialog.cancel()
                                        })

                                val alert = dialogBuilder.create()
                                alert.setTitle("PreOwned LBIT Register")
                                alert.show()
                                Utilities.hideSoftKeyboard(this@LoginActivity)

                            } else {
                                Log.e("TAG", "Login" + " " + string)

                                val parts = string.split(" ")
                                val lastWord = parts[parts.size - 1]
                                println(lastWord)
                                val otp = lastWord.substring(0, 4)

                                tv_testotp!!.setText("Your OTP : " + otp)

                            }
                            if (mProgressDialog.isShowing)
                                mProgressDialog.dismiss()

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e(
                            "TAG",
                            "onFailure() called with: call = [" + call.request()
                                .url() + "], t = [" + t + "]",
                            t
                        )

                        if (mProgressDialog.isShowing)
                            mProgressDialog.dismiss()
                    }
                })
        }

    }

    private fun getOTPLogin(mOTP: String?, mPhone: String?) {
        val mProgressDialog = ProgressDialog(this@LoginActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        if (mOTP != null) {
            if (mPhone != null) {
                Constants.qrCode_uat.getUserLogin(mPhone, "No", "No", mOTP, mTokenId)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {

                            try {
                                val string = response.body()!!.string()
                                Log.e("TAG", "Login" + " " + string)

                                if (!string.equals("[\"OTP Valid\"]", ignoreCase = true)) {

                                    pref = getSharedPreferences(
                                        Constants.PREFS_NAME,
                                        Context.MODE_PRIVATE
                                    )
                                    editor = pref!!.edit()
                                    editor!!.putString(Constants.LOGINUSER_MOBILE, mPhone)
                                    editor!!.commit()

                                    val jsonArray = JSONArray(string)
                                    val jsonObject = jsonArray.getJSONObject(0)

                                    val roles = jsonObject.getString("role")
                                    val parts = roles.split(",")

                                    for (i in parts!!.indices) {
                                        val part = parts.get(i)
                                        if (part.equals("wh_store_boy")) {
                                            mRole = "wh_store_boy"

                                        } else if (part.equals("wh_security")) {
                                            mRole = "wh_security"

                                        } else if (part.equals("stores")) {
                                            mRole = "stores"

                                        } else if (part.equals("counter")) {
                                            mRole = "counter"

                                        } else if (part.equals("security")) {
                                            mRole = "security"

                                        }
                                    }

//                                val mDealerId = (jsonObject as JSONObject).getString("dealer_id")

                                    val dict_data = JSONObject()
                                    try {
                                        dict_data.put(
                                            "user_name",
                                            jsonObject.getString("user_name")
                                        );
                                        dict_data.put("role", mRole);
                                        dict_data.put("uid", jsonObject.getString("uid"));
                                        Log.e("data", dict_data.toString())
                                    } catch (e: JSONException) {
                                        e.printStackTrace();
                                    }
                                    UserSession(this@LoginActivity).setLoginDetails(dict_data.toString())


                                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(i)
                                    overridePendingTransition(
                                        R.anim.move_left_enter,
                                        R.anim.move_left_exit
                                    )
                                    finish()


                                }

                                if (mProgressDialog.isShowing)
                                    mProgressDialog.dismiss()


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e(
                                "TAG",
                                "onFailure() called with: call = [" + call.request()
                                    .url() + "], t = [" + t + "]",
                                t
                            )

                            if (mProgressDialog.isShowing)
                                mProgressDialog.dismiss()
                        }
                    })

            }
        }
    }


    private fun getToken() {

        Thread(Runnable {
            try {
                mTokenId =
                    FirebaseInstanceId.getInstance().getToken(getString(R.string.SENDER_ID), "FCM")
                        .toString()
                UserSession(this@LoginActivity).setToken(mTokenId)

                Log.e("TAG", "TOKEN**** " + mTokenId)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }

}
