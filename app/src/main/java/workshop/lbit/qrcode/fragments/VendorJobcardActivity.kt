package workshop.lbit.qrcode.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import org.json.JSONException
import org.json.JSONObject
import workshop.lbit.qrcode.MainActivity
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.adapter.VendorJobcardAdapter
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.vendorjobcard.VendorJobCardFormActivity
import workshop.lbit.qrcode.viewpager.LockableViewPager

class VendorJobcardActivity : AppCompatActivity(), View.OnClickListener {

    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var mMobileNumber: String? = null
    private var mRole: String? = null
    private var tabLayout: TabLayout? = null
    private lateinit var ll_add_vendor: LinearLayout
    private var viewPager: LockableViewPager? = null
    private var viewPagerAdapter: VendorJobcardAdapter? = null
    lateinit var toolbar_title: MyTextView_Roboto_Bold


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vendor_jobcard_dashboard_fragment)
        getSupportActionBar()!!.hide()

        sharedpreferences = getSharedPreferences(
            Constants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")

        val logindata = UserSession(this@VendorJobcardActivity).getLoginDetails()

        var dict_data = JSONObject()
        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
        } catch (e: JSONException) {
            e.printStackTrace()
        }


        init()

        setViewPager()

    }


    private fun init() {
        toolbar_title = findViewById(R.id.toolbar_title)
        toolbar_title.text = "Vendor Job Card"
        viewPager = findViewById(R.id.live_jobcard_pager)
        tabLayout = findViewById(R.id.tabs)
        ll_add_vendor = findViewById(R.id.ll_add_vendor)

        ll_add_vendor.setOnClickListener(this)
    }

    private fun setViewPager() {
        val fm = supportFragmentManager

        viewPagerAdapter = VendorJobcardAdapter(fm)
        viewPager!!.adapter = viewPagerAdapter
        tabLayout!!.setupWithViewPager(viewPager)

    }

    override fun onClick(v: View?) {

        val i = v!!.id
        if (i == R.id.backtoolbar) {
            onBackPressed()
        } else if (i == R.id.ll_add_vendor) {
            val intent = Intent(this@VendorJobcardActivity, VendorJobCardFormActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
            finish()

        }
    }

    override fun onBackPressed() {

        val intent = Intent(this@VendorJobcardActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit)
        startActivity(intent)
        finish()
    }
}