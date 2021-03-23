package workshop.lbit.qrcode.ui

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.customfonts.MyTextView_Montserrat_Regular
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.utils.Constants

import java.io.ByteArrayOutputStream
import java.util.ArrayList

class ProfileEditActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var backtoolbar: RelativeLayout
    internal lateinit var llSearch: RelativeLayout
    internal lateinit var ivEditedImage: ImageView
    internal lateinit var toolbar_title: TextView

    internal var PICK_IMAGE_REQUEST = 111
    internal lateinit var bitmap: Bitmap
    internal lateinit var photo_bitmap: Bitmap
    internal var image_width: Int = 0
    internal var image_width_landscape: Int = 0

    internal lateinit var SC_jsonArray: JSONArray
    private var scList: ArrayList<String>? = null
    internal lateinit var mBranch_MObile: String


    internal lateinit var mAdv_Name: String
    internal lateinit var mEmply_ID: String
    internal lateinit var mDesignation: String
    internal lateinit var mDepartment: String
    internal lateinit var mManger: String
    internal lateinit var mBrand: String
    internal lateinit var mBranch: String
    internal lateinit var mDealerLoc: String
    internal var mMobileNumber: String? = null


    /*TextView*/
    private lateinit var tvVehicleno: TextView

    private lateinit var tvProfilename: MyTextView_Roboto_Bold
    private lateinit var tvPhoneNumbers: EditText
    private lateinit var tvEmployeeid: MyTextView_Montserrat_Regular
    private lateinit var etManger: EditText
    private lateinit var sp_designation: Spinner
    private lateinit var etdepartment: EditText
    private lateinit var tvbrand: MyTextView_Montserrat_Regular
    private lateinit var sp_branchlist: Spinner
    internal lateinit var tvdateofjoining: MyTextView_Montserrat_Regular

    private var sharedpreferences: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit)
        getSupportActionBar()!!.hide();

        scList = ArrayList()

        init()

        /*Shared*/
        sharedpreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        //        mNotification = sharedpreferences.getString(Constants.LOGINUSER_NOTIFY, "");
        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")


        val data = UserSession(this@ProfileEditActivity).getLoginDetails()
        var dict_data = JSONObject()
        try {
            dict_data = JSONObject(data)
            mAdv_Name = dict_data.optString("adv_name")
            mEmply_ID = dict_data.optString("emp_id")
            mDesignation = dict_data.optString("Desg")
            mDepartment = dict_data.optString("Dept")
            mBranch = dict_data.optString("dealer_loc_name")
            mDealerLoc = dict_data.optString("dealer_loc")
            mBrand = dict_data.optString("brand")
            mManger = dict_data.optString("Manager")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        getBranchList()



        tvProfilename.setText(mAdv_Name)
        tvPhoneNumbers.setText(mMobileNumber)
        tvEmployeeid.setText(mEmply_ID)
        etManger.setText(mManger)
        tvbrand.setText(mBrand)


        /*Assign_SC Selected Item*/
        sp_branchlist!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {

                /*Get Selected Id*/
                //                mSCMObile = getMobile(arg2);
                try {
                    val json = SC_jsonArray.getJSONObject(arg2)
                    mBranch_MObile = json.getString("mob")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                // TODO Auto-generated method stub

            }
        }

    }

    /*Initialize variables*/
    private fun init() {
        toolbar_title = findViewById(R.id.toolbar_title)
        llSearch = findViewById(R.id.llSearch)
        backtoolbar = findViewById(R.id.backtoolbar)
        ivEditedImage = findViewById(R.id.ivEditedImage)
        sp_branchlist = findViewById(R.id.edit_sp_branchlist)


        tvProfilename = findViewById(R.id.edit_tvProfilename)
        tvPhoneNumbers = findViewById(R.id.edit_tvPhoneNumbers)
        tvEmployeeid = findViewById(R.id.edit_tvEmployeeid)
        sp_designation = findViewById(R.id.edit_sp_designation)
        etdepartment = findViewById(R.id.edit_etdepartment)
        etManger = findViewById(R.id.edit_etManger)
        tvbrand = findViewById(R.id.edit_tvbrand)
        sp_branchlist = findViewById(R.id.edit_sp_branchlist)
        tvdateofjoining = findViewById(R.id.edit_tvdateofjoining)
        sp_branchlist = findViewById(R.id.edit_sp_branchlist)


        toolbar_title.text = resources.getString(R.string.profile_edit)
        llSearch.visibility = View.GONE

    }


    override fun onClick(v: View) {

        val i = v.id
        if (i == R.id.backtoolbar) {
            onBackPressed()

        } else if (i == R.id.ivEditedImage) {


            if (askForPermission()) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)

            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)

                //Setting image to ImageView
                ivEditedImage.visibility = View.VISIBLE
                //                ivImageView.setImageBitmap(bitmap);


                var height = (bitmap.height * (512.0.toFloat() / bitmap.width)).toInt()
                if (height <= image_width) {
                    height = image_width_landscape
                }

                photo_bitmap = Bitmap.createScaledBitmap(bitmap, image_width, height, false)

                val bmpheight = photo_bitmap.height
                val bmpWidth = photo_bitmap.width
                val profile_image_data = ByteArrayOutputStream()
                photo_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, profile_image_data)
                photo_bitmap = getResizedBitmap(photo_bitmap, bmpWidth, bmpheight)
                ivEditedImage.setImageBitmap(photo_bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun getResizedBitmap(image: Bitmap, bitmapWidth: Int, bitmapHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true)
    }

    private fun askForPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            val hasCallPermission = ContextCompat.checkSelfPermission(this@ProfileEditActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                // need to request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@ProfileEditActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // explain
                    showMessageOKCancel(
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                ActivityCompat.requestPermissions(this@ProfileEditActivity,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                        REQUEST_CODE_ASK_PERMISSIONS)
                            })
                    // if denied then working here
                } else {
                    // Request for permission
                    ActivityCompat.requestPermissions(this@ProfileEditActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_CODE_ASK_PERMISSIONS)
                }

                return false
            } else {
                // permission granted and calling function working
                return true
            }
        } else {
            return true
        }
    }

    private fun showMessageOKCancel(okListener: DialogInterface.OnClickListener) {

        val builder = AlertDialog.Builder(this@ProfileEditActivity)
        val dialog = builder.setMessage("You need to grant access to Read External Storage")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(this@ProfileEditActivity, android.R.color.holo_blue_light))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(this@ProfileEditActivity, android.R.color.holo_red_light))
        }

        dialog.show()

    }

    companion object {
        internal val REQUEST_CODE_ASK_PERMISSIONS = 124
    }


    /*GetSC Names List*/
    private fun getBranchList() {
        val mProgressDialog = ProgressDialog(this@ProfileEditActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getSClist("sales_consultant",mDealerLoc).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()
                    SC_jsonArray = JSONArray(string)

                    for (i in 0 until SC_jsonArray.length()) {
                        val jsonObject = SC_jsonArray.getJSONObject(i)
                        scList!!.add(jsonObject.getString("name"))
                    }

                    sp_branchlist!!.adapter = ArrayAdapter(this@ProfileEditActivity,
                            android.R.layout.simple_spinner_dropdown_item, scList!!)

                    mProgressDialog.dismiss()


                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("TAG", "onFailure() called with: call = [" + call.request().url() + "], t = [" + t + "]", t)

                if (mProgressDialog.isShowing)
                    mProgressDialog.dismiss()
            }
        })


        /*Buyer Details*/
    }


}


