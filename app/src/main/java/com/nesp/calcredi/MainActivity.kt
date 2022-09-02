package com.nesp.calcredi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nesp.calcredi.databinding.ActivityMainBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.round

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
                calcularCredito()
            }

            btnLimpiar.setOnClickListener{
                limpiarDatos()
            }
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
        binding.txtTem.setText(df.format(tem).toString())

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