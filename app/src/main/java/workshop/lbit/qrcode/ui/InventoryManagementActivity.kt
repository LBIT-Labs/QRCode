package workshop.lbit.qrcode.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_inventory_management.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.MainActivity
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.adapter.Qr_Inventory_Management_adapter
import workshop.lbit.qrcode.customfonts.MyTextView_Montserrat_Regular
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.QrData
import workshop.lbit.qrcode.interfaces.QrDataList
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.utils.Utilities
import java.util.*
import kotlin.collections.ArrayList

class InventoryManagementActivity : AppCompatActivity(), View.OnClickListener, QrDataList {
    internal lateinit var toolbar_title: MyTextView_Roboto_Bold
    internal lateinit var sp_partCategory: Spinner
    internal lateinit var mViewPager: ViewPager
    internal lateinit var sp_make: Spinner
    internal lateinit var ll_Parts_list: LinearLayout
    internal lateinit var ll_footer: LinearLayout
    internal lateinit var ll_search_image: LinearLayout
    internal lateinit var sv_scroll: NestedScrollView
    internal lateinit var ll_search_options: LinearLayout
    internal lateinit var et_search_part: AutoCompleteTextView
    internal lateinit var tv_submit: TextView
    internal lateinit var list: ArrayList<String>
    private var mPartsList: ArrayList<String>? = null
    internal lateinit var iv_close: ImageView

    private var mPartDetailsListArray: JSONArray? = null
    private var gson: Gson? = null
    private var mPageCount: String? = null
    internal lateinit var tvPagerCount: MyTextView_Roboto_Medium
    internal lateinit var ivprevious: MyTextView_Roboto_Medium
    internal lateinit var ivnext: MyTextView_Roboto_Medium

    private var tvNodata: MyTextView_Montserrat_Regular? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0

    private var mPartDesc = ""
    private var mPartmake = ""
    private var mPartCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_management)
        supportActionBar!!.hide()

        init()

        iv_close.setOnClickListener {
            mPartDesc = ""
            et_search_part.setText("")
            tvNodata!!.visibility = View.VISIBLE
            ll_footer.visibility = View.GONE
            ll_Parts_list.visibility = View.GONE
        }

        GetIMMake()

        GetIMCategoty()

        tv_submit.setOnClickListener {

            getPartDetails()
        }

        et_search_part.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString().trim()
                if (text.length == 4) {
                    GetIMPartDesc(text)

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

        sp_make.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {

                if (arg2 > 0) {
                    mPartmake = sp_make.selectedItem.toString()
                } else {
                    mPartmake = ""
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        sp_partCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {

                if (arg2 > 0) {
                    mPartCategory = sp_partCategory.selectedItem.toString()

                } else {
                    mPartCategory = ""

                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    private fun focusOnView() {
        Handler().post(Runnable {
            sv_scroll.scrollTo(0, ll_search_image.top)
        })
    }

    private fun getPartDetails() {
        val mProgressDialog = ProgressDialog(this@InventoryManagementActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.GetPartDetailsList(mPartDesc, mPartCategory, mPartmake)
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
                                ll_search_image.requestFocus()
                                sv_scroll.requestFocus(View.FOCUS_UP)
                                sv_scroll.scrollTo(0, 4)
                                focusOnView()

                                ll_search_image.visibility = View.VISIBLE
                                ll_search_options.visibility = View.VISIBLE
                                tvNodata!!.visibility = View.GONE
                                ll_footer.visibility = View.VISIBLE
                                ll_Parts_list.visibility = View.VISIBLE

                                val getLoansList = gson!!.fromJson<List<QrData>>(
                                    mPartDetailsListArray.toString(),
                                    object : TypeToken<List<QrData>>() {
                                    }.type
                                )
                                mViewPager.adapter =
                                    Qr_Inventory_Management_adapter(
                                        this@InventoryManagementActivity,
                                        this@InventoryManagementActivity,
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
                                tvNodata!!.visibility = View.VISIBLE
                                ll_footer.visibility = View.GONE
                                ll_search_options.visibility = View.VISIBLE
                            }
                        } else {
                            ll_search_image.visibility = View.VISIBLE
                            ll_Parts_list.visibility = View.GONE
                            ll_footer.visibility = View.GONE
                            tvNodata!!.visibility = View.VISIBLE
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

    private fun GetIMMake() {
        val mProgressDialog = ProgressDialog(this@InventoryManagementActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.IMFiltersData(
            "make",
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

                        Log.e("Test", "Make_List: $string")

                        list = Utilities.getItemList(list, string)
                        Log.e("Test", "Make_List: $list")

                        sp_make.adapter = ArrayAdapter(
                            this@InventoryManagementActivity,
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

    private fun GetIMCategoty() {
        val mProgressDialog = ProgressDialog(this@InventoryManagementActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.IMFiltersData(
            "category",
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

                        list = ArrayList<String>()

                        Log.e("Test", "Make_List: $string")

                        list = Utilities.getItemList(list, string)
                        Log.e("Test", "Make_List: $list")

                        sp_partCategory.adapter = ArrayAdapter(
                            this@InventoryManagementActivity,
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

    private fun GetIMPartDesc(text: String) {
        val mProgressDialog = ProgressDialog(this@InventoryManagementActivity)
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
                                    this@InventoryManagementActivity,
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

    fun init() {
        sv_scroll = findViewById(R.id.nest_scrollview)
        sv_scroll.isFillViewport = true
        iv_close = findViewById(R.id.iv_close)

        ll_Parts_list = findViewById(R.id.ll_Parts_list)
        ll_footer = findViewById(R.id.ll_footer)
        ll_search_image = findViewById(R.id.ll_search_image)
        ll_search_options = findViewById(R.id.ll_search_options)
        toolbar_title = findViewById(R.id.toolbar_title)
        sp_partCategory = findViewById(R.id.sp_partCategory)
        mViewPager = findViewById(R.id.pager)
        sp_make = findViewById(R.id.sp_make)
        et_search_part = findViewById(R.id.et_search_part)
        tv_submit = findViewById(R.id.tv_submit)
        ivprevious = findViewById(R.id.ivprevious)
        ivnext = findViewById(R.id.ivnext)
        tvPagerCount = findViewById(R.id.tvPagerCount)
        tvNodata = findViewById(R.id.tvNodata)
//        iv_search_parts = findViewById(R.id.iv_search_parts)
        toolbar_title.text = "Inventory Management"
        mPartsList = ArrayList()


//        val scrollView: NestedScrollView = findViewById(R.id.nest_scrollview) as NestedScrollView
//        scrollView.setFillViewport(true)
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

    private fun getItemofviewpager(i: Int): Int {
        return mViewPager.currentItem + i
    }

    override fun onBackPressed() {
        val intent = Intent(this@InventoryManagementActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit)
        startActivity(intent)
        finish()
    }

    override fun onNavigate(dict_crops: QrData, position: Int, s: String) {


    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
        }
    }
}