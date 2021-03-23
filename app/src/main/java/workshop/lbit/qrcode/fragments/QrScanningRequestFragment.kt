package workshop.lbit.qrcode.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import workshop.lbit.qrcode.adapter.QrScanningRequestListAdapter
import workshop.lbit.qrcode.customfonts.MyTextView_Montserrat_Regular
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.QrData
import workshop.lbit.qrcode.utils.Constants

class QrScanningRequestFragment @SuppressLint("ValidFragment") constructor() : Fragment() {

    private var sharedpreferences: SharedPreferences? = null
    internal var mMobileNumber: String? = null
    private var editor: SharedPreferences.Editor? = null
    private var mRole: String = ""
    private var mGatePassType: String = ""
    private var mUID: String = ""

    private var mRecyclerView: RecyclerView? = null
    private var srl_swipetorefresh: SwipeRefreshLayout? = null
    private var tvNodata: MyTextView_Montserrat_Regular? = null
    private var qr_req_invoice: MyTextView_Roboto_Medium? = null
    private var qr_req_ref: MyTextView_Roboto_Medium? = null
    private var qr_req_qty: MyTextView_Roboto_Medium? = null
    private var qr_req_custName: MyTextView_Roboto_Medium? = null
    private var gson: Gson? = null
    var jsonObject: JSONArray = (JSONArray())
    private lateinit var qrScanningAdapter: QrScanningRequestListAdapter
    private var isLoaded = false
    private var isVisibleToUser = false

    private var rg_typeof_gatepass: RadioGroup? = null
    internal lateinit var rb_value: RadioButton
    internal lateinit var rb_counterSale: RadioButton
    internal lateinit var ll_gatepassType: LinearLayout
    internal lateinit var rb_jobcard: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        super.setUserVisibleHint(isVisibleToUser);
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

    fun loadData() {

        if (mRole.equals("stores")) {
            qr_req_ref!!.visibility = View.VISIBLE
            qr_req_invoice!!.visibility = View.GONE

        } else if (mRole.equals("wh_store_boy")) {
            qr_req_ref!!.visibility = View.VISIBLE
            qr_req_ref!!.text = "Delivery #"
            qr_req_invoice!!.visibility = View.GONE

        } else if (mRole.equals("counter")) {
            qr_req_ref!!.visibility = View.VISIBLE
            qr_req_invoice!!.visibility = View.GONE

        } else if (mRole.equals("wh_security")) {
            qr_req_ref!!.visibility = View.GONE
            qr_req_invoice!!.visibility = View.VISIBLE
            qr_req_custName!!.text = "Gatepass #"
            qr_req_invoice!!.text = "Qty."
            qr_req_qty!!.text = "Net Wt(Kgs)"

        } else if (mRole.equals("security")) {
            qr_req_ref!!.visibility = View.GONE
            qr_req_invoice!!.visibility = View.VISIBLE

        }
        if (mRole.equals("wh_store_boy")) {
            ll_gatepassType.visibility = View.GONE

            GetQrRequestListWH(mMobileNumber!!)
        } else if (mRole.equals("wh_security")) {
            ll_gatepassType.visibility = View.GONE

            GetQrRequestListWHG(mMobileNumber!!)
        } else if (mRole.equals("stores") || mRole.equals("counter")) {
            ll_gatepassType.visibility = View.GONE

            GetQrRequestList(mMobileNumber!!)
        } else if (mRole.equals("security")) {

            ll_gatepassType.visibility = View.VISIBLE

            if (mGatePassType.isNotEmpty()){
                if(mGatePassType.equals("CounterSale")){
                    rb_counterSale.isChecked = true
                    GetQrRequestList(mMobileNumber!!)

                }else if (mGatePassType.equals("Jobcard")){
                    rb_jobcard.isChecked = true
                    GetQrRequestListGatepass(mMobileNumber!!)
                }
            }else {
                rb_counterSale.isChecked = true
                GetQrRequestList(mMobileNumber!!)
            }


        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.qrscanningrequest_fragment, container, false)
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
            mUID = dict_data.optString("uid")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e("test***", "Role  " + mRole)

        init(v)

        rg_typeof_gatepass!!.setOnCheckedChangeListener { group, checkedId ->
            rb_value = view!!.findViewById(checkedId)
            val value = rb_value.text.toString()


            if (value.equals("Counter Sale")) {

                mGatePassType = "CounterSale"
                GetQrRequestList(mMobileNumber!!)

            } else if (value.equals("JobCard")) {
                mGatePassType = "Jobcard"

                GetQrRequestListGatepass(mMobileNumber!!)

            }
        }


        srl_swipetorefresh!!.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.qr_code_bg
            )
        )
        srl_swipetorefresh!!.setColorSchemeColors(Color.WHITE)

        srl_swipetorefresh!!.setOnRefreshListener {

            loadData()
            srl_swipetorefresh!!.isRefreshing = false

        }
        return v
    }

    private fun init(view: View) {

        rg_typeof_gatepass = view.findViewById(R.id.rg_typeof_gatepass)
        rb_counterSale = view.findViewById(R.id.rb_counterSale)
        rb_jobcard = view.findViewById(R.id.rb_jobcard)
        ll_gatepassType = view.findViewById(R.id.ll_gatepassType)

        mRecyclerView = view.findViewById(R.id.qr_scanning_request_recycler_view)
        srl_swipetorefresh = view.findViewById(R.id.srl_swipetorefresh)
        tvNodata = view.findViewById(R.id.tvNodata)
        qr_req_invoice = view.findViewById(R.id.qr_req_invoice)
        qr_req_ref = view.findViewById(R.id.qr_req_ref)
        qr_req_qty = view.findViewById(R.id.qr_req_qty)
        qr_req_custName = view.findViewById(R.id.qr_req_custName)
        gson = Gson()

        val layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView!!.layoutManager = layoutManager

        /* mRecyclerView!!.addItemDecoration(
             DividerItemDecoration(
                 mRecyclerView!!.getContext(),
                 DividerItemDecoration.VERTICAL
             )
         )*/

    }

    private fun GetQrRequestList(mobile: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.QrRequestList(mobile).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.e("ws Data Req", string)

                    if (!string.equals("{}")) {
                        mRecyclerView!!.visibility = View.VISIBLE
                        tvNodata!!.visibility = View.GONE

                        jsonObject = JSONArray(string)

                        val saledatalist = gson!!.fromJson<ArrayList<QrData>>(
                            jsonObject.toString(),
                            object : TypeToken<ArrayList<QrData>>() {

                            }.type
                        )
                        qrScanningAdapter =
                            QrScanningRequestListAdapter(
                                requireContext(),
                                saledatalist,
                                mRole,
                                mGatePassType
                            )
                        mRecyclerView!!.adapter = qrScanningAdapter

                    } else {
                        mRecyclerView!!.visibility = View.GONE
                        tvNodata!!.visibility = View.VISIBLE
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

    private fun GetQrRequestListGatepass(mobile: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.QrRequestListGatepass(mobile).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.e("ws Data Req gatepass", string)

                    if (!string.equals("{}")) {
                        mRecyclerView!!.visibility = View.VISIBLE
                        tvNodata!!.visibility = View.GONE

                        jsonObject = JSONArray(string)

                        val saledatalist = gson!!.fromJson<ArrayList<QrData>>(
                            jsonObject.toString(),
                            object : TypeToken<ArrayList<QrData>>() {

                            }.type
                        )
                        qrScanningAdapter =
                            QrScanningRequestListAdapter(requireContext(), saledatalist, mRole,mGatePassType)
                        mRecyclerView!!.adapter = qrScanningAdapter

                    } else {
                        mRecyclerView!!.visibility = View.GONE
                        tvNodata!!.visibility = View.VISIBLE
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

    private fun GetQrRequestListWH(mobile: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat_wh.QrRequestListWH(
            "live",
            mMobileNumber!!,
            Constants.WH_User,
            Constants.WH_pwd,
            mUID
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()
                    Log.e("wh_store_boy Data Req", string)

                    if (!string.equals("{}")) {
                        mRecyclerView!!.visibility = View.VISIBLE
                        tvNodata!!.visibility = View.GONE

                        jsonObject = JSONArray(string)

                        val saledatalist = gson!!.fromJson<ArrayList<QrData>>(
                            jsonObject.toString(),
                            object : TypeToken<ArrayList<QrData>>() {

                            }.type
                        )
                        qrScanningAdapter =
                            QrScanningRequestListAdapter(
                                requireContext(),
                                saledatalist,
                                mRole,
                                mGatePassType
                            )
                        mRecyclerView!!.adapter = qrScanningAdapter

                    } else {
                        mRecyclerView!!.visibility = View.GONE
                        tvNodata!!.visibility = View.VISIBLE
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

    private fun GetQrRequestListWHG(mobile: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat_wh.QrRequestListWHG("live", Constants.WH_User, Constants.WH_pwd)
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()
                        Log.e("wh_security Data Req", string)
                        if (!string.equals("{}")) {
                            mRecyclerView!!.visibility = View.VISIBLE
                            tvNodata!!.visibility = View.GONE

                            jsonObject = JSONArray(string)

                            val saledatalist = gson!!.fromJson<ArrayList<QrData>>(
                                jsonObject.toString(),
                                object : TypeToken<ArrayList<QrData>>() {

                                }.type
                            )
                            qrScanningAdapter =
                                QrScanningRequestListAdapter(
                                    requireContext(),
                                    saledatalist,
                                    mRole,
                                    mGatePassType
                                )
                            mRecyclerView!!.adapter = qrScanningAdapter

                        } else {
                            mRecyclerView!!.visibility = View.GONE
                            tvNodata!!.visibility = View.VISIBLE
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

        fun getTitle(mRole: String): String {
            if (mRole.contains("stores") || mRole.equals("counter") || mRole.equals("wh_store_boy")) {
                return "Requests"
            } else {
                return "Live"

            }
        }

        fun newInstance(): QrScanningRequestFragment {

            return QrScanningRequestFragment()
        }
    }
}