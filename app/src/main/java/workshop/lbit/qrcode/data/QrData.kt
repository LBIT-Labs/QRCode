package workshop.lbit.qrcode.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class QrData {


    @SerializedName("nid")
    @Expose
    var qr_nid: String? = null

    @SerializedName("s_no")
    @Expose
    var qr_s_no: String? = null

    @SerializedName("customer")
    @Expose
    var qr_customerName: String? = null

    @SerializedName("reference_id")
    @Expose
    var qr_ref_id: String? = null

    @SerializedName("qty")
    @Expose
    var qr_quantity: String? = null

    @SerializedName("pps_part_no")
    @Expose
    var qr_pps_part_no: String? = null

    @SerializedName("oe_part_no")
    @Expose
    var qr_oe_part_no: String? = null

    @SerializedName("part_description")
    @Expose
    var qr_part_description: String? = null

    @SerializedName("grn_number")
    @Expose
    var qr_grn_number: String? = null

    @SerializedName("mrp")
    @Expose
    var qr_mrp: String? = null

    @SerializedName("supplier")
    @Expose
    var qr_supplier: String? = null

    @SerializedName("storage_bin")
    @Expose
    var qr_storage_bin: String? = null

    @SerializedName("counter_location")
    @Expose
    var qr_counter_location: String? = null

    @SerializedName("status")
    @Expose
    var qr_status: String? = null

    @SerializedName("inv_number")
    @Expose
    var qr_invoice_number: String? = null


    @SerializedName("crn")
    @Expose
    var qr_crn: String? = null

    @SerializedName("date")
    @Expose
    var qr_date: String? = null


    @SerializedName("type")
    @Expose
    var qr_type: String? = null

    @SerializedName("pid")
    @Expose
    var qr_pid: String? = null

    @SerializedName("pps_partno")
    @Expose
    var qr_oem_num: String? = null

    @SerializedName("bin_location")
    @Expose
    var qr_bin_location: String? = null

    @SerializedName("order_qty")
    @Expose
    var qr_order_qty: String? = null

    @SerializedName("gate_pass_no")
    @Expose
    var qr_gate_pass_no: String? = null

    @SerializedName("gross_weight")
    @Expose
    var qr_gross_weight: String? = null

    @SerializedName("order_nid")
    @Expose
    var qr_order_nid: String? = null

    @SerializedName("box_no")
    @Expose
    var qr_box_no: String? = null

    @SerializedName("invoice_no")
    @Expose
    var qr_invoice_no: String? = null

    @SerializedName("oem_num")
    @Expose
    var qr_part_oem_num: String? = null

    @SerializedName("part_dsc")
    @Expose
    var qr_part_part_dsc: String? = null

    @SerializedName("part_num")
    @Expose
    var qr_part_part_num: String? = null

    @SerializedName("hsn")
    @Expose
    var qr_part_hsn: String? = null

    @SerializedName("location")
    @Expose
    var qr_part_location: String? = null

    @SerializedName("make")
    @Expose
    var qr_part_make: String? = null


    @SerializedName("part_ctg")
    @Expose
    var qr_part_part_ctg: String? = null

    @SerializedName("man_qr_no")
    @Expose
    var qr_part_man_qr_no: String? = null

    @SerializedName("oe_part")
    @Expose
    var qr_oe_part: String? = null


    @SerializedName("part_desc")
    @Expose
    var qr_part_desc: String? = null

    @SerializedName("available_qty")
    @Expose
    var qr_available_qty: String? = null

    @SerializedName("tax")
    @Expose
    var qr_tax: String? = null

    constructor() {}

    /**
     *
     * @param fieldDriveCustomerValue
     * @param fieldTestDriveDateValue
     * @param fieldDriveSalesConsultantValue
     * @param fieldApprovalStatusValue
     * @param fieldTestDriveLocationValue
     */
    constructor(

        qr_nid: String?,
        qr_customerName: String?,
        qr_ref_id: String?,
        qr_quantity: String?,
        qr_pps_part_no: String?,
        qr_oe_part_no: String?,
        qr_part_description: String?,
        qr_mrp: String?,
        qr_supplier: String?,
        qr_storage_bin: String?,
        qr_counter_location: String?,
        qr_grn_number: String?,
        qr_status: String?,
        qr_invoice_number: String?,
        qr_crn: String?,
        qr_s_no: String?,
        qr_date: String?,
        qr_type: String?,
        qr_pid: String?,
        qr_oem_num: String?,
        qr_order_qty: String?,
        qr_bin_location: String?,
        qr_gate_pass_no: String?,
        qr_order_nid: String?,
        qr_gross_weight: String?,
        qr_box_no: String?,
        qr_invoice_no: String?,
        qr_part_oem_num: String?,
        qr_part_part_num: String?,
        qr_part_part_dsc: String?,
        qr_part_hsn: String?,
        qr_part_location: String?,
        qr_part_make: String?,
        qr_part_part_ctg: String?,
        qr_part_man_qr_no: String?,
        qr_oe_part: String?,
        qr_part_desc: String?,
        qr_available_qty: String?,
        qr_tax: String?

    ) : super() {

        this.qr_nid = qr_nid
        this.qr_customerName = qr_customerName
        this.qr_ref_id = qr_ref_id
        this.qr_quantity = qr_quantity
        this.qr_pps_part_no = qr_pps_part_no
        this.qr_oe_part_no = qr_oe_part_no
        this.qr_part_description = qr_part_description
        this.qr_mrp = qr_mrp
        this.qr_supplier = qr_supplier
        this.qr_storage_bin = qr_storage_bin
        this.qr_counter_location = qr_counter_location
        this.qr_grn_number = qr_grn_number
        this.qr_status = qr_status
        this.qr_invoice_number = qr_invoice_number
        this.qr_crn = qr_crn
        this.qr_s_no = qr_s_no
        this.qr_date = qr_date
        this.qr_type = qr_type
        this.qr_pid = qr_pid
        this.qr_oem_num = qr_oem_num
        this.qr_order_qty = qr_order_qty
        this.qr_bin_location = qr_bin_location
        this.qr_gate_pass_no = qr_gate_pass_no
        this.qr_order_nid = qr_order_nid
        this.qr_gross_weight = qr_gross_weight
        this.qr_box_no = qr_box_no
        this.qr_invoice_no = qr_invoice_no
        this.qr_part_oem_num = qr_part_oem_num
        this.qr_part_part_num = qr_part_part_num
        this.qr_part_part_dsc = qr_part_part_dsc
        this.qr_part_hsn = qr_part_hsn
        this.qr_part_location = qr_part_location
        this.qr_part_make = qr_part_make
        this.qr_part_part_ctg = qr_part_part_ctg
        this.qr_part_man_qr_no = qr_part_man_qr_no
        this.qr_oe_part = qr_oe_part
        this.qr_part_desc = qr_part_desc
        this.qr_available_qty = qr_available_qty
        this.qr_tax = qr_tax
    }

}