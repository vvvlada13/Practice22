package com.example.practice22

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView

    private val randomByteArraySize = 16777216

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        calculateButton = findViewById(R.id.button)
        resultTextView = findViewById(R.id.resultTextView)

        calculateButton.setOnClickListener {
            CalculateHashTask().execute()
        }
    }
    private inner class CalculateHashTask : AsyncTask<Void, Int, String>() {

        override fun onPreExecute() {
            progressBar.visibility = View.VISIBLE
            calculateButton.isEnabled = false
        }

        override fun doInBackground(vararg params: Void?): String {
            val randomByteArray = ByteArray(randomByteArraySize) { _ -> kotlin.random.Random.nextInt(256).toByte() }
            val hashSumByteArray = crc16(randomByteArray)
            val hashSumHex = hashSumByteArray.toHex()
            return hashSumHex
        }

        override fun onPostExecute(result: String) {
            progressBar.visibility = View.GONE
            calculateButton.isEnabled = true
            resultTextView.text = result
        }
    }

    fun crc16(byteArray: ByteArray): ByteArray {
        var crc = 0xffff
        byteArray.forEach { byte ->
            crc = (crc ushr 8 or crc shl 8) and 0xffff
            crc = crc xor (byte.toInt() and 0xff)
            crc = crc xor ((crc and 0xff) shr 4)
            crc = crc xor ((crc shl 12) and 0xffff)
            crc = crc xor (((crc and 0xff) shl 5) and 0xffff)
        }
        crc = crc and 0xffff
        return crc.to2ByteArray()
    }

    fun Int.to2ByteArray(): ByteArray = byteArrayOf(toByte(), shr(8).toByte())

    fun ByteArray.toHex(): String = joinToString("") { eachByte -> "%02x".format(eachByte) }
}