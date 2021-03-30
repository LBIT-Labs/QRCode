package workshop.lbit.qrcode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.interfaces.JobCardListService


class VendorJobcardSparesDataAdapter(
    private val mContext: Context,
    internal var mJobsList: List<JobcardData>,
    var mJOBCardList: JobCardListService
) : PagerAdapter() {


    internal lateinit var mJCData: JobcardData
    internal var layoutInflater: LayoutInflater
    private val listener = mJOBCardList


    lateinit var tv_pps_part_no: MyTextView_Roboto_Medium
    lateinit var tv_pps_part_desc: MyTextView_Roboto_Medium
    lateinit var tv_pps_part_quantity: MyTextView_Roboto_Medium
    lateinit var tv_pps_part_mrp: MyTextView_Roboto_Medium
    lateinit var tv_pps_discount: MyTextView_Roboto_Medium
    lateinit var tv_pps_finalprice: MyTextView_Roboto_Medium
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
        val itemView = layoutInflater.inflate(R.layout.vendor_jobcard_spares_data_row, container, false)

        mJCData = mJobsList[position]
        tv_pps_part_no = itemView.findViewById(R.id.tv_pps_part_no)
        tv_pps_part_desc = itemView.findViewById(R.id.tv_pps_part_desc)
        tv_pps_part_quantity = itemView.findViewById(R.id.tv_pps_part_quantity)
        tv_pps_part_mrp = itemView.findViewById(R.id.tv_pps_part_mrp)
        tv_pps_discount = itemView.findViewById(R.id.tv_pps_discount)
        tv_pps_finalprice = itemView.findViewById(R.id.tv_pps_finalprice)
        tv_edit = itemView.findViewById(R.id.tv_edit)
        tv_delete = itemView.findViewById(R.id.tv_delete)

        tv_pps_part_no.text = mJCData.jc_oepart
        tv_pps_part_desc.text = mJCData.jc_part_desc
        tv_pps_part_quantity.text = mJCData.jc_qty
        tv_pps_part_mrp.text = mJCData.jc_mrp
        tv_pps_discount.text = mJCData.jc_discount
        tv_pps_finalprice.text = mJCData.jc_final

        container.addView(itemView)

        tv_edit.setOnClickListener {
            mJCData = mJobsList[position]
            listener.onNavigate(mJCData, position, "edit")
        }

        tv_delete.setOnClickListener {
            mJCData = mJobsList[position]
            listener.onNavigate(mJCData, position, "delete")
        }
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}

