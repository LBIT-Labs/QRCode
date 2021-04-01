package workshop.lbit.qrcode.jobcard

import android.annotation.SuppressLint
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.helper.ExpandableButton
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.utils.Utilities
import java.util.regex.Pattern


@SuppressLint("ValidFragment")
class JobCardDetailsFragment @SuppressLint("ValidFragment") constructor() : Fragment() {


    lateinit var tv_vehicle_reg: MyTextView_Roboto_Bold
    lateinit var tv_vehicle_make: MyTextView_Roboto_Bold
    lateinit var tv_vehicle_model: MyTextView_Roboto_Bold
    lateinit var tv_vehicle_driver: MyTextView_Roboto_Bold
    lateinit var tv_vehicle_driver_mobile: MyTextView_Roboto_Bold

    lateinit var tv_customer_name: MyTextView_Roboto_Bold
    lateinit var tv_customer_mobile: MyTextView_Roboto_Bold
    lateinit var tv_customer_address: MyTextView_Roboto_Bold
    lateinit var tv_customer_paymentType: MyTextView_Roboto_Bold
    lateinit var tv_customer_gst_applicable: MyTextView_Roboto_Bold
    lateinit var tv_customer_pincode_txt: MyTextView_Roboto_Bold

    lateinit var tv_workshop_supervisor_mobile: MyTextView_Roboto_Bold
    lateinit var tv_workshop_supervisor: MyTextView_Roboto_Bold
    lateinit var tv_workshop_technician: MyTextView_Roboto_Bold

    lateinit var ll_search: LinearLayout
    lateinit var eb_jabcard_vehicleDetails: ExpandableButton

    /*Vehicle Details*/

    lateinit var av_reg: AutoCompleteTextView
    lateinit var et_vehicle_variant: EditText
    lateinit var et_vehicle_kms_driven: EditText
    lateinit var et_vehicle_engine_no: EditText
    lateinit var et_vehicle_chassis_no: EditText
    lateinit var et_vehicle_incharge_mobile: EditText
    lateinit var et_vehicle_incharge: EditText
    lateinit var et_vehicle_driver: EditText
    lateinit var et_vehicle_driver_mobile: EditText

    lateinit var sp_vehicle_make: Spinner
    lateinit var sp_location: Spinner
    lateinit var tv_location_txt: MyTextView_Roboto_Bold
    lateinit var sp_vehicle_model: Spinner
    lateinit var sp_vehicle_number_plate_colour: Spinner
    lateinit var sp_vehicle_mfg: Spinner
    lateinit var sp_vehicle_insurance: Spinner

    var mVehicleRegNumber: String = ""
    var mVehicleVariant: String = ""
    var mVehicleKmsDriven: String = ""
    var mVehicleEngineNo: String = ""
    var mVehicleChassisNo: String = ""
    var mVehicleIncharge: String = ""
    var mVehicleInchargeMobile: String = ""
    var mVehicleDriver: String = ""
    var mVehicleDriverMobile: String = ""

    var mVehicleMake: String = ""
    var mVehicleModel: String = ""
    var mvehicleNumberPlateColour: String = ""
    var mVehicleMfg: String = ""
    var mVehicleInsurance: String = ""
    var mVehicleLocation: String = ""

    /*Customer Details*/

    lateinit var et_customer_name: EditText
    lateinit var et_customer_mobile: EditText
    lateinit var et_customer_email: EditText
    lateinit var et_customer_address: EditText
    lateinit var et_customer_pincode: EditText
    lateinit var et_customer_gstin: EditText
    lateinit var ll_gstin: LinearLayout
    lateinit var ll_crn: LinearLayout

    lateinit var tv_customer_crn: MyTextView_Roboto_Medium
    lateinit var tv_customer_paymentType_value: MyTextView_Roboto_Medium

    //    lateinit var sp_customer_city: Spinner
    lateinit var av_city: AutoCompleteTextView
    lateinit var av_state: AutoCompleteTextView
    lateinit var sp_customer_payment_type: Spinner
    lateinit var sp_customer_gst_applicable: Spinner

    var mCustomerName: String = ""
    var mCustomerMobile: String = ""
    var mCustomerEmail: String = ""
    var mJObCardNid: String = ""
    var mJObCardScreen: String = ""
    var screen: String = ""
    var mCustomerAddress: String = ""
    var mCustomerPincode: String = ""
    var mCustomerGSTIN: String = ""

    var mCustomerCRN: String = ""

    var mCustomerCity: String = ""
    var mCustomerState: String = ""
    var mCustomerPaymentType: String = "Cash"
    var mCustomerGSTApplicable: String = ""

    /*Workshop Details*/

    lateinit var et_workshop_supervisor_mobile: EditText

    lateinit var sp_workshop_supervisor: Spinner
    lateinit var sp_workshop_technician: Spinner

    var mWorkshopTechnician: String = ""
    var mWorkshopSupervisor: String = ""
    var mWorkshopSupervisorMobile: String = ""

    private var makeList = java.util.ArrayList<String>()
    private var cityList = java.util.ArrayList<String>()
    private var stateList = java.util.ArrayList<String>()
    private var regList = java.util.ArrayList<String>()
    private var regListVendor = java.util.ArrayList<String>()
    private var modelList = java.util.ArrayList<String>()

    internal var mMobileNumber: String? = null
    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var mRole: String = ""
    private lateinit var dict_data: JSONObject
    private var isLoaded = false
    private var isVisibleToUser = false
    var mobileVerify: Boolean = false
    var pattern = "[6-9]{1}"

    internal var expandableButtonListener: ExpandableButton.ExpandableButtonListener? = null

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (isVisibleToUser && isAdded) {
            dict_data = JSONObject()
            val logindata = UserSession(requireContext()).getLoginDetails()

            try {
                dict_data = JSONObject(logindata)
                mRole = dict_data.optString("role")
                mCustomerMobile = dict_data.optString("CustomerMobile")
                mJObCardNid = dict_data.optString("JobCardCustID")
                mJObCardScreen = dict_data.optString("screenType")
                screen = dict_data.optString("Screen")


            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (screen.isNotEmpty()) {
                if (screen.equals("Live")) {
                    ll_search.visibility = View.GONE

                } else if (screen.equals("New")) {
                    ll_search.visibility = View.VISIBLE
                }
            }

            if (mCustomerMobile.isNotEmpty() && mJObCardNid.isNotEmpty()) {

                getDetailsWithMobile(mCustomerMobile, mJObCardNid, "")

            } else {
                loadData()

            }
            isLoaded = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isVisibleToUser && (!isLoaded)) {
            dict_data = JSONObject()
            val logindata = UserSession(requireContext()).getLoginDetails()

            try {
                dict_data = JSONObject(logindata)
                mRole = dict_data.optString("role")
                mCustomerMobile = dict_data.optString("CustomerMobile")
                mJObCardNid = dict_data.optString("JobCardCustID")
                mJObCardScreen = dict_data.optString("screenType")
                screen = dict_data.optString("Screen")

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (screen.isNotEmpty()) {
                if (screen.equals("Live")) {
                    ll_search.visibility = View.GONE

                } else if (screen.equals("New")) {
                    ll_search.visibility = View.VISIBLE
                }
            }
            if (mCustomerMobile.isNotEmpty() && mJObCardNid.isNotEmpty()) {

                getDetailsWithMobile(mCustomerMobile, mJObCardNid, "")

            } else {
                loadData()

            }
            isLoaded = true
        }
    }

    private fun loadData() {
        GetLocation()

        GetMakeList()

        GetCitiesList()

        GetStatesList()

        GetRegNo()

        GetRegNoVendor()

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_jobcard_deatils, container, false)

        sharedpreferences = activity!!.getSharedPreferences(
            Constants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")

        init(v)


        MandatoryReg(resources.getString(R.string.vehicle_reg))
        MandatoryMake(resources.getString(R.string.make))
        MandatoryModel(resources.getString(R.string.model))
        MandatoryDriver(resources.getString(R.string.driver))
        MandatoryDriverMobile(resources.getString(R.string.driver_mobile))
        MandatoryCustomerName(resources.getString(R.string.customer_name))
        MandatoryCustomerMobile(resources.getString(R.string.mobile_number))
        MandatoryCustomerAddress(resources.getString(R.string.address))
        MandatoryCustomerPaymentType(resources.getString(R.string.payment_type))
        MandatoryCustomerGST(resources.getString(R.string.gst))
        MandatoryPincode(resources.getString(R.string.pincode))
        MandatoryLocation(resources.getString(R.string.location))

        MandatoryTechnician(resources.getString(R.string.technician))
        MandatorySupervisor(resources.getString(R.string.supervisor))
        MandatorySupervisorMobile(resources.getString(R.string.supervisor_mobile))

        SetValues()

        return v
    }

    private fun getDetailsWithMobile(mobile: String, mJObCardNid: String, reg: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getDetails(mobile, mJObCardNid, reg).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Mobile", string)

                    if (!string.equals("{}")) {

                        val jsonArray = JSONArray(string)
                        val jsonObject = jsonArray.getJSONObject(0)

                        mVehicleRegNumber = jsonObject.getString("reg").toString()
                        mCustomerName = jsonObject.getString("customer_name").toString()
                        mCustomerMobile = jsonObject.getString("phone_number").toString()
                        mCustomerAddress = jsonObject.getString("cus_address").toString()
                        mCustomerCity = jsonObject.getString("cus_city").toString()
                        mCustomerCRN = jsonObject.getString("customer_crn").toString()
                        mCustomerEmail = jsonObject.getString("customer_email").toString()
                        mCustomerPincode = jsonObject.getString("pincode").toString()
                        mCustomerGSTIN = jsonObject.getString("gstin").toString()
                        mCustomerGSTApplicable = jsonObject.getString("gst_applicable").toString()
                        mVehicleChassisNo = jsonObject.getString("chassis_num").toString()
                        mVehicleEngineNo = jsonObject.getString("engine_num").toString()
                        mCustomerState = jsonObject.getString("cus_state").toString()
                        mVehicleMake = jsonObject.getString("cus_make").toString()
                        mVehicleModel = jsonObject.getString("cus_model").toString()
                        mVehicleVariant = jsonObject.getString("cus_variant").toString()
                        mCustomerPaymentType = jsonObject.getString("payment_type").toString()
                        mVehicleInsurance = jsonObject.getString("insurance").toString()
                        mVehicleMfg = jsonObject.getString("mfg_year").toString()
                        mvehicleNumberPlateColour = jsonObject.getString("num_plt_color").toString()
                        mVehicleKmsDriven = jsonObject.getString("kms_driven").toString()
                        mVehicleLocation = jsonObject.getString("location").toString()

                        mWorkshopSupervisorMobile = jsonObject.getString("sup_mobile").toString()
                        mVehicleDriverMobile = jsonObject.getString("driver_mobile").toString()
                        mVehicleDriver = jsonObject.getString("driver").toString()
                        mVehicleIncharge = jsonObject.getString("veh_incharge").toString()
                        mVehicleInchargeMobile = jsonObject.getString("incharge_mob").toString()
                        mWorkshopSupervisor = jsonObject.getString("supervisor").toString()
                        mWorkshopTechnician = jsonObject.getString("technician").toString()


                        if (!mCustomerName.equals("null")) {
                            et_customer_name.setText(mCustomerName)
                        }


                        if (!mVehicleDriver.equals("null")) {
                            et_vehicle_driver.setText(mVehicleDriver)
                        }
                        if (!mCustomerMobile.equals("null")) {
                            et_customer_mobile.setText(mCustomerMobile)
                        }

                        if (!mCustomerAddress.equals("null")) {
                            et_customer_address.setText(mCustomerAddress)
                        }

                        if (!mCustomerEmail.equals("null")) {
                            et_customer_email.setText(mCustomerEmail)
                        }

                        if (!mCustomerPincode.equals("null")) {
                            et_customer_pincode.setText(mCustomerPincode)
                        }

                        if (!mCustomerGSTIN.equals("null")) {
                            et_customer_gstin.setText(mCustomerGSTIN)
                        }

                        if (!mVehicleEngineNo.equals("null")) {
                            et_vehicle_engine_no.setText(mVehicleEngineNo)
                        }

                        if (!mVehicleChassisNo.equals("null")) {
                            et_vehicle_chassis_no.setText(mVehicleChassisNo)
                        }

                        if (!mVehicleDriverMobile.equals("null")) {
                            et_vehicle_driver_mobile.setText(mVehicleDriverMobile)
                        }

                        if (!mVehicleIncharge.equals("null")) {
                            et_vehicle_incharge.setText(mVehicleIncharge)
                        }

                        if (!mVehicleInchargeMobile.equals("null")) {
                            et_vehicle_incharge_mobile.setText(mVehicleInchargeMobile)
                        }

                        if (!mWorkshopSupervisorMobile.equals("null")) {
                            et_workshop_supervisor_mobile.setText(mWorkshopSupervisorMobile)
                        }

                        if (!mVehicleKmsDriven.equals("null")) {
                            et_vehicle_kms_driven.setText(mVehicleKmsDriven)
                        }

                        if (!mCustomerCRN.equals("null")) {
                            ll_crn.visibility = View.VISIBLE
                            tv_customer_crn.text = mCustomerCRN
                        }

                        if (!mCustomerPaymentType.equals("null")) {
                            if (mCustomerPaymentType.isNotEmpty()) {
                                tv_customer_paymentType_value.text = mCustomerPaymentType
                            } else {
                                mCustomerPaymentType = "Cash"
                                tv_customer_paymentType_value.text = mCustomerPaymentType

                            }
                        }

                        if (!mVehicleInsurance.equals("null") && mVehicleInsurance.isNotEmpty()) {
                            val list = resources.getStringArray(R.array.insurance_array).asList()
                            if (list.indexOf(mVehicleInsurance) > -1) {
                                sp_vehicle_insurance.setSelection(list.indexOf(mVehicleInsurance))

                            }
                        }

                        if (!mCustomerGSTApplicable.equals("null") && mCustomerGSTApplicable.isNotEmpty()) {
                            val list = resources.getStringArray(R.array.gst_array).asList()
                            if (list.indexOf(mCustomerGSTApplicable) > -1) {
                                sp_customer_gst_applicable.setSelection(
                                    list.indexOf(
                                        mCustomerGSTApplicable
                                    )
                                )

                            }
                        }

                        if (!mVehicleMfg.equals("null") && mVehicleMfg.isNotEmpty()) {
                            val list = resources.getStringArray(R.array.mfg_array).asList()
                            if (list.indexOf(mVehicleMfg) > -1) {
                                sp_vehicle_mfg.setSelection(list.indexOf(mVehicleMfg))

                            }
                        }

                        if (!mvehicleNumberPlateColour.equals("null") && mvehicleNumberPlateColour.isNotEmpty()) {
                            val list =
                                resources.getStringArray(R.array.number_plat_colour_array).asList()
                            if (list.indexOf(mvehicleNumberPlateColour) > -1) {
                                sp_vehicle_number_plate_colour.setSelection(
                                    list.indexOf(
                                        mvehicleNumberPlateColour
                                    )
                                )

                            }
                        }

                        if (!mWorkshopSupervisor.equals("null")) {
                            val list = resources.getStringArray(R.array.supervisor_array).asList()
                            if (list.indexOf(mWorkshopSupervisor) > -1) {
                                sp_workshop_supervisor.setSelection(
                                    list.indexOf(
                                        mWorkshopSupervisor
                                    )
                                )

                            }
                        }

                        if (!mWorkshopTechnician.equals("null")) {
                            val list = resources.getStringArray(R.array.technician_array).asList()
                            if (list.indexOf(mWorkshopTechnician) > -1) {
                                sp_workshop_technician.setSelection(
                                    list.indexOf(
                                        mWorkshopTechnician
                                    )
                                )

                            }
                        }

                        if (!mVehicleVariant.equals("null")) {
                            et_vehicle_variant.setText(mVehicleVariant)
                        }

                        if (!mCustomerState.equals("null") && mCustomerState.isNotEmpty()) {
                            Log.d("makelist", stateList.toString())

                            GetStatesList()

                        }

                        if (!mVehicleRegNumber.equals("null") && mCustomerState.isNotEmpty()) {

                            GetRegNo()
                        }


                        if (!mCustomerCity.equals("null") && mCustomerCity.isNotEmpty()) {
                            Log.d("makelist", cityList.toString())
                            av_city.setText(mCustomerCity)

                            GetCitiesList()


                        }

                        if (!mVehicleMake.equals("null") && mVehicleMake.isNotEmpty()) {
                            Log.d("makelist", makeList.toString())
                            if (makeList.size > 0) {
                                if (makeList.indexOf(mVehicleMake) > -1) {
                                    sp_vehicle_make.setSelection(makeList.indexOf(mVehicleMake))
                                }
                            } else {
                                GetMakeList()

                            }
                        }

                        if (!mVehicleModel.equals("null") && mVehicleModel.isNotEmpty()) {
                            Log.d("makelist", modelList.toString())
                            if (modelList.size > 0) {
                                if (modelList.indexOf(mVehicleMake) > -1) {
                                    sp_vehicle_model.setSelection(modelList.indexOf(mVehicleModel))
                                }
                            } else {
                                GetModelList(mVehicleMake)

                            }
                        }

                        if (!mVehicleLocation.equals("null") && mVehicleLocation.isNotEmpty()) {
                            GetLocation()
                        }

                    } else {
                        Toast.makeText(requireContext(), "No Record Found", Toast.LENGTH_LONG)
                            .show()
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

    private fun GetMakeList() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getMakeList("").enqueue(object :
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

                    makeList = Utilities.getItemList(list, string)

                    if (mVehicleMake.isNotEmpty()) {
                        if (makeList.indexOf(mVehicleMake) > -1) {
                            sp_vehicle_make.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                makeList
                            )
                            sp_vehicle_make.setSelection(makeList.indexOf(mVehicleMake))
                        }
                    } else {
                        sp_vehicle_make.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, makeList
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

    private fun GetCitiesList() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.GetFiltersData("cities", "")
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()
                        Log.d("city's", string)

                        if (!string.equals("{}")) {


                            val mCities = ArrayList<String>()


                            cityList = Utilities.getItemList(mCities, string)

                            if (mCustomerCity.isNotEmpty()) {
                                if (cityList.indexOf(mCustomerCity) > -1) {
                                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.select_dialog_item,
                                        cityList
                                    )
                                    av_city.setAdapter(adapter)
                                    av_city.setText(mCustomerCity)
                                    av_city.threshold = 1
                                    av_city.setTextColor(Color.BLACK)
                                }
                            } else {
                                val adapter: ArrayAdapter<String> = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.select_dialog_item, cityList
                                )
                                av_city.threshold = 1
                                av_city.setAdapter(adapter)
                                av_city.setTextColor(Color.BLACK)
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

    private fun GetStatesList() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("workshop.lbit.qrcode.scroll.MyNestedScrollView...")
        mProgressDialog.show()
        Constants.qrCode_uat.GetFiltersData("state", "")
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        Log.d("State", string)

                        if (!string.equals("{}")) {

                            val mStates = ArrayList<String>()
                            stateList = Utilities.getItemList(mStates, string)

                            if (mCustomerState.isNotEmpty()) {
                                if (stateList.indexOf(mCustomerState) > -1) {
                                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.select_dialog_item,
                                        stateList
                                    )
                                    av_state.setAdapter(adapter)
                                    av_state.setText(mCustomerState)
                                    av_state.threshold = 1
                                    av_state.setTextColor(Color.BLACK)
                                }
                            } else {
                                val adapter: ArrayAdapter<String> = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.select_dialog_item, stateList
                                )
                                av_state.threshold = 1
                                av_state.setAdapter(adapter)
                                av_state.setTextColor(Color.BLACK)
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

    private fun GetRegNo() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.GetFiltersData("reg", "", "jobcard")
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

                            if (mVehicleRegNumber.isNotEmpty()) {
                                if (regList.indexOf(mVehicleRegNumber) > -1) {
                                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.select_dialog_item,
                                        regList
                                    )
                                    av_reg.setAdapter(adapter)
                                    av_reg.setText(mVehicleRegNumber)
                                    av_reg.threshold = 1
                                    av_reg.setTextColor(Color.BLACK)
                                } else {
                                    av_reg.setText(mVehicleRegNumber)
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

    private fun GetRegNoVendor() {
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
                        Log.d("RegVendor", string)

                        if (!string.equals("{}")) {

                            val mStates = ArrayList<String>()
                            regListVendor = Utilities.getItemList(mStates, string)
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

    private fun VerifyMobile(text: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.VerifyMobile("user_mobile", text)
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()
                        Log.d("RegVendor", string)

                        if (string.contains("False")) {

                            mobileVerify = true
                            Toast.makeText(
                                requireContext(),
                                "Customer Mobile should not be user mobile",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            mProgressDialog.dismiss()

                            getDetailsWithMobile(text, "", "")

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

    private fun GetLocation() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.GetLocation(mMobileNumber.toString())
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        Log.d("Location", string)
                        if (!string.equals("{}")) {

                            val mStates = ArrayList<String>()
                            val list = Utilities.getItemList(mStates, string)

                            if (mVehicleLocation.isNotEmpty()) {
                                if (list.indexOf(mVehicleLocation) > -1) {
                                    sp_location.adapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        list
                                    )
                                    sp_location.setSelection(modelList.indexOf(mVehicleLocation))
                                }
                            } else {
                                sp_location.adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item, list
                                )
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


    private fun GetModelList(mVehicleMake: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getMakeList(mVehicleMake).enqueue(object :
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

                    modelList = Utilities.getItemList(list, string)
                    Log.e("Test", "Make_List: $list")

                    if (mVehicleModel.isNotEmpty()) {
                        if (modelList.indexOf(mVehicleMake) > -1) {
                            sp_vehicle_model.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                modelList
                            )
                            sp_vehicle_model.setSelection(modelList.indexOf(mVehicleModel))
                        }
                    } else {
                        sp_vehicle_model.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, modelList
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

    fun SetValues() {


        sp_vehicle_make.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mVehicleMake = sp_vehicle_make.selectedItem.toString()

                        GetModelList(mVehicleMake)
                    } else {
                        mVehicleMake = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        sp_location.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mVehicleLocation = sp_location.selectedItem.toString()

                    } else {
                        mVehicleLocation = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        sp_vehicle_model.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mVehicleModel = sp_vehicle_model.selectedItem.toString()
                    } else {
                        mVehicleModel = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        sp_vehicle_number_plate_colour.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mvehicleNumberPlateColour =
                            sp_vehicle_number_plate_colour.selectedItem.toString()
                    } else {
                        mvehicleNumberPlateColour = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        sp_vehicle_mfg.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {

                if (arg2 > 0) {
                    mVehicleMfg = sp_vehicle_mfg.selectedItem.toString()
                } else {
                    mVehicleMfg = ""
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        sp_vehicle_insurance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {

                if (arg2 > 0) {
                    mVehicleInsurance = sp_vehicle_insurance.selectedItem.toString()
                } else {
                    mVehicleInsurance = ""
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

//        sp_vehicle_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {
//
//                if (arg2 > 0) {
//                    mVehicleLocation = sp_vehicle_location.selectedItem.toString()
//                } else {
//                    mVehicleLocation = ""
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//
//            }
//        }
//
//        sp_customer_payment_type.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    arg0: AdapterView<*>,
//                    view: View,
//                    arg2: Int,
//                    arg3: Long
//                ) {
//
//                    if (arg2 > 0) {
//                        mCustomerPaymentType = sp_customer_payment_type.selectedItem.toString()
//                    } else {
//                        mCustomerPaymentType = ""
//                    }
//                }
//
//                override fun onNothingSelected(arg0: AdapterView<*>) {
//
//                }
//            }

        sp_customer_gst_applicable.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mCustomerGSTApplicable = sp_customer_gst_applicable.selectedItem.toString()
                        if (mCustomerGSTApplicable.equals("No")) {
                            ll_gstin.visibility = View.GONE
                        } else {
                            ll_gstin.visibility = View.VISIBLE

                        }
                    } else {
                        mCustomerGSTApplicable = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        sp_workshop_technician.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mWorkshopTechnician = sp_workshop_technician.selectedItem.toString()
                    } else {
                        mWorkshopTechnician = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

        av_city.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mCustomerCity = parent.getItemAtPosition(position).toString()
                hideKeyboard()
                av_city.clearFocus()

            }

        av_state.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mCustomerState = parent.getItemAtPosition(position).toString()
                hideKeyboard()
                av_state.clearFocus()

            }

        av_reg.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mVehicleRegNumber = parent.getItemAtPosition(position).toString()
                hideKeyboard()
                av_reg.clearFocus()

                getDetailsWithMobile("", "", mVehicleRegNumber)

            }

        av_reg.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()
                if (text.length == 10) {

                    mVehicleRegNumber = text

                    val pattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}")
                    val matcher = pattern.matcher(mVehicleRegNumber)
                    if (matcher.matches()) {

                        if (regListVendor.size > 0) {
                            if (regListVendor.indexOf(mVehicleRegNumber) > -1) {

                                Toast.makeText(
                                    requireContext(),
                                    "Reg Existing in live Jobcard",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Invalid Registation Number, Please Re-enter Register Number",
                            Toast.LENGTH_LONG
                        ).show()

                    }


                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        sp_workshop_supervisor.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mWorkshopSupervisor = sp_workshop_supervisor.selectedItem.toString()
                    } else {
                        mWorkshopSupervisor = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }


//        et_search.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(editable: Editable?) {
//                val text = editable.toString().trim()
//
//                if (text.length == 10) {
//
//                    getDetailsWithMobile(text, "", "")
//
//                }
//
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })

//        et_vehicle_reg_number.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(editable: Editable?) {
//                mVehicleRegNumber = editable.toString().trim()
//                if (mVehicleRegNumber.length == 10) {
//                    val pattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}")
//                    val matcher = pattern.matcher(mVehicleRegNumber)
//                    if (matcher.matches()) {
//
//                    } else {
//
//                        Toast.makeText(
//                            requireContext(),
//                            "Invalid Registation Number, Please Re-enter Register Number",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })

        et_vehicle_variant.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mVehicleVariant = editable.toString().trim()


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_vehicle_kms_driven.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()

                if (text.length == 8) {
                    mVehicleKmsDriven = text
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_vehicle_engine_no.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mVehicleEngineNo = editable.toString().trim()

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_vehicle_chassis_no.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mVehicleChassisNo = editable.toString().trim()

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_vehicle_incharge.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mVehicleIncharge = editable.toString().trim()

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_vehicle_incharge_mobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()

                if (text.length == 1) {
                    val pattern = Pattern.compile("[6-9]{1}")
                    val matcher = pattern.matcher(text)
                    if (matcher.matches()) {

                    } else {

                        et_vehicle_driver_mobile.setText("")

                    }
                }
                if (text.length == 10) {
                    mVehicleInchargeMobile = text
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_vehicle_driver.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mVehicleDriver = editable.toString().trim()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_vehicle_driver_mobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()

                if (text.length == 1) {
                    val pattern = Pattern.compile("[6-9]{1}")
                    val matcher = pattern.matcher(text)
                    if (matcher.matches()) {

                    } else {

                        et_vehicle_driver_mobile.setText("")

                    }
                }
                if (text.length == 10) {
                    mVehicleDriverMobile = text
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_customer_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mCustomerName = editable.toString().trim()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_customer_mobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()

                if (text.length == 1) {
                    val pattern = Pattern.compile("[6-9]{1}")
                    val matcher = pattern.matcher(text)
                    if (matcher.matches()) {

                    } else {

                        et_vehicle_driver_mobile.setText("")

                    }
                }

                if (text.length == 10) {
                    mCustomerMobile = text

                    VerifyMobile(text)
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_customer_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mCustomerEmail = editable.toString().trim()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_customer_address.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mCustomerAddress = editable.toString().trim()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_customer_pincode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()

                if (text.length == 6) {
                    mCustomerPincode = text
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_customer_gstin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mCustomerGSTIN = editable.toString().trim()
                if (mCustomerGSTIN.length == 15) {
                    val pattern =
                        Pattern.compile("[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9]{1}[A-Z]{1}[0-9]{1}")
                    val pattern1 =
                        Pattern.compile("[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9]{1}[A-Z]{1}[A-Z]{1}")
                    val matcher = pattern.matcher(mCustomerGSTIN)
                    val matcher1 = pattern1.matcher(mCustomerGSTIN)
                    if (matcher.matches()) {

                    } else if (matcher1.matches()) {

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Invalid GSTIN Number, Please Re-enter GSTIN Number",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })


        et_workshop_supervisor_mobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()

                if (text.length == 1) {
                    val pattern = Pattern.compile("[6-9]{1}")
                    val matcher = pattern.matcher(text)
                    if (matcher.matches()) {

                    } else {

                        et_vehicle_driver_mobile.setText("")

                    }
                }

                if (text.length == 10) {
                    mWorkshopSupervisorMobile = text
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    private fun hideKeyboard() {
        val view = requireActivity().currentFocus
        view?.let { v ->
            val imm =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
        }
    }


    private fun MandatoryReg(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_vehicle_reg.text = builder

    }

    private fun MandatoryMake(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_vehicle_make.text = builder

    }

    private fun MandatoryModel(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_vehicle_model.text = builder

    }

    private fun MandatoryDriver(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_vehicle_driver.text = builder

    }

    private fun MandatoryDriverMobile(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_vehicle_driver_mobile.text = builder

    }

    private fun MandatoryCustomerName(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_customer_name.text = builder

    }

    private fun MandatoryCustomerMobile(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_customer_mobile.text = builder

    }

    private fun MandatoryCustomerAddress(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_customer_address.text = builder

    }

    private fun MandatoryCustomerPaymentType(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_customer_paymentType.text = builder

    }

    private fun MandatoryCustomerGST(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_customer_gst_applicable.text = builder

    }

    private fun MandatoryTechnician(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_workshop_technician.text = builder

    }

    private fun MandatoryPincode(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_customer_pincode_txt.text = builder

    }

    private fun MandatoryLocation(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_location_txt.text = builder

    }

    private fun MandatorySupervisor(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_workshop_supervisor.text = builder

    }

    private fun MandatorySupervisorMobile(reg: String) {
        val colored = " *"
        val builder = SpannableStringBuilder()

        builder.append(reg)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length

        builder.setSpan(
            ForegroundColorSpan(Color.RED), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_workshop_supervisor_mobile.text = builder

    }

    fun init(v: View) {

        ll_search = v.findViewById(R.id.ll_search)
        eb_jabcard_vehicleDetails = v.findViewById(R.id.eb_jabcard_vehicleDetails)

        /* Text Fields to set the asteric mark*/
        tv_vehicle_reg = v.findViewById(R.id.tv_vehicle_reg)
        tv_vehicle_make = v.findViewById(R.id.tv_vehicle_make)
        tv_vehicle_model = v.findViewById(R.id.tv_vehicle_model)
        tv_vehicle_driver = v.findViewById(R.id.tv_vehicle_driver)
        tv_vehicle_driver_mobile = v.findViewById(R.id.tv_vehicle_driver_mobile)

        tv_customer_name = v.findViewById(R.id.tv_customer_name)
        tv_customer_mobile = v.findViewById(R.id.tv_customer_mobile)
        tv_customer_address = v.findViewById(R.id.tv_customer_address)
        tv_customer_paymentType = v.findViewById(R.id.tv_customer_paymentType)
        tv_customer_gst_applicable = v.findViewById(R.id.tv_customer_gst_applicable)
        tv_customer_pincode_txt = v.findViewById(R.id.tv_customer_pincode_txt)

        tv_workshop_technician = v.findViewById(R.id.tv_workshop_technician)
        tv_workshop_supervisor = v.findViewById(R.id.tv_workshop_supervisor)
        tv_workshop_supervisor_mobile = v.findViewById(R.id.tv_workshop_supervisor_mobile)

        /* Vehicle Details Fields to set and get the values*/

        av_reg = v.findViewById(R.id.av_reg)
        tv_location_txt = v.findViewById(R.id.tv_location_txt)
        sp_location = v.findViewById(R.id.sp_location)
        sp_vehicle_make = v.findViewById(R.id.sp_vehicle_make)
        sp_vehicle_model = v.findViewById(R.id.sp_vehicle_model)
        et_vehicle_variant = v.findViewById(R.id.et_vehicle_variant)
        et_vehicle_kms_driven = v.findViewById(R.id.et_vehicle_kms_driven)
        sp_vehicle_number_plate_colour = v.findViewById(R.id.sp_vehicle_number_plate_colour)
        sp_vehicle_mfg = v.findViewById(R.id.sp_vehicle_mfg)
        et_vehicle_engine_no = v.findViewById(R.id.et_vehicle_engine_no)
        et_vehicle_chassis_no = v.findViewById(R.id.et_vehicle_chassis_no)
        sp_vehicle_insurance = v.findViewById(R.id.sp_vehicle_insurance)
        et_vehicle_incharge = v.findViewById(R.id.et_vehicle_incharge)
        et_vehicle_incharge_mobile = v.findViewById(R.id.et_vehicle_incharge_mobile)
        et_vehicle_driver = v.findViewById(R.id.et_vehicle_driver)
        et_vehicle_driver_mobile = v.findViewById(R.id.et_vehicle_driver_mobile)
//        sp_vehicle_location = v.findViewById(R.id.sp_vehicle_location)

        /* Customer Details Fields to set and get the values*/

        et_customer_name = v.findViewById(R.id.et_customer_name)
        et_customer_mobile = v.findViewById(R.id.et_customer_mobile)
        et_customer_email = v.findViewById(R.id.et_customer_email)
        et_customer_address = v.findViewById(R.id.et_customer_address)
//        sp_customer_city = v.findViewById(R.id.sp_customer_city)
        av_city = v.findViewById(R.id.av_city)
        av_state = v.findViewById(R.id.av_state)
        et_customer_pincode = v.findViewById(R.id.et_customer_pincode)
        tv_customer_crn = v.findViewById(R.id.tv_customer_crn)
        tv_customer_paymentType_value = v.findViewById(R.id.tv_customer_paymentType_value)
        sp_customer_gst_applicable = v.findViewById(R.id.sp_customer_gst_applicable)
        et_customer_gstin = v.findViewById(R.id.et_customer_gstin)
        ll_gstin = v.findViewById(R.id.ll_gstin)
        ll_crn = v.findViewById(R.id.ll_crn)

        /* Customer Details Fields to set and get the values*/


        sp_workshop_technician = v.findViewById(R.id.sp_workshop_technician)
        sp_workshop_supervisor = v.findViewById(R.id.sp_workshop_supervisor)
        et_workshop_supervisor_mobile = v.findViewById(R.id.et_workshop_supervisor_mobile)

//        if(eb_jabcard_vehicleDetails.childView.isVisible == false){
//            if (expandableButtonListener != null) expandableButtonListener!!.onViewExpanded()
//
//        }
    }

    fun Save() {

        if (mJObCardScreen.isEmpty()) {

            dict_data.put("JobCardCustID", "")
            dict_data.put("CustomerMobile", mCustomerMobile)
            dict_data.put("CustomerName", mCustomerName)
            dict_data.put("RegNo", mVehicleRegNumber)
            dict_data.put("Make", mVehicleMake)
            dict_data.put("Model", mVehicleModel)
            UserSession(requireContext()).setLoginDetails(dict_data.toString())
        }

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.SaveJobCardVehicleDetails(
            mMobileNumber.toString(),
            mJObCardNid,
            mVehicleRegNumber,
            mVehicleMake,
            mVehicleModel,
            mVehicleVariant,
            mVehicleKmsDriven,
            mvehicleNumberPlateColour,
            mVehicleMfg,
            mVehicleEngineNo,
            mVehicleChassisNo,
            mVehicleInsurance,
            mVehicleIncharge,
            mVehicleInchargeMobile,
            mVehicleDriver,
            mVehicleDriverMobile,
            mVehicleLocation,
            mCustomerName,
            mCustomerMobile,
            mCustomerMobile,
            mCustomerEmail,
            mCustomerAddress,
            mCustomerCity,
            mCustomerState,
            mCustomerPincode,
            mCustomerPaymentType,
            mCustomerGSTApplicable,
            mCustomerGSTIN,
            mWorkshopTechnician,
            mWorkshopSupervisor,
            mWorkshopSupervisorMobile
        ).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.d("DATA Save", string)
                    Log.d(
                        "DATA ", mMobileNumber.toString() + " " +
                                mVehicleRegNumber + " " +
                                mVehicleMake + " " +
                                mVehicleModel + " " +
                                mVehicleVariant + " " +
                                mVehicleKmsDriven + " " +
                                mvehicleNumberPlateColour + " " +
                                mVehicleMfg + " " +
                                mVehicleEngineNo + " " +
                                mVehicleChassisNo + " " +
                                mVehicleInsurance + " " +
                                mVehicleIncharge + " " +
                                mVehicleInchargeMobile + " " +
                                mVehicleDriver + " " +
                                mVehicleDriverMobile + " " +
                                mVehicleLocation + " " +
                                mCustomerName + " " +
                                mCustomerMobile + " " +
                                mCustomerEmail + " " +
                                mCustomerAddress + " " +
                                mCustomerCity + " " +
                                mCustomerState + " " +
                                mCustomerPincode + " " +
                                mCustomerPaymentType + " " +
                                mCustomerGSTApplicable + " " +
                                mCustomerGSTIN + " " +
                                mWorkshopTechnician + " " +
                                mWorkshopSupervisor + " " +
                                mWorkshopSupervisorMobile
                    )


                    if (string.contains("Profile Created Succesfully")) {

                        val parts = string.split(" ")
                        val lastWord = parts[parts.size - 1]
                        var id = lastWord.replace("\\u0027.", "")
                        var id1 = id.replace(".\\u0027", "")
                        var mCustId = id1.replace("]", "")
                        var mCustId1 = mCustId.substring(0, mCustId.length - 1)

                        dict_data.put("JobCardCustID", mCustId1)
                        UserSession(requireContext()).setLoginDetails(dict_data.toString())

                        Toast.makeText(
                            requireContext(),
                            "Details Saved Succesfully " + mCustId1,
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

    fun allRequiredDataAvailable(): Boolean {

        if (mVehicleLocation.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Select Location",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (mVehicleRegNumber.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter Vehicle Registation Number in Vehicle Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mVehicleMake.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Select make in Vehicle Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mVehicleModel.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Select Model in Vehicle Detailske",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mVehicleDriver.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter Driver Name in Vehicle Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mVehicleDriverMobile.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter Driver Mobile in Vehicle Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mCustomerName.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter Customer Name in Customer Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mCustomerMobile.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter Customer Mobile in Customer Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        } else {
            if (mobileVerify == true) {
                return true
            } else {
                Toast.makeText(
                    requireContext(),
                    "Customer Mobile exists with User Mobile",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
        }

        if (mCustomerAddress.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter Customer Address in Customer Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mCustomerPincode.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter Pincode in Customer Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mCustomerPaymentType.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Select Payment Type in Customer Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mCustomerGSTApplicable.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter GST Applicable in Customer Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mWorkshopTechnician.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Select Techinician in Workshop Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (mWorkshopSupervisor.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Select Supervisor in Workshop Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mWorkshopSupervisorMobile.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter Supervisor Mobile in Workshop Details",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (mCustomerGSTIN.isNotEmpty()) {
            if (mCustomerGSTIN.length == 15) {
                val pattern =
                    Pattern.compile("[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9]{1}[A-Z]{1}[0-9]{1}")
                val pattern1 =
                    Pattern.compile("[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9]{1}[A-Z]{1}[A-Z]{1}")
                val matcher = pattern.matcher(mCustomerGSTIN)
                val matcher1 = pattern1.matcher(mCustomerGSTIN)
                if (matcher.matches()) {
                    return true

                } else if (matcher1.matches()) {
                    return true

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Invalid GSTIN Number, Please Re-enter GSTIN Number",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
            }
        }
        if (mVehicleRegNumber.isNotEmpty()) {
            if (mVehicleRegNumber.length == 10) {
                val pattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}")
                val matcher = pattern.matcher(mVehicleRegNumber)
                if (matcher.matches()) {

                    if (regListVendor.size > 0) {
                        if (regListVendor.indexOf(mVehicleRegNumber) > -1) {

                            Toast.makeText(
                                requireContext(),
                                "Reg Number exists in Live Jobcard",
                                Toast.LENGTH_LONG
                            ).show()
                            return false
                        }
                        return true

                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Invalid Registation Number, Please Re-enter Register Number",
                        Toast.LENGTH_LONG
                    ).show()
                    return false

                }
            }
        }

        return true
    }


    companion object {

        val TITLE = "Details"

        fun newInstance(): JobCardDetailsFragment {
            return JobCardDetailsFragment()
        }
    }
}