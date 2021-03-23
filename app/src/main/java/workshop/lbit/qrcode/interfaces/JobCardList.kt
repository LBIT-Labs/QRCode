package workshop.lbit.qrcode.interfaces

import workshop.lbit.qrcode.data.JobcardData

interface JobCardList {
    fun onNavigate(
        dict_crops: JobcardData,
        position: Int
    )
}