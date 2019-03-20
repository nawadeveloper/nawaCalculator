package np.com.nawarajbista.nawacalculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // click events
        numberClick()

        backspace.setOnClickListener {
            if(screen_result.text.length == 1) {
                screen_result.setText("0")
            }
            else {
                screen_result.setText(screen_result.text.dropLast(1))
            }
        }
    }


    private fun numberClick() {
        val clickListener = View.OnClickListener {
            val v = it as Button
            if(screen_result.text.toString() == "0" && v.text.toString() != ".") {
                screen_result.setText(v.text)
            }
            else {
                screen_result.append(v.text)
            }
        }

        zero.setOnClickListener ( clickListener )
        one.setOnClickListener ( clickListener )
        two.setOnClickListener ( clickListener )
        three.setOnClickListener ( clickListener )
        four.setOnClickListener ( clickListener )
        five.setOnClickListener ( clickListener )
        six.setOnClickListener ( clickListener )
        seven.setOnClickListener ( clickListener )
        eight.setOnClickListener ( clickListener )
        nine.setOnClickListener ( clickListener )
        decimal.setOnClickListener ( clickListener )
    }
}
