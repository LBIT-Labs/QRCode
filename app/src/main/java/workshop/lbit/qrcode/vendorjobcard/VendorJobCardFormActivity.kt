package workshop.lbit.qrcode.vendorjobcard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.json.JSONException
import org.json.JSONObject
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.fragments.VendorJobcardActivity
import workshop.lbit.qrcode.jobcard.*
import java.util.*

class VendorJobCardFormActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var dict_data: JSONObject
    lateinit var toolbar_title: MyTextView_Roboto_Bold
    private var tabLayout: TabLayout? = null
    private var mViewPager: ViewPager? = null

    lateinit var previous: MyTextView_Roboto_Bold
    lateinit var next: MyTextView_Roboto_Bold
    private var currentPageIndex = 0
    var tagstr: String? = null
    var screen: String = ""
    var mCustomerName: String = ""
    var mCustomerMobile: String = ""
    var mCustomerReg: String = ""
    var mJobcardNo: String = ""

    private lateinit var ll_customer_details: LinearLayout
    private lateinit var tv_customer_name: MyTextView_Roboto_Medium
    private lateinit var tv_mobile: MyTextView_Roboto_Medium
    private lateinit var tv_reg: MyTextView_Roboto_Medium
    private lateinit var tv_jobcard_no: MyTextView_Roboto_Medium

    private lateinit var vendorJobCardDetailsFragment: VendorJobCardDetailsFragment
    private lateinit var vendorJobCardSparesFragment: VendorJobcardSparesFragment
    private lateinit var vendorJobCardServicesFragment: VendorJobcardServicesFragment
    private lateinit var vendorJobCardSummaryFragment: VendorJobcardSummaryFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_jobcard_form)
        supportActionBar!!.hide()

        getLoginDetails()

        init()

        tagstr = intent.getStringExtra("TAG")

        if (tagstr != null) {

            if (tagstr.equals("0")) {

                mViewPager!!.currentItem = 0
                currentPageIndex = 0
            } else if (tagstr.equals("1")) {

                mViewPager!!.currentItem = 1
                currentPageIndex = 1
            } else if (tagstr.equals("2")) {

                mViewPager!!.currentItem = 2
                currentPageIndex = 2
            } else if (tagstr.equals("3")) {

                mViewPager!!.currentItem = 3
                currentPageIndex = 3
            }

        }

        if (screen.isNotEmpty()) {
            if (screen.equals("Live")) {
                ll_customer_details.visibility = View.VISIBLE
                tv_customer_name.text = mCustomerName
                tv_mobile.text = mCustomerMobile
                tv_reg.text = mCustomerReg
                tv_jobcard_no.text = mJobcardNo
            } else if (screen.equals("New")) {
                ll_customer_details.visibility = View.GONE
            }
        }

        previous.setOnClickListener {

//            Log.d("Index", currentPageIndex.toString())
            currentPageIndex--
            mViewPager!!.currentItem = currentPageIndex
        }
        next.setOnClickListener {
            val moveToNextTab = when (currentPageIndex) {
                0 -> vendorJobCardDetailsFragment.allRequiredDataAvailable()

                else -> true
            }

            val moveToNextMandatoryTab = when (currentPageIndex) {
                2 -> vendorJobCardServicesFragment.getMandatoryTab()

                else -> true
            }

            if (moveToNextTab) {

                if (currentPageIndex == 0) {
                    getLoginDetails()

                    ll_customer_details.visibility = View.VISIBLE
                    tv_customer_name.text = mCustomerName
                    tv_mobile.text = mCustomerMobile
                    tv_reg.text = mCustomerReg
                    tv_jobcard_no.text = mJobcardNo

                }

                if (currentPageIndex == 2) {
                    if (moveToNextMandatoryTab) {

                        currentPageIndex++
                        mViewPager!!.currentItem = currentPageIndex

                    } else {
                        Toast.makeText(
                            this@VendorJobCardFormActivity,
                            "Please add atleast one Service to proceed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    currentPageIndex++
                    mViewPager!!.currentItem = currentPageIndex
                }
            }
        }
    }

    private fun getLoginDetails() {

        dict_data = JSONObject()
        val logindata = UserSession(this@VendorJobCardFormActivity).getLoginDetails()

        try {
            dict_data = JSONObject(logindata)
            mCustomerMobile = dict_data.optString("CustomerMobile")
            screen = dict_data.optString("Screen")
            mCustomerName = dict_data.optString("CustomerName")
            mCustomerReg = dict_data.optString("RegNo")
            mJobcardNo = dict_data.optString("VendorJobCardID")

//            workshop.lbit.qrcode.scroll.MyNestedScrollViewe("data", dict_data.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun init() {
        toolbar_title = findViewById(R.id.toolbar_title)
        toolbar_title.text = "Vendor Job Card"
        tabLayout = findViewById(R.id.tabs)

        ll_customer_details = findViewById(R.id.ll_customer_details)
        tv_customer_name = findViewById(R.id.tv_customer_name)
        tv_mobile = findViewById(R.id.tv_mobile)
        tv_reg = findViewById(R.id.tv_reg)
        tv_jobcard_no = findViewById(R.id.tv_jobcard_no)

        mViewPager = findViewById(R.id.jobcard_dashboard_pager)

        tabLayout!!.isEnabled = false

        previous = findViewById(R.id.tvPrevious)
        next = findViewById(R.id.tvnext)

        next.visibility = View.VISIBLE
        previous.visibility = View.INVISIBLE

        setupViewPager(mViewPager!!)


        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    "Vehicle Details" -> {
                        currentPageIndex = 0
                        next.visibility = View.VISIBLE
                        previous.visibility = View.INVISIBLE
                    }
                    "Spares" -> {
                        currentPageIndex = 1
                        next.visibility = View.VISIBLE
                        previous.visibility = View.VISIBLE
                    }
                    "Services" -> {
                        currentPageIndex = 2
                        next.visibility = View.VISIBLE
                        previous.visibility = View.VISIBLE
                    }
                    "Summary" -> {
                        currentPageIndex = 4
                        next.visibility = View.INVISIBLE
                        previous.visibility = View.VISIBLE
                    }
                }
            }
        })


//        val tabStrip = tabLayout!!.getChildAt(0) as LinearLayout
//        for (i in 0 until tabStrip.childCount) {
//            tabStrip.getChildAt(i).setOnTouchListener { v, event -> true }
//        }
    }


    private fun setupViewPager(viewPager: ViewPager) {

        vendorJobCardDetailsFragment = VendorJobCardDetailsFragment.newInstance()
        vendorJobCardSparesFragment = VendorJobcardSparesFragment.newInstance()
        vendorJobCardServicesFragment = VendorJobcardServicesFragment.newInstance()
        vendorJobCardSummaryFragment = VendorJobcardSummaryFragment.newInstance()

        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(vendorJobCardDetailsFragment, "Vehicle Details")
        adapter.addFragment(vendorJobCardSparesFragment, "Spares")
        adapter.addFragment(vendorJobCardServicesFragment, "Services")
        adapter.addFragment(vendorJobCardSummaryFragment, "Summary")
        viewPager.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 4
    }

    internal inner class ViewPagerAdapter
        (manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
    }

    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.backtoolbar) {
            onBackPressed()
        }
    }

    override fun onBackPressed() {

        dict_data.put("VendorJobCardCustID", "")
        dict_data.put("Vendor", "")
        dict_data.put("VendorJobCardID", "")
        dict_data.put("gatepass_status", "")
        dict_data.put("gatepass_auth_status", "")
        dict_data.put("CustomerName", "")
        dict_data.put("CustomerMobile", "")
        dict_data.put("RegNo", "")
        dict_data.put("Screen", "")
        UserSession(this@VendorJobCardFormActivity).setLoginDetails(dict_data.toString())

        val intent = Intent(this@VendorJobCardFormActivity, VendorJobcardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit)
        startActivity(intent)
        finish()
    }
}