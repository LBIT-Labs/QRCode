package workshop.lbit.qrcode.interfaces

import workshop.lbit.qrcode.data.QrData


interface QrDataList {

    fun onNavigate(
        dict_crops: QrData,
        position: Int,
        s: String
    )
}