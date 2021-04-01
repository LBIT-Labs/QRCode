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
import workshop.lbit.qrcode.adapter.VendorJobcardSparesDataAdapter
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.interfaces.JobCardListService
import workshop.lbit.qrcode.utils.Constants


@SuppressLint("ValidFragment")
class VendorJobcardSparesFragment @SuppressLint("ValidFragment") constructor() : Fragment(),
    View.OnClickListener, JobCardListService {

    private lateinit var mAlertDialog: AlertDialog
    lateinit var ll_add_spares: LinearLayout
    lateinit var ll_spares_data: LinearLayout
    lateinit var ll_spares_data1: LinearLayout
    private var gson: Gson? = null

    private var isLoaded = false
    private var isVisibleToUser = false

    private var mRole: String = ""
    private var mVendorNid: String = ""
    private var mVendor: String = ""
    private var mJobcardID: String = ""
    private lateinit var dict_data: JSONObject

    lateinit var bt_submit: MyTextView_Roboto_Bold
    lateinit var bt_cancel: MyTextView_Roboto_Bold
    lateinit var et_spares_discount: EditText
    lateinit var et_spares_quantity: EditText
    lateinit var et_spares_OE_part_number: EditText
    lateinit var et_spares_part_desc: EditText
    lateinit var tv_spares_finalprice: MyTextView_Roboto_Medium
    lateinit var et_spares_mrp: EditText
    lateinit var et_spares_hsn: EditText
    lateinit var sp_spares_tax: Spinner
    lateinit var tv_spares_discount_txt: MyTextView_Roboto_Bold
    lateinit var tv_spares_OE_part_number_txt: MyTextView_Roboto_Bold
    lateinit var tv_spares_part_desc_txt: MyTextView_Roboto_Bold
    lateinit var tv_spares_quantity_txt: MyTextView_Roboto_Bold
    lateinit var tv_spares_mrp_txt: MyTextView_Roboto_Bold
    lateinit var tv_spares_hsn_txt: MyTextView_Roboto_Bold
    lateinit var tv_spares_tax_txt: MyTextView_Roboto_Bold


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
    var mSparesFinalPrice: String = ""
    var mSparesTax: String = ""
    var mSparesHSN: String = ""

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
        val v = inflater.inflate(R.layout.fragment_vendor_jobcard_spares, container, false)

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

    private fun getItemofviewpager(i: Int): Int {
        return vp_pager!!.currentItem + i
    }


    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.ll_add_spares) {
            mSparesOePart = ""
            mSparesOePartDesc = ""
            mSparesQuantity = ""
            mSparesMrp = ""
            mSparesDiscount = ""
            mSparesFinalPrice = ""
            mSparesHSN = ""
            mSparesTax = ""


            AddSparesDialog("")
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
        Constants.qrCode_uat.GetVendorJobcardData(
            mMobileNumber.toString(),
            mVendorNid,
            "spares",
            mJobcardID
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Jobs List ", mVendorNid + " " + mJobcardID + " " + string)

                    if (!string.equals("{}")) {
                        ll_spares_data.visibility = View.VISIBLE
                        ll_spares_data1.visibility = View.VISIBLE

                        val jsonObject = JSONArray(string)

                        val jobsdatalist = gson!!.fromJson<ArrayList<JobcardData>>(
                            jsonObject.toString(),
                            object : TypeToken<ArrayList<JobcardData>>() {

                            }.type
                        )
                        vp_pager!!.adapter = VendorJobcardSparesDataAdapter(
                            requireContext(),
                            jobsdatalist,
                            this@VendorJobcardSparesFragment
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

        mSparesOePart = mJCData.jc_oepart!!
        mSparesOePartDesc = mJCData.jc_part_desc!!
        mSparesQuantity = mJCData.jc_qty!!
        mSparesMrp = mJCData.jc_mrp!!
        mSparesDiscount = mJCData.jc_discount!!
        mSparesFinalPrice = mJCData.jc_final!!
        mSparePid = mJCData.jc_pid!!
        mSparesHSN = mJCData.jc_hsn!!
        mSparesTax = mJCData.jc_tax!!


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
            .inflate(R.layout.add_vendor_jobcard_spares_dialog, null)


        bt_submit =
            mDialogView.findViewById(R.id.bt_submit)
        bt_cancel =
            mDialogView.findViewById(R.id.bt_cancel)
        et_spares_quantity = mDialogView.findViewById(R.id.et_spares_quantity)
        et_spares_discount = mDialogView.findViewById(R.id.et_spares_discount)
        et_spares_OE_part_number = mDialogView.findViewById(R.id.et_spares_OE_part_number)
        et_spares_part_desc = mDialogView.findViewById(R.id.et_spares_part_desc)
        tv_spares_finalprice = mDialogView.findViewById(R.id.tv_spares_finalprice)
        et_spares_mrp = mDialogView.findViewById(R.id.et_spares_mrp)
        et_spares_hsn = mDialogView.findViewById(R.id.et_spares_hsn)
        sp_spares_tax = mDialogView.findViewById(R.id.sp_spares_tax)

        tv_spares_discount_txt = mDialogView.findViewById(R.id.tv_spares_discount_txt)
        tv_spares_quantity_txt = mDialogView.findViewById(R.id.tv_spares_quantity_txt)
        tv_spares_OE_part_number_txt = mDialogView.findViewById(R.id.tv_spares_OE_part_number_txt)
        tv_spares_part_desc_txt = mDialogView.findViewById(R.id.tv_spares_part_desc_txt)
        tv_spares_mrp_txt = mDialogView.findViewById(R.id.tv_spares_mrp_txt)
        tv_spares_hsn_txt = mDialogView.findViewById(R.id.tv_spares_hsn_txt)
        tv_spares_tax_txt = mDialogView.findViewById(R.id.tv_spares_tax_txt)

        MandatoryDiscount(resources.getString(R.string.discount1))
        MandatoryQuantity(resources.getString(R.string.qr_quantity))
        MandatoryOEPart(resources.getString(R.string.qr_oe_part_number))
        MandatoryOEPartDesc(resources.getString(R.string.part_desc))
        MandatoryMrp(resources.getString(R.string.qr_mrp))
        MandatoryHsn(resources.getString(R.string.hsn))
        MandatoryTax(resources.getString(R.string.tax))


        if (mSparesOePart.isNotEmpty()) {
            et_spares_OE_part_number.setText(mSparesOePart)
        }
        if (mSparesOePartDesc.isNotEmpty()) {
            et_spares_part_desc.setText(mSparesOePartDesc)
        }
        if (mSparesQuantity.isNotEmpty()) {
            et_spares_quantity.setText(mSparesQuantity)
        }
        if (mSparesMrp.isNotEmpty()) {
            et_spares_mrp.setText(mSparesMrp)
        }
        if (mSparesDiscount.isNotEmpty()) {
            et_spares_discount.setText(mSparesDiscount)
        }

        if (mSparesHSN.isNotEmpty()) {
            et_spares_hsn.setText(mSparesHSN)
        }

        if (mSparesFinalPrice.isNotEmpty()) {
            tv_spares_finalprice.text = mSparesFinalPrice
        }
        if (!mSparesTax.equals("null") && mSparesTax.isNotEmpty()) {
            val list = resources.getStringArray(R.array.tax_array).asList()
            if (list.indexOf(mSparesTax) > -1) {
                sp_spares_tax.setSelection(
                    list.indexOf(
                        mSparesTax
                    )
                )

            }
        }
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)
        mAlertDialog = mBuilder.show()

        getValues()

        bt_submit.setOnClickListener {

            if (mSparesOePart.isNotEmpty()) {
                if (mSparesOePartDesc.isNotEmpty()) {
                    if (mSparesQuantity.isNotEmpty()) {
                        if (mSparesMrp.isNotEmpty()) {
                            if (mSparesHSN.isNotEmpty()) {
                                if (mSparesHSN.length >= 4) {
                                    if (mSparesTax.isNotEmpty()) {
                                        if (mSparesDiscount.isNotEmpty()) {
                                            if (status.isNotEmpty()) {
                                                if (status.equals("edit")) {
                                                    SaveSpares(status, mSparePid)
                                                }
                                            } else {
                                                SaveSpares("", mSparePid)

                                            }

                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Please Enter Discount",
                                                Toast.LENGTH_LONG
                                            ).show()

                                        }

                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Please Enter Tax",
                                            Toast.LENGTH_LONG
                                        ).show()

                                    }

                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "HSN Value should be atleast 4 digits",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please Enter HSN",
                                    Toast.LENGTH_LONG
                                ).show()

                            }

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please Enter MRP",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please Enter Quantity",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                } else {
                    Toast.makeText(requireContext(), "Please Enter OE Part Desc", Toast.LENGTH_LONG)
                        .show()

                }
            } else {
                Toast.makeText(requireContext(), "Please Enter OE Part", Toast.LENGTH_LONG).show()

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
        tv_spares_discount_txt.text = builder

    }

    private fun MandatoryQuantity(string: String) {

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
        tv_spares_quantity_txt.text = builder

    }

    private fun MandatoryOEPart(string: String) {

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
        tv_spares_OE_part_number_txt.text = builder

    }

    private fun MandatoryOEPartDesc(string: String) {

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
        tv_spares_part_desc_txt.text = builder

    }

    private fun MandatoryMrp(string: String) {

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
        tv_spares_mrp_txt.text = builder

    }

    private fun MandatoryHsn(string: String) {

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
        tv_spares_hsn_txt.text = builder

    }

    private fun MandatoryTax(string: String) {

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
        tv_spares_tax_txt.text = builder

    }


    private fun getValues() {


        sp_spares_tax.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>,
                    view: View,
                    arg2: Int,
                    arg3: Long
                ) {

                    if (arg2 > 0) {
                        mSparesTax = sp_spares_tax.selectedItem.toString()

                    } else {
                        mSparesTax = ""
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {

                }
            }


        et_spares_quantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mSparesQuantity = editable.toString().trim()

                if (mSparesQuantity.toInt() > 0) {
                    if (mSparesMrp.isNotEmpty() && mSparesDiscount.isNotEmpty() && mSparesQuantity.isNotEmpty()) {
                        val qty = mSparesQuantity.toDouble()
                        val mrp = mSparesMrp.toDouble()
                        val mValue = mrp * qty.toInt()
                        val amount = mValue * mSparesDiscount.toLong() / 100
                        mSparesFinalPrice = (mValue - amount).toString()
                        tv_spares_finalprice.text = mSparesFinalPrice
                    }
                } else {
                    et_spares_quantity.setText("")
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_spares_OE_part_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mSparesOePart = editable.toString().trim()


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_spares_part_desc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mSparesOePartDesc = editable.toString().trim()


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_spares_mrp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mSparesMrp = editable.toString().trim()


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })


        et_spares_hsn.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val mSparesHSN1 = editable.toString().trim()

                if (mSparesHSN1.length >= 4) {
                    mSparesHSN = mSparesHSN1
                } else {
                    mSparesHSN = ""
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_spares_discount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mSparesDiscount = editable.toString().trim()

                if (mSparesMrp.isNotEmpty() && mSparesDiscount.isNotEmpty() && mSparesQuantity.isNotEmpty()) {
                    val qty = mSparesQuantity.toDouble()
                    val mrp = mSparesMrp.toDouble()
                    val mValue = mrp * qty.toInt()
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
        Constants.qrCode_uat.SaveJobCardSparesVendor(
            mMobileNumber.toString(),
            mVendorNid,
            mSparePid,
            mSparesOePart,
            mSparesOePartDesc,
            mSparesTax,
            mSparesHSN,
            mSparesQuantity,
            mSparesDiscount,
            mVendor,
            mJobcardID,
            mSparesFinalPrice,
            mSparesMrp,
            status
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
                        if (string.contains("Spares added succesfully")) {

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
        Constants.qrCode_uat.SaveJobCardSparesVendor(
            mMobileNumber.toString(),
            mVendorNid,
            mSparePid,
            mSparesOePart,
            mSparesOePartDesc,
            mSparesTax,
            mSparesHSN,
            mSparesQuantity,
            mSparesDiscount,
            mVendor,
            mJobcardID,
            mSparesFinalPrice,
            mSparesMrp,
            status
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
                        if (string.contains("Spares added succesfully")) {

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

        fun newInstance(): VendorJobcardSparesFragment {
            return VendorJobcardSparesFragment()
        }
    }

}