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


class VendorJobcardSummaryServicesDataAdapter(
    internal var mContext: Context,
    internal var mJobcardDataList: List<JobcardData>
) : PagerAdapter() {

    internal var layoutInflater: LayoutInflater

    lateinit var mJCData: JobcardData

    private lateinit var tv_service_service: MyTextView_Roboto_Medium
    private lateinit var tv_service_cost: MyTextView_Roboto_Medium
    private lateinit var tv_service_discount: MyTextView_Roboto_Medium
    private lateinit var tv_service_finalprice: MyTextView_Roboto_Medium


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
        val itemView = layoutInflater.inflate(R.layout.vendor_jobcard_summary_services_data_row, container, false)

        mJCData = mJobcardDataList[position]


        tv_service_service = itemView.findViewById(R.id.tv_service_service)
        tv_service_cost = itemView.findViewById(R.id.tv_service_cost)
        tv_service_discount = itemView.findViewById(R.id.tv_service_discount)
        tv_service_finalprice = itemView.findViewById(R.id.tv_service_finalPrice)

        tv_service_service.text = mJCData.jc_service
        tv_service_cost.text = mJCData.jc_mrp
        tv_service_discount.text = mJCData.jc_discount
        tv_service_finalprice.text = mJCData.jc_final

        container.addView(itemView)

        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
