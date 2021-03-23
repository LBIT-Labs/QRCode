package workshop.lbit.qrcode.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import workshop.lbit.qrcode.adapter.LiveJobcardLiveRecordsDataAdapter
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.interfaces.JobCardList
import workshop.lbit.qrcode.interfaces.JobCardListService
import workshop.lbit.qrcode.ui.JobCardFormActivity
import workshop.lbit.qrcode.utils.Constants

class LiveJobCardLiveRecordsFragment : Fragment(), View.OnClickListener, JobCardList {
    private lateinit var dict_data: JSONObject
    private lateinit var logindata: String
    private var isLoaded = false
    private var isVisibleToUser = false
    private var sharedpreferences: SharedPreferences? = null
    internal var mMobileNumber: String? = null
    private var editor: SharedPreferences.Editor? = null
    private var mRole: String = ""
    lateinit var et_search: EditText
    lateinit var layout_footer: RelativeLayout
    lateinit var ll_live_jobcard_data: LinearLayout
    lateinit var tv_size: MyTextView_Roboto_Bold
    lateinit var tvnext: MyTextView_Roboto_Bold
    lateinit var tvPrevious: MyTextView_Roboto_Bold
    private var srl_swipetorefresh: SwipeRefreshLayout? = null

    private var gson: Gson? = null
    private var vp_pager: ViewPager? = null
    private var mPageCount: String? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isAdded()) {
            et_search.setText("")

            loadData("")
            isLoaded = true;
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isVisibleToUser && (!isLoaded)) {
            et_search.setText("")

            loadData("")
            isLoaded = true;
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.live_jobcard_live_records_fragment, container, false)
        sharedpreferences = activity!!.getSharedPreferences(
            Constants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")

        logindata = UserSession(requireContext()).getLoginDetails()

        dict_data = JSONObject()
        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e("test***", "Role  " + mRole)

        init(v)


        srl_swipetorefresh!!.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(requireContext(), R.color.qr_code_bg))
        srl_swipetorefresh!!.setColorSchemeColors(Color.WHITE)

        srl_swipetorefresh!!.setOnRefreshListener {

            loadData("")
            srl_swipetorefresh!!.isRefreshing = false

        }

        et_search!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()

                if (text.length == 10) {

                    loadData(text)

                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        return v
    }

    fun init(v: View) {

        srl_swipetorefresh = v.findViewById(R.id.srl_swipetorefresh)


        et_search = v.findViewById(R.id.et_search)
        ll_live_jobcard_data = v.findViewById(R.id.ll_live_jobcard_data)
        layout_footer = v.findViewById(R.id.layout_footer)
        tvPrevious = v.findViewById(R.id.tvPrevious)
        tvnext = v.findViewById(R.id.tvnext)
        tv_size = v.findViewById(R.id.tv_size)
        vp_pager = v.findViewById(R.id.vp_pager)

        gson = Gson()
        tvnext.setOnClickListener(this)
        tvPrevious.setOnClickListener(this)


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
                    if (!(vp_pager!!.currentItem - 1).equals(mPageCount) && !mNext.equals("0")) {
                        tv_size.text = mNext
                    }


                } else {

                    val mNext =
                        (vp_pager!!.currentItem + 1).toString() + " of " + mPageCount
                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tv_size.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position;
            }

        })

    }

    private fun loadData(mCustMobile: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getLiveJobsData(mMobileNumber.toString(), mCustMobile).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Live Jobs List ", string)

                    if (!string.equals("{}")) {
                        ll_live_jobcard_data.visibility = View.VISIBLE
                        layout_footer.visibility = View.VISIBLE

                        val jsonObject = JSONArray(string)

                        val jobsdatalist = gson!!.fromJson<ArrayList<JobcardData>>(
                            jsonObject.toString(),
                            object : TypeToken<ArrayList<JobcardData>>() {

                            }.type
                        )
                        vp_pager!!.adapter = LiveJobcardLiveRecordsDataAdapter(
                            requireContext(),
                            jobsdatalist,
                            this@LiveJobCardLiveRecordsFragment
                        )

                        mPageCount = jobsdatalist.size.toString()
                        if (mPageCount!!.length > 0) {
                            tv_size.text = "1 of " + mPageCount!!
                        }

                    } else {
                        ll_live_jobcard_data.visibility = View.GONE
                        layout_footer.visibility = View.GONE

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

    private fun getItemofviewpager(i: Int): Int {
        return vp_pager!!.currentItem + i
    }

    override fun onClick(v: View?) {
        val i = v!!.id

        if (i == R.id.tvPrevious) {
            vp_pager!!.setCurrentItem(getItemofviewpager(-1), true)

            Log.e("TAG", "onClick_Previous: " + vp_pager!!.currentItem)
            val mPrev = (vp_pager!!.currentItem + 1).toString() + " of " + mPageCount
            tv_size!!.text = mPrev


        } else if (i == R.id.tvnext) {

            vp_pager!!.setCurrentItem(getItemofviewpager(+1), true)
            Log.e("TAG", "onClick_Next: " + vp_pager!!.currentItem + 1)
            val mNext = (vp_pager!!.currentItem + 1).toString() + " of " + mPageCount
            tv_size!!.text = mNext

        }
    }

    companion object {

        val TITLE = "Live"

        fun newInstance(): LiveJobCardLiveRecordsFragment {

            return LiveJobCardLiveRecordsFragment()
        }
    }

    override fun onNavigate(mJCData: JobcardData, position: Int) {

        if(mJCData.jc_live_jobcard_status!!.isNotEmpty() && !mJCData.jc_live_jobcard_status.equals("Invoiced")){

            dict_data.put("CustomerMobile", mJCData.jc_live_mobile)
            dict_data.put("JobCardCustID", mJCData.jc_nid)
            dict_data.put("status", mJCData.jc_live_jobcard_status)
            dict_data.put("service_status", mJCData.jc_live_service_status)
            dict_data.put("screenType", "LiveJobcard")
            UserSession(requireContext()).setLoginDetails(dict_data.toString())

            val intent = Intent(requireContext(), JobCardFormActivity::class.java)
            startActivity(intent)
        }

    }

}