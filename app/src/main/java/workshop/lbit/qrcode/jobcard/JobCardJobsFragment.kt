package workshop.lbit.qrcode.jobcard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
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
import workshop.lbit.qrcode.adapter.JobcardJobsDataAdapter
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.utils.Utilities


class JobCardJobsFragment @SuppressLint("ValidFragment") constructor() : Fragment(),
    View.OnClickListener {

    private lateinit var mAlertDialog: AlertDialog

    lateinit var ll_add_jobs: LinearLayout
    lateinit var ll_jobs_data: LinearLayout
    lateinit var ll_jobs_data1: LinearLayout
    lateinit var bt_submit: MyTextView_Roboto_Bold
    lateinit var bt_cancel: MyTextView_Roboto_Bold
    lateinit var et_jobs_wrokshop_notes: EditText
    lateinit var et_jobs_customer_notes: EditText
    lateinit var tv_jobs_jobId: MyTextView_Roboto_Medium
    lateinit var tv_job_txt: MyTextView_Roboto_Bold
    lateinit var tv_jobcategory_txt: MyTextView_Roboto_Bold
    lateinit var sp_jobs_job: Spinner
    lateinit var sp_jobs_jobCategory: Spinner
    private var vp_pager: ViewPager? = null
    private var tv_previous: MyTextView_Roboto_Bold? = null
    private var tv_size: MyTextView_Roboto_Bold? = null
    private var tv_next: MyTextView_Roboto_Bold? = null
    private var gson: Gson? = null

    private var mJob = ""
    private var mJobCategory = ""
    private var mJobId = ""
    private var mCustomerNottes = ""
    private var mWorkshopNotes = ""

    private var mPageCount: String? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0


    private var mRole: String = ""
    private var mJobCardCustID: String = ""
    private lateinit var dict_data: JSONObject

    private lateinit var jobcardJobsDataAdapter: JobcardJobsDataAdapter
    private var isLoaded = false
    private var isVisibleToUser = false

    private var jobsDataList = java.util.ArrayList<JobcardData>()


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
            mJobCardCustID = dict_data.optString("JobCardCustID")

            Log.d("data", dict_data.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        if (mJobCardCustID.isNotEmpty()) {
            getJobsList()

        } else {
            dict_data = JSONObject()

            val logindata = UserSession(requireContext()).getLoginDetails()

            try {
                dict_data = JSONObject(logindata)
                mRole = dict_data.optString("role")
                mJobCardCustID = dict_data.optString("JobCardCustID")

                getJobsList()

                Log.d("data", dict_data.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_jobcard_jobs, container, false)

        init(v)

        return v
    }

    private fun getJobsList() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading... get")
        mProgressDialog.show()
        Constants.qrCode_uat.GetJobcardData(mJobCardCustID, "jobs").enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Jobs List ", mJobCardCustID + " " + string)

                    if (!string.equals("{}")) {
                        ll_jobs_data.visibility = View.VISIBLE
                        ll_jobs_data1.visibility = View.VISIBLE

                        val jsonObject = JSONArray(string)

                        jobsDataList = gson!!.fromJson<ArrayList<JobcardData>>(
                            jsonObject.toString(),
                            object : TypeToken<ArrayList<JobcardData>>() {

                            }.type
                        )
                        vp_pager!!.adapter = JobcardJobsDataAdapter(requireContext(), jobsDataList)
                        mPageCount = jobsDataList.size.toString()
                        if (mPageCount!!.length > 0) {
                            tv_size!!.text = "1 of " + mPageCount!!
                        }

                    } else {
                        ll_jobs_data.visibility = View.GONE
                        ll_jobs_data1.visibility = View.GONE

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

    fun init(v: View) {
        ll_add_jobs = v.findViewById(R.id.ll_add_jobs)
        ll_jobs_data = v.findViewById(R.id.ll_jobs_data)
        ll_jobs_data1 = v.findViewById(R.id.ll_jobs_data1)
        vp_pager = v.findViewById(R.id.vp_pager)
        tv_previous = v.findViewById(R.id.tv_previous)
        tv_size = v.findViewById(R.id.tv_size)
        tv_next = v.findViewById(R.id.tv_next)

        gson = Gson()
        ll_add_jobs.setOnClickListener(this)
        tv_previous!!.setOnClickListener(this)
        tv_next!!.setOnClickListener(this)

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
                mCurrentFragmentPosition = position
            }

        })

    }

    private fun getValues() {

        sp_jobs_job.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View, arg2: Int, arg3: Long
                ) {

                    if (arg2 > 0) {
                        mJob = sp_jobs_job.selectedItem.toString()

                        getJObID(mJob)
                    } else {
                        mJob = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        sp_jobs_jobCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View, arg2: Int, arg3: Long
                ) {

                    if (arg2 > 0) {
                        mJobCategory = sp_jobs_jobCategory.selectedItem.toString()
                    } else {
                        mJobCategory = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        et_jobs_customer_notes.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mCustomerNottes = editable.toString().trim()


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_jobs_wrokshop_notes.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mWorkshopNotes = editable.toString().trim()


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun getJObID(mJob: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.GetFiltersData(
            "jobids",
            mJob
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        val jsonObject = JSONObject(string)
                        mJobId = jsonObject.getString("jobid").toString()
                        tv_jobs_jobId.text = mJobId
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

    private fun getItemofviewpager(i: Int): Int {
        return vp_pager!!.currentItem + i
    }

    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.ll_add_jobs) {
            Toast.makeText(requireContext(), "clicked", Toast.LENGTH_LONG).show()

            AddJobsDialog()
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

    private fun AddJobsDialog() {

        val mDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_jobcard_jobs_dialog, null)


        bt_submit =
            mDialogView.findViewById(R.id.bt_submit)
        bt_cancel =
            mDialogView.findViewById(R.id.bt_cancel)
        et_jobs_wrokshop_notes = mDialogView.findViewById(R.id.et_jobs_wrokshop_notes)
        et_jobs_customer_notes = mDialogView.findViewById(R.id.et_jobs_customer_notes)
        sp_jobs_jobCategory = mDialogView.findViewById(R.id.sp_jobs_jobCategory)
        sp_jobs_job = mDialogView.findViewById(R.id.sp_jobs_job)
        tv_jobs_jobId = mDialogView.findViewById(R.id.tv_jobs_jobId)

        tv_jobcategory_txt = mDialogView.findViewById(R.id.tv_jobcategory_txt)
        tv_job_txt = mDialogView.findViewById(R.id.tv_job_txt)

        MandatoryJobCat(resources.getString(R.string.job_category))
        MandatoryJob(resources.getString(R.string.job))

        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)
        mAlertDialog = mBuilder.show()


        getJobs()

        getValues()

        bt_submit.setOnClickListener {

            if (mJobCardCustID.isNotEmpty()) {

                if (mJobCategory.isNotEmpty()) {
                    if (mJob.isNotEmpty()) {
                        SaveJOB()

                    } else {
                        Toast.makeText(requireContext(), "Please select Job", Toast.LENGTH_LONG)
                            .show()

                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please select Job Category",
                        Toast.LENGTH_LONG
                    ).show()

                }

            } else {
                dict_data = JSONObject()

                val logindata = UserSession(requireContext()).getLoginDetails()

                try {
                    dict_data = JSONObject(logindata)
                    mRole = dict_data.optString("role")
                    mJobCardCustID = dict_data.optString("JobCardCustID")

                    if (mJobCategory.isNotEmpty()) {
                        if (mJob.isNotEmpty()) {
                            SaveJOB()

                        } else {
                            Toast.makeText(requireContext(), "Please select Job", Toast.LENGTH_LONG)
                                .show()

                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please select Job Category",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    Log.d("data", dict_data.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            mAlertDialog.dismiss()

        }
        bt_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }

    private fun MandatoryJobCat(string: String) {
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
        tv_jobcategory_txt.text = builder
    }

    private fun MandatoryJob(string: String) {
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
        tv_job_txt.text = builder
    }

    private fun SaveJOB() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading... Save")
        mProgressDialog.show()
        Constants.qrCode_uat.SaveJObCard(
            mJobCardCustID,
            "jobs",
            mJobCategory,
            mJob,
            mJobId,
            mCustomerNottes,
            mWorkshopNotes
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        Log.d("JOb Save", mJobCardCustID + " " + string)
                        if (string.contains("Added jobs Succesfully")) {

                            mAlertDialog.dismiss()
                            mProgressDialog.dismiss()

                            getJobsList()

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

    private fun getJobs() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.IMFiltersData(
            "jobs",
            ""
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        var list = ArrayList<String>()

                        list = Utilities.getItemList(list, string)

                        sp_jobs_job.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, list
                        )

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

        val TITLE = "Jobs"

        fun newInstance(): JobCardJobsFragment {
            return JobCardJobsFragment()
        }
    }

    fun getMandatoryTab(): Boolean {

        return jobsDataList.size > 0
    }


}