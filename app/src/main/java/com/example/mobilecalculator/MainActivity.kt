package com.example.mobilecalculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Double.parseDouble
import java.util.*


class MainActivity : AppCompatActivity()
{
    private var canAddOperation = false
    private var canAddDecimal = true
    private var historyData=Stack<String>()
    private var queue: Queue<String> = ArrayDeque<String>(10);
    private var prevAns=0
    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode!!.getReference("users");
    }



    fun numberAction(view: View)
    {
        if(view is Button)
        {
            if(view.text == ".")
            {
                if(canAddDecimal)
                    workingsTV.append(view.text)

                canAddDecimal = false
            }
            else{
                workingsTV.append(view.text)
            }


            canAddOperation = true
        }
    }

    fun operationAction(view: View)
    {
        if(view is Button && canAddOperation)
        {
            workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View)
    {
        resultsTV.setText("")
        workingsTV.setText("")
    }

    fun backSpaceAction(view: View)
    {
        val length = workingsTV.length()
        if(length > 0)
            workingsTV.text = workingsTV.text.subSequence(0, length - 1)
    }

    fun equalsAction(view: View)
    {
        if(workingsTV.text=="" || canAddOperation==false){
            var snackbar=Snackbar.make(view,"Enter some operations",Snackbar.LENGTH_SHORT)
            snackbar.show();
        }
        else {
            val digitsOperators = digitsOperators()

            resultsTV.text = calculateExpression(digitsOperators)

            if (queue.size == 10) {
                queue.remove();
            }

            queue.add(workingsTV.text.toString() + "=" + resultsTV.text.toString())
            reference?.child("admin")?.child("history")?.setValue(queuetoString())

        }

    }
    private fun queuetoString(): String{
           var result="";
        for (item in queue) {
            result+=(item.toString())
            result+=System.lineSeparator()
        }
        return result
    }


     private fun precedence(operator:Char):Int{

         return when (operator) {
             'x' -> {
                 4;
             }
             '+' -> {
                 3;
             }
             '/' -> {
                 3;
             }
             '-' -> {
                 1;
             }
             else -> {
                 0;
             }
         }

     }
    private fun applyOp(a: Double, b: Double, op: Char): Double? {

        return when (op) {
            '+' -> a + b
            '-' -> a - b
            'x' -> a * b
            '/' -> a / b
            else-> 0.0

        }
    }

    private fun calculateExpression(passedList: MutableList<Any>):String{

        var values=Stack<Double>();
        var opr=Stack<Char>();


        for(i in passedList.indices){
            var numeric = true

            try {
               parseDouble(passedList[i].toString())
            } catch (e: NumberFormatException) {
                numeric = false
            }

            if(numeric){
                 values.push(parseDouble(passedList[i].toString()))
            }
            else{
                while (!opr.isEmpty() && precedence(opr.peek()) >= precedence(passedList[i].toString()[0])){
                    val val2: Double = values.peek()
                    values.pop()

                    val val1: Double = values.peek()
                    values.pop()

                    val op: Char = opr.peek()
                    opr.pop()

                    values.push(applyOp(val1, val2, op))
                }
                opr.push(passedList[i].toString()[0]);
            }
        }
        while (!opr.empty()) {
            val val2: Double = values.peek()
            values.pop()
            val val1: Double = values.peek()
            values.pop()
            val op: Char = opr.peek()
            opr.pop()
            values.push(applyOp(val1, val2, op))
        }

        return values.peek().toString();
    }

    private fun digitsOperators(): MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in workingsTV.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit += character
            else
            {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if(currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }


    fun showHistory(view: View) {
        var printHistory=queuetoString();

        val intent = Intent(this, History::class.java).apply {
            putExtra("history", printHistory)
        }
        startActivity(intent)
    }

    fun answer(view: View) {
        if(canAddOperation==false){
          workingsTV.append(resultsTV.text);
          canAddOperation=true
        }
    }

}