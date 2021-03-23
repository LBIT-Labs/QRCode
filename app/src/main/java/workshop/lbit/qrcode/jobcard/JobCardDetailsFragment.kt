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
import android.widget.*
import androidx.core.view.get
import androidx.core.view.isVisible
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

    lateinit var tv_workshop_supervisor_mobile: MyTextView_Roboto_Bold
    lateinit var tv_workshop_supervisor: MyTextView_Roboto_Bold
    lateinit var tv_workshop_technician: MyTextView_Roboto_Bold

    lateinit var et_search: EditText
    lateinit var eb_jabcard_vehicleDetails: ExpandableButton

    /*Vehicle Details*/

    lateinit var et_vehicle_reg_number: EditText
    lateinit var et_vehicle_variant: EditText
    lateinit var et_vehicle_kms_driven: EditText
    lateinit var et_vehicle_engine_no: EditText
    lateinit var et_vehicle_chassis_no: EditText
    lateinit var et_vehicle_incharge_mobile: EditText
    lateinit var et_vehicle_incharge: EditText
    lateinit var et_vehicle_driver: EditText
    lateinit var et_vehicle_driver_mobile: EditText

    lateinit var sp_vehicle_make: Spinner
    lateinit var sp_vehicle_model: Spinner
    lateinit var sp_vehicle_number_plate_colour: Spinner
    lateinit var sp_vehicle_mfg: Spinner
    lateinit var sp_vehicle_insurance: Spinner
    lateinit var sp_vehicle_location: Spinner

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

    lateinit var tv_customer_crn: MyTextView_Roboto_Medium

    lateinit var sp_customer_city: Spinner
    lateinit var sp_customer_state: Spinner
    lateinit var sp_customer_payment_type: Spinner
    lateinit var sp_customer_gst_applicable: Spinner

    var mCustomerName: String = ""
    var mCustomerMobile: String = ""
    var mCustomerEmail: String = ""
    var mJObCardNid: String = ""
    var mJObCardScreen: String = ""
    var mCustomerAddress: String = ""
    var mCustomerPincode: String = ""
    var mCustomerGSTIN: String = ""

    var mCustomerCRN: String = ""

    var mCustomerCity: String = ""
    var mCustomerState: String = ""
    var mCustomerPaymentType: String = ""
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
    private var modelList = java.util.ArrayList<String>()

    internal var mMobileNumber: String? = null
    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var mRole: String = ""
    private lateinit var dict_data: JSONObject
    private var isLoaded = false
    private var isVisibleToUser = false
    var tabsuccess: Boolean = false
    var pattern = "[6-9]{1}"

    internal var expandableButtonListener: ExpandableButton.ExpandableButtonListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (isVisibleToUser && isAdded()) {
            dict_data = JSONObject()
            val logindata = UserSession(requireContext()).getLoginDetails()

            try {
                dict_data = JSONObject(logindata)
                mRole = dict_data.optString("role")
                mCustomerMobile = dict_data.optString("CustomerMobile")
                mJObCardNid = dict_data.optString("JobCardCustID")
                mJObCardScreen = dict_data.optString("screenType")


            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (mCustomerMobile.isNotEmpty() && mJObCardNid.isNotEmpty()) {

                getDetailsWithMobile(mCustomerMobile, mJObCardNid)

            } else {
                loadData()

            }
            isLoaded = true;
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


            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (mCustomerMobile.isNotEmpty() && mJObCardNid.isNotEmpty()) {

                getDetailsWithMobile(mCustomerMobile, mJObCardNid)

            } else {
                loadData()

            }
            isLoaded = true;
        }
    }

    private fun loadData() {
        GetMakeList()

        GetCitiesList()

        GetStatesList()
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

        MandatoryTechnician(resources.getString(R.string.technician))
        MandatorySupervisor(resources.getString(R.string.supervisor))
        MandatorySupervisorMobile(resources.getString(R.string.supervisor_mobile))

        SetValues()

        return v
    }

    private fun getDetailsWithMobile(mobile: String, mJObCardNid: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getDetails(mobile, mJObCardNid).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Make", string)

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

                        if (!mVehicleRegNumber.equals("null")) {
                            et_vehicle_reg_number.setText(mVehicleRegNumber)
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

                        if (mCustomerGSTIN.equals("null")) {
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
                            tv_customer_crn.setText(mCustomerCRN)
                        }

                        if (!mCustomerPaymentType.equals("null") && mCustomerPaymentType.isNotEmpty()) {
                            val list = resources.getStringArray(R.array.payment_type_array).asList()
                            if (list.indexOf(mCustomerPaymentType) > -1) {
                                sp_customer_payment_type.setSelection(
                                    list.indexOf(
                                        mCustomerPaymentType
                                    )
                                )

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
                            if (stateList.size > 0) {
                                if (stateList.indexOf(mCustomerState) > -1) {
                                    sp_customer_state.setSelection(stateList.indexOf(mCustomerState))
                                }
                            } else {
                                GetStatesList()

                            }
                        }

                        if (!mCustomerCity.equals("null") && mCustomerCity.isNotEmpty()) {
                            Log.d("makelist", cityList.toString())
                            if (cityList.size > 0) {
                                if (cityList.indexOf(mCustomerCity) > -1) {
                                    sp_customer_city.setSelection(cityList.indexOf(mCustomerCity))
                                }
                            } else {
                                GetCitiesList()

                            }
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

                        if (!string.equals("{}")) {


                            val mCities = ArrayList<String>()


                            cityList = Utilities.getItemList(mCities, string)

                            if (mCustomerCity.isNotEmpty()) {
                                if (cityList.indexOf(mCustomerCity) > -1) {
                                    sp_customer_city.adapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        cityList
                                    )
                                    sp_customer_city.setSelection(cityList.indexOf(mCustomerCity))
                                }
                            } else {
                                sp_customer_city.adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item, cityList
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

    private fun GetStatesList() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
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

                        if (!string.equals("{}")) {

                            val mStates = ArrayList<String>()
                            stateList = Utilities.getItemList(mStates, string)

                            if (mCustomerState.isNotEmpty()) {
                                if (stateList.indexOf(mCustomerState) > -1) {
                                    sp_customer_state.adapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        stateList
                                    )
                                    sp_customer_state.setSelection(stateList.indexOf(mCustomerState))
                                }
                            } else {
                                sp_customer_state.adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item, stateList
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

        sp_vehicle_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {

                if (arg2 > 0) {
                    mVehicleLocation = sp_vehicle_location.selectedItem.toString()
                } else {
                    mVehicleLocation = ""
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        sp_customer_payment_type.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mCustomerPaymentType = sp_customer_payment_type.selectedItem.toString()
                    } else {
                        mCustomerPaymentType = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }

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
                        if(mCustomerGSTApplicable.equals("No")){
                            ll_gstin.visibility = View.GONE
                        }else{
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


        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()

                if (text.length == 10) {

                    getDetailsWithMobile(text, "")

                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_vehicle_reg_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mVehicleRegNumber = editable.toString().trim()
                if (mVehicleRegNumber.length == 10) {
                    val pattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}")
                    val matcher = pattern.matcher(mVehicleRegNumber)
                    if (matcher.matches()) {

//                        et_vehicle_reg_number.setText(mVehicleRegNumber)
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
                    val matcher = pattern.matcher(mCustomerGSTIN)
                    if (matcher.matches()) {

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
        tv_vehicle_reg.setText(builder)

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
        tv_vehicle_make.setText(builder);

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
        tv_vehicle_model.setText(builder);

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
        tv_vehicle_driver.setText(builder);

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
        tv_vehicle_driver_mobile.setText(builder);

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
        tv_customer_name.setText(builder)

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
        tv_customer_mobile.setText(builder)

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
        tv_customer_address.setText(builder)

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
        tv_customer_paymentType.setText(builder)

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
        tv_customer_gst_applicable.setText(builder)

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
        tv_workshop_technician.setText(builder)

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
        tv_workshop_supervisor.setText(builder)

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
        tv_workshop_supervisor_mobile.setText(builder)

    }

    fun init(v: View) {

        et_search = v.findViewById(R.id.et_search)
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

        tv_workshop_technician = v.findViewById(R.id.tv_workshop_technician)
        tv_workshop_supervisor = v.findViewById(R.id.tv_workshop_supervisor)
        tv_workshop_supervisor_mobile = v.findViewById(R.id.tv_workshop_supervisor_mobile)

        /* Vehicle Details Fields to set and get the values*/

        et_vehicle_reg_number = v.findViewById(R.id.et_vehicle_reg_number)
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
        sp_vehicle_location = v.findViewById(R.id.sp_vehicle_location)

        /* Customer Details Fields to set and get the values*/

        et_customer_name = v.findViewById(R.id.et_customer_name)
        et_customer_mobile = v.findViewById(R.id.et_customer_mobile)
        et_customer_email = v.findViewById(R.id.et_customer_email)
        et_customer_address = v.findViewById(R.id.et_customer_address)
        sp_customer_city = v.findViewById(R.id.sp_customer_city)
        sp_customer_state = v.findViewById(R.id.sp_customer_state)
        et_customer_pincode = v.findViewById(R.id.et_customer_pincode)
        tv_customer_crn = v.findViewById(R.id.tv_customer_crn)
        sp_customer_payment_type = v.findViewById(R.id.sp_customer_payment_type)
        sp_customer_gst_applicable = v.findViewById(R.id.sp_customer_gst_applicable)
        et_customer_gstin = v.findViewById(R.id.et_customer_gstin)
        ll_gstin = v.findViewById(R.id.ll_gstin)

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

        if(mJObCardScreen.isEmpty()){

            dict_data.put("JobCardCustID", "")
            UserSession(requireContext()).setLoginDetails(dict_data.toString())
        }

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.SaveJobCardVehicleDetails(
            mMobileNumber.toString(),
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
                        dict_data.put("CustomerMobile", mCustomerMobile)
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
        }

        if (mCustomerAddress.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Please Enter Customer Address in Customer Details",
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

        return true
    }


    companion object {

        val TITLE = "Details"

        fun newInstance(): JobCardDetailsFragment {
            return JobCardDetailsFragment()
        }
    }
}