package com.example.kotlinclock

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import android.widget.SeekBar.OnSeekBarChangeListener
import java.io.File
import kotlin.collections.ArrayList
import android.widget.AdapterView.OnItemSelectedListener
import android.view.View
import android.widget.*

const val SLEEP_TIME: Long = 500
const val FONT_SUFFIX = ".ttf"

class MainActivity : AppCompatActivity() {

    private val formatter = SimpleDateFormat("HH:mm:ss")
    private val handler = Handler()
    private val fontList = fontList()
    private val colorMap = colorMap()
    private val colorNameList = colorMap.keys.toTypedArray()
    private val backColorNameList = colorMap.keys.sortedDescending().toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //時刻表示
        val timeView: TextView = findViewById(R.id.timeView)
        thread {
            while(true){
                handler.post{timeView.setText(formatter.format(Date()))}
                Thread.sleep(SLEEP_TIME)
            }
        }

        //フォントの大きさ用シークバー
        val sizeBar: SeekBar = findViewById(R.id.sizeBar)
        sizeBar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int, fromUser: Boolean
                ) {
                    timeView.setTextSize(sizeBar.progress.toFloat())
                    println("Update font size")
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            }
        )

        //フォントの選択用ドロップダウンリスト
        val fonts: Spinner = findViewById(R.id.fontSpinner)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, fontList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fonts.setAdapter(adapter)
        fonts.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                val spinner = parent as Spinner
                val item = spinner.selectedItem as String
                timeView.typeface = Typeface.createFromFile("/system/fonts/$item$FONT_SUFFIX")
                println("Update font")

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                //
            }
        })

        //文字色
        val fontColors: Spinner = findViewById(R.id.fontColor)
        val fontColorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colorNameList)
        fontColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontColors.setAdapter(fontColorAdapter)
        fontColors.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                val spinner = parent as Spinner
                val item = spinner.selectedItem as String
                val id = if(colorMap.get(item)!= null) colorMap.get(item) else Color.BLACK
                timeView.setTextColor(id!!)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                //
            }
        })

        //背景色
        val backColors: Spinner = findViewById(R.id.backColor)
        val backColorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, backColorNameList)
        backColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        backColors.setAdapter(backColorAdapter)
        backColors.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                val spinner = parent as Spinner
                val item = spinner.selectedItem as String
                val id = if(colorMap.get(item)!= null) colorMap.get(item) else Color.WHITE
                val layout: LinearLayout = findViewById(R.id.linearLayout)
                layout.setBackgroundColor(id!!)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                //
            }
        })


    }

    private fun colorMap(): Map<String, Int>{
        return mapOf("BLACK" to Color.BLACK, "WHITE" to Color.WHITE, "RED" to Color.RED, "GREEN" to Color.GREEN, "BLUE" to Color.BLUE)
    }

    private fun fontList(): ArrayList<String>{
        val fontNames = ArrayList<String>()
        val temp = File("/system/fonts/")

        for (font in temp.listFiles()) {
            val fontName = font.getName()
            if (fontName.endsWith(FONT_SUFFIX)) {
                fontNames.add(fontName.subSequence(0, fontName.lastIndexOf(FONT_SUFFIX)).toString())
            }
        }
        return  fontNames
    }

}
