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
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.data.QrData
import workshop.lbit.qrcode.interfaces.QrDataList


class Qr_Inventory_Management_adapter(
    internal var mContext: Context,
    var qrDataList1:QrDataList,
    internal var qrDataList: List<QrData>
    ) : PagerAdapter() {

    private val listener = qrDataList1

    internal var layoutInflater: LayoutInflater
    private lateinit var tv_im_part_desc: MyTextView_Roboto_Regular
    private lateinit var tv_im_oe_part_desc: MyTextView_Roboto_Regular
    private lateinit var tv_im_part_no: MyTextView_Roboto_Regular
    private lateinit var tv_im_part_category: MyTextView_Roboto_Regular
    private lateinit var tv_im_make: MyTextView_Roboto_Regular
    private lateinit var tv_im_qty: MyTextView_Roboto_Bold
    private lateinit var tv_im_mrp: MyTextView_Roboto_Regular
    private lateinit var tv_im_location: MyTextView_Roboto_Regular
    private lateinit var tv_im_supplier: MyTextView_Roboto_Regular
    private lateinit var tv_im_hsn: MyTextView_Roboto_Regular
    private lateinit var add_btn: MyTextView_Roboto_Regular

    lateinit var qrData: QrData


    init {

        layoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return qrDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.im_part_details_row, container, false)

        qrData = qrDataList[position]

        tv_im_part_desc = itemView.findViewById(R.id.tv_im_part_desc)
        tv_im_oe_part_desc = itemView.findViewById(R.id.tv_im_oe_part_desc)
        tv_im_part_no = itemView.findViewById(R.id.tv_im_part_no)
        tv_im_part_category = itemView.findViewById(R.id.tv_im_part_category)
        tv_im_make = itemView.findViewById(R.id.tv_im_make)
        tv_im_qty = itemView.findViewById(R.id.tv_im_qty)
        tv_im_mrp = itemView.findViewById(R.id.tv_im_mrp)
        tv_im_location = itemView.findViewById(R.id.tv_im_location)
        tv_im_supplier = itemView.findViewById(R.id.tv_im_supplier)
        tv_im_hsn = itemView.findViewById(R.id.tv_im_hsn)
        add_btn = itemView.findViewById(R.id.add_btn)


        if(qrData.qr_part_part_dsc != null){
            tv_im_part_desc.text = qrData.qr_part_part_dsc

        }

        if(qrData.qr_part_oem_num != null){
            tv_im_oe_part_desc.text = qrData.qr_part_oem_num

        }

        if(qrData.qr_part_part_num != null){
            tv_im_part_no.text = qrData.qr_part_part_num

        }

        if(qrData.qr_part_part_ctg != null){
            tv_im_part_category.text = qrData.qr_part_part_ctg

        }

        if(qrData.qr_part_make != null){
            tv_im_make.text = qrData.qr_part_make

        }

        if(qrData.qr_quantity != null){
            tv_im_qty.text = qrData.qr_quantity

        }

        if(qrData.qr_mrp != null){
            tv_im_mrp.text = qrData.qr_mrp

        }

        if(qrData.qr_part_location != null){
            tv_im_location.text = qrData.qr_part_location

        }

        if(qrData.qr_supplier != null){
            tv_im_supplier.text = qrData.qr_supplier

        }

        if(qrData.qr_part_hsn != null){
            tv_im_hsn.text = qrData.qr_part_hsn

        }



        container.addView(itemView)
        add_btn.setOnClickListener {
            qrData = qrDataList[position]

            listener.onNavigate(qrData,position,"")

        }
        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
