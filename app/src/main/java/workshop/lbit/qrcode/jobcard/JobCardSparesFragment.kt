package workshop.lbit.qrcode.jobcard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.add_jobcard_jobs_dialog.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.adapter.JobcardSparesDataAdapter
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.interfaces.JobCardListService
import workshop.lbit.qrcode.ui.JobcardSparesSearchActivity
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.utils.Utilities


@SuppressLint("ValidFragment")
class JobCardSparesFragment @SuppressLint("ValidFragment") constructor() : Fragment(),
    View.OnClickListener, JobCardListService {

    private lateinit var mAlertDialog: AlertDialog
    lateinit var ll_add_spares: LinearLayout
    lateinit var ll_spares_data: LinearLayout
    lateinit var ll_spares_data1: LinearLayout
    private var gson: Gson? = null

    private var isLoaded = false
    private var isVisibleToUser = false

    private var mRole: String = ""
    private var mJobCardCustID: String = ""
    private lateinit var dict_data: JSONObject

    lateinit var bt_submit: MyTextView_Roboto_Bold
    lateinit var bt_cancel: MyTextView_Roboto_Bold
    lateinit var et_spares_discount: EditText
    lateinit var et_spares_quantity: EditText
    lateinit var tv_spares_OE_part_number: MyTextView_Roboto_Medium
    lateinit var tv_spares_part_desc: MyTextView_Roboto_Medium
    lateinit var tv_spares_finalprice: MyTextView_Roboto_Medium
    lateinit var tv_spares_mrp: MyTextView_Roboto_Medium
    lateinit var tv_spares_discount_txt: MyTextView_Roboto_Bold
    lateinit var tv_spares_jobid_txt: MyTextView_Roboto_Bold
    lateinit var sp_spares_jobid: Spinner


    internal var mMobileNumber: String? = null
    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var vp_pager: ViewPager? = null
    private var tv_size: MyTextView_Roboto_Bold? = null
    private var tv_previous: MyTextView_Roboto_Bold? = null
    private var tv_next: MyTextView_Roboto_Bold? = null

    private var mPageCount: String? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0

    private var mSparesOePart = ""
    private var mSparesOePartDesc = ""
    private var mSparePid = ""
    private var mSparesMrp = ""
    var mSparesDiscount: String = ""
    var mSparesQuantity: String = ""
    var mSparesQuantityLatest: String = ""
    var mSparesTotalQuantity: String = ""
    var mSparesFinalPrice: String = ""
    var mSparesPartGRN: String = ""
    var mSparesTax: String = ""
    var mSparesHSN: String = ""
    var mSparesJobId: String = ""

    private var jobsList = java.util.ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isAdded()) {
            loadData()
            isLoaded = true;
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isVisibleToUser && (!isLoaded)) {
            loadData()
            isLoaded = true;
        }
    }

    private fun loadData() {

        dict_data = JSONObject()

        val logindata = UserSession(requireContext()).getLoginDetails()

        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
            mJobCardCustID = dict_data.optString("JobCardCustID")

            getSparesList()

            Log.d("data", dict_data.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_jobcard_spares, container, false)

        sharedpreferences =
            activity!!.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")

        init(v)

        return v
    }

    fun init(v: View) {
        ll_add_spares = v.findViewById(R.id.ll_add_spares)
        ll_spares_data = v.findViewById(R.id.ll_spares_data)
        ll_spares_data1 = v.findViewById(R.id.ll_spares_data1)
        tv_next = v.findViewById(R.id.tv_next)
        tv_size = v.findViewById(R.id.tv_size)
        tv_previous = v.findViewById(R.id.tv_previous)
        vp_pager = v.findViewById(R.id.vp_pager)

        gson = Gson()

        ll_add_spares.setOnClickListener(this)
        tv_next!!.setOnClickListener(this)
        tv_previous!!.setOnClickListener(this)

        vp_pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
                if (!scrollStarted && state == ViewPager.SCROLLBAR_POSITION_DEFAULT) {
                    scrollStarted = true;
                    checkDirection = true;
                } else {
                    scrollStarted = false;
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                if (thresholdOffset > positionOffset && positionOffsetPixels > thresholdOffsetPixels) {
                    Log.e("TAG", "going left")


                    Log.e("TAG", "onClick_Previous: " + (vp_pager!!.currentItem - 1))

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
                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tv_size!!.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position;
            }

        })

    }

    private fun getItemofviewpager(i: Int): Int {
        return vp_pager!!.currentItem + i
    }


    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.ll_add_spares) {

            val intent = Intent(requireContext(), JobcardSparesSearchActivity::class.java)
            startActivity(intent)
        } else if (i == R.id.tv_previous) {
            vp_pager!!.setCurrentItem(getItemofviewpager(-1), true)

            Log.e("TAG", "onClick_Previous: " + vp_pager!!.currentItem)
            val mPrev = (vp_pager!!.currentItem + 1).toString() + " of " + mPageCount
            tv_size!!.text = mPrev


        } else if (i == R.id.tv_next) {

            vp_pager!!.setCurrentItem(getItemofviewpager(+1), true)
            Log.e("TAG", "onClick_Next: " + vp_pager!!.currentItem + 1)
            val mNext = (vp_pager!!.currentItem + 1).toString() + " of " + mPageCount
            tv_size!!.text = mNext

        }
    }

    private fun getSparesList() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading... get")
        mProgressDialog.show()
        Constants.qrCode_uat.GetJobcardData(mJobCardCustID, "spares").enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Jobs List ", mJobCardCustID + " " + string)

                    if (!string.equals("{}")) {
                        ll_spares_data.visibility = View.VISIBLE
                        ll_spares_data1.visibility = View.VISIBLE

                        val jsonObject = JSONArray(string)

                        val jobsdatalist = gson!!.fromJson<ArrayList<JobcardData>>(
                            jsonObject.toString(),
                            object : TypeToken<ArrayList<JobcardData>>() {

                            }.type
                        )
                        vp_pager!!.adapter = JobcardSparesDataAdapter(
                            requireContext(),
                            jobsdatalist,
                            this@JobCardSparesFragment
                        )

                        mPageCount = jobsdatalist.size.toString()
                        if (mPageCount!!.length > 0) {
                            tv_size!!.text = "1 of " + mPageCount!!
                        }

                    } else {
                        ll_spares_data.visibility = View.GONE
                        ll_spares_data1.visibility = View.GONE

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


    override fun onNavigate(mJCData: JobcardData, position: Int, status: String) {

        mSparesOePart = mJCData.jc_oe_part!!
        mSparesOePartDesc = mJCData.jc_part_desc!!
        mSparesQuantity = mJCData.jc_qty!!
        mSparesMrp = mJCData.jc_mrp!!
        mSparesDiscount = mJCData.jc_discount!!
        mSparesFinalPrice = mJCData.jc_final!!
        mSparesPartGRN = mJCData.jc_part_grn!!
        mSparePid = mJCData.jc_pid!!
        mSparesHSN = mJCData.jc_part_hsn!!
        mSparesTax = mJCData.jc_tax!!
        mSparesJobId = mJCData.jc_job_id!!

        if (status.equals("edit")) {
            AddSparesDialog(status)

        } else if (status.equals("delete")) {

            DeleteSpareDialog()
        }
    }

    private fun DeleteSpareDialog() {

        val builder1 = AlertDialog.Builder(requireContext())
        builder1.setMessage("Are you sure to Delete Spare")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Yes"
        ) { dialog, id ->
            UserSession(requireContext()).removePhoneNum()

            Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show()

            SaveSparesDelete("delete", mSparePid)
        }

        builder1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()

    }

    private fun AddSparesDialog(status: String) {
        val mDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_jobcard_spares_dialog, null)


        bt_submit =
            mDialogView.findViewById(R.id.bt_submit)
        bt_cancel =
            mDialogView.findViewById(R.id.bt_cancel)
        et_spares_quantity = mDialogView.findViewById(R.id.et_spares_quantity)
        et_spares_discount = mDialogView.findViewById(R.id.et_spares_discount)
        tv_spares_OE_part_number = mDialogView.findViewById(R.id.tv_spares_OE_part_number)
        tv_spares_part_desc = mDialogView.findViewById(R.id.tv_spares_part_desc)
        tv_spares_finalprice = mDialogView.findViewById(R.id.tv_spares_finalprice)
        tv_spares_mrp = mDialogView.findViewById(R.id.tv_spares_mrp)

        tv_spares_discount_txt = mDialogView.findViewById(R.id.tv_spares_discount_txt)
        tv_spares_jobid_txt = mDialogView.findViewById(R.id.tv_spares_jobid_txt)
        sp_spares_jobid = mDialogView.findViewById(R.id.sp_spares_jobid)

        MandatoryDiscount(resources.getString(R.string.discount1))
        MandatoryJobid(resources.getString(R.string.job_id))


        if (mSparesOePart.isNotEmpty()) {
            tv_spares_OE_part_number.setText(mSparesOePart)
        }
        if (mSparesOePartDesc.isNotEmpty()) {
            tv_spares_part_desc.setText(mSparesOePartDesc)
        }
        if (mSparesQuantity.isNotEmpty()) {
            et_spares_quantity.setText(mSparesQuantity)
        }
        if (mSparesMrp.isNotEmpty()) {
            tv_spares_mrp.setText(mSparesMrp)
        }
        if (mSparesDiscount.isNotEmpty()) {
            et_spares_discount.setText(mSparesDiscount)
        }

        if (mSparesFinalPrice.isNotEmpty()) {
            tv_spares_finalprice.setText(mSparesFinalPrice)
        }
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)
        mAlertDialog = mBuilder.show()


        getJObID()

        getValues()

        bt_submit.setOnClickListener {

            if (mSparesDiscount.isNotEmpty()) {

                if (mSparesJobId.isNotEmpty()) {

                    if (mSparesQuantityLatest > mSparesTotalQuantity) {
                        Toast.makeText(
                            requireContext(),
                            "Quantity should not be greater than Available Quantity",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (status.isNotEmpty()) {
                            if (status.equals("edit")) {
                                SaveSpares(status, mSparePid)
                            }
                        }
                    }

                } else {
                    Toast.makeText(requireContext(), "Please Select JobId", Toast.LENGTH_LONG)
                        .show()

                }
            } else {
                Toast.makeText(requireContext(), "Please Enter Discount", Toast.LENGTH_LONG).show()

            }
        }
        bt_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }

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
        tv_spares_discount_txt.setText(builder)

    }

    private fun MandatoryJobid(string: String) {

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
        tv_spares_jobid_txt.setText(builder)

    }

    private fun getJObID() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getJObIDList(mJobCardCustID).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Make", string)


                    val list = ArrayList<String>()

                    Log.e("Test", "Make_List: $string")

                    jobsList = Utilities.getItemList(list, string)

                    if (mSparesJobId.isNotEmpty()) {
                        if (jobsList.indexOf(mSparesJobId) > -1) {
                            sp_spares_jobid.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                jobsList
                            )
                            sp_spares_jobid.setSelection(jobsList.indexOf(mSparesJobId))
                        }
                    } else {
                        sp_spares_jobid.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, jobsList
                        )
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

    private fun getValues() {

        sp_spares_jobid.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mSparesJobId = sp_spares_jobid.selectedItem.toString()
                    } else {
                        mSparesJobId = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        et_spares_quantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mSparesQuantityLatest = editable.toString().trim()

                if (mSparesQuantity > mSparesTotalQuantity) {
                    Toast.makeText(
                        requireContext(), "Quantity should not be greater than Available Quantity",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (mSparesMrp.isNotEmpty() && mSparesDiscount.isNotEmpty() && mSparesQuantity.isNotEmpty()) {

                        val mValue = mSparesMrp.toLong() * mSparesQuantity.toLong()
                        val amount = mValue * mSparesDiscount.toLong() / 100
                        mSparesFinalPrice = (mValue - amount).toString()
                        tv_spares_finalprice.text = mSparesFinalPrice
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_spares_discount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mSparesDiscount = editable.toString().trim()

                if (mSparesMrp.isNotEmpty() && mSparesDiscount.isNotEmpty() && mSparesQuantity.isNotEmpty()) {
                    val mValue = mSparesMrp.toLong() * mSparesQuantity.toLong()
                    val amount = mValue * mSparesDiscount.toLong() / 100
                    mSparesFinalPrice = (mValue - amount).toString()
                    tv_spares_finalprice.text = mSparesFinalPrice
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }


    private fun SaveSpares(status: String, mSparePid: String) {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading... Save")
        mProgressDialog.show()
        Constants.qrCode_uat.SaveJobCardSpares(
            mMobileNumber.toString(),
            mJobCardCustID,
            "",
            mSparePid,
            mSparesOePart,
            mSparesOePartDesc,
            mSparesTax,
            mSparesHSN,
            mSparesQuantity,
            mSparesQuantityLatest,
            mSparesPartGRN,
            status,
            mSparesJobId,
            mSparesDiscount,
            mSparesFinalPrice
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        Log.d("Spare Edit", mSparesDiscount + " " + string)
                        if (string.contains("Added Spare Succesfully")) {

                            mAlertDialog.dismiss()
                            mProgressDialog.dismiss()

                            getSparesList()
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

    private fun SaveSparesDelete(status: String, mSparePid: String) {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading... Save")
        mProgressDialog.show()
        Constants.qrCode_uat.SaveJobCardSpares(
            mMobileNumber.toString(),
            mJobCardCustID,
            "",
            mSparePid,
            mSparesOePart,
            mSparesOePartDesc,
            mSparesTax,
            mSparesHSN,
            mSparesQuantity,
            mSparesQuantityLatest,
            mSparesPartGRN,
            status,
            mSparesJobId,
            mSparesDiscount,
            mSparesFinalPrice
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        Log.d("Spare Edit", mSparesDiscount + " " + string)
                        if (string.contains("Added Spare Succesfully")) {

                            mProgressDialog.dismiss()

                            getSparesList()
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


    companion object {

        val TITLE = "Spares"

        fun newInstance(): JobCardSparesFragment {
            return JobCardSparesFragment()
        }
    }

}