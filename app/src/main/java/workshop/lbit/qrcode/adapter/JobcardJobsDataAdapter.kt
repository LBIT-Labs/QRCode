package workshop.lbit.qrcode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Medium
import workshop.lbit.qrcode.customfonts.MyTextView_Roboto_Regular
import workshop.lbit.qrcode.data.JobcardData
import workshop.lbit.qrcode.interfaces.JobCardListService
import workshop.lbit.qrcode.jobcard.JobCardServiceFragment


class JobcardJobsDataAdapter(
    private val mContext: Context,
    internal var mJobsList: List<JobcardData>
    ) : PagerAdapter() {

    internal lateinit var mJCData: JobcardData
    internal var layoutInflater: LayoutInflater


    lateinit var tv_job: MyTextView_Roboto_Medium
    lateinit var tv_job_id: MyTextView_Roboto_Medium
    lateinit var tv_job_category: MyTextView_Roboto_Medium
    lateinit var tv_customer_notes: MyTextView_Roboto_Medium
    lateinit var tv_workshop_notes: MyTextView_Roboto_Medium

    init {

        layoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mJobsList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.jobcard_jobs_data_row, container, false)

        mJCData = mJobsList[position]
        tv_job = itemView.findViewById(R.id.tv_job)
        tv_job_id = itemView.findViewById(R.id.tv_job_id)
        tv_job_category = itemView.findViewById(R.id.tv_job_category)
        tv_customer_notes = itemView.findViewById(R.id.tv_customer_notes)
        tv_workshop_notes = itemView.findViewById(R.id.tv_workshop_notes)

        tv_job.text = mJCData.jc_job_name
        tv_job_id.text = mJCData.jc_job_id
        tv_job_category.text = mJCData.jc_job_ctg
        tv_customer_notes.text = mJCData.jc_customer_note
        tv_workshop_notes.text = mJCData.jc_workshop_note
        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
