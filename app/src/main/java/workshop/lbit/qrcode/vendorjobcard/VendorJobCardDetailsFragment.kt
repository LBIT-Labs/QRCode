package workshop.lbit.qrcode.vendorjobcard

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_vendor_jobcard_details.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.jobcard.JobCardDetailsFragment
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.utils.Utilities

class VendorJobCardDetailsFragment : Fragment() {

    lateinit var sp_vendor_search_vendor: Spinner
    lateinit var sp_vendor_search_jobid: Spinner
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
    var mCustomerMobile: String = ""
    var mTechnician: String = ""
    private var isLoaded = false
    private var isVisibleToUser = false

    internal var mMobileNumber: String? = null
    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var mRole: String = ""
    private lateinit var dict_data: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
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

        if(mVendorNid.isNotEmpty() && mVendor.isNotEmpty() && mJobCard.isNotEmpty()){

            SetVendorAndJobCardID()
        }else{
            getVendorList()

            getJObCardList()
        }


    }

    private fun SetVendorAndJobCardID() {

        getVendorList()

        getJObCardList()

        getDetails()
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

                            getDetails()
                        }
                    } else {
                        mVendor = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        sp_vendor_search_jobid.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mJobCard = sp_vendor_search_jobid.selectedItem.toString()

                        if (mJobCard.isNotEmpty() && mVendor.isNotEmpty()) {

                            dict_data.put("VendorJobCardCustID", "")
                            dict_data.put("Vendor", "")
                            dict_data.put("VendorJobCardID", "")
                            UserSession(requireContext()).setLoginDetails(dict_data.toString())

                            getDetails()
                        }
                    } else {
                        mJobCard = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

    }

    private fun getDetails() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getVendorDetails(mMobileNumber.toString(),mJobCard,"","job_details").enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Details", string)


                    val jsonObject = JSONObject(string)
                    mVendorNid = jsonObject.getString("nid").toString()
                    mCustomerName = jsonObject.getString("customer").toString()
                    mJobNumber = jsonObject.getString("job").toString()
                    mRegNo = jsonObject.getString("reg").toString()
                    mCustomerMobile= jsonObject.getString("mobile").toString()
                    mTechnician = jsonObject.getString("tech").toString()

                    tv_vender_jobcard_number.text = mJobNumber
                    tv_vender_vehicle_number.text = mRegNo
                    tv_vender_customer_name.text = mCustomerName
                    tv_vender_customer_mobile.text = mCustomerMobile
                    tv_vender_Technician_name.text = mTechnician
                    tv_vender_service_start_time.text = ""

                    dict_data.put("VendorJobCardCustID", mVendorNid)
                    dict_data.put("Vendor", mVendor)
                    dict_data.put("VendorJobCardID", mJobCard)
                    UserSession(requireContext()).setLoginDetails(dict_data.toString())


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

    private fun getJObCardList() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getVendorDetails(mMobileNumber.toString(),"","","jobcard_id").enqueue(object :
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

                    mJonCardList = Utilities.getItemList(list, string)

                    if (mJobCard.isNotEmpty()) {
                        if (mJonCardList.indexOf(mJobCard) > -1) {
                            sp_vendor_search_jobid.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                mJonCardList
                            )
                            sp_vendor_search_jobid.setSelection(mJonCardList.indexOf(mJobCard))
                        }
                    } else {
                        sp_vendor_search_jobid.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, mJonCardList
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

    private fun getVendorList() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getVendorDetails(mMobileNumber.toString(),"","","vendor").enqueue(object :
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
        sp_vendor_search_jobid = v.findViewById(R.id.sp_vendor_search_jobid)

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