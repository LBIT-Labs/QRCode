package workshop.lbit.qrcode

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import org.json.JSONException
import org.json.JSONObject
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.fragments.LiveJobcardFragment
import workshop.lbit.qrcode.fragments.QrScanningFragment
import workshop.lbit.qrcode.fragments.VendorJobcardActivity
import workshop.lbit.qrcode.ui.InventoryManagementActivity
import workshop.lbit.qrcode.ui.JobCardFormActivity
import workshop.lbit.qrcode.ui.LoginActivity
import workshop.lbit.qrcode.utils.Constants


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var mRole: String = ""
    private var mProfileName: String = ""
    lateinit var fragmentManager: FragmentManager
    lateinit var nav_vendorService: MenuItem
    lateinit var nav_Jobcard: MenuItem
    lateinit var nav_Livejobs: MenuItem
    lateinit var nav_serviceHistory: MenuItem
    lateinit var nav_invManagement: MenuItem
    private var drawer: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar_title: TextView? = null
    private var llProfileLayout: LinearLayout? = null
    private var tvheadername: MyTextView_Roboto_Regular? = null
    private var tvheaderMobile: MyTextView_Roboto_Regular? = null
    private var mMobileNumber: String = ""
    private lateinit var dict_data: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        sharedpreferences = getSharedPreferences(
            Constants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "").toString()

        val logindata = UserSession(this@MainActivity).getLoginDetails()

        dict_data = JSONObject()
        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
            mProfileName = dict_data.optString("user_name")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.e("test***", "Role  " + mRole)

        Log.e("test***", "toolbarRole  " + mRole)
        /*Shared*/
        sharedpreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()


        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar!!.title = ""

        navigationView = findViewById<NavigationView>(R.id.nav_view)
        toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        val headerview = navigationView!!.getHeaderView(0)
        val nav_Menu = navigationView!!.menu
        llProfileLayout = headerview.findViewById(R.id.llProfileLayout)
        tvheadername = headerview.findViewById(R.id.tvheadername)
        tvheaderMobile = headerview.findViewById(R.id.tvheaderMobile)
        navigationView!!.setNavigationItemSelectedListener(this)
        nav_Jobcard = nav_Menu.findItem(R.id.nav_Jobcard)
        nav_Livejobs = nav_Menu.findItem(R.id.nav_Livejobs)
        nav_serviceHistory = nav_Menu.findItem(R.id.nav_serviceHistory)
        nav_invManagement = nav_Menu.findItem(R.id.nav_invManagement)
        nav_vendorService = nav_Menu.findItem(R.id.nav_vendorService)
        tvheadername!!.text = mRole
        tvheaderMobile!!.text = mMobileNumber

        if (mRole.equals("stores")) {
            toolbar_title!!.text = "Store Boy"
            nav_Jobcard.isVisible = false
            nav_Livejobs.isVisible = false
            nav_serviceHistory.isVisible = true
            nav_invManagement.isVisible = true
            nav_vendorService.isVisible = false

        } else if (mRole.equals("wh_store_boy")) {
            toolbar_title!!.text = "Store Boy"

            nav_Jobcard.isVisible = false
            nav_Livejobs.isVisible = false
            nav_serviceHistory.isVisible = false
            nav_invManagement.isVisible = false
            nav_vendorService.isVisible = false

        } else if (mRole.equals("counter")) {
            toolbar_title!!.text = "Counter Sale"

            nav_Jobcard.isVisible = false
            nav_Livejobs.isVisible = false
            nav_serviceHistory.isVisible = true
            nav_invManagement.isVisible = true
            nav_vendorService.isVisible = false
        }  else if (mRole.equals("security")) {
            toolbar_title!!.text = "Gate Pass"

            nav_Jobcard.isVisible = false
            nav_Livejobs.isVisible = false
            nav_serviceHistory.isVisible = true
            nav_invManagement.isVisible = false
            nav_vendorService.isVisible = false
        }  else if (mRole.equals("wh_security")) {
            toolbar_title!!.text = "Gate Pass"

            nav_Jobcard.isVisible = false
            nav_Livejobs.isVisible = false
            nav_serviceHistory.isVisible = true
            nav_invManagement.isVisible = false
            nav_vendorService.isVisible = false
        } else {
            toolbar_title!!.text = "Jobcard"
            nav_Jobcard.isVisible = true
            nav_Livejobs.isVisible = false
            nav_serviceHistory.isVisible = false
            nav_invManagement.isVisible = true
            nav_vendorService.isVisible = true
        }

//        llProfileLayout!!.setOnClickListener {
//            val i = Intent(this@MainActivity, ProfileActivity::class.java)
//            startActivity(i)
//            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
//            finish()
//        }

        if(mRole.equals("stores") || mRole.equals("wh_store_boy") ||
            mRole.equals("counter") || mRole.equals("wh_security") || mRole.equals("security")){
            fragmentManager = supportFragmentManager
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, QrScanningFragment())
            transaction.commit()
        }else {
            fragmentManager = supportFragmentManager
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container,LiveJobcardFragment())
            transaction.commit()
        }

    }


    override fun onBackPressed() {
        drawer = findViewById(R.id.drawer_layout)
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun Logout() {

        val builder1 = AlertDialog.Builder(this@MainActivity)
        builder1.setMessage("Are you sure to Logout")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Yes"
        ) { dialog, id ->
            sharedpreferences!!.edit().remove(Constants.LOGINUSER_MOBILE).commit()
            UserSession(this@MainActivity).removePhoneNum()

            UserSession(this@MainActivity).removeToken()

            Toast.makeText(this@MainActivity, "LoggedOut Successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        builder1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.nav_logout) {
            drawer!!.closeDrawer(GravityCompat.START)
            Logout()
        } else if (id == R.id.nav_Jobcard) {

            dict_data.put("Screen", "New")
            dict_data.put("JobCardCustID", "")
            dict_data.put("CustomerMobile", "")
            dict_data.put("status", "")
            dict_data.put("service_status", "")
            dict_data.put("screenType", "")
            dict_data.put("CustomerName", "")
            dict_data.put("RegNo", "")
            dict_data.put("Make", "")
            dict_data.put("Model", "")
            UserSession(this@MainActivity).setLoginDetails(dict_data.toString())

            val i = Intent(this@MainActivity, JobCardFormActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
            finish()

        } else if (id == R.id.nav_Livejobs) {

        } else if (id == R.id.nav_invManagement) {

            val i = Intent(this@MainActivity, InventoryManagementActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
            finish()
        } else if (id == R.id.nav_serviceHistory) {
            Toast.makeText(this, "notifications", Toast.LENGTH_LONG).show()

        } else if (id == R.id.nav_vendorService) {

            dict_data.put("VendorJobCardCustID", "")
            dict_data.put("Vendor", "")
            dict_data.put("VendorJobCardID", "")
            dict_data.put("gatepass_status", "")
            dict_data.put("gatepass_auth_status", "")
            dict_data.put("CustomerName", "")
            dict_data.put("CustomerMobile", "")
            dict_data.put("RegNo", "")
            dict_data.put("Screen", "New")
            UserSession(this@MainActivity).setLoginDetails(dict_data.toString())

            val i = Intent(this@MainActivity, VendorJobcardActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
            finish()
        }

        drawer = findViewById(R.id.drawer_layout)
        drawer!!.closeDrawer(GravityCompat.START)
        return true

    }
}
