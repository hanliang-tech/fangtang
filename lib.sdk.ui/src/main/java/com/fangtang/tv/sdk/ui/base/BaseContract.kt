package com.fangtang.tv.sdk.ui.base

import android.os.Bundle
import android.view.View

class BaseContract {

    interface IView {
        fun <T : View> findView(viewId: Int): T?
    }

    open class Presenter {

        open fun onCreate() {}
        open fun onResume() {}
        open fun onStart() {}
        open fun onPause() {}
        open fun onStop() {}
        open fun onSaveInstance(bundle: Bundle) {}
        open fun onDestroy() {}

    }
}