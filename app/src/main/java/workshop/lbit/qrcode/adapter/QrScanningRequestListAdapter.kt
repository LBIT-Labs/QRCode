package workshop.lbit.qrcode.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.activity.QrScanningGatepassDetails
import workshop.lbit.qrcode.activity.QrScanningRequestPartDetails
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.QrData


class QrScanningRequestListAdapter(
    private val mContext: Context,
    internal var qrScanningRequestList: List<QrData>,
    var mRole: String,
    var mGatePassType: String
) : RecyclerView.Adapter<QrScanningRequestListAdapter.MyViewHolder>() {

    internal lateinit var mData: QrData


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var qr_req_sno: MyTextView_Roboto_Medium
        var qr_req_custName: MyTextView_Roboto_Medium
        var qr_req_ref: MyTextView_Roboto_Medium
        var qr_req_qty: MyTextView_Roboto_Medium
        var qr_req_invoice: MyTextView_Roboto_Medium

        init {
            qr_req_sno = view.findViewById(R.id.qr_req_sno)
            qr_req_custName = view.findViewById(R.id.qr_req_custName)
            qr_req_ref = view.findViewById(R.id.qr_req_ref)
            qr_req_qty = view.findViewById(R.id.qr_req_qty)
            qr_req_invoice = view.findViewById(R.id.qr_req_invoice)

            if (mRole.equals("stores")) {
                qr_req_invoice.visibility = View.GONE
                qr_req_ref.visibility = View.VISIBLE
            } else if (mRole.equals("wh_store_boy")) {
                qr_req_invoice.visibility = View.GONE
                qr_req_ref.visibility = View.VISIBLE
            } else if (mRole.equals("counter")) {
                qr_req_invoice.visibility = View.GONE
                qr_req_ref.visibility = View.VISIBLE
            } else {
                qr_req_invoice.visibility = View.VISIBLE
                qr_req_ref.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.qr_scanning_request_list_adapter, parent, false)

        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        mData = qrScanningRequestList[position]


        if (mRole.equals("wh_security")) {

            holder.qr_req_sno.text = mData.qr_s_no
            holder.qr_req_custName.text = mData.qr_gate_pass_no
            holder.qr_req_custName.setPaintFlags(holder.qr_req_custName.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            holder.qr_req_ref.text = mData.qr_quantity
            holder.qr_req_qty.text = mData.qr_gross_weight
            holder.qr_req_invoice.text = mData.qr_quantity

        } else {
            holder.qr_req_sno.text = mData.qr_s_no
            holder.qr_req_custName.text = mData.qr_customerName
            holder.qr_req_custName.setPaintFlags(holder.qr_req_custName.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            holder.qr_req_ref.text = mData.qr_ref_id
            holder.qr_req_qty.text = mData.qr_quantity
            holder.qr_req_invoice.text = mData.qr_invoice_number
        }

        holder.qr_req_custName.setOnClickListener(View.OnClickListener {
            mData = qrScanningRequestList[position]


            if (mRole.equals("wh_security")) {
                val intent: Intent = Intent(mContext, QrScanningRequestPartDetails::class.java)

                intent.putExtra("nid", mData.qr_gate_pass_no)
                intent.putExtra("invoice", "")
                intent.putExtra("cust_name", "")
                intent.putExtra("crn", mData.qr_quantity)
                intent.putExtra("ref", mData.qr_gate_pass_no)
                mContext.startActivity(intent)

            } else if (mRole.equals("security")) {

                if (mGatePassType.isNotEmpty()) {
                    if (mGatePassType.equals("CounterSale")) {
                        val intent: Intent =
                            Intent(mContext, QrScanningRequestPartDetails::class.java)

                        intent.putExtra("nid", mData.qr_nid)
                        intent.putExtra("cust_name", mData.qr_customerName)

                        if (!mData.qr_invoice_number.equals(null)) {
                            intent.putExtra("invoice", mData.qr_invoice_number)
                            intent.putExtra("crn", mData.qr_crn)

                        }
                        if (!mData.qr_ref_id.equals(null)) {
                            intent.putExtra("ref", mData.qr_ref_id)

                        }
                        mContext.startActivity(intent)
                    } else if (mGatePassType.equals("Jobcard")) {
                        val intent: Intent = Intent(mContext, QrScanningGatepassDetails::class.java)

                        intent.putExtra("nid", mData.qr_nid)

                        mContext.startActivity(intent)
                    }
                }


            } else {
                val intent: Intent = Intent(mContext, QrScanningRequestPartDetails::class.java)

                intent.putExtra("nid", mData.qr_nid)
                intent.putExtra("cust_name", mData.qr_customerName)

                if (!mData.qr_invoice_number.equals(null)) {
                    intent.putExtra("invoice", mData.qr_invoice_number)
                    intent.putExtra("crn", mData.qr_crn)

                }
                if (!mData.qr_ref_id.equals(null)) {
                    intent.putExtra("ref", mData.qr_ref_id)

                }
                mContext.startActivity(intent)

            }

        })

    }

    override fun getItemCount(): Int {
        return qrScanningRequestList.size
    }
}
