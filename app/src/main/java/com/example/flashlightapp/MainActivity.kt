package com.example.flashlightapp

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.SearchView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var flashLightSwitch: Switch
    private lateinit var searchFlashLightOption: SearchView
    private lateinit var gestureDetector: GestureDetector
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraIdWithFlash: String


    var x1:Float = 0.0f
    var x2:Float = 0.0f
    var y1:Float = 0.0f
    var y2:Float = 0.0f
    
    companion object {
        const val MIN_DISTANCE = 150
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashLightSwitch = findViewById(R.id.switch1)
        searchFlashLightOption = findViewById(R.id.searchBox)
        gestureDetector = GestureDetector(this, this)

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
    }
    private fun turnOnLight() {
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

    private fun turnOffLight() {
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            gestureDetector.onTouchEvent(event)
            return true // Consume the event to prevent app from exiting on blank area tap
        }
        return super.onTouchEvent(event)
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

    override fun onDown(p0: MotionEvent): Boolean {
        TODO("Not yet implemented")
    }

    override fun onShowPress(p0: MotionEvent) {
        TODO("Not yet implemented")
    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        super.onPause()
        cameraManager.setTorchMode(cameraIdWithFlash,false)
    }

    override fun onLongPress(p0: MotionEvent) {
        TODO("Not yet implemented")
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val deltaX = e2.x - (e1?.x ?: 0f)
        val deltaY = e2.y - (e1?.y ?: 0f)
        val minFlingVelocity = 1000

        if (Math.abs(deltaY) > minFlingVelocity) {
            if (deltaY > 0) {
                // Fling up, turn off flashlight and switch off
                turnOffLight()
                flashLightSwitch.isChecked = false
            } else {
                // Fling down, turn on flashlight and switch on
                turnOnLight()
                flashLightSwitch.isChecked = true
            }
            return true
        }

        return false
    }




}
