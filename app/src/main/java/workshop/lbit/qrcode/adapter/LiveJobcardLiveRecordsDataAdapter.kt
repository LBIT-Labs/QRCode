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


class LiveJobcardLiveRecordsDataAdapter(
    private val mContext: Context,
    internal var mJobsList: List<JobcardData>,
    var mJOBCardList: JobCardList
) : PagerAdapter() {

    internal lateinit var mJCData: JobcardData
    internal var layoutInflater: LayoutInflater
    private val listener = mJOBCardList


    lateinit var ll_live_invoiceNo: LinearLayout
    lateinit var ll_live_invoiceDate: LinearLayout
    lateinit var ll_live_invoiceAmount: LinearLayout
    lateinit var ll_live_invoiceView: LinearLayout
    lateinit var ll_live_estimateView: LinearLayout

    lateinit var tv_live_customer: MyTextView_Roboto_Medium
    lateinit var tv_live_mobile: MyTextView_Roboto_Medium
    lateinit var tv_live_jobId: MyTextView_Roboto_Medium
    lateinit var tv_live_jobcard_date: MyTextView_Roboto_Medium
    lateinit var tv_live_make: MyTextView_Roboto_Medium
    lateinit var tv_live_regNo: MyTextView_Roboto_Medium
    lateinit var tv_live_inviceNo: MyTextView_Roboto_Medium
    lateinit var tv_live_inviceDate: MyTextView_Roboto_Medium
    lateinit var tv_live_inviceAmount: MyTextView_Roboto_Medium
    lateinit var tv_live_status: MyTextView_Roboto_Medium
    lateinit var tv_live_estimateView: MyTextView_Roboto_Medium
    lateinit var tv_live_inviceView: MyTextView_Roboto_Medium

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
            layoutInflater.inflate(R.layout.live_jobcard_live_records_data_row, container, false)

        mJCData = mJobsList[position]

        ll_live_invoiceNo = itemView.findViewById(R.id.ll_live_invoiceNo)
        ll_live_invoiceDate = itemView.findViewById(R.id.ll_live_invoiceDate)
        ll_live_invoiceAmount = itemView.findViewById(R.id.ll_live_invoiceAmount)
        ll_live_invoiceView = itemView.findViewById(R.id.ll_live_invoiceView)
        ll_live_estimateView = itemView.findViewById(R.id.ll_live_estimateView)

        tv_live_customer = itemView.findViewById(R.id.tv_live_customer)
        tv_live_mobile = itemView.findViewById(R.id.tv_live_mobile)
        tv_live_jobId = itemView.findViewById(R.id.tv_live_jobId)
        tv_live_jobcard_date = itemView.findViewById(R.id.tv_live_jobcard_date)
        tv_live_make = itemView.findViewById(R.id.tv_live_make)
        tv_live_regNo = itemView.findViewById(R.id.tv_live_regNo)
        tv_live_inviceNo = itemView.findViewById(R.id.tv_live_inviceNo)
        tv_live_inviceDate = itemView.findViewById(R.id.tv_live_inviceDate)
        tv_live_inviceAmount = itemView.findViewById(R.id.tv_live_inviceAmount)
        tv_live_status = itemView.findViewById(R.id.tv_live_status)
        tv_live_estimateView = itemView.findViewById(R.id.tv_live_estimateView)
        tv_live_inviceView = itemView.findViewById(R.id.tv_live_inviceView)

        tv_live_customer.text = mJCData.jc_live_customer
        tv_live_mobile.text = mJCData.jc_live_mobile
        tv_live_jobId.text = mJCData.jc_live_jobcard_id
        tv_live_jobcard_date.text = mJCData.jc_live_jobcard_date
        tv_live_make.text = mJCData.jc_live_make
        tv_live_regNo.text = mJCData.jc_live_reg
        tv_live_inviceNo.text = mJCData.jc_live_inv_num
        tv_live_inviceDate.text = mJCData.jc_live_invdate
        tv_live_inviceAmount.text = mJCData.jc_live_inv_amt
        tv_live_status.text = mJCData.jc_live_jobcard_status
//        tv_live_estimateView.text = mJCData.jc_live_estimate_view
//        tv_live_inviceView.text = mJCData.jc_live_invoice_view

        if(mJCData.jc_live_estimate_view!!.isNotEmpty()){
            tv_live_estimateView.text = "Click Here to View"
        }

        if(mJCData.jc_live_invoice_view!!.isNotEmpty()){
            tv_live_inviceView.text = "Click Here to View"
        }

        tv_live_estimateView.setPaintFlags(tv_live_estimateView.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        tv_live_estimateView.setTextColor(mContext.resources.getColor(R.color.yellow))
        tv_live_inviceView.setPaintFlags(tv_live_inviceView.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        tv_live_inviceView.setTextColor(mContext.resources.getColor(R.color.yellow))

        if (mJCData.jc_live_jobcard_status.equals("Job Card")) {

            tv_live_status.setPaintFlags(tv_live_status.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            tv_live_status.setTextColor(mContext.resources.getColor(R.color.yellow))

            ll_live_invoiceNo.visibility = View.GONE
            ll_live_invoiceDate.visibility = View.GONE
            ll_live_invoiceAmount.visibility = View.GONE
            ll_live_invoiceView.visibility = View.GONE
            ll_live_estimateView.visibility = View.GONE

        } else if (mJCData.jc_live_jobcard_status.equals("Estimate") || mJCData.jc_live_jobcard_status.equals(
                "Under Progress"
            )
        ) {

            tv_live_status.setPaintFlags(tv_live_status.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            tv_live_status.setTextColor(mContext.resources.getColor(R.color.yellow))

            ll_live_invoiceNo.visibility = View.GONE
            ll_live_invoiceDate.visibility = View.GONE
            ll_live_invoiceAmount.visibility = View.GONE
            ll_live_invoiceView.visibility = View.GONE
            ll_live_estimateView.visibility = View.VISIBLE

        }else if (mJCData.jc_live_jobcard_status.equals("Invoiced")) {
            ll_live_invoiceNo.visibility = View.VISIBLE
            ll_live_invoiceDate.visibility = View.VISIBLE
            ll_live_invoiceAmount.visibility = View.VISIBLE
            ll_live_invoiceView.visibility = View.VISIBLE
            ll_live_estimateView.visibility = View.GONE

        }

        container.addView(itemView)
        tv_live_estimateView.setOnClickListener {

            mJCData = mJobsList[position]

            val pdf_url = mJCData.jc_live_estimate_view

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
            mContext.startActivity(browserIntent)
        }

        tv_live_inviceView.setOnClickListener {

            mJCData = mJobsList[position]

            val pdf_url = mJCData.jc_live_invoice_view

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
            mContext.startActivity(browserIntent)
        }

        tv_live_status.setOnClickListener {
            mJCData = mJobsList[position]

            listener.onNavigate(mJCData, position)

        }

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
