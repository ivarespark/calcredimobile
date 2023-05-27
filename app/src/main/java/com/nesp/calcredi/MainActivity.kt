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

            btnCompartir.setOnClickListener{
                compartir()
            }
        }
    }

    private fun validarCampos() : Boolean {
        var retorno : Boolean = true
        cargarDatos()
        if (capital == 0.0) retorno = false
        if (tcea == 0.0) retorno = false
        if (cuotas == 0) retorno = false
        return retorno
    }

    private fun armarInfo() : String {
        var cInfo = ""
        cInfo += "Kalcredi 1.0" + "\r\n"
        cInfo += "--------------------" + "\n"
        cInfo += "Capital: $ " + dfm.format(capital).toString() + "\n"
        cInfo += "Cuotas: " + cuotas + "\n"
        cInfo += "TCEA/TEA: " + df.format(tcea).toString() + " %\n"
        cInfo += "TEM: " + df.format(tem * 100).toString() + " %\n"
        cInfo += "Monto cuota: $ " + dfm.format(cuota).toString() + "\n"
        cInfo += "Cuota sin interes: $ " + dfm.format(cuotaSin).toString() + "\n"
        cInfo += "Interes cuota: $ " + dfm.format(cuotaInt).toString() + "\n"
        cInfo += "Interes total: $ " + dfm.format(interes).toString() + "\n"
        cInfo += "Monto total a pagar: $ " + dfm.format(montoPagar).toString()
        return cInfo
    }

    private fun compartir() {
        var cInfo = ""
        if (cuota > 0){
            cInfo = armarInfo()
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,cInfo)
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "Compartir datos del cr�dito")
            }
            val sharedIntent = Intent.createChooser(intent,null)
            startActivity(sharedIntent)
        }else{
            mostrarMensaje("Primero debe calcular")
        }
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

    private fun mostrarMensaje(msg : String){
        Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
    }

    private fun cargarDatos(){
        try {
            with(binding){
                capital = if (!txtCapital.text.toString().equals("")) txtCapital.text.toString().toDouble() else 0.0
                tcea = if (!txtTcea.text.toString().equals("")) txtTcea.text.toString().toDouble() else 0.0
                cuotas = if (!txtCuotas.text.toString().equals("")) txtCuotas.text.toString().toInt() else 0
            }
        } catch (e : Exception){
            mostrarMensaje("Error cargando datos: " + e.message.toString())
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
        try {
            if (!validarCampos()) throw ValidacionException("Debe completar todos los datos para calcular")
            cargarDatos();
            tem = ((1 + (tcea / 100.00)).pow(1.00 / 12.00)) - 1
            df.roundingMode = RoundingMode.UP
            dfm.roundingMode = RoundingMode.UP
            binding.txtTem.setText(df.format(tem * 100).toString())

            cuota = capital * ((tem * (1 + tem).pow(cuotas.toDouble())) / ((1 + tem).pow(cuotas.toDouble()) - 1))
            binding.txtRCuota.setText(dfm.format(cuota).toString())

            cuotaSin = capital / cuotas
            binding.txtRCuotaSin.setText(dfm.format(cuotaSin).toString())

            cuotaInt = cuota - cuotaSin
            binding.txtRCuotaInt.setText(dfm.format(cuotaInt).toString())

            montoPagar = cuotas * cuota
            binding.txtRMontoPagar.setText(dfm.format(montoPagar).toString())

            interes = montoPagar - capital
            binding.txtRInteres.setText(dfm.format(interes).toString())
        } catch (e: ValidacionException){
            mostrarMensaje(e.message.toString())
        } catch (e: Exception){
            mostrarMensaje("Error no controlado: " + e.message.toString())
        }
    }
}