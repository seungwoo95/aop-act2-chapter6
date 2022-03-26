package com.example.aop_act2_chapter6

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }
    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }
    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }
    private val soundPool = SoundPool.Builder().build()
    private var tickingSoundPool: Int? = null
    private var bellSound: Int? = null

    private var currentCountDownTimer: CountDownTimer? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser){
                        updateRemainTimer(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    p0 ?: return
                    if(p0.progress == 0){
                        stopCountDown()
                    }else{
                        startCountDown()
                    }
                }
            }
        )
    }

    private fun initSounds(){
       tickingSoundPool = soundPool.load(this,R.raw.timer_ticking,1)
       bellSound = soundPool.load(this,R.raw.timer_bell,1)
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis,1000L){
            override fun onTick(p0: Long) {
                updateRemainTimer(p0)
                updateSeekBar(p0)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }

    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()
        tickingSoundPool?.let { soundId ->
            soundPool.play(soundId,1f,1f,0,-1,1f)
        }
    }

    private fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun completeCountDown(){
        updateRemainTimer(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSound?.let { soundId ->
            soundPool.play(soundId,1f,1f,0,0,1f)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateRemainTimer(remainMillis: Long){
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text ="%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text ="%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long){
        seekBar.progress = (remainMillis/ 1000 / 60).toInt()
    }
}