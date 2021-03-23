package workshop.lbit.qrcode.jobcard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import workshop.lbit.qrcode.R


@SuppressLint("ValidFragment")
class JobCardPicsFragment @SuppressLint("ValidFragment") constructor() : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_jobcard_pics, container, false)

        return v
    }

    companion object {

        val TITLE = "PICS"

        fun newInstance(): JobCardPicsFragment {
            return JobCardPicsFragment()
        }
    }
}