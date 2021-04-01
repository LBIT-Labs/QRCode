package workshop.lbit.qrcode.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class JobcardData {

    @SerializedName("nid")
    @Expose
    var jc_nid: String? = null

    @SerializedName("job_ctg")
    @Expose
    var jc_job_ctg: String? = null

    @SerializedName("job_name")
    @Expose
    var jc_job_name: String? = null

    @SerializedName("job_id")
    @Expose
    var jc_job_id: String? = null

    @SerializedName("workshop_note")
    @Expose
    var jc_workshop_note: String? = null

    @SerializedName("customer_note")
    @Expose
    var jc_customer_note: String? = null


    @SerializedName("pid")
    @Expose
    var jc_pid: String? = null

    @SerializedName("part_grn")
    @Expose
    var jc_part_grn: String? = null

    @SerializedName("part_desc")
    @Expose
    var jc_part_desc: String? = null

    @SerializedName("oe_part")
    @Expose
    var jc_oe_part: String? = null

    @SerializedName("qty")
    @Expose
    var jc_qty: String? = null

    @SerializedName("mrp")
    @Expose
    var jc_mrp: String? = null

    @SerializedName("part_hsn")
    @Expose
    var jc_part_hsn: String? = null

    @SerializedName("hsn")
    @Expose
    var jc_hsn: String? = null

    @SerializedName("tax")
    @Expose
    var jc_tax: String? = null

    @SerializedName("spare")
    @Expose
    var jc_spare: String? = null

    @SerializedName("oepart")
    @Expose
    var jc_oepart: String? = null

    @SerializedName("service")
    @Expose
    var jc_service: String? = null


    @SerializedName("dis")
    @Expose
    var jc_dis: String? = null

    @SerializedName("sno")
    @Expose
    var jc_sno: String? = null

    @SerializedName("service_type")
    @Expose
    var jc_service_type: String? = null

    @SerializedName("service_mrp")
    @Expose
    var jc_service_mrp: String? = null

    @SerializedName("discount")
    @Expose
    var jc_discount: String? = null

    @SerializedName("final")
    @Expose
    var jc_final: String? = null

    @SerializedName("hours")
    @Expose
    var jc_hours: String? = null


    @SerializedName("customer")
    @Expose
    var jc_live_customer: String? = null

    @SerializedName("mobile")
    @Expose
    var jc_live_mobile: String? = null

    @SerializedName("jobcard_id")
    @Expose
    var jc_live_jobcard_id: String? = null

    @SerializedName("reg")
    @Expose
    var jc_live_reg: String? = null

    @SerializedName("jobcard_status")
    @Expose
    var jc_live_jobcard_status: String? = null

    @SerializedName("service_status")
    @Expose
    var jc_live_service_status: String? = null

    @SerializedName("estimate_view")
    @Expose
    var jc_live_estimate_view: String? = null

    @SerializedName("invoice_view")
    @Expose
    var jc_live_invoice_view: String? = null

    @SerializedName("invdate")
    @Expose
    var jc_live_invdate: String? = null

    @SerializedName("tech")
    @Expose
    var jc_live_tech: String? = null


    @SerializedName("jobcard_date")
    @Expose
    var jc_live_jobcard_date: String? = null

    @SerializedName("inv_num")
    @Expose
    var jc_live_inv_num: String? = null

    @SerializedName("inv_amt")
    @Expose
    var jc_live_inv_amt: String? = null

    @SerializedName("make")
    @Expose
    var jc_live_make: String? = null

    @SerializedName("model")
    @Expose
    var jc_live_model: String? = null

    @SerializedName("vendor")
    @Expose
    var jc_live_vendor: String? = null

    @SerializedName("vendor_id")
    @Expose
    var jc_live_vendor_id: String? = null

    @SerializedName("status")
    @Expose
    var jc_live_status: String? = null

    @SerializedName("gatepass_no")
    @Expose
    var jc_live_gatepass_no: String? = null

    @SerializedName("gatepass_status")
    @Expose
    var jc_live_gatepass_status: String? = null

    @SerializedName("gatepass_date")
    @Expose
    var jc_live_gatepass_date: String? = null

    @SerializedName("gatepass_in_out_status")
    @Expose
    var jc_live_gatepass_in_out_status: String? = null

    @SerializedName("type")
    @Expose
    var jc_live_type: String? = null

    @SerializedName("field_gatepass_status")
    @Expose
    var jc_live_field_gatepass_status: String? = null

    @SerializedName("qr_scan_status")
    @Expose
    var jc_live_qr_scan_status: String? = null


    constructor()
    constructor(

        jc_nid: String?,
        jc_job_ctg: String?,
        jc_job_id: String?,
        jc_job_name: String?,
        jc_workshop_note: String?,
        jc_customer_note: String?,
        jc_pid: String?,
        jc_oe_part: String?,
        jc_part_desc: String?,
        jc_part_grn: String?,
        jc_part_hsn: String?,
        jc_qty: String?,
        jc_tax: String?,
        jc_mrp: String?,
        jc_spare: String?,
        jc_oepart: String?,
        jc_service: String?,
        jc_dis: String?,
        jc_sno: String?,
        jc_service_type: String?,
        jc_service_mrp: String?,
        jc_discount: String?,
        jc_final: String?,
        jc_hours: String?,
        jc_live_customer: String?,
        jc_live_mobile: String?,
        jc_live_jobcard_id: String?,
        jc_live_jobcard_date: String?,
        jc_live_jobcard_status: String?,
        jc_live_service_status: String?,
        jc_live_reg: String?,
        jc_live_invdate: String?,
        jc_live_invoice_view: String?,
        jc_live_estimate_view: String?,
        jc_live_inv_num: String?,
        jc_live_inv_amt: String?,
        jc_live_make: String?,
        jc_live_model: String?,
        jc_live_vendor: String?,
        jc_live_vendor_id: String?,
        jc_live_status: String?,
        jc_hsn: String?,
        jc_live_gatepass_no: String?,
        jc_live_gatepass_date: String?,
        jc_live_gatepass_status: String?,
        jc_live_gatepass_in_out_status: String?,
        jc_live_tech: String?,
        jc_live_type: String?,
        jc_live_field_gatepass_status: String?,
        jc_live_qr_scan_status: String?

    ) : super() {

        this.jc_nid = jc_nid
        this.jc_job_ctg = jc_job_ctg
        this.jc_job_id = jc_job_id
        this.jc_job_name = jc_job_name
        this.jc_workshop_note = jc_workshop_note
        this.jc_customer_note = jc_customer_note
        this.jc_pid = jc_pid
        this.jc_oe_part = jc_oe_part
        this.jc_part_desc = jc_part_desc
        this.jc_part_grn = jc_part_grn
        this.jc_part_hsn = jc_part_hsn
        this.jc_qty = jc_qty
        this.jc_tax = jc_tax
        this.jc_mrp = jc_mrp
        this.jc_spare = jc_spare
        this.jc_oepart = jc_oepart
        this.jc_service = jc_service
        this.jc_dis = jc_dis
        this.jc_sno = jc_sno
        this.jc_service_type = jc_service_type
        this.jc_service_mrp = jc_service_mrp
        this.jc_discount = jc_discount
        this.jc_final = jc_final
        this.jc_hours = jc_hours
        this.jc_live_customer = jc_live_customer
        this.jc_live_mobile = jc_live_mobile
        this.jc_live_jobcard_id = jc_live_jobcard_id
        this.jc_live_jobcard_date = jc_live_jobcard_date
        this.jc_live_jobcard_status = jc_live_jobcard_status
        this.jc_live_service_status = jc_live_service_status
        this.jc_live_reg = jc_live_reg
        this.jc_live_invdate = jc_live_invdate
        this.jc_live_invoice_view = jc_live_invoice_view
        this.jc_live_estimate_view = jc_live_estimate_view
        this.jc_live_inv_num = jc_live_inv_num
        this.jc_live_inv_amt = jc_live_inv_amt
        this.jc_live_make = jc_live_make
        this.jc_live_model = jc_live_model
        this.jc_live_vendor = jc_live_vendor
        this.jc_live_vendor_id = jc_live_vendor_id
        this.jc_live_status = jc_live_status
        this.jc_hsn = jc_hsn
        this.jc_live_gatepass_no = jc_live_gatepass_no
        this.jc_live_gatepass_date = jc_live_gatepass_date
        this.jc_live_gatepass_status = jc_live_gatepass_status
        this.jc_live_gatepass_in_out_status = jc_live_gatepass_in_out_status
        this.jc_live_tech = jc_live_tech
        this.jc_live_type = jc_live_type
        this.jc_live_field_gatepass_status = jc_live_field_gatepass_status
        this.jc_live_qr_scan_status = jc_live_qr_scan_status
    }
}