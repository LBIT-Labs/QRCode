package workshop.lbit.qrcode.customfonts

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by wolfsoft1 on 31/1/18.
 */

class MyTextView_Montserrat_Light : TextView {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        if (!isInEditMode) {
            val tf = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Light.ttf")
            typeface = tf
        }
    }
}
