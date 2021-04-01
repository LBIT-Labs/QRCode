package workshop.lbit.qrcode.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.interfaces.JobCardList


class VendorJobcardNewRecordsDataAdapter(
    private val mContext: Context,
    internal var mJobsList: List<JobcardData>,
    var mJOBCardList: JobCardList
) : PagerAdapter() {

    internal lateinit var mJCData: JobcardData
    internal var layoutInflater: LayoutInflater
    private val listener = mJOBCardList

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
    lateinit var ll_live_GatepassNo: LinearLayout
    lateinit var ll_live_GatepassDate: LinearLayout
    lateinit var ll_live_GatepassStatus: LinearLayout
    lateinit var ll_live_GatepassAuthStatus: LinearLayout

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
        ll_live_GatepassNo = itemView.findViewById(R.id.ll_live_GatepassNo)
        ll_live_GatepassDate = itemView.findViewById(R.id.ll_live_GatepassDate)
        ll_live_GatepassStatus = itemView.findViewById(R.id.ll_live_GatepassStatus)
        ll_live_GatepassAuthStatus = itemView.findViewById(R.id.ll_live_GatepassAuthStatus)


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


        if(mJCData.jc_live_gatepass_status!!.isNotEmpty()){
            if(mJCData.jc_live_gatepass_status.equals("gatepass_generated")){
                tv_live_gatpassStatus.text = "Live"
            }
            ll_live_GatepassNo.visibility=  View .VISIBLE
            ll_live_GatepassDate.visibility = View.VISIBLE
        }else {
            tv_live_gatpassStatus.text = "Not Generated"
            ll_live_GatepassNo.visibility=  View .GONE
            ll_live_GatepassDate.visibility = View.GONE
        }

        if(mJCData.jc_live_gatepass_in_out_status!!.isNotEmpty()){
            if(mJCData.jc_live_gatepass_in_out_status.equals("in")){
                tv_live_GatepassAuthStatus.text = "Close"
            }
        }else {
            tv_live_GatepassAuthStatus.text = "Not Yet Started"

        }

        if (mJCData.jc_live_status.equals("Job Card") || mJCData.jc_live_status.equals("Estimate") || mJCData.jc_live_status.equals("Under Progress")) {

            tv_live_jobcardStatus.paintFlags =
                tv_live_jobcardStatus.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            tv_live_jobcardStatus.setTextColor(mContext.resources.getColor(R.color.yellow))

        }

        container.addView(itemView)


        tv_live_jobcardStatus.setOnClickListener {
            mJCData = mJobsList[position]


            if (mJCData.jc_live_status.equals("Job Card") || mJCData.jc_live_status.equals("Estimate") || mJCData.jc_live_status.equals(
                    "Under Progress"
                )
            ) {

                listener.onNavigate(mJCData, position)

            }

        }

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
