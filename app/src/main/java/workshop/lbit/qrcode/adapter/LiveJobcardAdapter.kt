package workshop.lbit.qrcode.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import workshop.lbit.qrcode.fragments.LiveJobCardLiveRecordsFragment
import workshop.lbit.qrcode.fragments.LiveJobcardHistoryFragment
import workshop.lbit.qrcode.fragments.QrScanningHistoryFragment
import workshop.lbit.qrcode.fragments.QrScanningRequestFragment

class LiveJobcardAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val TAB_COUNT = 2

    override fun getItem(position: Int): Fragment {


        when (position) {
            0 -> return LiveJobCardLiveRecordsFragment()
            1 -> return LiveJobcardHistoryFragment()
        }

        return null!!
    }

    override fun getCount(): Int {
        return TAB_COUNT

    }

    override fun getPageTitle(position: Int): CharSequence? {

        when (position) {
            0 -> return LiveJobCardLiveRecordsFragment.TITLE
            1 -> return LiveJobcardHistoryFragment.TITLE
        }

        return super.getPageTitle(position)
    }
}