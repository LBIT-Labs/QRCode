package workshop.lbit.qrcode.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import org.json.JSONException
import org.json.JSONObject
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.adapter.QrScanningAdapter
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.viewpager.LockableViewPager

class QrScanningFragment() : Fragment() {

    private var tabLayout: TabLayout? = null
    private var viewPager: LockableViewPager? = null
    internal lateinit var backtoolbar: RelativeLayout
    internal lateinit var toolbar_title: TextView
    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var mMobileNumber: String? = null
    private var mRole: String? = null
    private var viewPagerAdapter: QrScanningAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.qrscanningrequest_activity, container, false)

        sharedpreferences = activity!!.getSharedPreferences(
            Constants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")

        val logindata = UserSession(requireContext()).getLoginDetails()

        var dict_data = JSONObject()
        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        /* if (mRole.equals("stores")) {
             (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle("Store Boy")
             *//*(activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            (activity as AppCompatActivity?)!!.supportActionBar!!.setCustomView(R.layout.toolbar_custom)
*//*
        }else  if (mRole.equals("counter")){
            (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle("Counter Sale")

        }else{
            (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle("Gate Pass")

        }*/

        init(v)

        setViewPager()

        return v
    }

    private fun init(v: View) {

        viewPager = v.findViewById(R.id.qr_scanning_pager)
        tabLayout = v.findViewById(R.id.tabs)
    }

    private fun setViewPager() {
        val fm = this@QrScanningFragment.fragmentManager

        viewPagerAdapter = QrScanningAdapter(fm!!, mRole.toString())
        viewPager!!.adapter = viewPagerAdapter
        tabLayout!!.setupWithViewPager(viewPager)

    }

    companion object {

        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        private val SPLASH_TIME_OUT = 1000
    }
}