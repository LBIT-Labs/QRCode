package workshop.lbit.qrcode.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.QrData


class QrScanningHistoryListAdapter(
    private val mContext: Context,
    internal var qrScanningHistoryList: List<QrData>,
    var mRole: String,
    var mGatePassType: String
) : RecyclerView.Adapter<QrScanningHistoryListAdapter.MyViewHolder>() {

    internal lateinit var mData: QrData

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var qr_his_sno: MyTextView_Roboto_Medium
        var qr_his_custName: MyTextView_Roboto_Medium
        var qr_his_ref: MyTextView_Roboto_Medium
        var qr_his_qty: MyTextView_Roboto_Medium
        var qr_his_date: MyTextView_Roboto_Medium
        var qr_his_invoice: MyTextView_Roboto_Medium
        var qr_his_jobcard: MyTextView_Roboto_Medium

        init {
            qr_his_sno = view.findViewById(R.id.qr_his_sno)
            qr_his_custName = view.findViewById(R.id.qr_his_custName)
            qr_his_ref = view.findViewById(R.id.qr_his_ref)
            qr_his_qty = view.findViewById(R.id.qr_his_qty)
            qr_his_date = view.findViewById(R.id.qr_his_date)
            qr_his_invoice = view.findViewById(R.id.qr_his_invoice)
            qr_his_jobcard = view.findViewById(R.id.qr_his_jobcard)

            if (mRole.equals("stores") || mRole.equals("counter")) {
                qr_his_invoice.visibility = View.GONE
                qr_his_ref.visibility = View.VISIBLE
            } else if (mRole.equals("wh_store_boy")) {
                qr_his_invoice.visibility = View.GONE
                qr_his_ref.visibility = View.VISIBLE
                qr_his_date.visibility = View.VISIBLE
            } else {
                qr_his_invoice.visibility = View.VISIBLE
                qr_his_ref.visibility = View.GONE
                qr_his_date.visibility = View.VISIBLE

            }


            if (mGatePassType.isNotEmpty()) {
                if (mGatePassType.equals("CounterSale")) {
                    qr_his_qty.visibility = View.VISIBLE
                    qr_his_jobcard.visibility = View.GONE
                } else if (mGatePassType.equals("Jobcard")) {
                    qr_his_qty.visibility = View.GONE
                    qr_his_jobcard.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.qr_scanning_history__list_adapter, parent, false)

        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        mData = qrScanningHistoryList[position]

        if(mRole.equals("wh_security")) {

            holder.qr_his_sno.text = mData.qr_s_no
            holder.qr_his_custName.text = mData.qr_gate_pass_no
            holder.qr_his_custName.paintFlags =
                holder.qr_his_custName.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.qr_his_ref.text = mData.qr_quantity
            holder.qr_his_qty.text = mData.qr_gross_weight
            holder.qr_his_invoice.text = mData.qr_quantity
            holder.qr_his_date.text = mData.qr_date

        }else {
            holder.qr_his_sno.text = mData.qr_s_no
            holder.qr_his_custName.text = mData.qr_customerName
            holder.qr_his_ref.text = mData.qr_ref_id
            holder.qr_his_qty.text = mData.qr_quantity
            holder.qr_his_date.text = mData.qr_date
            holder.qr_his_invoice.text = mData.qr_invoice_number
            holder.qr_his_jobcard.text = mData.qr_jobcard_id
        }

    }

    override fun getItemCount(): Int {
        return qrScanningHistoryList.size
    }
}
