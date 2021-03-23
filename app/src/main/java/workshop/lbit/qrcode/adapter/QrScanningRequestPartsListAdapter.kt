package workshop.lbit.qrcode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.data.QrData
import workshop.lbit.qrcode.interfaces.QrDataList


class QrScanningRequestPartsListAdapter(
    internal var mContext: Context,
    var qrDataList1: QrDataList,
    internal var qrDataList: List<QrData>,
    mRole: String
) : PagerAdapter() {

    private val listener = qrDataList1

    val mRole = mRole
    internal var layoutInflater: LayoutInflater
    private lateinit var qr_part_grn_number: MyTextView_Roboto_Regular
    private lateinit var qr_part_pps_part_number: MyTextView_Roboto_Regular
    private lateinit var qr_part_oe_part_number: MyTextView_Roboto_Regular
    private lateinit var qr_part_part_desc: MyTextView_Roboto_Regular
    private lateinit var qr_part_quantity: MyTextView_Roboto_Regular
    private lateinit var qr_part_mrp: MyTextView_Roboto_Regular
    private lateinit var qr_part_pps_storageBin: MyTextView_Roboto_Regular
    private lateinit var qr_part_supplier: MyTextView_Roboto_Regular
    private lateinit var qr_part_counter_location: MyTextView_Roboto_Regular
    private lateinit var qr_part_select_btn: MyTextView_Roboto_Regular
    private lateinit var qr_part_edit_btn: MyTextView_Roboto_Regular
    private lateinit var ll_qr_part_counterLocation: LinearLayout
    private lateinit var ll_qr_part_supplier: LinearLayout
    private lateinit var ll_qr_part_pps_part_no: LinearLayout
    private lateinit var ll_qr_part_oe_part_no: LinearLayout
    private lateinit var ll_qr_part_grn: LinearLayout

    lateinit var qrData: QrData


    init {

        layoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return qrDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.qr_part_data_row, container, false)

        qrData = qrDataList[position]

        qr_part_grn_number = itemView.findViewById(R.id.qr_part_grn_number)
        qr_part_pps_part_number = itemView.findViewById(R.id.qr_part_pps_part_number)
        qr_part_oe_part_number = itemView.findViewById(R.id.qr_part_oe_part_number)
        qr_part_part_desc = itemView.findViewById(R.id.qr_part_part_desc)
        qr_part_quantity = itemView.findViewById(R.id.qr_part_quantity)
        qr_part_mrp = itemView.findViewById(R.id.qr_part_mrp)
        qr_part_pps_storageBin = itemView.findViewById(R.id.qr_part_pps_storageBin)
        qr_part_supplier = itemView.findViewById(R.id.qr_part_supplier)
        qr_part_counter_location = itemView.findViewById(R.id.qr_part_counter_location)
        qr_part_select_btn = itemView.findViewById(R.id.qr_part_select_btn)
        qr_part_edit_btn = itemView.findViewById(R.id.qr_part_edit_btn)
        ll_qr_part_counterLocation = itemView.findViewById(R.id.ll_qr_part_counterLocation)
        ll_qr_part_supplier = itemView.findViewById(R.id.ll_qr_part_supplier)
        ll_qr_part_pps_part_no = itemView.findViewById(R.id.ll_qr_part_pps_part_no)
        ll_qr_part_oe_part_no = itemView.findViewById(R.id.ll_qr_part_oe_part_no)
        ll_qr_part_grn = itemView.findViewById(R.id.ll_qr_part_grn)




        if (mRole.equals("wh_store_boy")){

            ll_qr_part_counterLocation.visibility = View.GONE
            ll_qr_part_supplier.visibility = View.GONE
            ll_qr_part_pps_part_no.visibility = View.VISIBLE
            ll_qr_part_oe_part_no.visibility = View.GONE
            qr_part_edit_btn.visibility = View.VISIBLE
            qr_part_grn_number.text = qrData.qr_grn_number
            qr_part_pps_part_number.text = qrData.qr_oem_num
            qr_part_part_desc.text = qrData.qr_part_description
            qr_part_mrp.text = qrData.qr_mrp
            qr_part_quantity.text = qrData.qr_order_qty
            qr_part_pps_storageBin.text = qrData.qr_bin_location
        }else{
            qr_part_grn_number.text = qrData.qr_grn_number
            qr_part_pps_part_number.text = qrData.qr_pps_part_no
            qr_part_oe_part_number.text = qrData.qr_oe_part_no
            qr_part_part_desc.text = qrData.qr_part_description
            qr_part_mrp.text = qrData.qr_mrp
            qr_part_quantity.text = qrData.qr_quantity
            qr_part_supplier.text = qrData.qr_supplier
            qr_part_pps_storageBin.text = qrData.qr_storage_bin
            qr_part_counter_location.text = qrData.qr_counter_location

        }


        container.addView(itemView)
        qr_part_select_btn.setOnClickListener {
            qrData = qrDataList[position]

            listener?.onNavigate(qrData,position,"scan")


        }

        qr_part_edit_btn.setOnClickListener {
            qrData = qrDataList[position]

            listener?.onNavigate(qrData,position,"edit")


        }
        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}
