package workshop.lbit.qrcode.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.activity.QrScanningRequestPartDetails
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.data.QrData


class QrScanningBoxesListAdapter(
    private val mContext: Context,
    qrScanningRequestList1: QrScanningRequestPartDetails,
    internal var qrScanningRequestList: List<QrData>,
    var mRole: String
) : RecyclerView.Adapter<QrScanningBoxesListAdapter.MyViewHolder>() {
    private val listener = qrScanningRequestList1

    internal lateinit var mData: QrData


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var qr_req_sno: MyTextView_Roboto_Medium
        var qr_req_custName: MyTextView_Roboto_Medium
        var qr_req_box_no: MyTextView_Roboto_Medium
        var qr_req_invoice: MyTextView_Roboto_Medium

        init {
            qr_req_sno = view.findViewById(R.id.qr_req_sno)
            qr_req_custName = view.findViewById(R.id.qr_req_custName)
            qr_req_box_no = view.findViewById(R.id.qr_req_box_no)
            qr_req_invoice = view.findViewById(R.id.qr_req_invoice)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.qr_scanning_box_list_adapter, parent, false)

        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        mData = qrScanningRequestList[position]


        holder.qr_req_sno.text = mData.qr_s_no
        holder.qr_req_custName.text = mData.qr_customerName
        holder.qr_req_box_no.text = mData.qr_box_no
        holder.qr_req_invoice.text = mData.qr_invoice_no
        holder.qr_req_box_no.setPaintFlags(holder.qr_req_box_no.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

        holder.qr_req_box_no.setOnClickListener {
            mData = qrScanningRequestList[position]

            listener?.onNavigate(mData, position, "edit")


            val integrator = IntentIntegrator(mContext as Activity?)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(false)
            integrator.initiateScan()
        }

    }

    override fun getItemCount(): Int {
        return qrScanningRequestList.size
    }
}
