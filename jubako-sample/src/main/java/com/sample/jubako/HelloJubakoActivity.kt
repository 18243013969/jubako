package com.sample.jubako

import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.justeat.jubako.Jubako
import com.justeat.jubako.withView
import kotlinx.android.synthetic.main.activity_jubako_recycler.*

class HelloJubakoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jubako_recycler)

        Jubako.logger.enabled = true

        Jubako.into(this, jubakoRecycler).load {
            for (i in 0..100) {
                withView { textView("Hello Jubako!") }
                withView { textView("こんにちはジュバコ") }
            }
        }
    }

    private fun textView(text: String): TextView {
        return TextView(this).apply {
            setText(text)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48f)
        }
    }
}
