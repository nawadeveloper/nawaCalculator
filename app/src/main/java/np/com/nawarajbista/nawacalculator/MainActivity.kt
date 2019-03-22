package np.com.nawarajbista.nawacalculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {

    private var firstNumber: BigDecimal? = null
    private var secondNumber: BigDecimal? = null
    private var operator: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // click events
        numberClick()

        operatorClick()

        backspace.setOnClickListener {
            if(screen_operator.text.isNotEmpty()) {
                screen_operator.text = null
                operator = null
                firstNumber = null
            }
            else {
                if(screen_result.text.length == 1) {
                    screen_result.setText("0")
                }
                else {
                    screen_result.setText(screen_result.text.dropLast(1))
                }
            }
        }

        reset.setOnClickListener {
            resetAll()
        }

        positiveNegative.setOnClickListener {
            val screenNum = BigDecimal(screen_result.text.toString())
            val valueChanger = BigDecimal("-1")
            val newValue = screenNum.multiply(valueChanger)
            screen_result.setText(newValue.toPlainString())
            if(operator != null) {
                firstNumber = newValue
            }
        }
    }

    //function for handling click event
    private fun operatorClick() {
        val operatorClickListener = View.OnClickListener {
            val v = it as Button

            if(firstNumber != null && screen_operator.text.isEmpty()) {
                secondNumber = BigDecimal(screen_result.text.toString())
            }
            else {
                firstNumber = BigDecimal(screen_result.text.toString())
            }

            result()
            screen_operator.text = v.text
            if(screen_operator.text.toString() == "=") {
                firstNumber = null
                secondNumber = null
            }

            operator = v.text.toString()

        }

        plus.setOnClickListener ( operatorClickListener )
        minus.setOnClickListener ( operatorClickListener )
        multiply.setOnClickListener ( operatorClickListener )
        divide.setOnClickListener ( operatorClickListener )
        equal.setOnClickListener( operatorClickListener )

    }


    private fun numberClick() {
        val numberClickListener = View.OnClickListener {
            val v = it as Button

            if(screen_operator.text.isNotEmpty()) {
                //if screen_operator has operator sign
                if(v.text.toString() == ".") {

                    //if user press decimal at the beginning "0." should appear in the screen
                    screen_result.setText("0.")
                }
                else {
                    screen_result.setText(v.text)
                    Log.d("click", screen_operator.text.toString())

                }
                //removing operator sign from screen
                screen_operator.text = null
            }
            else {
                //if screen_operator has no operator sign
                if(screen_result.text.toString() == "0" && v.text.toString() != ".") {
                    //remove zero before any number and and zero before decimal
                    screen_result.setText(v.text)
                    Log.d("click", "0 and .")

                }
                else {
                    screen_result.append(v.text)
                    Log.d("click", "append")

                }
            }



        }

        zero.setOnClickListener ( numberClickListener )
        one.setOnClickListener ( numberClickListener )
        two.setOnClickListener ( numberClickListener )
        three.setOnClickListener ( numberClickListener )
        four.setOnClickListener ( numberClickListener )
        five.setOnClickListener ( numberClickListener )
        six.setOnClickListener ( numberClickListener )
        seven.setOnClickListener ( numberClickListener )
        eight.setOnClickListener ( numberClickListener )
        nine.setOnClickListener ( numberClickListener )
        decimal.setOnClickListener ( numberClickListener )
    }


    private fun resetAll() {
        screen_operator.text = null
        screen_result.setText(R.string.zero)
        firstNumber = null
        secondNumber = null
        operator = null
    }


    private fun result() {
        if(firstNumber != null && secondNumber != null && operator != null) {
            var answer: BigDecimal? = null
            when(operator) {
                "+" -> answer = firstNumber?.add(secondNumber)
                "-" -> answer = firstNumber?.subtract(secondNumber)
                "X" -> answer = firstNumber?.multiply(secondNumber)
                "÷" -> answer = firstNumber?.divide(secondNumber,11, RoundingMode.CEILING)

            }


            answer = answer!!.stripTrailingZeros()
            screen_result.setText(answer.toPlainString())
            Log.d("click", answer.toString())
            firstNumber = answer
            secondNumber = null
        }
    }
}
