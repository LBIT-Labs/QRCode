package workshop.lbit.qrcode.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
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
import workshop.lbit.qrcode.adapter.QrScanningBoxesListAdapter
import workshop.lbit.qrcode.adapter.QrScanningRequestPartsListAdapter
import workshop.lbit.qrcode.customfonts.MyTextView_Montserrat_Regular
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.data.QrData
import workshop.lbit.qrcode.interfaces.QrDataList
import workshop.lbit.qrcode.utils.Constants
import java.util.regex.Pattern

class QrScanningRequestPartDetails : AppCompatActivity(), View.OnClickListener, QrDataList {


    private var gson: Gson? = null
    var jsonObject: JSONArray = (JSONArray())
    internal lateinit var ivprevious: MyTextView_Roboto_Medium
    internal lateinit var ivnext: MyTextView_Roboto_Medium
    internal lateinit var tvPagerCount: MyTextView_Roboto_Medium
    private var llToolbar: LinearLayout? = null
    private var llViewpaer: LinearLayout? = null
    private var llRecyclerView: LinearLayout? = null
    private var ll_boarder_down_view: LinearLayout? = null
    private var ll_qr_box_headers: LinearLayout? = null
    private var RLPartDetails: RelativeLayout? = null
    private var tvNodata: MyTextView_Montserrat_Regular? = null
    private var mViewPager: ViewPager? = null

    private var sharedpreferences: SharedPreferences? = null
    internal var mMobileNumber: String? = null
    private var editor: SharedPreferences.Editor? = null
    private var mRole: String = ""
    private var mUID: String = ""

    private var ll_qr_part_reference: LinearLayout? = null
    private var ll_qr_part_crn: LinearLayout? = null
    private var ll_qr_part_invoice: LinearLayout? = null

    private var qr_req_ref: MyTextView_Roboto_Medium? = null
    private var qr_req_custName: MyTextView_Roboto_Medium? = null
    private var qr_request_text: MyTextView_Roboto_Regular? = null
    private var qr_req_crn: MyTextView_Roboto_Medium? = null
    private var qr_req_invoice_number: MyTextView_Roboto_Medium? = null

    private var qr_req_ref_value: String? = null
    private var qr_req_invoice_value: String? = null
    private var qr_req_crn_value: String? = null
    private var qr_req_oe_part_value: String? = null
    private var qr_req_custName_value: String? = null
    private var qr_req_nid_value: String? = null

    internal lateinit var layout_footer: RelativeLayout
    internal lateinit var toolbar_title: MyTextView_Roboto_Bold
    internal lateinit var backtoolbar: RelativeLayout

    private var mPageCount: String? = null
    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0
    private var qrValue: String? = null
    private var qrValidation: String? = null
    private var qrValidationCount: String? = null
    private var mManualQr: String? = null

    private val TAG = QrScanningRequestPartDetails::class.java.name

    lateinit var qr_match_icon: ImageView
    lateinit var qr_unmatch_icon: ImageView
    private lateinit var ll_qr_parts_counterLocation: LinearLayout
    private lateinit var ll_qr_parts_pps_part_no: LinearLayout
    private lateinit var ll_qr_parts_pps_part_desc: LinearLayout
    private lateinit var ll_qr_parts_oe_part_no: LinearLayout
    private lateinit var ll_qr_parts_pps_customer_name: LinearLayout
    private lateinit var ll_qr_parts_pps_gate_pass: LinearLayout
    private lateinit var ll_qr_parts_pps_invoice: LinearLayout
    private lateinit var ll_qr_parts_pps_quantity: LinearLayout
    private lateinit var ll_qr_parts_pps_mrp: LinearLayout
    private lateinit var ll_qr_parts_pps_storage_bin: LinearLayout

    private lateinit var qr_part_pps_part_number: MyTextView_Roboto_Regular
    private lateinit var qr_part_oe_part_number: MyTextView_Roboto_Regular
    private lateinit var qr_part_part_desc: MyTextView_Roboto_Regular
    private lateinit var qr_part_quantity: MyTextView_Roboto_Regular
    private lateinit var qr_part_mrp: MyTextView_Roboto_Regular
    private lateinit var qr_part_pps_storageBin: MyTextView_Roboto_Regular
    private lateinit var qr_part_counter_location: MyTextView_Roboto_Regular
    private lateinit var qr_part_customer_name: MyTextView_Roboto_Regular
    private lateinit var qr_part_gatepass: MyTextView_Roboto_Regular
    private lateinit var qr_part_invoice: MyTextView_Roboto_Regular
    private lateinit var qr_part_confirm_btn: MyTextView_Roboto_Regular
    private lateinit var qr_part_back_btn: MyTextView_Roboto_Regular
    private lateinit var et_qrcode: EditText


    private var qr_part_pps_part_number_value: String? = null
    private var qr_part_part_desc_value: String? = null
    private var qr_part_quantity_value: String? = null
    private var qr_part_Customer_value: String? = null
    private var qr_part_invoice_value: String? = null
    private var qr_part_mrp_value: String? = null
    private var qr_part_pid_value: String = ""
    private var qr_part_nid_value: String = ""
    private var qr_part_box_value: String = ""
    private var mManualQrCode: String = ""
    private var qr_part_pps_storageBin_value: String? = null
    private var qr_part_counter_location_value: String? = null
    private var qr_grn_number_value: String? = null
    private var qr_type_value: String? = null

    private lateinit var qrScanningAdapter: QrScanningBoxesListAdapter
    private var mRecyclerView: RecyclerView? = null

    private var qrPartDataList: List<QrData>? = null
    internal lateinit var mData: QrData
    internal var mPosition: Int? = null
    internal var mInvoiceIdentifier: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_scanning_request_part_details)
        supportActionBar!!.hide()

        qr_req_nid_value = intent.getStringExtra("nid")
        qr_req_custName_value = intent.getStringExtra("cust_name")
        qr_req_ref_value = intent.getStringExtra("ref")
        qr_req_invoice_value = intent.getStringExtra("invoice")
        qr_req_crn_value = intent.getStringExtra("crn")
        sharedpreferences = getSharedPreferences(
            Constants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")

        val logindata = UserSession(this@QrScanningRequestPartDetails).getLoginDetails()

        var dict_data = JSONObject()
        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
            mUID = dict_data.optString("uid")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e("test***", "Role  " + mRole)

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        layout_footer = findViewById(R.id.layout_footer)
        llToolbar = findViewById(R.id.llToolbar)
        toolbar_title = findViewById(R.id.toolbar_title)
        backtoolbar = findViewById(R.id.backtoolbar)
        mViewPager = findViewById(R.id.pager)
        mRecyclerView = findViewById(R.id.qr_scanning_boxes_recycler_view)

        ll_qr_part_reference = findViewById(R.id.ll_qr_part_reference)
        ll_qr_part_crn = findViewById(R.id.ll_qr_part_crn)
        ll_qr_part_invoice = findViewById(R.id.ll_qr_part_invoice)

        qr_request_text = findViewById(R.id.qr_request_text)
        qr_req_custName = findViewById(R.id.qr_custName)
        qr_req_ref = findViewById(R.id.qr_ref)
        qr_req_crn = findViewById(R.id.qr_crn)
        qr_req_invoice_number = findViewById(R.id.qr_invoice_number)
        backtoolbar = findViewById(R.id.backtoolbar)
        ivprevious = findViewById(R.id.ivprevious)
        ivnext = findViewById(R.id.ivnext)
        tvPagerCount = findViewById(R.id.tvPagerCount)
        tvNodata = findViewById(R.id.tvNodata)
        llViewpaer = findViewById(R.id.llViewpaer)
        llRecyclerView = findViewById(R.id.llRecyclerView)
        ll_boarder_down_view = findViewById(R.id.ll_boarder_down_view)
        ll_qr_box_headers = findViewById(R.id.ll_qr_box_headers)
        RLPartDetails = findViewById(R.id.qr_part_details_view)
        gson = Gson()

        qr_req_custName!!.text = qr_req_custName_value
        qr_req_invoice_number!!.text = "Invoice#" + " " + qr_req_invoice_value
        qr_req_crn!!.text = "CRN#" + " " + qr_req_crn_value

        if (mRole.equals("stores")) {
            qr_request_text!!.text = resources.getString(R.string.qr_request)
            llRecyclerView!!.visibility = View.GONE
            ll_qr_box_headers!!.visibility = View.GONE
            ll_boarder_down_view!!.visibility = View.VISIBLE
            llViewpaer!!.visibility = View.VISIBLE

            toolbar_title.text = "Store Boy"
            ll_qr_part_reference!!.visibility = View.VISIBLE
            ll_qr_part_crn!!.visibility = View.GONE
            ll_qr_part_invoice!!.visibility = View.GONE
            qr_req_ref!!.text = "Reference#" + " " + qr_req_ref_value

        } else if (mRole.equals("wh_store_boy")) {
            qr_request_text!!.text = resources.getString(R.string.qr_request)

            llRecyclerView!!.visibility = View.GONE
            llViewpaer!!.visibility = View.VISIBLE
            ll_qr_box_headers!!.visibility = View.GONE
            ll_boarder_down_view!!.visibility = View.VISIBLE


            toolbar_title.text = "Store Boy"
            ll_qr_part_reference!!.visibility = View.VISIBLE
            ll_qr_part_crn!!.visibility = View.GONE
            ll_qr_part_invoice!!.visibility = View.GONE
            qr_req_ref!!.text = "Delivery#" + " " + qr_req_ref_value

        } else if (mRole.equals("wh_security")) {
            qr_request_text!!.text = resources.getString(R.string.qr_live)

            llRecyclerView!!.visibility = View.VISIBLE
            llViewpaer!!.visibility = View.GONE
            ll_qr_box_headers!!.visibility = View.VISIBLE
            ll_boarder_down_view!!.visibility = View.GONE

            toolbar_title.text = "Gate Pass"
            ll_qr_part_reference!!.visibility = View.VISIBLE
            ll_qr_part_crn!!.visibility = View.GONE
            ll_qr_part_invoice!!.visibility = View.GONE
            qr_req_ref!!.text = "gate_pass #" + " " + qr_req_ref_value

            val layoutManager = LinearLayoutManager(this@QrScanningRequestPartDetails)
            mRecyclerView!!.layoutManager = layoutManager

        } else if (mRole.equals("counter")) {
            qr_request_text!!.text = resources.getString(R.string.qr_request)

            llRecyclerView!!.visibility = View.GONE
            llViewpaer!!.visibility = View.VISIBLE
            ll_qr_box_headers!!.visibility = View.GONE
            ll_boarder_down_view!!.visibility = View.VISIBLE

            toolbar_title.text = "Counter Sale"
            ll_qr_part_reference!!.visibility = View.VISIBLE
            ll_qr_part_crn!!.visibility = View.GONE
            ll_qr_part_invoice!!.visibility = View.GONE
        } else {
            qr_request_text!!.text = resources.getString(R.string.qr_live)

            llRecyclerView!!.visibility = View.GONE
            llViewpaer!!.visibility = View.VISIBLE
            ll_qr_box_headers!!.visibility = View.GONE
            ll_boarder_down_view!!.visibility = View.VISIBLE

            toolbar_title.text = "Gate Pass"
            ll_qr_part_reference!!.visibility = View.GONE
            ll_qr_part_crn!!.visibility = View.VISIBLE
            ll_qr_part_invoice!!.visibility = View.VISIBLE
        }

        mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
                if (!scrollStarted && state == ViewPager.SCROLLBAR_POSITION_DEFAULT) {
                    scrollStarted = true
                    checkDirection = true
                } else {
                    scrollStarted = false
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                if (thresholdOffset > positionOffset && positionOffsetPixels > thresholdOffsetPixels) {
                    Log.i(TAG, "going left")


                    Log.e(TAG, "onClick_Previous: " + (mViewPager!!.currentItem - 1))

                    val mNext = (mViewPager!!.currentItem + 1).toString() + " of " + mPageCount
                    if (!(mViewPager!!.currentItem - 1).equals(mPageCount) && !mNext.equals("0")) {
                        tvPagerCount.text = mNext
                    }


                } else {

                    val mNext = (mViewPager!!.currentItem + 1).toString() + " of " + mPageCount
                    Log.i(TAG, mNext)
                    if (!mNext.equals("0")) {
                        tvPagerCount.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
            }

        })

        if (mRole.equals("wh_store_boy")) {

            GetQrRequestPartDetailsListWH(mMobileNumber!!, qr_req_nid_value!!)
        } else if (mRole.equals("wh_security")) {

            GetQrRequestPartDetailsListWHG(mMobileNumber!!, qr_req_nid_value!!)
        } else {
            GetQrRequestPartDetailsList(mMobileNumber!!, qr_req_nid_value!!)


        }
    }

    private fun GetQrRequestPartDetailsList(mobile: String, nid: String) {
        val mProgressDialog = ProgressDialog(this@QrScanningRequestPartDetails)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.QrRequestPartList(mobile, nid).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    if (string.equals("{}")) {
                        llViewpaer!!.visibility = View.GONE
                        llRecyclerView!!.visibility = View.GONE
                        ll_qr_box_headers!!.visibility = View.GONE
                        layout_footer.visibility = View.GONE
                        tvNodata!!.visibility = View.VISIBLE
                    } else {
                        if (!string.trim { it <= ' ' }.equals("{}", ignoreCase = true)) {
                            jsonObject = JSONArray(string)

                            val qrPartDataList = gson!!.fromJson<ArrayList<QrData>>(
                                jsonObject.toString(),
                                object : TypeToken<ArrayList<QrData>>() {

                                }.type
                            )
                            if (qrPartDataList.size > 0) {

                                llViewpaer!!.visibility = View.VISIBLE
                                layout_footer.visibility = View.VISIBLE
                                tvNodata!!.visibility = View.GONE
                                mViewPager!!.adapter =

                                    QrScanningRequestPartsListAdapter(
                                        this@QrScanningRequestPartDetails,
                                        this@QrScanningRequestPartDetails,
                                        qrPartDataList,
                                        mRole
                                    )

                                mPageCount = qrPartDataList.size.toString()
                                if (mPageCount!!.length > 0) {
                                    tvPagerCount.text = "1 of " + mPageCount!!
                                }

                                mProgressDialog.dismiss()

                            } else {
                                llViewpaer!!.visibility = View.GONE
                                tvNodata!!.visibility = View.VISIBLE
                                layout_footer.visibility = View.GONE
                                mProgressDialog.dismiss()

                            }
                        }
                    }
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

    private fun GetQrRequestPartDetailsListWH(mobile: String, nid: String) {
        val mProgressDialog = ProgressDialog(this@QrScanningRequestPartDetails)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat_wh.QrRequestPartsListWH(
            nid,
            "live",
            Constants.WH_User,
            Constants.WH_pwd,
            mUID
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        if (string.equals("{}")) {
                            llViewpaer!!.visibility = View.GONE
                            layout_footer.visibility = View.GONE
                            tvNodata!!.visibility = View.VISIBLE
                        } else {
                            if (!string.trim { it <= ' ' }.equals("{}", ignoreCase = true)) {
                                jsonObject = JSONArray(string)

                                val qrPartDataList = gson!!.fromJson<ArrayList<QrData>>(
                                    jsonObject.toString(),
                                    object : TypeToken<ArrayList<QrData>>() {

                                    }.type
                                )
                                if (qrPartDataList.size > 0) {

                                    llViewpaer!!.visibility = View.VISIBLE
                                    layout_footer.visibility = View.VISIBLE
                                    tvNodata!!.visibility = View.GONE
                                    mViewPager!!.adapter =

                                        QrScanningRequestPartsListAdapter(
                                            this@QrScanningRequestPartDetails,
                                            this@QrScanningRequestPartDetails,
                                            qrPartDataList, mRole
                                        )

                                    mPageCount = qrPartDataList.size.toString()
                                    if (mPageCount!!.length > 0) {
                                        tvPagerCount.text = "1 of " + mPageCount!!
                                    }

                                    mProgressDialog.dismiss()

                                } else {
                                    llViewpaer!!.visibility = View.GONE
                                    tvNodata!!.visibility = View.VISIBLE
                                    layout_footer.visibility = View.GONE
                                    mProgressDialog.dismiss()

                                }
                            }
                        }
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

    private fun GetQrRequestPartDetailsListWHG(mobile: String, nid: String) {
        val mProgressDialog = ProgressDialog(this@QrScanningRequestPartDetails)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Log.e("Box Invoice", nid)
        Constants.qrCode_uat_wh.QrRequestBoxListWHG(
            nid,
            "live",
            Constants.WH_User,
            Constants.WH_pwd
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        Log.e("Box Invoice", "Box Invoice List " + string)


                        if (string.equals("{}")) {
                            llRecyclerView!!.visibility = View.GONE
                            ll_qr_box_headers!!.visibility = View.GONE
                            layout_footer.visibility = View.GONE
                            tvNodata!!.visibility = View.VISIBLE
                        } else {
                            if (!string.trim { it <= ' ' }.equals("{}", ignoreCase = true)) {
                                jsonObject = JSONArray(string)

                                qrPartDataList = gson!!.fromJson<ArrayList<QrData>>(
                                    jsonObject.toString(),
                                    object : TypeToken<ArrayList<QrData>>() {

                                    }.type
                                )

                                Log.e("Box Invoice", "Box Invoice List " + qrPartDataList!!.size)

                                if (qrPartDataList!!.size > 0) {

                                    llViewpaer!!.visibility = View.GONE
                                    llRecyclerView!!.visibility = View.VISIBLE
                                    layout_footer.visibility = View.GONE
                                    tvNodata!!.visibility = View.GONE
                                    qrScanningAdapter =
                                        QrScanningBoxesListAdapter(
                                            this@QrScanningRequestPartDetails,
                                            this@QrScanningRequestPartDetails,
                                            qrPartDataList!!,
                                            mRole
                                        )
                                    mRecyclerView!!.adapter = qrScanningAdapter

                                    mProgressDialog.dismiss()

                                } else {
                                    llViewpaer!!.visibility = View.GONE
                                    llRecyclerView!!.visibility = View.GONE
                                    tvNodata!!.visibility = View.VISIBLE
                                    layout_footer.visibility = View.GONE
                                    mProgressDialog.dismiss()

                                }
                            }
                        }
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


    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.ivprevious) {
            mViewPager!!.setCurrentItem(getItemofviewpager(-1), true)

            Log.e(TAG, "onClick_Previous: " + mViewPager!!.currentItem)
            val mPrev = (mViewPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvPagerCount.text = mPrev


        } else if (i == R.id.ivnext) {

            mViewPager!!.setCurrentItem(getItemofviewpager(+1), true)
            Log.e(TAG, "onClick_Next: " + mViewPager!!.currentItem + 1)
            val mNext = (mViewPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvPagerCount.text = mNext

        } else if (i == R.id.backtoolbar) {
            onBackPressed()
        }
    }

    private fun getItemofviewpager(i: Int): Int {
        return mViewPager!!.currentItem + i
    }

    override fun onNavigate(qrData: QrData, position: Int, value: String) {


        if (value.equals("scan")) {

            if (mRole.equals("wh_store_boy")) {
                qr_part_part_desc_value = qrData.qr_part_description
                qr_part_quantity_value = qrData.qr_order_qty
                qr_part_pid_value = qrData.qr_pid!!
                qr_part_mrp_value = qrData.qr_mrp
                qr_part_pps_storageBin_value = qrData.qr_bin_location
                qr_req_oe_part_value = qrData.qr_oem_num
                qr_grn_number_value = qrData.qr_grn_number
            } else if (mRole.equals("wh_security")) {
                mPosition = position
                qr_req_custName_value = qrData.qr_customerName
                qr_part_invoice_value = qrData.qr_invoice_no
                qr_part_pid_value = qrData.qr_pid!!
                qr_part_nid_value = qrData.qr_nid!!
                qr_part_box_value = qrData.qr_box_no!!

            } else if (mRole.equals("stores")) {
                qr_part_pps_part_number_value = qrData.qr_pps_part_no
                qr_part_part_desc_value = qrData.qr_part_description
                qr_part_quantity_value = qrData.qr_quantity
                qr_part_mrp_value = qrData.qr_mrp
                qr_part_pps_storageBin_value = qrData.qr_storage_bin
                qr_part_counter_location_value = qrData.qr_counter_location
                qr_req_oe_part_value = qrData.qr_oe_part_no
                qr_grn_number_value = qrData.qr_grn_number
                qr_type_value = qrData.qr_type
                qr_req_nid_value = qrData.qr_nid

            } else {
                qr_part_pps_part_number_value = qrData.qr_pps_part_no
                qr_part_part_desc_value = qrData.qr_part_description
                qr_part_quantity_value = qrData.qr_quantity
                qr_part_mrp_value = qrData.qr_mrp
                qr_part_pps_storageBin_value = qrData.qr_storage_bin
                qr_part_counter_location_value = qrData.qr_counter_location
                qr_req_oe_part_value = qrData.qr_oe_part_no
                qr_grn_number_value = qrData.qr_grn_number
                qr_type_value = qrData.qr_type
            }

            val integrator = IntentIntegrator(this@QrScanningRequestPartDetails)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(false)
            integrator.initiateScan()

        } else if (value.equals("edit")) {
            if (mRole.equals("wh_store_boy")) {
                qr_part_part_desc_value = qrData.qr_part_description
                qr_part_quantity_value = qrData.qr_order_qty
                qr_part_pid_value = qrData.qr_pid!!
                qr_part_mrp_value = qrData.qr_mrp
                qr_part_pps_storageBin_value = qrData.qr_bin_location
                qr_req_oe_part_value = qrData.qr_oem_num
                qr_grn_number_value = qrData.qr_grn_number
                mManualQr = qrData.qr_part_man_qr_no
            } else if (mRole.equals("wh_security")) {
                mPosition = position
                qr_req_custName_value = qrData.qr_customerName
                qr_part_invoice_value = qrData.qr_invoice_no
                qr_part_pid_value = qrData.qr_pid!!
                qr_part_nid_value = qrData.qr_nid!!
                qr_part_box_value = qrData.qr_box_no!!
                mManualQr = qrData.qr_part_man_qr_no

            } else if (mRole.equals("stores")) {
                qr_part_pps_part_number_value = qrData.qr_pps_part_no
                qr_part_part_desc_value = qrData.qr_part_description
                qr_part_quantity_value = qrData.qr_quantity
                qr_part_mrp_value = qrData.qr_mrp
                qr_part_pps_storageBin_value = qrData.qr_storage_bin
                qr_part_counter_location_value = qrData.qr_counter_location
                qr_req_oe_part_value = qrData.qr_oe_part_no
                qr_grn_number_value = qrData.qr_grn_number
                qr_type_value = qrData.qr_type
                qr_req_nid_value = qrData.qr_nid
                mManualQr = qrData.qr_part_man_qr_no

            } else {
                qr_part_pps_part_number_value = qrData.qr_pps_part_no
                qr_part_part_desc_value = qrData.qr_part_description
                qr_part_quantity_value = qrData.qr_quantity
                qr_part_mrp_value = qrData.qr_mrp
                qr_part_pps_storageBin_value = qrData.qr_storage_bin
                qr_part_counter_location_value = qrData.qr_counter_location
                qr_req_oe_part_value = qrData.qr_oe_part_no
                qr_grn_number_value = qrData.qr_grn_number
                qr_type_value = qrData.qr_type
                mManualQr = qrData.qr_part_man_qr_no

            }
            showEditValueDialog()
        }

    }


    private fun showEditValueDialog() {

        val mDialogView = LayoutInflater.from(this@QrScanningRequestPartDetails)
            .inflate(R.layout.custom_qrcode_edit_dialogue, null)


        qr_part_confirm_btn =
            mDialogView.findViewById(R.id.qr_part_confirm_btn) as MyTextView_Roboto_Regular

        qr_part_back_btn =
            mDialogView.findViewById(R.id.qr_part_back_btn) as MyTextView_Roboto_Regular

        et_qrcode = mDialogView.findViewById(R.id.et_qrcode) as EditText


        val mBuilder = AlertDialog.Builder(this@QrScanningRequestPartDetails)
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)
        val mAlertDialog = mBuilder.show()


        et_qrcode.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(textVal: Editable) {
                mManualQrCode = textVal.toString()

                if (mManualQrCode.length == 14) {
                    val pattern = Pattern.compile("[A-Z]{2}[_]{1}[A-Z]{2}[0-9]{2}[_]{1}[0-9]{6}")
//                    val pattern = Pattern.compile("[A-Z]{2}[_]{1}")
                    val matcher = pattern.matcher(mManualQrCode)
                    // Check if pattern matches
                    if (matcher.matches()) {
//                        Snackbar.make(rlHomeLayout, "Valid", Snackbar.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(
                            this@QrScanningRequestPartDetails,
                            "Invalid Qr Code",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

            }
        })

        qr_part_confirm_btn.setOnClickListener(View.OnClickListener {


            if(mManualQrCode.isNotEmpty()){
                if (mManualQrCode.length == 14){
                    val pattern = Pattern.compile("[A-Z]{2}[_]{1}[A-Z]{2}[0-9]{2}[_]{1}[0-9]{6}")
                    val matcher = pattern.matcher(mManualQrCode)
                    if (matcher.matches()) {
                        if (mManualQr!!.equals(mManualQrCode)) {
                            qrValidationCount = "1"
                            qrValidation = "Verified"
                        } else {
                            qrValidationCount = "0"
                            qrValidation = "Rejected"
                        }

                        RLPartDetails!!.visibility = View.GONE
                        mAlertDialog.dismiss()

                        ShowDialog()

                    } else {
                        Toast.makeText(
                            this@QrScanningRequestPartDetails,
                            "Invalid Qr Code",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

            }

        })

        qr_part_back_btn.setOnClickListener(View.OnClickListener {

            mAlertDialog.dismiss()
        })
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Log.e("Scan*******", "Cancelled scan")
            } else {
                Log.e("Scan", "Scanned")
                qrValue = result.contents

                if (mRole.equals("wh_security")) {
                    if (qrValue!!.contains(qr_part_box_value)) {

                        for (i in 0 until qrPartDataList!!.size) {

                            if (mPosition != i) {
                                mData = qrPartDataList!![i]

                                if (qr_part_invoice_value != mData.qr_invoice_no) {
                                    mInvoiceIdentifier = qr_part_nid_value
                                }
                            }
                        }
                        qrValidationCount = "1"
                        qrValidation = "Verified"
                    } else {
                        qrValidationCount = "0"
                        qrValidation = "Rejected"
                    }
                } else if (mRole.equals("wh_store_boy")) {
                    if (qrValue!!.contains(qr_grn_number_value!!)) {
                        qrValidationCount = "1"
                        qrValidation = "Verified"
                    } else {
                        qrValidationCount = "0"
                        qrValidation = "Rejected"
                    }
                } else {
                    if (qrValue!!.contains(qr_grn_number_value + "_" + qr_req_oe_part_value + "_" + qr_part_pps_part_number_value)) {
                        qrValidationCount = "1"
                        qrValidation = "Verified"
                    } else {
                        qrValidationCount = "0"
                        qrValidation = "Rejected"
                    }
                }

                /* Toast.makeText(
                     this,
                     "Scanned: " + result.contents,
                     Toast.LENGTH_LONG
                 ).show()*/

                RLPartDetails!!.visibility = View.GONE
                ShowDialog()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun ShowDialog() {

        val mDialogView = LayoutInflater.from(this@QrScanningRequestPartDetails)
            .inflate(R.layout.custom_qrcode_dialog, null)

        ll_qr_parts_pps_part_no =
            mDialogView.findViewById(R.id.ll_qr_parts_pps_part_no) as LinearLayout
        ll_qr_parts_pps_part_desc =
            mDialogView.findViewById(R.id.ll_qr_parts_pps_part_desc) as LinearLayout
        ll_qr_parts_counterLocation =
            mDialogView.findViewById(R.id.ll_qr_parts_counterLocation) as LinearLayout
        ll_qr_parts_oe_part_no =
            mDialogView.findViewById(R.id.ll_qr_parts_oe_part_no) as LinearLayout
        ll_qr_parts_pps_customer_name =
            mDialogView.findViewById(R.id.ll_qr_parts_pps_customer_name) as LinearLayout
        ll_qr_parts_pps_gate_pass =
            mDialogView.findViewById(R.id.ll_qr_parts_pps_gate_pass) as LinearLayout
        ll_qr_parts_pps_invoice =
            mDialogView.findViewById(R.id.ll_qr_parts_pps_invoice) as LinearLayout
        ll_qr_parts_pps_quantity =
            mDialogView.findViewById(R.id.ll_qr_parts_pps_quantity) as LinearLayout
        ll_qr_parts_pps_mrp = mDialogView.findViewById(R.id.ll_qr_parts_pps_mrp) as LinearLayout
        ll_qr_parts_pps_storage_bin =
            mDialogView.findViewById(R.id.ll_qr_parts_pps_storage_bin) as LinearLayout
        ll_qr_parts_counterLocation =
            mDialogView.findViewById(R.id.ll_qr_parts_counterLocation) as LinearLayout

        qr_match_icon = mDialogView.findViewById(R.id.qr_match_icon) as ImageView
        qr_unmatch_icon = mDialogView.findViewById(R.id.qr_unmatch_icon) as ImageView
        qr_part_pps_part_number =
            mDialogView.findViewById(R.id.qr_part_pps_part_number) as MyTextView_Roboto_Regular
        qr_part_oe_part_number =
            mDialogView.findViewById(R.id.qr_part_oe_part_number) as MyTextView_Roboto_Regular
        qr_part_part_desc =
            mDialogView.findViewById(R.id.qr_part_part_desc) as MyTextView_Roboto_Regular
        qr_part_quantity =
            mDialogView.findViewById(R.id.qr_part_quantity) as MyTextView_Roboto_Regular
        qr_part_mrp = mDialogView.findViewById(R.id.qr_part_mrp) as MyTextView_Roboto_Regular
        qr_part_pps_storageBin =
            mDialogView.findViewById(R.id.qr_part_pps_storageBin) as MyTextView_Roboto_Regular
        qr_part_counter_location =
            mDialogView.findViewById(R.id.qr_part_counter_location) as MyTextView_Roboto_Regular
        qr_part_customer_name =
            mDialogView.findViewById(R.id.qr_part_customer_name) as MyTextView_Roboto_Regular
        qr_part_gatepass =
            mDialogView.findViewById(R.id.qr_part_gatepass) as MyTextView_Roboto_Regular
        qr_part_invoice =
            mDialogView.findViewById(R.id.qr_part_invoice) as MyTextView_Roboto_Regular
        qr_part_confirm_btn =
            mDialogView.findViewById(R.id.qr_part_confirm_btn) as MyTextView_Roboto_Regular

        if (mRole.equals("wh_store_boy")) {
            ll_qr_parts_counterLocation.visibility = View.GONE
            ll_qr_parts_pps_part_no.visibility = View.GONE
            ll_qr_parts_oe_part_no.visibility = View.VISIBLE
            ll_qr_parts_pps_customer_name.visibility = View.GONE
            ll_qr_parts_pps_gate_pass.visibility = View.GONE
            ll_qr_parts_pps_invoice.visibility = View.GONE

            qr_part_oe_part_number.text = qr_req_oe_part_value
            qr_part_part_desc.text = qr_part_part_desc_value
            qr_part_quantity.text = qr_part_quantity_value
            qr_part_mrp.text = qr_part_mrp_value
            qr_part_pps_storageBin.text = qr_part_pps_storageBin_value
            qr_part_counter_location.text = qr_part_counter_location_value

        } else if (mRole.equals("wh_security")) {

            ll_qr_parts_counterLocation.visibility = View.GONE
            ll_qr_parts_pps_part_no.visibility = View.GONE
            ll_qr_parts_pps_part_desc.visibility = View.GONE
            ll_qr_parts_oe_part_no.visibility = View.GONE
            ll_qr_parts_pps_mrp.visibility = View.GONE
            ll_qr_parts_pps_storage_bin.visibility = View.GONE
            ll_qr_parts_counterLocation.visibility = View.GONE

            ll_qr_parts_pps_customer_name.visibility = View.VISIBLE
            ll_qr_parts_pps_gate_pass.visibility = View.VISIBLE
            ll_qr_parts_pps_invoice.visibility = View.VISIBLE
            ll_qr_parts_pps_quantity.visibility = View.VISIBLE

            qr_part_customer_name.text = qr_req_custName_value
            qr_part_invoice.text = qr_part_invoice_value
            qr_part_gatepass.text = qr_req_ref_value
            qr_part_quantity.text = qr_req_crn_value
        } else {
            ll_qr_parts_counterLocation.visibility = View.VISIBLE
            ll_qr_parts_pps_part_no.visibility = View.VISIBLE
            ll_qr_parts_oe_part_no.visibility = View.GONE
            ll_qr_parts_pps_customer_name.visibility = View.GONE
            ll_qr_parts_pps_gate_pass.visibility = View.GONE
            ll_qr_parts_pps_invoice.visibility = View.GONE
            qr_part_pps_part_number.text = qr_part_pps_part_number_value
            qr_part_part_desc.text = qr_part_part_desc_value
            qr_part_quantity.text = qr_part_quantity_value
            qr_part_mrp.text = qr_part_mrp_value
            qr_part_pps_storageBin.text = qr_part_pps_storageBin_value
            qr_part_counter_location.text = qr_part_counter_location_value
        }
        if (qrValidation.equals("Verified")) {
            qr_match_icon.visibility = View.VISIBLE
            qr_unmatch_icon.visibility = View.GONE
            qr_part_confirm_btn.text = "Confirm"

        } else {
            qr_match_icon.visibility = View.GONE
            qr_unmatch_icon.visibility = View.VISIBLE
            qr_part_confirm_btn.text = "Back"


        }
        val mBuilder = AlertDialog.Builder(this@QrScanningRequestPartDetails)
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)
        val mAlertDialog = mBuilder.show()


        qr_part_confirm_btn.setOnClickListener(View.OnClickListener {

            if (qr_part_confirm_btn.text.equals("Confirm")) {
                if (mRole.equals("wh_store_boy")) {
                    SendQrCodeVerificationValueWH()

                } else if (mRole.equals("wh_security")) {
                    SendQrCodeVerificationValueWHG()

                } else {
                    SendQrCodeVerificationValue()

                }

                if (mRole.equals("wh_store_boy")) {
                    GetQrRequestPartDetailsListWH(mMobileNumber!!, qr_req_nid_value!!)

                } else if (mRole.equals("wh_security")) {
                    GetQrRequestPartDetailsListWHG(mMobileNumber!!, qr_req_nid_value!!)

                } else {
                    GetQrRequestPartDetailsList(mMobileNumber!!, qr_req_nid_value!!)

                }

                mAlertDialog.dismiss()
                RLPartDetails!!.visibility = View.VISIBLE
            } else {

                mAlertDialog.dismiss()
                RLPartDetails!!.visibility = View.VISIBLE
            }
        })
    }

    private fun SendQrCodeVerificationValue() {

        val mProgressDialog = ProgressDialog(this@QrScanningRequestPartDetails)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.show()
        Constants.qrCode_uat.SaveQrVerifiedValue(
            qr_req_nid_value!!,
            mMobileNumber!!,
            qr_grn_number_value!!, qrValidation!!, qr_type_value!!).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()
                    if (string.contains("QR Succesfully Validated")) {
                        mProgressDialog.dismiss()

                        Toast.makeText(
                            this@QrScanningRequestPartDetails,
                            "Qr Verified Value Saved",
                            Toast.LENGTH_LONG
                        ).show()
                    }
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

    private fun SendQrCodeVerificationValueWH() {

        val mProgressDialog = ProgressDialog(this@QrScanningRequestPartDetails)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.show()
        mProgressDialog.setMessage("Loading...")
        Constants.qrCode_uat_wh.SaveQrVerifiedValueWH(
            qr_part_pid_value,
            qrValidationCount!!,
            Constants.WH_User,
            Constants.WH_pwd
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()
                    Log.e("TAG", "Grn Verify" + " " + string)

//                    val jsonArray = JSONArray(string)
                    val jsonObject = JSONObject(string)
                    val verify = jsonObject.get("success").toString()
                    if (verify.contains("Verified")) {
                        mProgressDialog.dismiss()

                        Toast.makeText(
                            this@QrScanningRequestPartDetails,
                            "Qr Verified Value Saved",
                            Toast.LENGTH_LONG
                        ).show()
                    }
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

    private fun SendQrCodeVerificationValueWHG() {

        val mProgressDialog = ProgressDialog(this@QrScanningRequestPartDetails)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.show()
        mProgressDialog.setMessage("Loading...")
        Constants.qrCode_uat_wh.SaveQrVerifiedValueWHG(
            qr_part_pid_value,
            qrValidationCount!!,
            Constants.WH_User,
            Constants.WH_pwd,
            mInvoiceIdentifier
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()
                    Log.e("TAG", "Grn Verify" + " " + string)

//                    val jsonArray = JSONArray(string)
                    val jsonObject = JSONObject(string)
                    val verify = jsonObject.get("success").toString()
                    if (verify.contains("Verified")) {
                        mProgressDialog.dismiss()

                        Toast.makeText(
                            this@QrScanningRequestPartDetails,
                            "Qr Verified Value Saved",
                            Toast.LENGTH_LONG
                        ).show()
                    }
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

    override fun onBackPressed() {
        val intent = Intent(this@QrScanningRequestPartDetails, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit)
        startActivity(intent)
        finish()
    }
}