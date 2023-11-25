package com.example.netdata.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.netdata.R

class VerifyActivity : AppCompatActivity() {

    private lateinit var tvcodc : TextView
    private lateinit var tvsndn : TextView
    private lateinit var tvzone : TextView
    private lateinit var tvrxdn : TextView
    private lateinit var tvtxdn : TextView
    private lateinit var tvtemp : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        val listValues = intent.getStringArrayListExtra("Message")
        tvcodc = findViewById(R.id.tv_codc)
        tvsndn = findViewById(R.id.tv_sndn)
        tvzone = findViewById(R.id.tv_zone)
        tvrxdn = findViewById(R.id.tv_rxdn)
        tvtxdn = findViewById(R.id.tv_txdn)
        tvtemp = findViewById(R.id.tv_temp)

        if (listValues != null) {
            val codc = listValues[0]
            val sndn = listValues[1]
            val zone = listValues[2]
            val rxdn = listValues[3]
            val txdn = listValues[4]
            val temp = listValues[5]

            tvcodc.text = codc
            tvsndn.text = sndn
            tvzone.text = zone
            tvrxdn.text = rxdn
            tvtxdn.text = txdn
            tvtemp.text = temp

            if (rxdn.toFloat() > -25 && rxdn.toFloat() < -19) {
                tvrxdn.setTextColor(ContextCompat.getColor(this, R.color.green))
            } else {
                tvrxdn.setTextColor(ContextCompat.getColor(this, R.color.red))
            }

            if (txdn.toFloat() > 0.5f && txdn.toFloat() < 5.0f) {
                tvtxdn.setTextColor(ContextCompat.getColor(this, R.color.green))
            } else {
                tvtxdn.setTextColor(ContextCompat.getColor(this, R.color.red))
            }

            if (temp.toFloat() > 37 && temp.toFloat() < 48) {
                tvtemp.setTextColor(ContextCompat.getColor(this, R.color.green))
            } else {
                tvtemp.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
        }

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}