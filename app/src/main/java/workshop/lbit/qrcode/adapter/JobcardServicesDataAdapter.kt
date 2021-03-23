package workshop.lbit.qrcode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.interfaces.JobCardListService
import workshop.lbit.qrcode.jobcard.JobCardServiceFragment


class JobcardServicesDataAdapter(
    private val mContext: Context,
    internal var mJobsList: List<JobcardData>,
    var mJOBCardList: JobCardListService
) : PagerAdapter() {

    internal lateinit var mJCData: JobcardData
    internal var layoutInflater: LayoutInflater
    private val listener = mJOBCardList


    lateinit var tv_service: MyTextView_Roboto_Medium
    lateinit var tv_cost: MyTextView_Roboto_Medium
    lateinit var tv_discount: MyTextView_Roboto_Medium
    lateinit var tv_finalPrice: MyTextView_Roboto_Medium
    lateinit var tv_jobid: MyTextView_Roboto_Medium
    lateinit var tv_edit: MyTextView_Roboto_Regular
    lateinit var tv_delete: MyTextView_Roboto_Regular

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
        val itemView = layoutInflater.inflate(R.layout.jobcard_services_data_row, container, false)

        mJCData = mJobsList[position]
        tv_service = itemView.findViewById(R.id.tv_service)
        tv_cost = itemView.findViewById(R.id.tv_cost)
        tv_discount = itemView.findViewById(R.id.tv_discount)
        tv_finalPrice = itemView.findViewById(R.id.tv_finalPrice)
        tv_edit = itemView.findViewById(R.id.tv_edit)
        tv_delete = itemView.findViewById(R.id.tv_delete)
        tv_jobid = itemView.findViewById(R.id.tv_jobid)

        tv_service.text = mJCData.jc_service_type
        tv_cost.text = mJCData.jc_service_mrp
        tv_discount.text = mJCData.jc_discount
        tv_finalPrice.text = mJCData.jc_final
        tv_jobid.text = mJCData.jc_job_id

        container.addView(itemView)


        tv_edit.setOnClickListener {
            mJCData = mJobsList[position]
            listener.onNavigate(mJCData, position,"edit")
        }

        tv_delete.setOnClickListener {
            mJCData = mJobsList[position]
            listener.onNavigate(mJCData, position,"delete")
        }
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
