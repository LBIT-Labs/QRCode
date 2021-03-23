package workshop.lbit.qrcode.helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import workshop.lbit.qrcode.R


/**
 * Created by Salmaan Ahmed on 10/10/2017.
 * Expandable button is used to expand and collapse a child view which can be anything
 * It should be passed a child view which will automatically hide and show on button click
 * It also have expand and collapse listener which can be used for some events
 */

class ExpandableButton : FrameLayout {

    internal lateinit var context: Context            //context
    internal lateinit var textView: TextView          //text view
    internal lateinit var imageArrow: ImageView       //image arrow
    internal lateinit var viewColor: View             //strip color

    internal var childViewResId = 0     //child view id
    internal lateinit var childView: View             //child view

    internal var expandableButtonListener: ExpandableButtonListener? = null

    /**
     * Interface for callbacks and listener
     */
    interface ExpandableButtonListener {
        fun onViewExpanded()
        fun onViewCollapsed()
    }


    constructor(context: Context) : super(context) {
        initButton(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initButton(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initButton(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initButton(context, attrs)
    }

    /**
     * Initialize button on every constructor call.
     * Used to reference views and set attributes to the view.
     * @param context context
     * @param attrs attribute set for setting some properties
     */
    private fun initButton(context: Context, attrs: AttributeSet?) {
        this.context = context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.expandable_button, this)
        textView = view.findViewById(R.id.tv_text)
        imageArrow = view.findViewById(R.id.iv_arrow)
        viewColor = view.findViewById(R.id.view_color)

        setClickListener()

        if (attrs == null) return

        val typedArray = context.theme.obtainStyledAttributes(
                attrs, R.styleable.ExpandableButton, 0, 0)

        try {
            childViewResId = typedArray.getResourceId(R.styleable.ExpandableButton_childView, 0)
            setBarColor(typedArray.getColor(R.styleable.ExpandableButton_color, 0))
            setText(typedArray.getString(R.styleable.ExpandableButton_text))
            setIcon(typedArray.getDrawable(R.styleable.ExpandableButton_icon))
        } finally {
            typedArray.recycle()
        }
    }

    /**
     * Set text to expandable button
     * @param text string text
     */
    fun setText(text: String?) {
        textView.text = text
    }

    /**
     * Set color to bar on left side of the button
     * User can set color anytime to this view as indicators
     * @param color color of bar
     */
    fun setBarColor(color: Int) {
        if (color != 0) viewColor.setBackgroundColor(color)
    }

    /**
     * User can set any icon as arrow
     * @param icon drawable
     */
    fun setIcon(icon: Drawable?) {
        if (icon != null) imageArrow.setImageDrawable(icon)
    }

    /**
     * Set child view programmatically
     * @param view child view
     */
    fun setChildView(view: View) {
        this.childView = view
    }

    /**
     * Setting up event listeners for expandable button
     * @param expandableButtonListener listener
     */
    fun setCallbackListener(expandableButtonListener: ExpandableButtonListener) {
        this.expandableButtonListener = expandableButtonListener
    }

    /**
     * Setting up the click listener
     */
    private fun setClickListener() {
        this.isClickable = true
        this.setOnClickListener { onClicked() }
    }

    /**
     * Toggle view visibility on click listener and fire callbacks
     */
    private fun onClicked() {
        rotateArrow()
        if (childView.visibility == View.GONE) {
            childView.visibility = View.VISIBLE
            if (expandableButtonListener != null) expandableButtonListener!!.onViewExpanded()
        } else {
            childView.visibility = View.GONE
            if (expandableButtonListener != null) expandableButtonListener!!.onViewCollapsed()
        }
    }

    /**
     * Rotate arrow view on expanding and collapsing
     */
    private fun rotateArrow() {
        if (imageArrow.rotation == 0f)
            imageArrow.animate().rotation(180f).start()
        else
            imageArrow.animate().rotation(0f).start()
    }

    /**
     * Get child view on this method
     * Have to wait until the view is attached to window
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (childViewResId != 0) {
            childView = rootView.findViewById(childViewResId)
            childView.visibility = View.GONE
        }
    }
}
