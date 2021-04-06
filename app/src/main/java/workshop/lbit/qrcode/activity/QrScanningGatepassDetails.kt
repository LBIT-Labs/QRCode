package workshop.lbit.qrcode.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import workshop.lbit.qrcode.MainActivity
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.customfonts.MyTextView_Montserrat_Regular
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.fragments.QrScanningFragment
import workshop.lbit.qrcode.utils.Constants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class QrScanningGatepassDetails : AppCompatActivity(), View.OnClickListener {

    private lateinit var sv_scrollview: NestedScrollView
    private lateinit var ll_image: LinearLayout
    private lateinit var ll_vendorName: LinearLayout
    private lateinit var ll_capture_image: LinearLayout
    private lateinit var ll_receiptno: LinearLayout
    private lateinit var tv_capture_image: MyTextView_Montserrat_Regular
    private lateinit var tv_gp_vendorName: MyTextView_Roboto_Regular
    private lateinit var tv_gp_regNo: MyTextView_Roboto_Regular
    private lateinit var tv_gp_customerName: MyTextView_Roboto_Regular
    private lateinit var tv_gp_technicianName: MyTextView_Roboto_Regular
    private lateinit var tv_gp_Mobile: MyTextView_Roboto_Regular
    private lateinit var tv_gp_jobcard_no: MyTextView_Roboto_Regular
    private lateinit var tv_gp_payment_type: MyTextView_Roboto_Regular
    private lateinit var tv_gp_receipt_no: MyTextView_Roboto_Regular
    private lateinit var tv_gp_submit: MyTextView_Roboto_Regular
    private lateinit var ivCaptureImage: ImageView
    private val CAMERA = 1
    internal var base64: String = ""
    internal var mGatepassStatus: String = ""
    internal var mGatepassType: String = ""
    private var qr_req_nid_value: String? = null
    private var sharedpreferences: SharedPreferences? = null
    internal var mMobileNumber: String? = null
    private var editor: SharedPreferences.Editor? = null
    lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanning_gatepass)
        supportActionBar!!.hide()

        qr_req_nid_value = intent.getStringExtra("nid")

        requestMultiplePermissions()

        sharedpreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()

        mMobileNumber = sharedpreferences!!.getString(Constants.LOGINUSER_MOBILE, "")


        init()


        if (qr_req_nid_value!!.isNotEmpty()) {

            getGatepassDetails()
        }
        tv_capture_image.setOnClickListener {

            val myAnim: Animation =
                AnimationUtils.loadAnimation(this@QrScanningGatepassDetails, R.anim.bounce)
            tv_capture_image.startAnimation(myAnim)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        }
        tv_gp_submit.setOnClickListener {


            if (mGatepassType.isNotEmpty()) {
                if (mGatepassType.equals("Non Returnable")) {

                    AllowGatepass("Delivered", "")
                } else {
                    if (base64.isNotEmpty()) {
                        if (tv_gp_submit.text.equals("OUT")) {
                            AllowGatepass("out", base64)

                        } else if (tv_gp_submit.text.equals("IN")) {
                            AllowGatepass("in", base64)

                        }
                    } else {
                        Toast.makeText(
                            this@QrScanningGatepassDetails,
                            "Please Capture Image to go further",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }
    }

    private fun AllowGatepass(status: String, veh_image: String) {

        val mProgressDialog = ProgressDialog(this@QrScanningGatepassDetails)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.QrReturnableGatepassAllow(
            mMobileNumber.toString(),
            qr_req_nid_value.toString(),
            status,
            veh_image
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

//                    Log.d("gatepass Details Allow", status + " " + string)

                    if (status.equals("Delivered")) {

                        if (string.contains("Delivered succesfully")) {
                            mProgressDialog.dismiss()

                            fragmentManager = supportFragmentManager
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.qr_part_details_view, QrScanningFragment())
                            transaction.commit()

                        }
                    }

                    if (string.contains("Pics uploaded succesfully")) {
                        mProgressDialog.dismiss()

                        fragmentManager = supportFragmentManager
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.qr_part_details_view, QrScanningFragment())
                        transaction.commit()

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

    private fun getGatepassDetails() {
        val mProgressDialog = ProgressDialog(this@QrScanningGatepassDetails)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.qrCode_uat.QrReturnableGatepass(
            mMobileNumber.toString(),
            qr_req_nid_value.toString()
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.d("gatepass Details", string)
                    if (!string.equals("{}")) {
                        val jsonObject = JSONObject(string)

                        val mCustomerName = jsonObject.getString("customer").toString()
                        val mJobCardNo = jsonObject.getString("job").toString()
                        val mRegNo = jsonObject.getString("reg").toString()
                        val mCustomerMobile = jsonObject.getString("mobile").toString()
                        val mTechnician = jsonObject.getString("tech").toString()
                        val mService = jsonObject.getString("service").toString()
                        mGatepassStatus = jsonObject.getString("gatepass_status").toString()
                        mGatepassType = jsonObject.getString("gatepass_type").toString()
                        val mVendor = jsonObject.getString("vendor").toString()
                        val mPayment_type = jsonObject.getString("payment_type").toString()
                        val mReceiptNo = jsonObject.getString("receipt_no").toString()


                        tv_gp_vendorName.text = "Vendor : " + mVendor
                        tv_gp_regNo.text = mRegNo
                        tv_gp_customerName.text = mCustomerName
                        tv_gp_technicianName.text = mTechnician
                        tv_gp_Mobile.text = mCustomerMobile
                        tv_gp_jobcard_no.text = mJobCardNo
                        tv_gp_payment_type.text = mPayment_type
                        tv_gp_receipt_no.text = mReceiptNo

                        if (mPayment_type.isNotEmpty()) {
                            if (mPayment_type.equals("Cash")) {
                                ll_receiptno.visibility = View.VISIBLE
                            } else {
                                ll_receiptno.visibility = View.GONE

                            }
                        }

                        if (mGatepassType.isNotEmpty()) {
                            if (mGatepassType.equals("Non Returnable")) {
                                ll_vendorName.visibility = View.INVISIBLE
                                ll_capture_image.visibility = View.INVISIBLE
                                ll_image.visibility = View.INVISIBLE
                                tv_gp_submit.text = "OUT"
                            } else if (mGatepassType.equals("Returnable")) {
                                ll_vendorName.visibility = View.VISIBLE
                                ll_capture_image.visibility = View.VISIBLE
                                ll_image.visibility = View.VISIBLE
                                if (mGatepassStatus.isNotEmpty()) {
                                    if (mGatepassStatus.equals("out")) {
                                        tv_gp_submit.text = "IN"

                                    }
                                }
                            }
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

    private fun focusOnView() {
        Handler().post(Runnable {
            sv_scrollview.scrollTo(0, ll_image.top)
        })
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(this@QrScanningGatepassDetails)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {

//                        Toast.makeText(
//                            applicationContext, "Camera Permission Granted", Toast.LENGTH_SHORT
//                        )
//                            .show()
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        //openSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<com.karumi.dexter.listener.PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT).show()
            }
            .onSameThread()
            .check()
    }

    private fun hasStoragePermission(requestCode: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this@QrScanningGatepassDetails,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode
                )
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = data.extras!!.get("data") as Bitmap
                    val permission = hasStoragePermission(requestCode)
                    Log.e("TAG", "permission " + permission)

                    if (permission == true) {
                        val path = saveImage(bitmap)
                        Log.e("TAG", "image_bitmap Path" + path)

//                        Toast.makeText(
//                            this@QrScanningGatepassDetails,
//                            "Image Saved!",
//                            Toast.LENGTH_SHORT
//                        ).show()

//                        ivCaptureImage.setImageBitmap(bitmap)

//                        Picasso.with(this).load(bitmap).
//                        resize(50, 50).
//                        centerCrop().into(ivCaptureImage)
                        Glide.with(this)
                            .load(path) // Uri of the picture
                            .transform(CenterInside())
                            .into(ivCaptureImage)

                        ll_image.requestFocus()
                        sv_scrollview.requestFocus(View.FOCUS_UP)
                        sv_scrollview.scrollTo(0, 4)
                        focusOnView()

                        val bytes = File(path).readBytes()
                        base64 = android.util.Base64.encodeToString(bytes, 0)
                        Log.e("TAG", "image_bitmap " + base64)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@QrScanningGatepassDetails, "Failed!", Toast.LENGTH_SHORT)
                        .show()
                }

            }

        }
    }

    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + "/Letzbank_upload/Qr_upload"
        )


        // have the object build the directory structure, if needed.
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())


            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg")
            )


            if (!f.exists()) {
                f.parentFile.mkdirs()

                f.createNewFile()
                Log.e("TAG", "File Created")
            }

            Log.d("File Name", f.name)

            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this@QrScanningGatepassDetails,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }


    fun init() {
        ll_image = findViewById(R.id.ll_image)
        ll_vendorName = findViewById(R.id.ll_vendorName)
        ll_capture_image = findViewById(R.id.ll_capture_image)
        ll_receiptno = findViewById(R.id.ll_receiptno)
        sv_scrollview = findViewById(R.id.sv_scrollview)
        tv_capture_image = findViewById(R.id.tv_capture_image)
        ivCaptureImage = findViewById(R.id.ivCaptureImage)
        tv_gp_vendorName = findViewById(R.id.tv_gp_vendorName)
        tv_gp_regNo = findViewById(R.id.tv_gp_regNo)
        tv_gp_customerName = findViewById(R.id.tv_gp_customerName)
        tv_gp_technicianName = findViewById(R.id.tv_gp_technicianName)
        tv_gp_Mobile = findViewById(R.id.tv_gp_Mobile)
        tv_gp_jobcard_no = findViewById(R.id.tv_gp_jobcard_no)
        tv_gp_payment_type = findViewById(R.id.tv_gp_payment_type)
        tv_gp_receipt_no = findViewById(R.id.tv_gp_receipt_no)
        tv_gp_submit = findViewById(R.id.tv_gp_submit)
    }

    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.backtoolbar) {
            onBackPressed()

        }
    }

    override fun onBackPressed() {


        val intent = Intent(this@QrScanningGatepassDetails, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit)
        startActivity(intent)
        finish()
    }

}