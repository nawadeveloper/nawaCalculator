package np.com.nawarajbista.nawacalculator

import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {

    private var firstNumber: BigDecimal? = null
    private var secondNumber: BigDecimal? = null
    private var memoryTotal = BigDecimal.ZERO
    private var operator: String? = null
    private var calculationError: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //landscape mode
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            supportActionBar?.hide()


            pie?.setOnClickListener {
                screen_result.setText(R.string.pie_value)
                screen_operator.text = null
            }

            square?.setOnClickListener {
                val numOnScreen = stringToBigDecimal(screen_result.text.toString())
                val square = setZeroEqualToZero(multiplication(numOnScreen, numOnScreen))

                screen_operator.text = null

                if(square != null) {
                    screen_result.setText(square.toPlainString())
                }
                else {
                    resetAll()
                    screen_operator.setText(R.string.error)
                }

            }

            factorial?.setOnClickListener {
                val screenNum = stringToBigDecimal(screen_result.text.toString())
                var result = BigDecimal.ZERO

                if(screenNum == null){
                    resetAll()
                    screen_operator.setText(R.string.error)
                    return@setOnClickListener
                }
                screen_operator.text = null

                if(screenNum.scale() <= 0 ) {
                    result = factorialResult(screenNum)
                }

                Log.d("click", screenNum.scale().toString())
                screen_result.setText(result.toPlainString())
            }

            log?.setOnClickListener {
                val screenNum = screen_result.text.toString()
                val result = findLog(screenNum)

                screen_operator.text = null
                screen_result.setText(result)
            }

            one_divide_x?.setOnClickListener {
                val screenNum = screen_result.text.toString()
                val result = division(BigDecimal.ONE, stringToBigDecimal(screenNum))

                if(calculationError) {
                    screen_operator.setText(R.string.error)
                    calculationError = false
                }
                else {
                    screen_operator.text = null
                }

                screen_result.setText(setZeroEqualToZero(result)!!.toPlainString())


            }

        }

        // click events
        numberClick()

        operatorClick()

        memory_plus.setOnClickListener {

            getInputValue()

            if(firstNumber != null && secondNumber != null && operator != null) {
                screen_result.setText(calculate()?.toPlainString())
            }

            val mPlus = stringToBigDecimal(screen_result.text.toString())
            screen_operator.setText(R.string.equal)

            firstNumber = null
            secondNumber = null

            if(mPlus != null) {
                memoryTotal = addition(memoryTotal, mPlus)
            }
            else {
                resetAll()
                screen_operator.setText(R.string.error)
            }

        }

        memory_minus.setOnClickListener {

            getInputValue()

            if(firstNumber != null && secondNumber != null && operator != null) {
                screen_result.setText(calculate()?.toPlainString())
            }

            val mMinus = stringToBigDecimal(screen_result.text.toString())
            screen_operator.setText(R.string.equal)

            firstNumber = null
            secondNumber = null

            if(mMinus != null) {
                memoryTotal = subtraction(memoryTotal, mMinus)
            }
            else {
                resetAll()
                screen_operator.setText(R.string.error)
            }

        }

        memory_total.setOnClickListener {
            screen_result.setText(memoryTotal.toPlainString())
            screen_operator.setText(R.string.equal)
            memoryTotal = BigDecimal.ZERO
        }

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
            val screenNum = stringToBigDecimal(screen_result.text.toString())
            val valueChanger = BigDecimal("-1")

            if(screenNum != null) {
                val newValue = screenNum.multiply(valueChanger)
                screen_result.setText(newValue.toPlainString())
            }
            else {
                resetAll()
                screen_operator.setText(R.string.error)
                return@setOnClickListener
            }

            if(screen_operator.text != null) {
                screen_operator.text = null
            }
        }

        root.setOnClickListener {
            val screenNum: Double?
            try {
               screenNum = screen_result.text.toString().toDouble()
            } catch (e: Exception) {
                resetAll()
                screen_operator.setText(R.string.error)
                return@setOnClickListener
            }

            screen_operator.text = null


            try {
                val rootNum = Math.sqrt(screenNum)
                val rn = setZeroEqualToZero(rootNum.toBigDecimal())
                screen_result.setText(rn?.toPlainString())
            }
            catch (e: Exception) {
                resetAll()
                screen_operator.setText(R.string.error)
            }

        }

        percent.setOnClickListener {

            if(firstNumber != null) {
                secondNumber = stringToBigDecimal(screen_result.text.toString())
            }

            val ans = getPercentResult()

            if(ans != null) { //and calculationError is false
                //handling error
                screen_result.setText(ans.toPlainString())
                screen_operator.setText(R.string.equal)
            }
            else {
                screen_result.setText(R.string.zero)
                screen_operator.setText(R.string.error)
                calculationError = false
            }

            firstNumber = null
            secondNumber = null
        }
    }

    private fun stringToBigDecimal(screenNum: String): BigDecimal? {
       return try {
            setZeroEqualToZero(BigDecimal(screenNum))
        }
        catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun findLog(num: String?): String {

        return try {
            val doubleNum = num!!.toDouble()
            Math.log10(doubleNum).toString()
        }
        catch (e: Exception) {
            screen_operator.setText(R.string.error)
            "0"
        }
    }

    private fun factorialResult(num: BigDecimal):BigDecimal? {

        var result: BigDecimal? = BigDecimal.ZERO
        if(num == BigDecimal.ZERO || num == BigDecimal.ONE) {
            return BigDecimal.ONE
        }

        //negative factorial error
        if(num < BigDecimal.ZERO) {
            resetAll()
            screen_operator.setText(R.string.error)
            Toast.makeText(applicationContext, "Factorial for negative number are not allowed", Toast.LENGTH_SHORT)
                .show()
            return result
        }

        if(num.toInt() > 9999) {
            Toast.makeText(applicationContext, "Too large number", Toast.LENGTH_SHORT).show()
            return num
        }

        try {
            result = num.multiply(factorialResult(num.subtract(BigDecimal.ONE)))
        }
        catch (e: Exception) {
            resetAll()
            screen_operator.setText(R.string.error)
        }

        return result

    }

    //function for handling click event
    private fun operatorClick() {
        val operatorClickListener = View.OnClickListener {
            val v = it as Button

            getInputValue()

            result()

            if(calculationError) {
                screen_operator.setText(R.string.error)
                calculationError = false
                firstNumber = null
                secondNumber = null
            }
            else {
                if(v.text == resources.getString(R.string.power_y)) {
                    screen_operator.text = "^"
                }
                else {
                    screen_operator.text = v.text
                }
            }

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
        power_y?.setOnClickListener ( operatorClickListener )

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
                }

                //removing operator sign from screen
                screen_operator.text = null
            }
            else {
                //if screen_operator has no operator sign
                if(screen_result.text.toString() == "0" && v.text.toString() != ".") {
                    //remove zero before any number and and zero before decimal
                    screen_result.setText(v.text)
                }
                else {
                    screen_result.append(v.text)

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


    private fun getInputValue() {
        if(firstNumber != null && screen_operator.text.isEmpty()) {
            // stopping same number value from assigning while pressing operator twice
            secondNumber = stringToBigDecimal(screen_result.text.toString())
        }
        else {
            firstNumber = stringToBigDecimal(screen_result.text.toString())
        }
    }


    private fun resetAll() {
        screen_operator.text = null
        screen_result.setText(R.string.zero)
        firstNumber = null
        secondNumber = null
        operator = null
        memoryTotal = BigDecimal.ZERO
    }


    private fun result() {
        if(firstNumber != null && secondNumber != null && operator != null) {
            val answer = calculate()

            //need to work on this to print scientific notation when number is large
//                screen_result.setText(answer.toEngineeringString())

            screen_result.setText(answer?.toPlainString())
            firstNumber = answer
            secondNumber = null
        }
    }

    private fun getPercentResult(): BigDecimal? {
        var ans: BigDecimal? = BigDecimal.ZERO

        if(firstNumber != null && secondNumber != null && operator != null) {
            val sv: BigDecimal? = firstNumber?.multiply(secondNumber)?.
                divide(BigDecimal("100"), 11, RoundingMode.CEILING)


            when(operator) {
                "+" -> ans = firstNumber?.add(sv)
                "-" -> ans = firstNumber?.subtract(sv)
                "X" -> ans = sv
                "÷" -> {
                    try {
                        // handling fool user trying to divide number by zero
                        ans = firstNumber?.divide(secondNumber, 11, RoundingMode.CEILING)?.multiply(BigDecimal("100"))
                    }
                    catch (e: Exception) {
                        resetAll()
                        calculationError = true
                        Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                        return null
                    }
                }
            }
        }

        return setZeroEqualToZero(ans)
    }




// helper function


    private fun calculate(): BigDecimal? {
        var answer = BigDecimal.ZERO
        when(operator) {
            resources.getString(R.string.plus) -> answer = addition(firstNumber, secondNumber)
            resources.getString(R.string.minus) -> answer = subtraction(firstNumber, secondNumber)
            resources.getString(R.string.multiply) -> answer = multiplication(firstNumber, secondNumber)
            resources.getString(R.string.divide) -> answer = division(firstNumber, secondNumber)
            resources.getString(R.string.power_y) -> answer = powerResult(firstNumber, secondNumber)
        }

        return setZeroEqualToZero(answer)
    }

    private fun powerResult(x: BigDecimal?, y: BigDecimal?): BigDecimal? {
        val ys = y!!.stripTrailingZeros()
        val zero = BigDecimal.ZERO
        val one = BigDecimal.ONE

        //for integer
        if(ys.scale() <= 0) {
            //for negative power
            if(ys < zero) {
                val changeToPositive = BigDecimal("-1")
                val cys = ys.multiply(changeToPositive).intValueExact()
                val xPowCys = x?.pow(cys)

                return one.divide(xPowCys)
            }
            // for positive power
            return x?.pow(ys.intValueExact())
        }

        calculationError = true
        return zero

    }


    private fun addition(fn: BigDecimal?, sn: BigDecimal?): BigDecimal? {
        return fn?.add(sn)
    }

    private fun subtraction(fn: BigDecimal?, sn: BigDecimal?): BigDecimal? {
        return fn?.subtract(sn)
    }

    private fun multiplication(fn: BigDecimal?, sn: BigDecimal?): BigDecimal? {
        return fn?.multiply(sn)
    }

    private fun division(fn: BigDecimal?, sn: BigDecimal?): BigDecimal? {
        var ans = BigDecimal.ZERO

        try {
            ans = fn?.divide(sn,11, RoundingMode.CEILING)
        }
        catch (e: Exception) {
            resetAll()
            calculationError = true
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
        finally {
            return ans
        }
    }

    //handling stripTrailingZeros() 0.0000000 error
    private fun setZeroEqualToZero(num: BigDecimal?): BigDecimal? {
        if(num?.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO
        }

        return num?.stripTrailingZeros()
    }
}


