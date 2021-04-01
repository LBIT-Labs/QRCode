package workshop.lbit.qrcode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.JobcardData


class JobcardSummarySparesDataAdapter(
    internal var mContext: Context,
    internal var mJobcardDataList: List<JobcardData>
) : PagerAdapter() {

    internal var layoutInflater: LayoutInflater

    lateinit var mJCData: JobcardData

    private lateinit var tv_spare_ppsPartNo: MyTextView_Roboto_Medium
    private lateinit var tv_spare_partDesc: MyTextView_Roboto_Medium
    private lateinit var tv_spare_quantity: MyTextView_Roboto_Medium
    private lateinit var tv_spare_mrp: MyTextView_Roboto_Medium
    private lateinit var tv_spare_finalprice: MyTextView_Roboto_Medium
    private lateinit var tv_spare_jobid: MyTextView_Roboto_Medium
    private lateinit var tv_spare_gatepass_status: MyTextView_Roboto_Medium
    private lateinit var tv_spare_scanStatus: MyTextView_Roboto_Medium
    private lateinit var tv_spare_source: MyTextView_Roboto_Medium
    private lateinit var tv_spare_discount: MyTextView_Roboto_Medium
    private lateinit var ll_gatepassStatus: LinearLayout


    init {

        layoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mJobcardDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.jobcard_summary_spares_data_row, container, false)

        mJCData = mJobcardDataList[position]

        tv_spare_ppsPartNo = itemView.findViewById(R.id.tv_spare_ppsPartNo)
        tv_spare_partDesc = itemView.findViewById(R.id.tv_spare_partDesc)
        tv_spare_quantity = itemView.findViewById(R.id.tv_spare_quantity)
        tv_spare_mrp = itemView.findViewById(R.id.tv_spare_mrp)
        tv_spare_finalprice = itemView.findViewById(R.id.tv_spare_finalprice)
        tv_spare_discount = itemView.findViewById(R.id.tv_spare_discount)
        tv_spare_jobid = itemView.findViewById(R.id.tv_spare_jobid)
        tv_spare_source = itemView.findViewById(R.id.tv_spare_source)
        tv_spare_gatepass_status = itemView.findViewById(R.id.tv_spare_gatepass_status)
        tv_spare_scanStatus = itemView.findViewById(R.id.tv_spare_scanStatus)
        ll_gatepassStatus = itemView.findViewById(R.id.ll_gatepassStatus)

        tv_spare_ppsPartNo.text = mJCData.jc_oepart
        tv_spare_partDesc.text = mJCData.jc_spare
        tv_spare_quantity.text = mJCData.jc_qty
        tv_spare_mrp.text = mJCData.jc_mrp
        tv_spare_finalprice.text = mJCData.jc_final
        tv_spare_discount.text = mJCData.jc_discount
        tv_spare_jobid.text = mJCData.jc_job_id

        if (mJCData.jc_live_type!!.isNotEmpty()) {
            if (mJCData.jc_live_type.equals("inhouse")) {
                tv_spare_source.text = "In-House"
                tv_spare_scanStatus.text = mJCData.jc_live_qr_scan_status

            } else {
                tv_spare_source.text = "Out-House"
                tv_spare_scanStatus.text = "NA"

            }
        } else {
            tv_spare_source.text = "Out-House"
            tv_spare_scanStatus.text = "NA"

        }

        if (mJCData.jc_live_field_gatepass_status!!.isNotEmpty()) {
            if (mJCData.jc_live_field_gatepass_status.equals("in")) {
                tv_spare_gatepass_status.text = "Closed"

            } else {
                tv_spare_gatepass_status.text = "Open"

            }
        } else {
            tv_spare_gatepass_status.text = "Open"

        }

        if (mJCData.jc_live_type!!.isNotEmpty()) {
            if (mJCData.jc_live_type.equals("inhouse")) {
                ll_gatepassStatus.visibility = View.GONE
            } else {
                ll_gatepassStatus.visibility = View.VISIBLE
            }
        } else {
            ll_gatepassStatus.visibility = View.GONE

        }

        container.addView(itemView)

        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
