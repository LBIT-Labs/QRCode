package workshop.lbit.qrcode.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import workshop.lbit.qrcode.fragments.QrScanningHistoryFragment
import workshop.lbit.qrcode.fragments.QrScanningRequestFragment

class QrScanningAdapter(fm: FragmentManager,
                        var mRole: String): FragmentStatePagerAdapter(fm) {

    private val TAB_COUNT = 2

    override fun getItem(position: Int): Fragment {


        when (position) {
            0 -> return QrScanningRequestFragment.newInstance()
            1 -> return QrScanningHistoryFragment.newInstance()
        }

        return null!!
    }

    override fun getCount(): Int {
        return TAB_COUNT

    }

    override fun getPageTitle(position: Int): CharSequence? {

        when (position) {
            0 -> return QrScanningRequestFragment.getTitle(mRole)
            1 -> return QrScanningHistoryFragment.TITLE
        }

        return super.getPageTitle(position)
    }
}