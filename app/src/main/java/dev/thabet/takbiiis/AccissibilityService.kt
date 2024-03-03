package dev.thabet.takbiiis

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.accessibility.AccessibilityEvent
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class Action(val n: Int){
    CLICK(1),CLICK10(10),CLICK100(100),
}

class TakbiiisAccissibilityService: AccessibilityService() {
    init {
        setupInstance(this)
    }
    companion object{
        private var instance:TakbiiisAccissibilityService? = null
        fun setupInstance(a:TakbiiisAccissibilityService){
            this.instance = a
        }
        lateinit var windowManager:WindowManager
        var screenWidth = 0
        var screenHeight = 0
        var x = 0f
        var y = 600f
        var distance = 300

        fun refresh(){
            instance?.draw()
        }
    }

    private val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams ()

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val displayMetrics = windowMetrics.bounds
            screenHeight = displayMetrics.height()
            screenWidth = displayMetrics.width()
        }


        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START

        draw()

        btn(params,1,1,"01",Action.CLICK)
        btn(params,2,1,"10",Action.CLICK10)
        btn(params,3,1,"100",Action.CLICK100)

    }
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

    private fun click() {
        // For a single tap, a duration of 1 ms is enough
        val DURATION = 50L
        // Create a path for the gesture
        val clickPath = Path()
        clickPath.moveTo(x+(0..distance).random(), y+(0..distance).random())
        // Create a stroke for the gesture
        val clickStroke = GestureDescription.StrokeDescription(clickPath, 0, DURATION)
        // Create a gesture description
        val clickBuilder = GestureDescription.Builder()
        clickBuilder.addStroke(clickStroke)
        // Dispatch the gesture to the system
        dispatchGesture(clickBuilder.build(), null, null)
    }
    class RectangleView (context: Context): View(context) {
        private val paint: Paint = Paint ()
        init {
            val translucentRed = Color(255, 255, 255, 50)
            paint.color = translucentRed.toArgb()
        }
        override fun onDraw (canvas: Canvas) {
            super.onDraw (canvas)
            canvas.drawRect (0F, 0F, width.toFloat(), height.toFloat(), paint)
        }
    }
    var focusView:RectangleView? = null
    fun draw(){
        if (focusView != null){
            windowManager.removeView (focusView)
            focusView = null
        }
        layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.width = distance
        layoutParams.height = distance
        layoutParams.gravity = Gravity.TOP or Gravity.START

        layoutParams.x = x.toInt()
        layoutParams.y = y.toInt()- distance/2

        focusView = RectangleView(this)
        windowManager.addView(focusView, layoutParams)

    }

    private fun btn(params: WindowManager.LayoutParams,x:Int,y:Int,text:String,action:Action){
        val button = Button(this)
        button.text = text
        button.setBackgroundColor(0xFFFFFFF)
        val thirdSize = screenWidth / 3
        params.x = thirdSize * (x-1)
        params.y = y * 120
        button.setOnClickListener {
            for( i in 0 .. 10){
                click()
                Thread.sleep(90)
                for (j in 0 until action.n){
                    click()
                    Thread.sleep((100..250).random().toLong())
                }
                Thread.sleep(5000)
            }

        }
        windowManager.addView(button, params)
    }
}