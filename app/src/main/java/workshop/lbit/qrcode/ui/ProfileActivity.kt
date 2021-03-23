package workshop.lbit.qrcode.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import android.util.Log
import workshop.lbit.qrcode.MainActivity
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.customfonts.MyTextView_Montserrat_Regular
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.utils.Constants


class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var backtoolbar: RelativeLayout
    internal lateinit var llSearch: RelativeLayout
    internal lateinit var toolbar_title: TextView

    internal lateinit var mAdv_Name: String
    internal lateinit var mEmply_ID: String
    internal lateinit var mDesignation: String
    internal lateinit var mDepartment: String
    internal lateinit var mManger: String
    internal lateinit var mBrand: String
    internal lateinit var mBranch: String
    internal lateinit var mDealerLoc: String
    internal var mMobileNumber: String? = null


    /*TextView*/
    internal var tvProfilename: MyTextView_Roboto_Bold? = null
    internal var tvPhoneNumbers: MyTextView_Montserrat_Regular? = null
    internal var tvEmployeeid: MyTextView_Montserrat_Regular? = null
    internal var tvdesignation: MyTextView_Montserrat_Regular? = null
    internal var tvdepartment: MyTextView_Montserrat_Regular? = null
    internal var tvManger: MyTextView_Montserrat_Regular? = null
    internal var tvbrand: MyTextView_Montserrat_Regular? = null
    internal var tvbranch: MyTextView_Montserrat_Regular? = null
    internal lateinit var tvdateofjoining: MyTextView_Montserrat_Regular
    internal lateinit var sp_branchlist: MyTextView_Montserrat_Regular


    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profilescreen)
        getSupportActionBar()!!.hide();


        /*Shared*/
        sharedpreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        //        mNotification = sharedpreferences.getString(Constants.LOGINUSER_NOTIFY, "");
        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")


        val data = UserSession(this@ProfileActivity).getLoginDetails()

        var dict_data = JSONObject()
        try {
            dict_data = JSONObject(data)
            mAdv_Name = dict_data.optString("adv_name")
            mEmply_ID = dict_data.optString("emp_id")
            mDesignation = dict_data.optString("Desg")
            mDepartment = dict_data.optString("Dept")
            mBranch = dict_data.optString("dealer_loc_name")
            mDealerLoc = dict_data.optString("dealer_loc")
            mBrand = dict_data.optString("brand")
            mManger = dict_data.optString("Manager")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e("test***", "Name  " + mAdv_Name)

        init()

        tvProfilename?.setText(mAdv_Name)
        tvPhoneNumbers?.setText(mMobileNumber)
        tvEmployeeid?.setText(mEmply_ID)
        tvdesignation?.setText(mDesignation)
        tvdepartment?.setText(mDepartment)
        tvManger?.setText(mManger)
        tvbrand?.setText(mBrand)

    }

    /*Initialize variables*/
    private fun init() {
        toolbar_title = findViewById(R.id.toolbar_title)
        llSearch = findViewById(R.id.llSearch)
        backtoolbar = findViewById(R.id.backtoolbar)

        tvProfilename = findViewById(R.id.tvProfilename)
        tvPhoneNumbers = findViewById(R.id.tvPhoneNumbers)
        tvEmployeeid = findViewById(R.id.tvEmployeeid)
        tvdesignation = findViewById(R.id.tvdesignation)
        tvdepartment = findViewById(R.id.tvdepartment)
        tvManger = findViewById(R.id.tvManger)
        tvbrand = findViewById(R.id.tvbrand)
        tvbranch = findViewById(R.id.tvbranch)
        tvdateofjoining = findViewById(R.id.tvdateofjoining)
        tvbranch = findViewById(R.id.tvbranch)


        toolbar_title.text = resources.getString(R.string.profile)
        llSearch.visibility = View.GONE

    }


    override fun onClick(v: View) {

        val i = v.id
        if (i == R.id.backtoolbar) {
         onBackPressed()
        } else if (i == R.id.tvEditProfile) {


            val intent = Intent(this@ProfileActivity, ProfileEditActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)


        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit)
        startActivity(intent)
        finish()
    }
}
