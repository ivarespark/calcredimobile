package com.nesp.calcredi

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.nesp.calcredi.databinding.ActivityMainBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val df = DecimalFormat("#.##")
    val dfm = DecimalFormat("##,###.##")
    var capital: Double = 0.0
    var tcea: Double = 0.0
    var tem: Double = 0.0
    var cuotas: Int = 0
    var cuota: Double = 0.0
    var cuotaSin: Double = 0.0
    var cuotaInt: Double = 0.0
    var interes: Double = 0.0
    var montoPagar: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){
            txtRCuota.keyListener = null
            txtRInteres.keyListener = null
            txtRMontoPagar.keyListener = null
            txtRCuotaSin.keyListener = null
            txtRCuotaInt.keyListener = null

            btnCalcular.setOnClickListener {
                ocultarTeclado()
                calcularCredito()
            }

            btnLimpiar.setOnClickListener{
                mostrarTeclado()
                limpiarDatos()
            }

            btnCopiar.setOnClickListener{
                copiarInfo(armarInfo())
            }
        }
    }

    private fun armarInfo() : String {
        var cInfo:String = ""
        cInfo += "CalcrediMobile\r\n"
        cInfo += "Capital: $ " + dfm.format(capital).toString() + "\r\n"
        cInfo += "Cuotas: " + cuotas + "\r\n"
        cInfo += "TCEA/TEA: " + df.format(tcea).toString() + " %\r\n"
        cInfo += "TEM: " + df.format(tem * 100).toString() + " %\r\n"
        cInfo += "Monto cuota: $ " + dfm.format(cuota).toString() + "\r\n"
        cInfo += "Cuota sin interes: $ " + dfm.format(cuotaSin).toString() + "\r\n"
        cInfo += "Interes cuota: $ " + dfm.format(cuotaInt).toString() + "\r\n"
        cInfo += "Interes total: $ " + dfm.format(interes).toString() + "\r\n"
        cInfo += "Monto total a pagar: $ " + dfm.format(montoPagar).toString() + "\r\n"
        print(cInfo)
        return cInfo
    }

    private fun copiarInfo(vararg cinfo:String) {
        val clip = ClipData.newPlainText(null, cinfo.toString())
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this,"Copiado al portapapeles",Toast.LENGTH_LONG).show()
    }

    private fun ocultarTeclado() {
        var view  = this.currentFocus
        if (view != null){
            var hideMe = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMe.hideSoftInputFromWindow(view.windowToken,0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun mostrarTeclado(){
        var view  = this.currentFocus
        if (view != null){
            var showMe = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            showMe.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun limpiarDatos() {
        with(binding){
            txtCapital.setText("")
            txtTcea.setText("")
            txtCuotas.setText("")
            txtTem.setText("")
            txtRCuota.setText("")
            txtRCuotaSin.setText("")
            txtRCuotaInt.setText("")
            txtRMontoPagar.setText("")
            txtRInteres.setText("")
            txtCapital.requestFocus()
        }
        capital= 0.0
        tcea = 0.0
        tem = 0.0
        cuotas = 0
        cuota = 0.0
        cuotaSin = 0.0
        cuotaInt = 0.0
        interes = 0.0
        montoPagar = 0.0
    }

    private fun calcularCredito() {
        with(binding){
            capital = txtCapital.text.toString().toDouble()
            tcea = txtTcea.text.toString().toDouble()
            cuotas = txtCuotas.text.toString().toInt()
        }
        tem = ((1 + (tcea / 100.00)).pow(1.00 / 12.00)) -1
        df.roundingMode = RoundingMode.UP
        dfm.roundingMode = RoundingMode.UP
        binding.txtTem.setText(df.format(tem * 100).toString())

        cuota = capital * ((tem * (1 + tem).pow(cuotas.toDouble()))/((1 + tem).pow(cuotas.toDouble()) - 1))
        binding.txtRCuota.setText(dfm.format(cuota).toString())

        cuotaSin = capital / cuotas;
        binding.txtRCuotaSin.setText(dfm.format(cuotaSin).toString())

        cuotaInt = cuota - cuotaSin;
        binding.txtRCuotaInt.setText(dfm.format(cuotaInt).toString())

        montoPagar = cuotas * cuota
        binding.txtRMontoPagar.setText(dfm.format(montoPagar).toString())

        interes = montoPagar - capital
        binding.txtRInteres.setText(dfm.format(interes).toString())

    }


}