package workshop.lbit.qrcode.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import org.json.JSONException
import org.json.JSONObject
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.adapter.LiveJobcardAdapter
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.viewpager.LockableViewPager

class LiveJobcardFragment : Fragment() {

    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var mMobileNumber: String? = null
    private var mRole: String? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: LockableViewPager? = null
    private var viewPagerAdapter: LiveJobcardAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.live_jobcard_dashboard_fragment, container, false)

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


        init(v)

        setViewPager()

        return v
    }

    private fun init(v: View) {

        viewPager = v.findViewById(R.id.live_jobcard_pager)
        tabLayout = v.findViewById(R.id.tabs)
    }

    private fun setViewPager() {
        val fm = this@LiveJobcardFragment.fragmentManager

        viewPagerAdapter = LiveJobcardAdapter(fm!!)
        viewPager!!.adapter = viewPagerAdapter
        tabLayout!!.setupWithViewPager(viewPager)

    }
}