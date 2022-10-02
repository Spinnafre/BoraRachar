package com.example.borarachar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DecimalFormat
import java.util.*


class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    val df = DecimalFormat("#.##")
    var tts: TextToSpeech? = null
    var buttonToSpeak : FloatingActionButton ? = null
    private val myLocale : Locale = Locale("pt","BR")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonToSpeak = findViewById(R.id.floatingActionButtonSpeak)
        //Not-null assertion -> converte para qualquer valor para non-null e
        // se o valor é null então lança uma exceção
        buttonToSpeak!!.isEnabled = false

        tts = TextToSpeech(this, this)

        val inputPrice : EditText = findViewById(R.id.textInputPrice)
        val inputDivisor : EditText = findViewById(R.id.textInputQtd)
        val inputTotalPrice : TextView = findViewById(R.id.totalPrice)
        val btnToSpeak : FloatingActionButton =findViewById(R.id.floatingActionButtonSpeak)
        val btnToShare : FloatingActionButton = findViewById(R.id.floatingActionButtonShare)

        inputPrice.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.v("test","Before text changed -> ${s.toString()}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.v("test","Text changed -> ${s.toString()}")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.v("test","Texto alterado para ${s.toString()}")
                val price : Float?  = s.toString().toFloatOrNull()

                if(price === null){
                    inputTotalPrice.text = "R$ ${0.00}"
                    return
                }


                val divisor : Float? = inputDivisor.text.toString().toFloatOrNull()

                if(divisor === null){
                    Toast.makeText(applicationContext,"Insira um valor inteiro na quantidade de pessoas ",Toast.LENGTH_SHORT).show()
                    return
                }

                if(divisor<=0.00){
                    Toast.makeText(applicationContext,"Insira um valor maior do que 0 na quantidade de pessoas ",Toast.LENGTH_SHORT).show()
                    inputTotalPrice.text = "R$ ${0.00}"
                    return
                }
2
                val result : Float = price / divisor

                inputTotalPrice.text = "R$ ${df.format(result)}"
            }

        })

        inputDivisor.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.v("test","Before text changed -> ${s.toString()}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.v("test","Text changed -> ${s.toString()}")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.v("test","Texto alterado para ${s.toString()}")
                val divisor : Float? = s.toString().toFloatOrNull()

                if(divisor  === null ){
                    inputTotalPrice.text = "R$ ${0.00}"
                    return
                }

                if(divisor<=0.00){
                    inputTotalPrice.text = "R$ ${0.00}"
                    return
                }

                val price : Float? = inputPrice.text.toString().toFloatOrNull()

                if(price === null){
                    return
                }

                if(price.equals(0)){
                    inputTotalPrice.text = "R$ ${0.00}"
                    return
                }

                val result : Float = price / divisor

                inputTotalPrice.text = "R$ ${df.format(result)}"
            }

        })

        btnToSpeak.setOnClickListener{
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
            val result = tts!!.setLanguage(this.myLocale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                buttonToSpeak!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    private fun speakOut(text : String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    private fun shareResult(text:String){
        val sendIntent : Intent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"BoraRachar!")
        sendIntent.putExtra(Intent.EXTRA_TEXT,"Resultado da rachadinha : $text")
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent,"Compartilhar via"))
    }

    override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}