package com.example.flashlightapp

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.SearchView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity() : AppCompatActivity() {

    private lateinit var flashLightSwitch: Switch
    private lateinit var searchFlashLightOption: SearchView
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraIdWithFlash: String
    lateinit var gestureDetector: GestureDetector
    var MIN_DISTANCE=75

    var x1:Float = 0.0f
    var x2:Float = 0.0f
    var y1:Float = 0.0f
    var y2:Float = 0.0f

    var isOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashLightSwitch = findViewById(R.id.switch1)
        searchFlashLightOption = findViewById(R.id.searchBox)

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraIdWithFlash = findCameraWithFlash(cameraManager)

        if (cameraIdWithFlash.isNotEmpty()) {
            flashLightSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    turnOnLight()
                } else {
                    turnOffLight()
                }
            }

            searchFlashLightOption.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        val lowercaseQuery = query.toLowerCase()
                        if (lowercaseQuery == "on") {
                            turnOnLight()
                        } else if (lowercaseQuery == "off") {
                            turnOffLight()
                        } else {
                            showToast("Please type 'on' or 'off' to enable or disable the flashlight.")
                        }
                    } else {
                        showToast("Type something.")
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        } else {
            showToast("Flashlight is not available on this device.")
            flashLightSwitch.isEnabled = false
            searchFlashLightOption.isEnabled = false
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                // Your fling logic here. For instance, you could check fling direction and turn the flashlight on/off.
                if (e1 != null && e2 != null) {
                    val deltaY = e2.y - e1.y

                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        if (isOn){
                            turnOffLight()
                            isOn = false
                        }else{
                            turnOnLight()
                            isOn = true
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // This allows the gestureDetector to handle the caught motion events
        if (event != null) {
            gestureDetector.onTouchEvent(event)
        }
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event)
    }


    fun turnOnLight() {
        try {
            if (cameraIdWithFlash.isNotEmpty()) {
                cameraManager.setTorchMode(cameraIdWithFlash, true)
                showToast("Flashlight is on")
                flashLightSwitch.isChecked = true
            }
        } catch (e: Exception) {
            Toast.makeText(this, "An error occurred while turning on the flashlight: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun turnOffLight() {
        try {
            if (cameraIdWithFlash.isNotEmpty()) {
                cameraManager.setTorchMode(cameraIdWithFlash, false)
                showToast("Flashlight is off")
                flashLightSwitch.isChecked = false
            }
        } catch (e: Exception) {
            Toast.makeText(this, "An error occurred while turning off the flashlight: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun findCameraWithFlash(cameraManager: CameraManager): String {
        val cameraList = cameraManager.cameraIdList
        for (cameraId in cameraList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val flashInfo = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
            if (flashInfo == true) {
                return cameraId
            }
        }
        return ""
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        cameraManager.setTorchMode(cameraIdWithFlash,false)
    }


}
