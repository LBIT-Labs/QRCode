package workshop.lbit.qrcode.jobcard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.MainActivity
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.adapter.JobcardSummaryServicesDataAdapter
import workshop.lbit.qrcode.adapter.JobcardSummarySparesDataAdapter
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.utils.Constants


@SuppressLint("ValidFragment")
class JobCardSummaryFragment @SuppressLint("ValidFragment") constructor() : Fragment(),
    View.OnClickListener {

    lateinit var tv_summary_customerName: MyTextView_Roboto_Medium
    lateinit var tv_summary_customerMobile: MyTextView_Roboto_Medium
    lateinit var tv_spare_size: MyTextView_Roboto_Bold
    lateinit var tv_spare_previous: MyTextView_Roboto_Bold
    lateinit var tv_spare_next: MyTextView_Roboto_Bold
    lateinit var tv_service_size: MyTextView_Roboto_Bold
    lateinit var tv_service_next: MyTextView_Roboto_Bold
    lateinit var tv_service_previous: MyTextView_Roboto_Bold
    lateinit var tv_generateEstimate: MyTextView_Roboto_Regular

    lateinit var tv_job_technicianName: MyTextView_Roboto_Medium
    lateinit var tv_job_jobcardNo: MyTextView_Roboto_Medium
    lateinit var tv_job_jobcardDate: MyTextView_Roboto_Medium
    lateinit var tv_job_vehicleNumber: MyTextView_Roboto_Medium

    private var mRole: String = ""
    private var mJobCardCustID: String = ""
    private var mJobCardStatus: String = ""
    private var mJobCardServiceStatus: String = ""
    private var mTotalAmount: String = ""
    private var mCustomerMobile: String = ""
    private var mJobsList: String = "0"
    private var mServicesList: String = "0"
    private var mInvoicePrevieURL: String = ""
    private var mIsPreviewClicked: Boolean = false
    private lateinit var dict_data: JSONObject
    internal var mMobileNumber: String = ""
    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var isLoaded = false
    private var isVisibleToUser = false

    private var llSparesViewpaer: LinearLayout? = null
    private var llServicesViewpaer: LinearLayout? = null

    private var vp_serviepager: ViewPager? = null
    private var vp_sparespager: ViewPager? = null

    private var gson: Gson? = null
    private lateinit var ll_spares_added_details: LinearLayout
    private lateinit var ll_services_added_details: LinearLayout

    private var mSparePageCount: String? = null
    private var mServicePageCount: String? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0
    private lateinit var mAlertDialog: AlertDialog

    var qr_scan_verified: Boolean = false


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

        sharedpreferences =
            activity!!.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "").toString()

        dict_data = JSONObject()

        val logindata = UserSession(requireContext()).getLoginDetails()

        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
            mJobCardCustID = dict_data.optString("JobCardCustID")
            mJobCardStatus = dict_data.optString("status")
            mJobCardServiceStatus = dict_data.optString("service_status")

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        if (mJobCardStatus.equals("Job Card")) {
            tv_generateEstimate.text = "Generate Estimate"

        } else if (mJobCardStatus.equals("Estimate")) {
            tv_generateEstimate.text = "Start Service"

        } else if (mJobCardStatus.equals("Under Progress")) {

            if (mJobCardServiceStatus.isNotEmpty() && mJobCardServiceStatus.equals("Start")) {
                tv_generateEstimate.text = "End Service"

            } else if (mJobCardServiceStatus.isNotEmpty() && mJobCardServiceStatus.equals("End")) {
                tv_generateEstimate.text = "Generate Invoice"
                tv_generateEstimate.visibility = View.GONE

            }
        }

        if (mJobCardCustID.isNotEmpty()) {
            getJobsSummaryList()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_jobcard_summary, container, false)

        init(v)
        return v
    }

    fun init(v: View) {
        ll_spares_added_details = v.findViewById(R.id.ll_spares_added_details)
        ll_services_added_details = v.findViewById(R.id.ll_services_added_details)
        llSparesViewpaer = v.findViewById(R.id.llSparesViewpaer)
        llServicesViewpaer = v.findViewById(R.id.llServicesViewpaer)
        vp_serviepager = v.findViewById(R.id.vp_serviepager)
        vp_sparespager = v.findViewById(R.id.vp_sparespager)

        tv_service_size = v.findViewById(R.id.tv_service_size)
        tv_service_next = v.findViewById(R.id.tv_service_next)
        tv_service_previous = v.findViewById(R.id.tv_service_previous)
        tv_spare_size = v.findViewById(R.id.tv_spare_size)
        tv_spare_next = v.findViewById(R.id.tv_spare_next)
        tv_spare_previous = v.findViewById(R.id.tv_spare_previous)
        tv_generateEstimate = v.findViewById(R.id.tv_generateEstimate)
        tv_summary_customerName = v.findViewById(R.id.tv_summary_customerName)
        tv_summary_customerMobile = v.findViewById(R.id.tv_summary_customerMobile)

        tv_job_jobcardNo = v.findViewById(R.id.tv_job_jobcardNo)
        tv_job_jobcardDate = v.findViewById(R.id.tv_job_jobcardDate)
        tv_job_vehicleNumber = v.findViewById(R.id.tv_job_vehicleNumber)
        tv_job_technicianName = v.findViewById(R.id.tv_job_technicianName)

        tv_service_next.setOnClickListener(this)
        tv_service_previous.setOnClickListener(this)
        tv_spare_next.setOnClickListener(this)
        tv_spare_previous.setOnClickListener(this)
        tv_generateEstimate.setOnClickListener(this)
        gson = Gson()


        vp_sparespager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

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
                    Log.e("TAG", "going left")


                    Log.e("TAG", "onClick_Previous: " + (vp_sparespager!!.currentItem - 1))

                    val mNext =
                        (vp_sparespager!!.currentItem + 1).toString() + " of " + mSparePageCount
                    if (!(vp_sparespager!!.currentItem - 1).equals(mSparePageCount) && !mNext.equals(
                            "0"
                        )
                    ) {
                        tv_spare_size.text = mNext
                    }


                } else {

                    val mNext =
                        (vp_sparespager!!.currentItem + 1).toString() + " of " + mSparePageCount
                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tv_spare_size.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
            }

        })

        vp_serviepager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

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
                    Log.e("TAG", "going left")


                    Log.e("TAG", "onClick_Previous: " + (vp_serviepager!!.currentItem - 1))

                    val mNext =
                        (vp_serviepager!!.currentItem + 1).toString() + " of " + mServicePageCount
                    if (!(vp_serviepager!!.currentItem - 1).equals(mServicePageCount) && !mNext.equals(
                            "0"
                        )
                    ) {
                        tv_service_size.text = mNext
                    }


                } else {

                    val mNext =
                        (vp_serviepager!!.currentItem + 1).toString() + " of " + mServicePageCount
                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tv_service_size.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
            }

        })


    }

    private fun getJobsSummaryList() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.GetJobcardSummary(mJobCardCustID).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Summary List ", mJobCardCustID + " " + string)

                    if (!string.equals("{}")) {

                        val jsonObject = JSONObject(string)

                        val detailsjsonObject = jsonObject.getJSONObject("jobcard_details")
                        val sparejsonArray = jsonObject.getJSONArray("spare_details")
                        val servicejsonArray = jsonObject.getJSONArray("service_details")
                        mTotalAmount = jsonObject.getString("total_final")
                        mInvoicePrevieURL = detailsjsonObject.getString("preview").toString()


                        val mCustomerName = detailsjsonObject.getString("customer").toString()
                        mCustomerMobile = detailsjsonObject.getString("mobile").toString()
                        val mJobCardId = detailsjsonObject.getString("jobcard_id").toString()
                        val mJobcardDate = detailsjsonObject.getString("jobcard_date").toString()
                        val mtechnician = detailsjsonObject.getString("tech").toString()
                        val mvehicleReg = detailsjsonObject.getString("reg").toString()
                        mJobsList = detailsjsonObject.getString("no_of_jobs").toString()
                        mServicesList = detailsjsonObject.getString("no_of_services").toString()

                        tv_summary_customerName.text = mCustomerName
                        tv_summary_customerMobile.text = mCustomerMobile
                        tv_job_jobcardNo.text = mJobCardId
                        tv_job_jobcardDate.text = mJobcardDate
                        tv_job_technicianName.text = mtechnician
                        tv_job_vehicleNumber.text = mvehicleReg

                        if (sparejsonArray.length() > 0) {

                            for (i in 0 until sparejsonArray.length()) {
                                val jsonObject = sparejsonArray.getJSONObject(i)

                                val qr_scan_status =
                                    jsonObject.getString("qr_scan_status").toString()
                                qr_scan_verified = qr_scan_status.equals("Verified")
                            }
                        }

                        if (sparejsonArray.length() > 0) {
                            val jobsdatalistSpares = gson!!.fromJson<ArrayList<JobcardData>>(
                                sparejsonArray.toString(),
                                object : TypeToken<ArrayList<JobcardData>>() {
                                }.type
                            )
                            vp_sparespager!!.adapter = JobcardSummarySparesDataAdapter(
                                requireContext(),
                                jobsdatalistSpares
                            )
                            mSparePageCount = jobsdatalistSpares.size.toString()

                            if (mSparePageCount!!.length > 0) {
                                tv_spare_size.text = "1 of " + mSparePageCount!!
                            }

                        } else {
                            ll_spares_added_details.visibility = View.GONE
                        }

                        if (servicejsonArray.length() > 0) {
                            val jobsdatalistServices = gson!!.fromJson<ArrayList<JobcardData>>(
                                servicejsonArray.toString(),
                                object : TypeToken<ArrayList<JobcardData>>() {

                                }.type
                            )



                            vp_serviepager!!.adapter = JobcardSummaryServicesDataAdapter(
                                requireContext(),
                                jobsdatalistServices
                            )

                            mServicePageCount = jobsdatalistServices.size.toString()

                            if (mServicePageCount!!.length > 0) {
                                tv_service_size.text = "1 of " + mServicePageCount!!
                            }
                        } else {
                            ll_services_added_details.visibility = View.GONE
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

    companion object {

        val TITLE = "Summary"

        fun newInstance(): JobCardSummaryFragment {
            return JobCardSummaryFragment()
        }
    }

    private fun getItemofviewpager(i: Int): Int {
        return vp_sparespager!!.currentItem + i
    }

    private fun getItemofviewpager1(i: Int): Int {
        return vp_serviepager!!.currentItem + i
    }

    override fun onClick(v: View?) {

        val i = v!!.id
        if (i == R.id.tv_spare_previous) {

            vp_sparespager!!.setCurrentItem(getItemofviewpager(-1), true)
            Log.e("TAG", "onClick_Previous: " + vp_sparespager!!.currentItem)
            val mPrev = (vp_sparespager!!.currentItem + 1).toString() + " of " + mSparePageCount
            tv_spare_size.text = mPrev


        } else if (i == R.id.tv_spare_next) {

            vp_sparespager!!.setCurrentItem(getItemofviewpager(+1), true)
            Log.e("TAG", "onClick_Next: " + vp_sparespager!!.currentItem + 1)
            val mNext = (vp_sparespager!!.currentItem + 1).toString() + " of " + mSparePageCount
            tv_spare_size.text = mNext

        } else if (i == R.id.tv_service_previous) {

            vp_serviepager!!.setCurrentItem(getItemofviewpager1(-1), true)
            Log.e("TAG", "onClick_Previous: " + vp_serviepager!!.currentItem)
            val mPrev = (vp_serviepager!!.currentItem + 1).toString() + " of " + mServicePageCount
            tv_service_size.text = mPrev


        } else if (i == R.id.tv_service_next) {

            vp_serviepager!!.setCurrentItem(getItemofviewpager1(+1), true)
            Log.e("TAG", "onClick_Next: " + vp_serviepager!!.currentItem + 1)
            val mNext = (vp_serviepager!!.currentItem + 1).toString() + " of " + mServicePageCount
            tv_service_size.text = mNext

        } else if (i == R.id.tv_generateEstimate) {

            if (mJobCardStatus.isNotEmpty()) {
                if (mJobCardStatus.equals("Job Card")) {
                    if (mJobsList.isNotEmpty()) {
                        if (mJobsList.toInt() > 0) {
                            if (mServicesList.isNotEmpty()) {
                                if (mServicesList.toInt() > 0) {
                                    GenerateEstimate("estimate")

                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please Add Atleast one Service to Proceed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } else {

                            Toast.makeText(
                                requireContext(),
                                "Please Add Atleast one Job to Proceed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else if (mJobCardStatus.equals("Estimate")) {
                    GenerateEstimate("start")
                } else if (mJobCardStatus.equals("Under Progress")) {
                    if (mJobCardServiceStatus.isNotEmpty() && mJobCardServiceStatus.equals("Start")) {
                        GenerateEstimate("end")

//                    } else if (mJobCardServiceStatus.isNotEmpty() && mJobCardServiceStatus.equals("End")) {
//
//                        if (mSparePageCount!!.isEmpty()) {
//                            PreViewDialog()
//
//                        } else {
//                            if (qr_scan_verified == true) {
//                                PreViewDialog()
//
//                            } else {
//                                Toast.makeText(
//                                    requireContext(),
//                                    "Please Scan Spares before generating the Invoice",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        }

                    }
                }
            } else {
                GenerateEstimate("estimate")

            }
        }

    }

    private fun PreViewDialog() {

        val mDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.jobcard_invoice_preview_dialog, null)

        val bt_submit =
            mDialogView.findViewById(R.id.bt_submit) as MyTextView_Roboto_Bold
        val bt_cancel =
            mDialogView.findViewById(R.id.bt_cancel) as MyTextView_Roboto_Bold

        val bt_preview =
            mDialogView.findViewById(R.id.btn_preview) as MyTextView_Roboto_Bold

        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)
        mAlertDialog = mBuilder.show()

        bt_submit.setOnClickListener {

            if (mIsPreviewClicked == true) {
                mAlertDialog.dismiss()
                GenerateEstimate("invoice")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please preview proforma before Generating the Invoice",
                    Toast.LENGTH_LONG
                ).show()

            }
        }

        bt_preview.setOnClickListener {


            mIsPreviewClicked = true

            if (mInvoicePrevieURL.isNotEmpty()) {
                val pdf_url = mInvoicePrevieURL

                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                startActivity(browserIntent)
            }
        }
        bt_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }


    private fun GenerateEstimate(status: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.GenerateEstimate(
            mMobileNumber,
            mCustomerMobile,
            mJobCardCustID,
            status,
            mTotalAmount
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Jobs List ", mJobCardCustID + " " + string)

                    if (!string.equals("{}")) {

                        val jsonObject = JSONObject(string)

                        val mURL = jsonObject.getString("url").toString()
                        val mStatus = jsonObject.getString("status").toString()

                        if (mURL.isNotEmpty()) {

                            val pdf_url = mURL

                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                            startActivity(browserIntent)
                        }

                        if (mStatus.isNotEmpty()) {

                            if (mStatus.equals("Service Started succefully")) {
                                Toast.makeText(
                                    requireContext(),
                                    "Service Stared succefully",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (mStatus.equals("Service ended succefully")) {
                                Toast.makeText(
                                    requireContext(),
                                    "Service ended succefully",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            dict_data.put("JobCardCustID", "")
                            dict_data.put("CustomerMobile", "")
                            dict_data.put("status", "")
                            dict_data.put("service_status", "")
                            dict_data.put("screenType", "")
                            dict_data.put("CustomerName", "")
                            dict_data.put("RegNo", "")
                            dict_data.put("Make", "")
                            dict_data.put("Model", "")
                            UserSession(requireContext()).setLoginDetails(dict_data.toString())

                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
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
}