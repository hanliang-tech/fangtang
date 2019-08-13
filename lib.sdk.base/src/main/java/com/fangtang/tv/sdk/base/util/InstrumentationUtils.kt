package com.fangtang.tv.sdk.base.util

import android.app.Instrumentation
import android.view.MotionEvent
import com.blankj.utilcode.util.ShellUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object InstrumentationUtils {

    private val mInstrumentation: Instrumentation by lazy {
        Instrumentation()
    }

    private val mUIScope = MainScope()

    fun touch(x: Float, y: Float, delay: Long = 0, callBack: () -> Unit = {}) {
        GlobalScope.launch {
            var time = System.currentTimeMillis()
            mInstrumentation.sendPointerSync(MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, x, y, 0))
            time += 30
            mInstrumentation.sendPointerSync(MotionEvent.obtain(time, time, MotionEvent.ACTION_UP, x, y, 0))
            if (delay > 0) {
                kotlinx.coroutines.delay(delay)
            }
            mUIScope.launch {
                callBack()
            }
        }
    }

    fun keyCode(keyCode: Int) {
        GlobalScope.launch {
            mInstrumentation.sendKeyDownUpSync(keyCode)
        }
    }

    fun keyCodeShell(keyCode: Int) {
        val cmds = arrayOf(
                "/dev/input/event0 4 4 29",
                "/dev/input/event0 1 2bf 1",
                "/dev/input/event0 0 0 0",
                "/dev/input/event0 1 2bf 0",
                "/dev/input/event0 0 0 0"
        )

        cmds.forEach {
            ShellUtils.execCmd("sendevent $it", false)
        }
    }

}