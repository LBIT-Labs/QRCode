package workshop.lbit.qrcode.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.gson.Gson

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.utils.Constants
import workshop.lbit.qrcode.utils.Utilities


class RegistrationActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var backtoolbar: RelativeLayout
    internal lateinit var llSearch: RelativeLayout
    internal lateinit var ivregistered_image: ImageView
    internal lateinit var toolbar_title: TextView

    internal var PICK_IMAGE_REQUEST = 111
    internal lateinit var bitmap: Bitmap
    internal var photo_bitmap: Bitmap? = null
    internal var image_width: Int = 0
    internal var image_width_landscape: Int = 0
    private val TAG = RegistrationActivity::class.java!!.getName()
    private lateinit var sp_branch: Spinner
    private lateinit var sp_designation: Spinner
    private lateinit var sp_brand: Spinner

    private lateinit var mName: String

    private lateinit var etName: EditText
    private lateinit var etPhoneNumbers: EditText
    private lateinit var etEmail: EditText
    private lateinit var etEmplyId: EditText
    private lateinit var etManager: EditText

    private lateinit var rlPhoneNumber: RelativeLayout
    private lateinit var rlEmail: RelativeLayout
    private lateinit var rlEmplyId: RelativeLayout
    private lateinit var rlManager: RelativeLayout

    private var branchlist: ArrayList<String>? = null
    private var designationList: ArrayList<String>? = null
    private var gson: Gson? = null
    internal lateinit var jsonArray: JSONArray

    private lateinit var mMobile: String
    private lateinit var mEmailId: String
    private lateinit var mEmpId: String
    private lateinit var mManager: String
    private lateinit var mBrand: String
    private lateinit var mBranch: String
    private lateinit var mDesignation: String
    private lateinit var mDepartment: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_screen)
        init()
        gson = Gson()
        branchlist = ArrayList()
        designationList = ArrayList()


        /*Get Branch List*/
        getBranchList()
//        getDesignations()
        /*Spinner Selected Item*/
        sp_branch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {

                mBranch = sp_branch.selectedItem.toString()
                Log.e("TAG", "List_selec " + sp_branch)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        sp_brand.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {

                mBrand = sp_branch.selectedItem.toString()
                Log.e("TAG", "List_selec " + sp_branch)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        sp_designation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, view: View, arg2: Int, arg3: Long) {

                mDesignation = sp_designation.selectedItem.toString()
                Log.e("TAG", "List_selec " + mDesignation)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

    }

    /**/
    private fun getId(position: Int): String {
        var id = ""
        try {
            val json = jsonArray.getJSONObject(position)
            id = json.getString("id")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return id
    }

    /*Initialize variables*/
    private fun init() {
        toolbar_title = findViewById(R.id.toolbar_title)
        llSearch = findViewById(R.id.llSearch)
        ivregistered_image = findViewById(R.id.ivregistered_image)
        backtoolbar = findViewById(R.id.backtoolbar)
        sp_branch = findViewById(R.id.sp_branch)
        sp_designation = findViewById(R.id.sp_designation)
        sp_brand = findViewById(R.id.sp_brand)


        rlPhoneNumber = findViewById(R.id.rlPhoneNumber)
        rlEmail = findViewById(R.id.rlEmail)
        rlEmplyId = findViewById(R.id.rlEmplyId)
        rlManager = findViewById(R.id.rlManager)


        etName = findViewById(R.id.etName)
        etPhoneNumbers = findViewById(R.id.etPhoneNumbers)
        etEmail = findViewById(R.id.etEmail)
        etEmplyId = findViewById(R.id.etEmplyId)
        etManager = findViewById(R.id.etManager)



        toolbar_title.text = resources.getString(R.string.profile_signup)
        llSearch.visibility = View.GONE


    }


    override fun onClick(v: View) {

        val i = v.id
        if (i == R.id.backtoolbar) {
            onBackPressed()

        } else if (i == R.id.ivregistered_image) {

            if (askForPermission()) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image"),
                    PICK_IMAGE_REQUEST
                )

            }


        } else if (i == R.id.btn_submitregistrion) {

            mName = etName.text.toString()
            mMobile = etPhoneNumbers.text.toString()
            mEmailId = etEmail.text.toString()
            mEmpId = etEmplyId.text.toString()
            mManager = etManager.text.toString()

            val is_validate = validateRequest()
            if (is_validate) {

                registerUser()

            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val path = data.data

            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, path)
                ivregistered_image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }


    private fun askForPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            val hasCallPermission = ContextCompat.checkSelfPermission(
                this@RegistrationActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                // need to request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@RegistrationActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    // explain
                    showMessageOKCancel(
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            ActivityCompat.requestPermissions(
                                this@RegistrationActivity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                REQUEST_CODE_ASK_PERMISSIONS
                            )
                        })
                    // if denied then working here
                } else {
                    // Request for permission
                    ActivityCompat.requestPermissions(
                        this@RegistrationActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_ASK_PERMISSIONS
                    )
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

        val builder = AlertDialog.Builder(this@RegistrationActivity)
        val dialog = builder.setMessage("You need to grant access to Read External Storage")
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(this@RegistrationActivity, android.R.color.holo_blue_light)
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(this@RegistrationActivity, android.R.color.holo_red_light)
            )
        }

        dialog.show()

    }

    /*Convert To Base 64*/
    private fun convertToString(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imgByte = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imgByte, Base64.DEFAULT)
    }

    private fun registerUser() {
        val mProgressDialog = ProgressDialog(this@RegistrationActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.registration(
            "Yes", "", mName, mEmailId,
            mBranch, mDesignation, mEmpId, mManager, mMobile, "Hyundai", ""
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()
                    val jsonObject = JSONObject(string)


                    mProgressDialog.dismiss()


                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(
                    "TAG",
                    "onFailure() called with: call = [" + call.request().url() + "], t = [" + t + "]",
                    t
                )

                if (mProgressDialog.isShowing)
                    mProgressDialog.dismiss()
            }
        })


    }


    private fun getBranchList() {
        val mProgressDialog = ProgressDialog(this@RegistrationActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.branchdetails.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()
                    jsonArray = JSONArray(string)

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        branchlist!!.add(jsonObject.getString("name").trim { it <= ' ' })
                    }
                    sp_branch.adapter = ArrayAdapter(
                        this@RegistrationActivity,
                        android.R.layout.simple_spinner_dropdown_item, branchlist!!
                    )

                    mProgressDialog.dismiss()


                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(
                    "TAG",
                    "onFailure() called with: call = [" + call.request().url() + "], t = [" + t + "]",
                    t
                )

                if (mProgressDialog.isShowing)
                    mProgressDialog.dismiss()
            }
        })


    }

    /*GetSC Names List*/
    private fun getDesignations() {
        val mProgressDialog = ProgressDialog(this@RegistrationActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.getDesignation("roles")
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {
                        val string = response.body()!!.string()
                        val jsonArray = JSONArray(string)
                        var name: String
                        val list = ArrayList<String>()
                        for (listItem in jsonArray.toString()) {

                        }

                        sp_designation.adapter = ArrayAdapter(
                            this@RegistrationActivity,
                            android.R.layout.simple_spinner_dropdown_item, designationList!!
                        )

                        mProgressDialog.dismiss()


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(
                        "TAG",
                        "onFailure() called with: call = [" + call.request().url() + "], t = [" + t + "]",
                        t
                    )

                    if (mProgressDialog.isShowing)
                        mProgressDialog.dismiss()
                }
            })


        /*Buyer Details*/
    }


    private fun validateRequest(): Boolean {
        var is_validate = true

        if (etPhoneNumbers.text.toString().equals("")) {
            val errorMessage = resources.getString(R.string.phonenumber)
            Utilities.showMessage(rlPhoneNumber, errorMessage)
            etPhoneNumbers.error = "Enter Phone Number"
            is_validate = false
        } else if (etEmail.text.toString().equals("")) {
            val errorMessage = resources.getString(R.string.email)
            Utilities.showMessage(rlEmail, errorMessage)
            etEmail.error = "Enter Valid Emaid Id"
            is_validate = false
        } else if (etEmplyId.text.toString().equals("")) {
            val errorMessage = resources.getString(R.string.employesId)
            Utilities.showMessage(rlEmplyId, errorMessage)
            etEmplyId.error = "Enter Employee Id"
            is_validate = false
        } else if (etManager.text.toString().equals("")) {
            val errorMessage = resources.getString(R.string.manger)
            Utilities.showMessage(rlManager, errorMessage)
            etManager.error = "Enter Manager"
            is_validate = false
        }




        return is_validate
    }


    companion object {
        internal val REQUEST_CODE_ASK_PERMISSIONS = 124
        private val IMAGE = 100
    }

    override fun onBackPressed() {
        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}



