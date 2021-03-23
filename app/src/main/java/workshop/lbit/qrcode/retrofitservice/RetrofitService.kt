package workshop.lbit.qrcode.retrofitservice

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by SUJITH on 19/10/2019
 */
interface retrofit_qrcode {


    @FormUrlEncoded
    @POST("/user-login")
    fun getUserLogin(
        @Field("user_mobile") user_mobile: String,
        @Field("create_update") create_update: String,
        @Field("resend_otp") resend_otp: String,
        @Field("otp") otp: String,
        @Field("token_id") token_id: String

    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/qr-requests")
    fun QrRequestList(
        @Field("user_mobile") mobile: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/security-gatepass")
    fun QrRequestListGatepass(
        @Field("user_mobile") mobile: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/security-gatepass")
    fun QrRequestListGatepass(
        @Field("user_mobile") mobile: String,
        @Field("status") status: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/qr-validates")
    fun QrRequestListWH(
        @Field("status") status: String,
        @Field("user_mobile") mobile: String,
        @Field("user") user: String,
        @Field("pwd") pwd: String,
        @Field("uid") uid: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/gate-pass-requests")
    fun QrRequestListWHG(
        @Field("status") mobile: String,
        @Field("user") user: String,
        @Field("pwd") pwd: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/invoice-list-requests")
    fun QrRequestBoxListWHG(
        @Field("gate_pass_no") orderId: String,
        @Field("status") status: String,
        @Field("user") user: String,
        @Field("pwd") pwd: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/parts-list")
    fun QrRequestPartsListWH(
        @Field("nid") nid: String,
        @Field("status") status: String,
        @Field("user") user: String,
        @Field("pwd") pwd: String,
        @Field("uid") uid: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/invoice-verification")
    fun QrInvoiceRequestList(
        @Field("user_mobile") mobile: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/filters-data")
    fun IMFiltersData(
        @Field("status") status: String,
        @Field("search_part") search: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/filters-data")
    fun GetFiltersData(
        @Field("status") status: String,
        @Field("job_type") search: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/services-save")
    fun SaveJObCard(
        @Field("nid") nid: String,
        @Field("type") type: String,
        @Field("job_category") job_category: String,
        @Field("job") job: String,
        @Field("job_id") job_id: String,
        @Field("cus_note") cus_note: String,
        @Field("work_note") work_note: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/services-save")
    fun SaveJobcardService(
        @Field("nid") nid: String,
        @Field("type") type: String,
        @Field("service") service: String,
        @Field("hours") hours: String,
        @Field("mrp") mrp: String,
        @Field("discount") discount: String,
        @Field("final") final: String,
        @Field("pid") pid: String,
        @Field("edit_type") editType: String,
        @Field("job_id") job_id: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/spare-save")
    fun SaveJobCardSpares(
        @Field("user_mobile") user_mobile: String,
        @Field("nid") nid: String,
        @Field("pid") pid: String,
        @Field("spare_id") spare_id: String,
        @Field("oe_part") oe_part: String,
        @Field("part_desc") part_desc: String,
        @Field("tax_percent") tax_percent: String,
        @Field("part_hsn") part_hsn: String,
        @Field("qty") qty: String,
        @Field("total_cart_qty") total_cart_qty: String,
        @Field("grn_edit_number") grn_edit_number: String,
        @Field("edit") edit: String,
        @Field("job_id") job_id: String,
        @Field("discount") discount: String,
        @Field("final") final: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/vendor-spares-save")
    fun SaveJobCardSparesVendor(
        @Field("user_mobile") user_mobile: String,
        @Field("nid") nid: String,
        @Field("pid") pid: String,
        @Field("oepart") oe_part: String,
        @Field("part_desc") part_desc: String,
        @Field("tax") tax_percent: String,
        @Field("hsn") part_hsn: String,
        @Field("qty") qty: String,
        @Field("discount") discount: String,
        @Field("vendor") vendor: String,
        @Field("jobcard_id") jobcard_id: String,
        @Field("final") final: String,
        @Field("mrp") mrp: String,
        @Field("edit_type") edit: String

        ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/vendor-services-save")
    fun SaveJobCardServicesendor(
        @Field("user_mobile") user_mobile: String,
        @Field("nid") nid: String,
        @Field("pid") pid: String,
        @Field("service") service: String,
        @Field("cost") cost: String,
        @Field("discount") discount: String,
        @Field("vendor") vendor: String,
        @Field("jobcard_id") jobcard_id: String,
        @Field("edit_type") edit_type: String,
        @Field("final") final: String


        ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/retrieve-services")
    fun GetJobcardData(
        @Field("nid") nid: String,
        @Field("type") type: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/vendor-retrieval")
    fun GetVendorJobcardData(
        @Field("user_mobile") user_mobile: String,
        @Field("nid") nid: String,
        @Field("type") type: String,
        @Field("jobcard_id") jobcard_id: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/generate-estimate")
    fun GenerateEstimate(
        @Field("user_mobile") user_mobile: String,
        @Field("phone_number") phone_number: String,
        @Field("nid") nid: String,
        @Field("type") type: String,
        @Field("tot_amount") tot_amount: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/vendor-gatepass-gen")
    fun GenerateGatepass(
        @Field("nid") user_mobile: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/jobcard-summary")
    fun GetJobcardSummary(
        @Field("nid") nid: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/vendor-summary")
    fun GetVendorJobcardSummary(
        @Field("nid") nid: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/jobcard-history")
    fun getHistoryobsData(
        @Field("user_mobile") user_mobile: String,
        @Field("search") search: String

    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/live-jobs-api")
    fun getLiveJobsData(
        @Field("user_mobile") user_mobile: String,
        @Field("search") search: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/vendor-dashboard-list")
    fun getVendorJobsData(
        @Field("user_mobile") user_mobile: String,
        @Field("search") search: String,
        @Field("status") status: String

    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/jobcard-makes")
    fun getMakeList(
        @Field("make") make: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/vendor-api")
    fun getVendorDetails(
        @Field("user_mobile") user_mobile: String,
        @Field("jobcard_id") jobcard_id: String,
        @Field("nid") nid: String,
        @Field("type") type: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/jobid-list")
    fun getJObIDList(
        @Field("nid") make: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/vehicle-search")
    fun getDetails(
        @Field("cust_mobile") mobile: String,
        @Field("nid") nid: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/stock-search")
    fun GetPartDetailsList(
        @Field("oe_part") part: String,
        @Field("category") partCategory: String,
        @Field("make") make: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/spare-search")
    fun GetSpareDetailsList(
        @Field("user_mobile") part: String,
        @Field("search") partCategory: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/history-requests")
    fun QrHistoryList(
        @Field("user_mobile") mobile: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/history-requests")
    fun QrHistoryListWH(
        @Field("user_mobile") mobile: String,
        @Field("user") user: String,
        @Field("pwd") pwd: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/qr-requests")
    fun QrRequestPartList(
        @Field("user_mobile") mobile: String,
        @Field("nid") nid: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/returnable-gatepass")
    fun QrReturnableGatepass(
        @Field("user_mobile") mobile: String,
        @Field("nid") nid: String
    ): Call<ResponseBody>


 @FormUrlEncoded
    @POST("/pics-upload")
    fun QrReturnableGatepassAllow(
        @Field("user_mobile") mobile: String,
        @Field("nid") nid: String,
        @Field("gatepass_status") status: String,
        @Field("veh_image") veh_image: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/GRN-qr-check")
    fun SaveQrVerifiedValue(
        @Field("nid") mobile: String,
        @Field("user_mobile") user_mobile: String,
        @Field("grn_number") grn_number: String,
        @Field("status") status: String,
        @Field("type") type: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/verify-grn")
    fun SaveQrVerifiedValueWH(
        @Field("pid") mobile: String,
        @Field("status") status: String,
        @Field("user") user: String,
        @Field("pwd") pwd: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("verify-gatepass")
    fun SaveQrVerifiedValueWHG(
        @Field("pid") mobile: String,
        @Field("status") status: String,
        @Field("user") user: String,
        @Field("pwd") pwd: String,
        @Field("gp_nid") nid: String
    ): Call<ResponseBody>

    @get:GET("/dealer-locations-sd")
    val branchdetails: Call<ResponseBody>


    @FormUrlEncoded
    @POST("/create-update-user")
    fun registration(
        @Field("create_update") create_update: String,
        @Field("uid") uid: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("branch") branch: String,
        @Field("designation") designation: String,
        @Field("emp_id") emp_id: String,
        @Field("manager") manager: String,
        @Field("mobile") mobile: String,
        @Field("brand") brand: String,
        @Field("image") image: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("/vehicle-details")
    fun SaveJobCardVehicleDetails(
        @Field("user_mobile") user_mobile: String,
        @Field("reg_num") reg_num: String,
        @Field("make") make: String,
        @Field("model") model: String,
        @Field("variant") variant: String,
        @Field("kms") kms: String,
        @Field("color") color: String,
        @Field("mfg") mfg: String,
        @Field("engine") engine: String,
        @Field("chassis") chassis: String,
        @Field("ins") ins: String,
        @Field("incharge") incharge: String,
        @Field("incharge_mob") incharge_mob: String,
        @Field("driver") driver: String,
        @Field("driver_mob") driver_mob: String,
        @Field("location") location: String,
        @Field("customer") customer: String,
        @Field("customer_mob") customerPhone: String,
        @Field("phone_number") customer_mob: String,
        @Field("email") email: String,
        @Field("address") address: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("pincode") pincode: String,
        @Field("payment") payment: String,
        @Field("gst_applicable") gst_applicable: String,
        @Field("gstin") gstin: String,
        @Field("tech") tech: String,
        @Field("supervisor") supervisor: String,
        @Field("sup_mob") sup_mob: String
        ): Call<ResponseBody>


    /*SC List Names*/
    @FormUrlEncoded
    @POST("/dropdownlist-sd")
    fun getSClist(
        @Field("dropdown_type") dropdown_type: String,
        @Field("branch") branch: String
    ): Call<ResponseBody>

    /*SC List Names*/
    @FormUrlEncoded
    @POST("/dropdownlist-sd")
    fun getDesignation(
        @Field("dropdown_type") dropdown_type: String
    ): Call<ResponseBody>

}
