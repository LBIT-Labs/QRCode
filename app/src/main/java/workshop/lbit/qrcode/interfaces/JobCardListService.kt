package workshop.lbit.qrcode.interfaces

import workshop.lbit.qrcode.data.JobcardData

interface JobCardListService {
    fun onNavigate(
        dict_crops: JobcardData,
        position: Int,
        status: String
    )
}