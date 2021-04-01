package workshop.lbit.qrcode.vendorjobcard

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.utils.Utilities

class VendorJobCardDetailsFragment : Fragment() {

    lateinit var sp_vendor_search_vendor: Spinner
    lateinit var av_reg: AutoCompleteTextView
    lateinit var tv_vender_jobcard_number: MyTextView_Roboto_Medium
    lateinit var tv_vender_vehicle_number: MyTextView_Roboto_Medium
    lateinit var tv_vender_customer_name: MyTextView_Roboto_Medium
    lateinit var tv_vender_customer_mobile: MyTextView_Roboto_Medium
    lateinit var tv_vender_Technician_name: MyTextView_Roboto_Medium
    lateinit var tv_vender_service_start_time: MyTextView_Roboto_Medium

    private var mVendorList = java.util.ArrayList<String>()
    private var mJonCardList = java.util.ArrayList<String>()

    var mVendor: String = ""
    var mJobCard: String = ""
    var mCustomerName: String = ""
    var mVendorNid: String = ""
    var mJobNumber: String = ""
    var mRegNo: String = ""
    var mSearchRegNo: String = ""
    var mCustomerMobile: String = ""
    var mTechnician: String = ""
    private var isLoaded = false
    private var isVisibleToUser = false
    private var regList = java.util.ArrayList<String>()

    internal var mMobileNumber: String? = null
    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var mRole: String = ""
    private lateinit var dict_data: JSONObject

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

        if (mVendorNid.isNotEmpty() && mVendor.isNotEmpty() && mJobCard.isNotEmpty()) {

            SetVendorAndJobCardID()
        } else {
            getVendorList("")

            GetRegNo()
        }


    }

    private fun SetVendorAndJobCardID() {

        getVendorList("")


        if (mSearchRegNo.isNotEmpty()) {
            GetRegNo()

            getDetails(mSearchRegNo)

        }
    }

    private fun GetRegNo() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.GetFiltersData("reg", "", "vendor")
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()
                        Log.d("Reg", string)

                        if (!string.equals("{}")) {

                            val mStates = ArrayList<String>()
                            regList = Utilities.getItemList(mStates, string)

                            if (mSearchRegNo.isNotEmpty()) {
                                if (regList.indexOf(mSearchRegNo) > -1) {
                                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.select_dialog_item,
                                        regList
                                    )
                                    av_reg.setAdapter(adapter)
                                    av_reg.setText(mSearchRegNo)
                                    av_reg.threshold = 1
                                    av_reg.setTextColor(Color.BLACK)
                                } else {
                                    av_reg.setText(mSearchRegNo)
                                }
                            } else {
                                val adapter: ArrayAdapter<String> = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.select_dialog_item, regList
                                )
                                av_reg.threshold = 1
                                av_reg.setAdapter(adapter)
                                av_reg.setTextColor(Color.BLACK)
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_vendor_jobcard_details, container, false)

        sharedpreferences = activity!!.getSharedPreferences(
            Constants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")


        dict_data = JSONObject()
        val logindata = UserSession(requireContext()).getLoginDetails()

        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
            mVendorNid = dict_data.optString("VendorJobCardCustID")
            mVendor = dict_data.optString("Vendor")
            mJobCard = dict_data.optString("VendorJobCardID")
            mSearchRegNo = dict_data.optString("RegNo")

            UserSession(requireContext()).setLoginDetails(dict_data.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }


        init(v)

        setValues()
        return v
    }

    private fun setValues() {
        sp_vendor_search_vendor.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mVendor = sp_vendor_search_vendor.selectedItem.toString()

                        if (mJobCard.isNotEmpty() && mVendor.isNotEmpty()) {
                            dict_data.put("VendorJobCardCustID", "")
                            dict_data.put("Vendor", "")
                            dict_data.put("VendorJobCardID", "")
                            UserSession(requireContext()).setLoginDetails(dict_data.toString())

                            getDetails(mSearchRegNo)
                        }
                    } else {
                        mVendor = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        av_reg.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mSearchRegNo = parent.getItemAtPosition(position).toString()
                hideKeyboard()
                av_reg.clearFocus()

                getDetails(mSearchRegNo)

            }

        av_reg.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()

                if (text.length == 10) {

                    getDetails(text)

                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

//        sp_vendor_search_jobid.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    arg0: AdapterView<*>,
//                    view: View,
//                    arg2: Int,
//                    arg3: Long
//                ) {
//
//                    if (arg2 > 0) {
//                        mJobCard = sp_vendor_search_jobid.selectedItem.toString()
//
//                        if (mJobCard.isNotEmpty() && mVendor.isNotEmpty()) {
//
//                            dict_data.put("VendorJobCardCustID", "")
//                            dict_data.put("Vendor", "")
//                            dict_data.put("VendorJobCardID", "")
//                            UserSession(requireContext()).setLoginDetails(dict_data.toString())
//
//                            getDetails(mSearchRegNo)
//                        }
//                    } else {
//                        mJobCard = ""
//                    }
//                }
//
//                override fun onNothingSelected(arg0: AdapterView<*>) {
//
//                }
//            }

    }

    private fun hideKeyboard() {
        val view = requireActivity().currentFocus
        view?.let { v ->
            val imm =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
        }
    }

    private fun getDetails(mSearchRegNo: String) {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getVendorDetails(
            mMobileNumber.toString(),
            mJobCard,
            "",
            "job_details",
            mSearchRegNo
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Details", mJobCard + " " + mSearchRegNo + "" + string)


                    if (!string.equals("{}")) {

                        val jsonObject = JSONObject(string)
                        mVendorNid = jsonObject.getString("nid").toString()
                        mCustomerName = jsonObject.getString("customer").toString()
                        mJobNumber = jsonObject.getString("job").toString()
                        mRegNo = jsonObject.getString("reg").toString()
                        mCustomerMobile = jsonObject.getString("mobile").toString()
                        mTechnician = jsonObject.getString("tech").toString()

                        tv_vender_jobcard_number.text = mJobNumber
                        tv_vender_vehicle_number.text = mRegNo
                        tv_vender_customer_name.text = mCustomerName
                        tv_vender_customer_mobile.text = mCustomerMobile
                        tv_vender_Technician_name.text = mTechnician
                        tv_vender_service_start_time.text = ""

                        dict_data.put("VendorJobCardCustID", mVendorNid)
                        dict_data.put("Vendor", mVendor)
                        dict_data.put("VendorJobCardID", mJobNumber)
                        dict_data.put("CustomerName", mCustomerName)
                        dict_data.put("RegNo", mRegNo)
                        dict_data.put("CustomerMobile", mCustomerMobile)
                        dict_data.put("Screen", "Live")
                        UserSession(requireContext()).setLoginDetails(dict_data.toString())
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

//    private fun getJObCardList() {
//
//        val mProgressDialog = ProgressDialog(requireContext())
//        mProgressDialog.isIndeterminate = true
//        mProgressDialog.setMessage("Loading...")
//        mProgressDialog.show()
//        Constants.qrCode_uat.getVendorDetails(mMobileNumber.toString(),"","","jobcard_id").enqueue(object :
//            Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: Response<ResponseBody>
//            ) {
//
//                try {
//                    val string = response.body()!!.string()
//
//                    Log.e("Make", string)
//
//
//                    val list = ArrayList<String>()
//
//                    Log.e("Test", "Make_List: $string")
//
//                    mJonCardList = Utilities.getItemList(list, string)
//
//                    if (mJobCard.isNotEmpty()) {
//                        if (mJonCardList.indexOf(mJobCard) > -1) {
//                            sp_vendor_search_jobid.adapter = ArrayAdapter(
//                                requireContext(),
//                                android.R.layout.simple_spinner_dropdown_item,
//                                mJonCardList
//                            )
//                            sp_vendor_search_jobid.setSelection(mJonCardList.indexOf(mJobCard))
//                        }
//                    } else {
//                        sp_vendor_search_jobid.adapter = ArrayAdapter(
//                            requireContext(),
//                            android.R.layout.simple_spinner_dropdown_item, mJonCardList
//                        )
//                    }
//                    mProgressDialog.dismiss()
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.e(
//                    "TAG",
//                    "onFailure() called with: call = [" + call.request()
//                        .url() + "], t = [" + t + "]",
//                    t
//                )
//
//                if (mProgressDialog.isShowing)
//                    mProgressDialog.dismiss()
//            }
//        })
//    }

    private fun getVendorList(reg: String) {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getVendorDetails(mMobileNumber.toString(), "", "", "vendor", reg)
            .enqueue(object :
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

                        mVendorList = Utilities.getItemList(list, string)

                        if (mVendor.isNotEmpty()) {
                            if (mVendorList.indexOf(mVendor) > -1) {
                                sp_vendor_search_vendor.adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    mVendorList
                                )
                                sp_vendor_search_vendor.setSelection(mVendorList.indexOf(mVendor))
                            }
                        } else {
                            sp_vendor_search_vendor.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item, mVendorList
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

    private fun init(v: View) {
        sp_vendor_search_vendor = v.findViewById(R.id.sp_vendor_search_vendor)
        av_reg = v.findViewById(R.id.av_reg)

        tv_vender_jobcard_number = v.findViewById(R.id.tv_vender_jobcard_number)
        tv_vender_vehicle_number = v.findViewById(R.id.tv_vender_vehicle_number)
        tv_vender_customer_name = v.findViewById(R.id.tv_vender_customer_name)
        tv_vender_customer_mobile = v.findViewById(R.id.tv_vender_customer_mobile)
        tv_vender_Technician_name = v.findViewById(R.id.tv_vender_Technician_name)
        tv_vender_service_start_time = v.findViewById(R.id.tv_vender_service_start_time)

    }

    fun allRequiredDataAvailable(): Boolean {

//        if (mCustomerName.isEmpty()) {
//
//            Toast.makeText(
//                requireContext(),
//                "Vehicle Details Should not be Empty",
//                Toast.LENGTH_LONG
//            ).show()
//            return false
//        }
//
//        if (mCustomerMobile.isEmpty()) {
//
//            Toast.makeText(
//                requireContext(),
//                "Vehicle Details Should not be Empty",
//                Toast.LENGTH_LONG
//            ).show()
//            return false
//        }

        return true
    }


    companion object {

        val TITLE = "Vehicle Details"

        fun newInstance(): VendorJobCardDetailsFragment {
            return VendorJobCardDetailsFragment()
        }
    }
}