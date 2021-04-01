package workshop.lbit.qrcode.ui

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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
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
import workshop.lbit.qrcode.adapter.Jobcard_spares_search_data_adapter
import workshop.lbit.qrcode.customfonts.MyTextView_Montserrat_Regular
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.QrData
import workshop.lbit.qrcode.interfaces.QrDataList
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.utils.Utilities

class JobcardSparesSearchActivity : AppCompatActivity(), View.OnClickListener, QrDataList {

    private lateinit var mAlertDialog: AlertDialog
    internal lateinit var toolbar_title: MyTextView_Roboto_Bold
    internal lateinit var ll_Parts_list: LinearLayout
    internal lateinit var ll_footer: LinearLayout
    internal lateinit var ll_search_image: LinearLayout
    internal lateinit var sv_scroll: NestedScrollView
    internal lateinit var ll_search_options: LinearLayout

    internal lateinit var mViewPager: ViewPager
    internal lateinit var et_search_part: AutoCompleteTextView
    internal lateinit var iv_close: ImageView
    internal lateinit var tv_submit: TextView
    internal lateinit var list: ArrayList<String>
    private var mPartsList: ArrayList<String>? = null
    private lateinit var tvNodata: MyTextView_Montserrat_Regular

    private var mPartDesc = ""
    private var mValue_OE_num = ""
    private var mValue_part_desc = ""
    private var mValue_part_quantity = ""
    private var mValue_mrp = ""
    private var mValue_hsn = ""
    private var mValue_tax = ""
    private var mValue_pid = ""

    private var mPartDetailsListArray: JSONArray? = null
    private var gson: Gson? = null
    private var mPageCount: String? = null
    internal lateinit var tvPagerCount: MyTextView_Roboto_Medium
    internal lateinit var ivprevious: MyTextView_Roboto_Medium
    internal lateinit var ivnext: MyTextView_Roboto_Medium
    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0

    lateinit var bt_submit: MyTextView_Roboto_Bold
    lateinit var bt_cancel: MyTextView_Roboto_Bold
    lateinit var et_spares_discount: EditText
    lateinit var et_spares_quantity: EditText
    lateinit var tv_spares_OE_part_number: MyTextView_Roboto_Medium
    lateinit var tv_spares_part_desc: MyTextView_Roboto_Medium
    lateinit var tv_spares_finalprice: MyTextView_Roboto_Medium
    lateinit var tv_spares_mrp: MyTextView_Roboto_Medium
    lateinit var tv_spares_quantity_txt: MyTextView_Roboto_Bold
    lateinit var tv_spares_jobid_txt: MyTextView_Roboto_Bold
    lateinit var sp_spares_jobid: Spinner

    var mSparesDiscount: String = "0"
    var mSparesQuantity: String = ""
    var mSparesJobId: String = ""
    var mSparesFinalPrice: String = ""

    internal var mMobileNumber: String? = null
    private var sharedpreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var mJobCardCustID: String = ""
    private var mRole: String = ""
    private lateinit var dict_data: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobcard_spares_search)
        supportActionBar!!.hide()

        sharedpreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")

        dict_data = JSONObject()

        val logindata = UserSession(this@JobcardSparesSearchActivity).getLoginDetails()

        try {
            dict_data = JSONObject(logindata)
            mRole = dict_data.optString("role")
            mJobCardCustID = dict_data.optString("JobCardCustID")

            Log.d("data", dict_data.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        init()

        iv_close.setOnClickListener {
            mPartDesc = ""
            et_search_part.setText("")
            tvNodata.visibility = View.VISIBLE
            ll_footer.visibility = View.GONE
            ll_Parts_list.visibility = View.GONE
        }

        tv_submit.setOnClickListener {

            if (mPartDesc.isNotEmpty()) {

                val stringarry = mPartDesc.split("-")
                val partNumber = stringarry.get(0)
                getPartDetails(partNumber)

            } else {
                Toast.makeText(this@JobcardSparesSearchActivity, "Clicked", Toast.LENGTH_LONG)
                    .show()

            }

        }

        et_search_part.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()
                if (text.length == 4) {

                    GetSparesPartDesc(text)

                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_search_part.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mPartDesc = parent.getItemAtPosition(position).toString()
                et_search_part.setText(mPartDesc)
                hideKeyboard()
                et_search_part.clearFocus()

            }
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
        }
    }

    private fun getPartDetails(partNumber: String) {
        val mProgressDialog = ProgressDialog(this@JobcardSparesSearchActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.GetSpareDetailsList(mMobileNumber.toString(), partNumber)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        val string = response.body()!!.string()
                        Log.e("TAG", "DashBoard " + string)

                        if (!string.equals("{}")) {
                            mPartDetailsListArray = JSONArray(string)

                            gson = Gson()

                            if (mPartDetailsListArray!!.length() > 0) {

                                et_search_part.clearFocus()

                                ll_search_image.visibility = View.VISIBLE
                                ll_search_options.visibility = View.VISIBLE
                                tvNodata.visibility = View.GONE
                                ll_footer.visibility = View.VISIBLE
                                ll_Parts_list.visibility = View.VISIBLE

                                val getLoansList = gson!!.fromJson<List<QrData>>(
                                    mPartDetailsListArray.toString(),
                                    object : TypeToken<List<QrData>>() {
                                    }.type
                                )
                                mViewPager.adapter =
                                    Jobcard_spares_search_data_adapter(
                                        this@JobcardSparesSearchActivity,
                                        this@JobcardSparesSearchActivity,
                                        getLoansList
                                    )
                                mViewPager.overScrollMode = View.OVER_SCROLL_NEVER

                                mPageCount = mPartDetailsListArray!!.length().toString()

                                if (mPageCount!!.length > 0) {
                                    tvPagerCount.text = mPageCount!!
                                }
                            } else {
                                ll_search_image.visibility = View.VISIBLE
                                ll_Parts_list.visibility = View.GONE
                                tvNodata.visibility = View.VISIBLE
                                ll_footer.visibility = View.GONE
                                ll_search_options.visibility = View.VISIBLE
                            }
                        } else {
                            ll_search_image.visibility = View.VISIBLE
                            ll_Parts_list.visibility = View.GONE
                            ll_footer.visibility = View.GONE
                            tvNodata.visibility = View.VISIBLE
                            ll_search_options.visibility = View.VISIBLE
                        }

                        if (mProgressDialog.isShowing)
                            mProgressDialog.dismiss()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    mProgressDialog.dismiss()

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(
                        "TAG",
                        "onFailure() called with: call = [" + call.request()
                            .url() + "], t = [" + t + "]", t
                    )

                    if (mProgressDialog.isShowing)
                        mProgressDialog.dismiss()
                }
            })


    }


    fun init() {
        toolbar_title = findViewById(R.id.toolbar_title)
        toolbar_title.text = "Add Spare"

        sv_scroll = findViewById(R.id.nest_scrollview)
        sv_scroll.isFillViewport = true

        ll_Parts_list = findViewById(R.id.ll_Parts_list)
        ll_footer = findViewById(R.id.ll_footer)
        ll_search_image = findViewById(R.id.ll_spares_search_image)
        ll_search_options = findViewById(R.id.ll_spare_search_options)
        tvNodata = findViewById(R.id.tvNodata)

        mViewPager = findViewById(R.id.pager)
        et_search_part = findViewById(R.id.et_spares_search_part)
        iv_close = findViewById(R.id.iv_close)
        tv_submit = findViewById(R.id.tv_submit)
        ivprevious = findViewById(R.id.ivprevious)
        ivnext = findViewById(R.id.ivnext)
        tvPagerCount = findViewById(R.id.tvPagerCount)

        mPartsList = ArrayList()

        ivnext.setOnClickListener(this)
        ivprevious.setOnClickListener(this)

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

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


                    Log.e("TAG", "onClick_Previous: " + (mViewPager.currentItem - 1))

                    val mNext = (mViewPager.currentItem + 1).toString() + " of " + mPageCount
                    if (!(mViewPager.currentItem - 1).equals(mPageCount) && !mNext.equals("0")) {
                        tvPagerCount.text = mNext
                    }


                } else {

                    val mNext = (mViewPager.currentItem + 1).toString() + " of " + mPageCount
                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tvPagerCount.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
            }

        })


    }

    private fun getItemofviewpager(i: Int): Int {
        return mViewPager.currentItem + i
    }

    private fun GetSparesPartDesc(text: String) {
        val mProgressDialog = ProgressDialog(this@JobcardSparesSearchActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.IMFiltersData("part", text)
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()

                        if (!string.equals("{}")) {

                            mPartsList = ArrayList<String>()

                            Log.e("Test", "Parts_List: $string")

                            mPartsList = Utilities.getItemListPart(mPartsList!!, string)
                            Log.e("Test", "Make_List: $mPartsList")

                            et_search_part.setAdapter(
                                ArrayAdapter(
                                    this@JobcardSparesSearchActivity,
                                    android.R.layout.select_dialog_item, mPartsList!!
                                )
                            )
                            et_search_part.showDropDown()
                            et_search_part.requestFocus()
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

    override fun onClick(v: View?) {

        val i = v!!.id
        if (i == R.id.ivprevious) {
            mViewPager.setCurrentItem(getItemofviewpager(-1), true)

            Log.e("TAG", "onClick_Previous: " + mViewPager.currentItem)
            val mPrev = (mViewPager.currentItem + 1).toString() + " of " + mPageCount
            tvPagerCount.text = mPrev


        } else if (i == R.id.ivnext) {

            mViewPager.setCurrentItem(getItemofviewpager(+1), true)
            Log.e("TAG", "onClick_Next: " + mViewPager.currentItem + 1)
            val mNext = (mViewPager.currentItem + 1).toString() + " of " + mPageCount
            tvPagerCount.text = mNext

        } else if (i == R.id.backtoolbar) {
            onBackPressed()
        }
    }

    override fun onNavigate(data: QrData, position: Int, s: String) {
        mValue_OE_num = data.qr_oe_part!!
        mValue_part_desc = data.qr_part_desc!!
        mValue_part_quantity = data.qr_available_qty!!
        mValue_mrp = data.qr_mrp!!
        mValue_hsn = data.qr_part_hsn!!
        mValue_tax = data.qr_tax!!
        mValue_pid = data.qr_pid!!

//        mSparesQuantity = mValue_part_quantity
        AddSparesDialog()
    }

    private fun AddSparesDialog() {
        val mDialogView = LayoutInflater.from(this@JobcardSparesSearchActivity)
            .inflate(R.layout.add_jobcard_spares_dialog, null)

        et_spares_quantity = mDialogView.findViewById(R.id.et_spares_quantity)
        et_spares_discount = mDialogView.findViewById(R.id.et_spares_discount)
        tv_spares_OE_part_number = mDialogView.findViewById(R.id.tv_spares_OE_part_number)
        tv_spares_part_desc = mDialogView.findViewById(R.id.tv_spares_part_desc)
        tv_spares_finalprice = mDialogView.findViewById(R.id.tv_spares_finalprice)
        tv_spares_mrp = mDialogView.findViewById(R.id.tv_spares_mrp)

        sp_spares_jobid = mDialogView.findViewById(R.id.sp_spares_jobid)
        tv_spares_quantity_txt = mDialogView.findViewById(R.id.tv_spares_quantity_txt)
        tv_spares_jobid_txt = mDialogView.findViewById(R.id.tv_spares_jobid_txt)

        bt_submit =
            mDialogView.findViewById(R.id.bt_submit)
        bt_cancel =
            mDialogView.findViewById(R.id.bt_cancel)

        MandatoryQuantity(resources.getString(R.string.qr_quantity))
        MandatoryJobid(resources.getString(R.string.job_id))

        tv_spares_OE_part_number.text = mValue_OE_num
        tv_spares_part_desc.text = mValue_part_desc
        et_spares_quantity.setText("")
        et_spares_discount.setText("0")
        tv_spares_mrp.text = mValue_mrp
        tv_spares_finalprice.text = mValue_mrp

        val mBuilder = AlertDialog.Builder(this@JobcardSparesSearchActivity)
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)
        mAlertDialog = mBuilder.show()

        getJObID()



        getValues()

        bt_submit.setOnClickListener {

            if (mSparesDiscount.isNotEmpty()) {

                if (mSparesJobId.isNotEmpty()) {

                    if(mSparesQuantity.isNotEmpty()){
                        try {
                            if (mSparesQuantity.toDouble() > mValue_part_quantity.toDouble()) {
                                Toast.makeText(
                                    this@JobcardSparesSearchActivity,
                                    "Quantity should not be greater than Available Quantity",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                SaveSpares()

                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }

                } else {
                    Toast.makeText(
                        this@JobcardSparesSearchActivity,
                        "Please Select JobId",
                        Toast.LENGTH_LONG
                    ).show()

                }
            } else {
                Toast.makeText(
                    this@JobcardSparesSearchActivity,
                    "Please Enter Discount",
                    Toast.LENGTH_LONG
                ).show()

            }

        }
        bt_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }

    private fun getJObID() {

        val mProgressDialog = ProgressDialog(this@JobcardSparesSearchActivity)
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

                    val makeList = Utilities.getItemList(list, string)

                    sp_spares_jobid.adapter = ArrayAdapter(
                        this@JobcardSparesSearchActivity,
                        android.R.layout.simple_spinner_dropdown_item, makeList
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

    private fun SaveSpares() {

        val mProgressDialog = ProgressDialog(this@JobcardSparesSearchActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.SaveJobCardSpares(
            mMobileNumber.toString(),
            mJobCardCustID,
            mValue_pid,
            "",
            mValue_OE_num,
            mValue_part_desc,
            mValue_tax,
            mValue_hsn,
            mSparesQuantity,
            "",
            "",
            "",
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

                        Log.d("Spare Save", string)

                        if (string.contains("Added Spare Succesfully")) {

                            mProgressDialog.dismiss()
                            mAlertDialog.dismiss()


                            dict_data.put("Screen", "Live")
                            UserSession(this@JobcardSparesSearchActivity).setLoginDetails(dict_data.toString())
                            Log.d("User Data", dict_data.toString())

                            val intent = Intent(
                                this@JobcardSparesSearchActivity,
                                JobCardFormActivity::class.java
                            )
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            overridePendingTransition(
                                R.anim.move_right_enter,
                                R.anim.move_right_exit
                            )
                            intent.putExtra("TAG", "2")
                            startActivity(intent)
                            finish()
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
        tv_spares_jobid_txt.text = builder

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
                mSparesQuantity = editable.toString().trim()

                if (mSparesQuantity.isNotEmpty()) {

                    if (mSparesQuantity.toInt() > 0) {
                        try {
                            if (mSparesQuantity.toDouble() > mValue_part_quantity.toInt()
                                    .toDouble()
                            ) {
                                Toast.makeText(
                                    this@JobcardSparesSearchActivity,
                                    "Quantity should not be greater than Available Quantity",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                if (mValue_mrp.isNotEmpty() && mSparesDiscount.isNotEmpty() && mSparesQuantity.isNotEmpty()) {

                                    val qty = mSparesQuantity.toDouble()
                                    val mrp = mValue_mrp.toDouble()
                                    val mValue = mrp * qty.toInt()
                                    val amount = mValue * mSparesDiscount.toLong() / 100
                                    mSparesFinalPrice = (mValue - amount).toString()
                                    tv_spares_finalprice.text = mSparesFinalPrice
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        et_spares_quantity.setText("")
                    }

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_spares_discount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mSparesDiscount = editable.toString().trim()

                try {
                    if (mValue_mrp.isNotEmpty() && mSparesQuantity.isNotEmpty()) {

                        val qty = mSparesQuantity.toDouble()
                        val mrp = mValue_mrp.toDouble()
                        val mValue = mrp * qty
                        val amount = mValue * mSparesDiscount.toLong() / 100
                        mSparesFinalPrice = (mValue - amount).toString()
                        tv_spares_finalprice.text = mSparesFinalPrice
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this@JobcardSparesSearchActivity, JobCardFormActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit)
        intent.putExtra("TAG", "2")
        startActivity(intent)
        finish()
    }
}