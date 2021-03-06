package workshop.lbit.qrcode.vendorjobcard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.adapter.VendorJobcardServicesDataAdapter
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.interfaces.JobCardListService
import workshop.lbit.qrcode.utils.Constants


@SuppressLint("ValidFragment")
class VendorJobcardServicesFragment @SuppressLint("ValidFragment") constructor() : Fragment(),
    View.OnClickListener, JobCardListService {


    private var servicesdatalist = java.util.ArrayList<JobcardData>()

    private lateinit var mAlertDialog: AlertDialog

    lateinit var ll_add_services: LinearLayout
    lateinit var ll_services_data: LinearLayout
    lateinit var ll_services_data1: LinearLayout
    lateinit var bt_submit: MyTextView_Roboto_Bold
    lateinit var bt_cancel: MyTextView_Roboto_Bold
    lateinit var et_services_service: EditText
    lateinit var tv_service_finalprice: MyTextView_Roboto_Medium
    lateinit var tv_service_discount_txt: MyTextView_Roboto_Bold
    lateinit var tv_service_cost_txt: MyTextView_Roboto_Bold
    lateinit var tv_service_service_txt: MyTextView_Roboto_Bold
    lateinit var et_services_cost: EditText
    lateinit var et_services_discount: EditText
    private var vp_pager: ViewPager? = null
    private var tv_size: MyTextView_Roboto_Bold? = null
    private var tv_previous: MyTextView_Roboto_Bold? = null
    private var tv_next: MyTextView_Roboto_Bold? = null
    private var gson: Gson? = null

    private var mRole: String = ""
    private var mJobCardCustID: String = ""
    private lateinit var dict_data: JSONObject

    private var isLoaded = false
    private var isVisibleToUser = false

    private var mServiceCost = ""
    private var mServiceHours = ""
    private var mServicePid = ""
    private var mServiceDiscount = "0"
    private var mServiceService = ""
    private var mServiceFinalPrice = ""
    private var mServiceJobid = ""
    private var mVendorNid: String = ""
    private var mVendor: String = ""
    private var mJobcardID: String = ""

    private var mPageCount: String? = null
    internal var mMobileNumber: String? = null
    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (isVisibleToUser && isAdded) {
            loadData()
            isLoaded = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isVisibleToUser && (!isLoaded)) {
            loadData()
            isLoaded = true
        }
    }

    private fun loadData() {

        dict_data = JSONObject()

        val logindata = UserSession(requireContext()).getLoginDetails()

        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
            mVendorNid = dict_data.optString("VendorJobCardCustID")
            mVendor = dict_data.optString("Vendor")
            mJobcardID = dict_data.optString("VendorJobCardID")

            getServicesList()

//            Log.d("data", dict_data.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_vendor_jobcard_services, container, false)

        sharedpreferences =
            activity!!.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")

        init(v)

        return v
    }

    fun init(v: View) {
        ll_add_services = v.findViewById(R.id.ll_add_services)
        ll_services_data = v.findViewById(R.id.ll_services_data)
        ll_services_data1 = v.findViewById(R.id.ll_service_data1)
        tv_next = v.findViewById(R.id.tv_next)
        tv_size = v.findViewById(R.id.tv_size)
        tv_previous = v.findViewById(R.id.tv_previous)
        vp_pager = v.findViewById(R.id.vp_pager)

        gson = Gson()
        ll_add_services.setOnClickListener(this)
        tv_next!!.setOnClickListener(this)
        tv_previous!!.setOnClickListener(this)


        vp_pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

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
//                    Log.e("TAG", "going left")


//                    Log.e("TAG", "onClick_Previous: " + (vp_pager!!.currentItem - 1))

                    val mNext =
                        (vp_pager!!.currentItem + 1).toString() + " of " + mPageCount
                    if (!(vp_pager!!.currentItem - 1).equals(mPageCount) && !mNext.equals(
                            "0"
                        )
                    ) {
                        tv_size!!.text = mNext
                    }


                } else {

                    val mNext =
                        (vp_pager!!.currentItem + 1).toString() + " of " + mPageCount
//                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tv_size!!.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
            }

        })

    }

    private fun getItemofviewpager(i: Int): Int {
        return vp_pager!!.currentItem + i
    }

    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.ll_add_services) {

            mServiceCost = ""
            mServiceService = ""
            mServiceHours = ""
            mServiceDiscount = "0"
            mServiceFinalPrice = ""
            mServiceJobid = ""

            AddJobsDialog("")
        } else if (i == R.id.tv_previous) {
            vp_pager!!.setCurrentItem(getItemofviewpager(-1), true)

//            Log.e("TAG", "onClick_Previous: " + vp_pager!!.currentItem)
            val mPrev = (vp_pager!!.currentItem + 1).toString() + " of " + mPageCount
            tv_size!!.text = mPrev


        } else if (i == R.id.tv_next) {

            vp_pager!!.setCurrentItem(getItemofviewpager(+1), true)
//            Log.e("TAG", "onClick_Next: " + vp_pager!!.currentItem + 1)
            val mNext = (vp_pager!!.currentItem + 1).toString() + " of " + mPageCount
            tv_size!!.text = mNext

        }
    }

    private fun AddJobsDialog(status: String) {

        val mDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_vendor_jobcard_services_dialog, null)

        bt_submit =
            mDialogView.findViewById(R.id.bt_submit)
        bt_cancel =
            mDialogView.findViewById(R.id.bt_cancel)
        et_services_service = mDialogView.findViewById(R.id.et_services_service)
        et_services_cost = mDialogView.findViewById(R.id.et_services_cost)
        et_services_discount = mDialogView.findViewById(R.id.et_services_discount)
        tv_service_finalprice = mDialogView.findViewById(R.id.tv_service_finalprice)

        tv_service_service_txt = mDialogView.findViewById(R.id.tv_service_service_txt)
        tv_service_cost_txt = mDialogView.findViewById(R.id.tv_service_cost_txt)
        tv_service_discount_txt = mDialogView.findViewById(R.id.tv_service_discount_txt)

        MandatoryService(resources.getString(R.string.select_service))
        MandatoryCost(resources.getString(R.string.cost))

        if (mServiceService.isNotEmpty()) {
            et_services_service.setText(mServiceService)
        }
        if (mServiceCost.isNotEmpty()) {
            et_services_cost.setText(mServiceCost)
        }
        if (mServiceDiscount.isNotEmpty()) {
            et_services_discount.setText(mServiceDiscount)
        }
        if (mServiceFinalPrice.isNotEmpty()) {
            tv_service_finalprice.text = mServiceFinalPrice
        }

        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)
        mAlertDialog = mBuilder.show()


        getValues()

        bt_submit.setOnClickListener {

            if (mServiceService.isNotEmpty()) {
                if (mServiceCost.isNotEmpty()) {

                    if (status.isNotEmpty()) {
                        if (status.equals("edit")) {
                            SaveService(status, mServicePid)
                        }
                    } else {
                        SaveService("", "")

                    }

                } else {
                    Toast.makeText(requireContext(), "Please Enter Service Cost", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "Please Enter Service", Toast.LENGTH_LONG).show()
            }
        }
        bt_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }

    private fun MandatoryService(string: String) {

        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(string)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_service_service_txt.text = builder
    }

    private fun MandatoryCost(string: String) {

        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(string)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_service_cost_txt.text = builder
    }

    private fun MandatoryDiscount(string: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(string)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_service_discount_txt.text = builder
    }

    private fun SaveService(editType: String, pid: String) {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading... Save")
        mProgressDialog.show()
        Constants.qrCode_uat.SaveJobCardServicesendor(
            mMobileNumber.toString(),
            mVendorNid,
            pid,
            mServiceService,
            mServiceCost,
            mServiceDiscount,
            mVendor,
            mJobcardID,
            editType,
            mServiceFinalPrice
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        Log.d(
                            "JOb Save",
                            mJobCardCustID + mServiceService + mServiceHours + mServiceCost + mServiceDiscount + mServiceFinalPrice + " " + string
                        )
                        if (string.contains("Services added succesfully")) {

                            mAlertDialog.dismiss()
                            mProgressDialog.dismiss()

                            getServicesList()

                        }
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

    private fun getValues() {

        et_services_service.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mServiceService = editable.toString().trim()


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_services_cost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mServiceCost = editable.toString().trim()

                if (mServiceCost.isNotEmpty() && mServiceDiscount.isNotEmpty()) {

                    val amount = mServiceCost.toLong() * mServiceDiscount.toLong() / 100
                    mServiceFinalPrice = (mServiceCost.toLong() - amount).toString()
                    tv_service_finalprice.text = mServiceFinalPrice
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_services_discount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mServiceDiscount = editable.toString().trim()

                if (mServiceCost.isNotEmpty() && mServiceDiscount.isNotEmpty()) {

                    val amount = mServiceCost.toLong() * mServiceDiscount.toLong() / 100
                    mServiceFinalPrice = (mServiceCost.toLong() - amount).toString()
                    tv_service_finalprice.text = mServiceFinalPrice
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun getServicesList() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading... get")
        mProgressDialog.show()
        Constants.qrCode_uat.GetVendorJobcardData(
            mMobileNumber.toString(),
            mVendorNid,
            "services",
            mJobcardID
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

//                    Log.e("Service List ", mVendorNid + " " + string)

                    if (!string.equals("{}")) {
                        ll_services_data.visibility = View.VISIBLE
                        ll_services_data1.visibility = View.VISIBLE

                        val jsonObject = JSONArray(string)

                        servicesdatalist = gson!!.fromJson<ArrayList<JobcardData>>(
                            jsonObject.toString(),
                            object : TypeToken<ArrayList<JobcardData>>() {

                            }.type
                        )
                        vp_pager!!.adapter = VendorJobcardServicesDataAdapter(
                            requireContext(),
                            servicesdatalist,
                            this@VendorJobcardServicesFragment
                        )

                        mPageCount = servicesdatalist.size.toString()
                        if (mPageCount!!.length > 0) {
                            tv_size!!.text = "1 of " + mPageCount!!
                        }

                    } else {
                        ll_services_data.visibility = View.GONE
                        ll_services_data1.visibility = View.GONE

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


    companion object {

        val TITLE = "Services"

        fun newInstance(): VendorJobcardServicesFragment {
            return VendorJobcardServicesFragment()
        }
    }

    override fun onNavigate(mJCData: JobcardData, position: Int, status: String) {


        mServiceService = mJCData.jc_service!!

        mServiceCost = mJCData.jc_mrp!!

        if (mJCData.jc_discount != null) {
            mServiceDiscount = mJCData.jc_discount!!

        }
        mServiceFinalPrice = mJCData.jc_final!!
        mServicePid = mJCData.jc_pid!!

        if (status.equals("edit")) {
            AddJobsDialog(status)

        } else if (status.equals("delete")) {

            DeleteServiceDialog()
        }

    }

    private fun DeleteServiceDialog() {

        val builder1 = AlertDialog.Builder(requireContext())
        builder1.setMessage("Are you sure to Delete Service")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Yes"
        ) { dialog, id ->
            UserSession(requireContext()).removePhoneNum()

            Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show()

            SaveService("delete", mServicePid)
        }

        builder1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()

    }

    fun getMandatoryTab(): Boolean {

        return servicesdatalist.size > 0
    }

}