package com.example.netdata.app

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.netdata.R

class MainActivity : AppCompatActivity() {

    private lateinit var sqlManager: SQLManager
    private lateinit var list : MutableList<List<SpannableString>>
    private lateinit var listorder : List<SQLManager.Registro>

    enum class TIPO_ORDEN {
        ID,
        ZONA,
        CODIGO,
        SERIAL
    }

    private lateinit var lvRegistro : ListView
    private lateinit var etBuscar : EditText
    private lateinit var btnBuscar : Button
    private lateinit var btnNuevo : Button
    private lateinit var btnReset :Button

    private lateinit var radioGroup : RadioGroup
    private lateinit var radioCodigo : RadioButton
    private lateinit var radioSerial : RadioButton
    private lateinit var radioZona : RadioButton

    private var tipoOrdenActual :TIPO_ORDEN = TIPO_ORDEN.ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar
        lvRegistro = findViewById(R.id.lvRegistro)
        etBuscar = findViewById(R.id.search_bar)
        btnBuscar = findViewById(R.id.btnBusqueda)
        btnNuevo = findViewById(R.id.btnNuevoRegistro)
        btnReset = findViewById(R.id.btnReset)

        radioGroup = findViewById(R.id.radioGroup)
        radioCodigo = findViewById(R.id.rBtnCode)
        radioZona = findViewById(R.id.rBtnZone)
        radioSerial = findViewById(R.id.rBtnSerial)

        // Mostrar el formato de lista
        sqlManager = SQLManager(this)
        listorder = sqlManager.readData(tipoOrdenActual)

        list = mutableListOf()
        actualizar(list)

        // Al pulsar sobre un registro
        lvRegistro.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            val intent = Intent(this, VerifyActivity::class.java)
            val message : List<String> = listOf(
                listorder[position].codc.toString(), // Client Code
                listorder[position].sndn,            // Serial Number
                listorder[position].zone,            // Zone
                listorder[position].rxdn.toString(), // Rx Power
                listorder[position].txdn.toString(), // Tx Power
                listorder[position].temp.toString()) // Temperature
            intent.putStringArrayListExtra("Message", ArrayList(message))
            startActivity(intent)
        }

        // Buscar segun el texto
        btnBuscar.setOnClickListener {
            val searchText = etBuscar.text.toString().trim()

            if (searchText.isEmpty()) {
                ordenar()
            } else {
                busqueda(searchText)
            }
        }

        // Se restablece la lista
        btnReset.setOnClickListener {
            etBuscar.text.clear()
            radioGroup.clearCheck()
            tipoOrdenActual = TIPO_ORDEN.ID
            ordenar()
        }

        // Añadir nuevo registro
        btnNuevo.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        radioGroup.setOnCheckedChangeListener { _, _ ->
            tipoOrdenActual = when (radioGroup.checkedRadioButtonId) {
                R.id.rBtnZone -> TIPO_ORDEN.ZONA
                R.id.rBtnCode -> TIPO_ORDEN.CODIGO
                R.id.rBtnSerial -> TIPO_ORDEN.SERIAL
                else -> TIPO_ORDEN.ID
            }
        }

        etBuscar.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                runOnUiThread {
                    etBuscar.let {
                        val textoBusqueda = p0.toString().trim()
                        btnBuscar.text = if (textoBusqueda.isEmpty()) "Ordenar" else "Buscar"
                    }
                }
            }
        })
    }

    private fun actualizar(datos: MutableList<List<SpannableString>>) {
        // listorder = sqlManager.readData(tipoOrdenActual)

        for ((i, data) in listorder.withIndex()) {
            val id = SpannableString(data.id.toString())
            val cod = SpannableString("Cod: ${data.codc}")
            val details = SpannableString("Zone: ${data.zone} | Serial Number: ${data.sndn}")

            // Text Color
            val txtrxdn = data.rxdn.toString()
            val txttxdn = data.txdn.toString()
            val txttemp = data.temp.toString()

            val metrics = "Rx: $txtrxdn | Tx: $txttxdn | Temp: $txttemp °C"

            val spannableString = SpannableString(metrics)

            // RxPower Color
            if (txtrxdn.toFloat() > -25 && txtrxdn.toFloat() < -19) {
                spannableString.setSpan(ForegroundColorSpan(Color.GREEN), metrics.indexOf(txtrxdn),
                    metrics.indexOf(txtrxdn) + txtrxdn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannableString.setSpan(ForegroundColorSpan(Color.RED), metrics.indexOf(txtrxdn),
                    metrics.indexOf(txtrxdn) + txtrxdn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            // TxPower
            if (txttxdn.toFloat() > 0.5f && txttxdn.toFloat() < 5.0f) {
                spannableString.setSpan(ForegroundColorSpan(Color.GREEN), metrics.indexOf(txttxdn),
                    metrics.indexOf(txttxdn) + txttxdn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannableString.setSpan(ForegroundColorSpan(Color.RED), metrics.indexOf(txttxdn),
                    metrics.indexOf(txttxdn) + txttxdn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            // Temperature Color
            if (txttemp.toFloat() > 37 && txttemp.toFloat() < 48) {
                spannableString.setSpan(ForegroundColorSpan(Color.GREEN), metrics.indexOf(txttemp),
                    metrics.indexOf(txttemp) + txttemp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannableString.setSpan(ForegroundColorSpan(Color.RED), metrics.indexOf(txttemp),
                    metrics.indexOf(txttemp) + txttemp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            val listArray = arrayListOf(id, cod, details, spannableString)
            list.add(i, listArray)
        }

        val adapter = DataAdapter(this, datos)
        lvRegistro.adapter = adapter
    }

    private fun busqueda(textoBusqueda: String) {
        when (tipoOrdenActual) {
            TIPO_ORDEN.ZONA -> listorder = sqlManager.searchData(textoBusqueda, TIPO_ORDEN.ZONA)
            TIPO_ORDEN.CODIGO -> listorder = sqlManager.searchData(textoBusqueda, TIPO_ORDEN.CODIGO)
            TIPO_ORDEN.SERIAL -> listorder = sqlManager.searchData(textoBusqueda, TIPO_ORDEN.SERIAL)
            else -> listorder = sqlManager.searchData(textoBusqueda, TIPO_ORDEN.ID)
        }
        if (listorder.isEmpty()) {
            Toast.makeText(this, "No se encontró informacion por $tipoOrdenActual", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Se encontró la informacion", Toast.LENGTH_LONG).show()
        }
        list.clear()
        actualizar(list)
    }

    private fun ordenar() {
        when (tipoOrdenActual) {
            TIPO_ORDEN.ZONA -> listorder = sqlManager.readData(TIPO_ORDEN.ZONA)
            TIPO_ORDEN.CODIGO -> listorder = sqlManager.readData(TIPO_ORDEN.CODIGO)
            TIPO_ORDEN.SERIAL -> listorder = sqlManager.readData(TIPO_ORDEN.SERIAL)
            else -> listorder = sqlManager.readData(TIPO_ORDEN.ID)
        }
        Toast.makeText(this, "Se ordenó la informacion por $tipoOrdenActual", Toast.LENGTH_SHORT).show()
        list.clear()
        actualizar(list)
    }
}