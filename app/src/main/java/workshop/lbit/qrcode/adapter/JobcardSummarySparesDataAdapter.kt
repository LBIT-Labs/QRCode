package workshop.lbit.qrcode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Bold
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.data.QrData
import workshop.lbit.qrcode.interfaces.QrDataList


class JobcardSummarySparesDataAdapter(
    internal var mContext: Context,
    internal var mJobcardDataList: List<JobcardData>
    ) : PagerAdapter() {

    internal var layoutInflater: LayoutInflater

    lateinit var mJCData: JobcardData

    private lateinit var tv_spare_ppsPartNo: MyTextView_Roboto_Medium
    private lateinit var tv_spare_partDesc: MyTextView_Roboto_Medium
    private lateinit var tv_spare_quantity: MyTextView_Roboto_Medium
    private lateinit var tv_spare_mrp: MyTextView_Roboto_Bold
    private lateinit var tv_spare_jobid: MyTextView_Roboto_Bold


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
        val itemView = layoutInflater.inflate(R.layout.jobcard_summary_spares_data_row, container, false)

        mJCData = mJobcardDataList[position]

        tv_spare_ppsPartNo = itemView.findViewById(R.id.tv_spare_ppsPartNo)
        tv_spare_partDesc = itemView.findViewById(R.id.tv_spare_partDesc)
        tv_spare_quantity = itemView.findViewById(R.id.tv_spare_quantity)
        tv_spare_mrp = itemView.findViewById(R.id.tv_spare_mrp)
        tv_spare_jobid = itemView.findViewById(R.id.tv_spare_jobid)

        tv_spare_ppsPartNo.text = mJCData.jc_oepart
        tv_spare_partDesc.text = mJCData.jc_spare
        tv_spare_quantity.text = mJCData.jc_qty
        tv_spare_mrp.text = mJCData.jc_final
        tv_spare_jobid.text = mJCData.jc_job_id

        container.addView(itemView)

        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
