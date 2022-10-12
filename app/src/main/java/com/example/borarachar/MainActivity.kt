package com.example.borarachar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private val myLocale : Locale = Locale("pt","BR")

    lateinit var inputPrice : EditText

    lateinit var inputDivisor : EditText
    lateinit var inputTotalPrice : TextView
    private lateinit var buttonToSpeak : FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputDivisor = findViewById(R.id.textInputQtd)
        inputPrice = findViewById(R.id.textInputPrice)
        inputTotalPrice = findViewById(R.id.totalPrice)

        buttonToSpeak = findViewById(R.id.floatingActionButtonSpeak)
        //Not-null assertion -> converte para qualquer valor para non-null e
        // se o valor é null então lança uma exceção
        buttonToSpeak.isEnabled = false

        tts = TextToSpeech(this, this)

        val btnToShare : FloatingActionButton = findViewById(R.id.floatingActionButtonShare)

        val format: NumberFormat = NumberFormat.getCurrencyInstance(myLocale)
        format.maximumFractionDigits = 2

        val myTextWatcher = object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val divisor : Float? = inputDivisor.text.toString().toFloatOrNull()
                val isValidDivisor =  againstNullOrLessThan(divisor)

                val price : Float? = inputPrice.text.toString().toFloatOrNull()
                val isValidPrice = againstNullOrLessThan(price)

                if(!isValidDivisor || !isValidPrice){
                    changeInputTotalPrice(inputTotalPrice,format.format(0.00))
                    return
                }

                val result : Float? = price?.div(divisor!!)

                changeInputTotalPrice(inputTotalPrice,format.format(result))
            }

        }

        inputTotalPrice.text = format.format(0.00)

        inputPrice.addTextChangedListener(myTextWatcher)
        inputDivisor.addTextChangedListener(myTextWatcher)

        buttonToSpeak.setOnClickListener{
            speakOut(inputTotalPrice.text.toString())
        }

        btnToShare.setOnClickListener{
            shareResult(inputTotalPrice.text.toString())
        }

    }
    //Quando o TextToSpeech é inicializado, irá cair no onInit
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts.setLanguage(this.myLocale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                buttonToSpeak.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    private fun againstNullOrLessThan(value:Float?,min:Double=0.00):Boolean{
        if(value === null || value <= min){
            return false
        }
        return true
    }

    private fun changeInputTotalPrice(input:TextView,value:String){
        input.text = value
    }

    private fun speakOut(text : String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    private fun shareResult(text:String){
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"BoraRachar!")
        sendIntent.putExtra(Intent.EXTRA_TEXT,"Resultado da rachadinha : $text")
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent,"Compartilhar via"))
    }

    override fun onDestroy() {
        // Shutdown TTS
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}