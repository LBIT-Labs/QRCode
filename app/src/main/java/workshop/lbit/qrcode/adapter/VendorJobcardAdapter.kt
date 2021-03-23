package workshop.lbit.qrcode.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import workshop.lbit.qrcode.fragments.*

class VendorJobcardAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val TAB_COUNT = 2

    override fun getItem(position: Int): Fragment {


        when (position) {
            0 -> return VendorNewJobcardFragment()
            1 -> return VendorHistoryJobcardFragment()
        }

        return null!!
    }

    override fun getCount(): Int {
        return TAB_COUNT

    }

    override fun getPageTitle(position: Int): CharSequence? {

        when (position) {
            0 -> return VendorNewJobcardFragment.TITLE
            1 -> return VendorHistoryJobcardFragment.TITLE
        }

        return super.getPageTitle(position)
    }
}