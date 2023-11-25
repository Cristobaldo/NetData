package com.example.netdata.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.netdata.R
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class RegisterActivity : AppCompatActivity() {

    private var connectSQL = ConnectSQL()

    private var codR : Int = 0
    private var sndR : String = ""
    private var zonR : String = ""

    private lateinit var etcodc : EditText
    private lateinit var etrxdn : EditText
    private lateinit var ettxdn : EditText
    private lateinit var ettemp : EditText

    private lateinit var sqlManager: SQLManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sqlManager = SQLManager(this)

        etcodc = findViewById(R.id.et_codc)
        etrxdn = findViewById(R.id.et_rxdn)
        ettxdn = findViewById(R.id.et_txdn)
        ettemp = findViewById(R.id.et_temp)

        val btnCancelar = findViewById<Button>(R.id.btCancelar)
        btnCancelar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnGuadar = findViewById<Button>(R.id.btnGuardar)
        btnGuadar.setOnClickListener {

            consultaCod()

            val rxdn = etrxdn.text.toString()
            val txdn = ettxdn.text.toString()
            val temp = ettemp.text.toString()

            sqlManager.insertData(
                codR, zonR, sndR,
                rxdn.toFloat(), txdn.toFloat(), temp.toFloat()
            )
            val intent = Intent(this, VerifyActivity::class.java)
            val message : List<String> = listOf(codR.toString(), sndR, zonR, rxdn, txdn, temp)
            intent.putStringArrayListExtra("Message", ArrayList(message))
            startActivity(intent)
        }
    }

    private fun consultaCod() {
        try {
            val statem : Statement = connectSQL.dbConn()!!.createStatement()
            val search : ResultSet = statem.executeQuery("SELECT CodigoCliente, Zonal, SerieDispNuevo FROM reporte WHERE CodigoCliente = " + etcodc.text.toString())

            if (search.next()) {
                codR = search.getInt(1)
                zonR = search.getString(2)
                sndR = search.getString(3)
            } else {
                codR = 0
                sndR = "00:AA:00:AA:00:AA"
                zonR = "LIMA"
            }

            Toast.makeText(this, "Se encontro el cliente", Toast.LENGTH_LONG).show()
        } catch (ex:SQLException) {
            Toast.makeText(this, "No se encontro el cliente", Toast.LENGTH_LONG).show()
        }
    }
}