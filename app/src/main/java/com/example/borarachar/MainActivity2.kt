package com.example.borarachar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity2 : AppCompatActivity() {
    lateinit var backToMain : Button
    lateinit var textDescription : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val txt : String = intent.getStringExtra("key") as String

        backToMain = findViewById(R.id.btnBackToMain)
        textDescription = findViewById(R.id.textView)

        textDescription.text = "Projeto desenvolvido por com $txt por Davi Silva"

        backToMain.setOnClickListener {
            finish()
        }
    }
}