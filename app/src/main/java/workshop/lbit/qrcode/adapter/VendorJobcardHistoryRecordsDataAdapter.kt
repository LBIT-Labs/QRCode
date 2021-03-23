package workshop.lbit.qrcode.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.Singleton.UserSession
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.interfaces.JobCardList
import workshop.lbit.qrcode.interfaces.JobCardListService
import workshop.lbit.qrcode.jobcard.JobCardServiceFragment


class VendorJobcardHistoryRecordsDataAdapter(
    private val mContext: Context,
    internal var mJobsList: List<JobcardData>
    ) : PagerAdapter() {

    internal lateinit var mJCData: JobcardData
    internal var layoutInflater: LayoutInflater

    lateinit var tv_live_vendor_name: MyTextView_Roboto_Medium
    lateinit var tv_live_vendorId: MyTextView_Roboto_Medium
    lateinit var tv_live_jobId: MyTextView_Roboto_Medium
    lateinit var tv_live_jobcard_date: MyTextView_Roboto_Medium
    lateinit var tv_live_jobcardStatus: MyTextView_Roboto_Medium
    lateinit var tv_live_customer: MyTextView_Roboto_Medium
    lateinit var tv_live_customerMobile: MyTextView_Roboto_Medium
    lateinit var tv_live_TotalAmount: MyTextView_Roboto_Medium
    lateinit var tv_live_gatepassNo: MyTextView_Roboto_Medium
    lateinit var tv_live_gatepassDate: MyTextView_Roboto_Medium
    lateinit var tv_live_gatpassStatus: MyTextView_Roboto_Medium
    lateinit var tv_live_GatepassAuthStatus: MyTextView_Roboto_Medium
    init {

        layoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mJobsList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.vendor_jobcard_live_records_data_row, container, false)

        mJCData = mJobsList[position]


        tv_live_vendor_name = itemView.findViewById(R.id.tv_live_vendor_name)
        tv_live_vendorId = itemView.findViewById(R.id.tv_live_vendorId)
        tv_live_jobId = itemView.findViewById(R.id.tv_live_jobId)
        tv_live_jobcard_date = itemView.findViewById(R.id.tv_live_jobcard_date)
        tv_live_jobcardStatus = itemView.findViewById(R.id.tv_live_jobcardStatus)
        tv_live_customer = itemView.findViewById(R.id.tv_live_customer)
        tv_live_customerMobile = itemView.findViewById(R.id.tv_live_customerMobile)
        tv_live_TotalAmount = itemView.findViewById(R.id.tv_live_TotalAmount)
        tv_live_gatepassNo = itemView.findViewById(R.id.tv_live_gatepassNo)
        tv_live_gatepassDate = itemView.findViewById(R.id.tv_live_gatepassDate)
        tv_live_gatpassStatus = itemView.findViewById(R.id.tv_live_gatpassStatus)
        tv_live_GatepassAuthStatus = itemView.findViewById(R.id.tv_live_GatepassAuthStatus)

        tv_live_vendor_name.text = mJCData.jc_live_vendor
        tv_live_vendorId.text = mJCData.jc_live_vendor_id
        tv_live_jobId.text = mJCData.jc_live_jobcard_id
        tv_live_jobcard_date.text = mJCData.jc_live_jobcard_date
        tv_live_jobcardStatus.text = mJCData.jc_live_status
        tv_live_customer.text = mJCData.jc_live_customer
        tv_live_customerMobile.text = mJCData.jc_live_mobile
        tv_live_gatepassNo.text = mJCData.jc_live_gatepass_no
        tv_live_gatepassDate.text = mJCData.jc_live_gatepass_date
        tv_live_gatpassStatus.text = mJCData.jc_live_gatepass_status
        tv_live_GatepassAuthStatus.text = mJCData.jc_live_gatepass_in_out_status
        tv_live_TotalAmount.text = ""

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
