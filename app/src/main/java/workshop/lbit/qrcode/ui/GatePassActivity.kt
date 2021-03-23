package workshop.lbit.qrcode.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import workshop.lbit.qrcode.R

class GatePassActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        getSupportActionBar()!!.hide();

    }
}