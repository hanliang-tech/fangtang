package com.fangtang.tv.sdk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fangtang.tv.base.fragment.FBaseFragment
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.base.IBaseFragment
import com.fangtang.tv.sdk.ui.presenter.IotFragmentPresenter

class IotFragment : FBaseFragment(), IBaseFragment, IotFragmentPresenter.IView {

    private var presenter: IotFragmentPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_iot, container, false)
        presenter = IotFragmentPresenter(this)
        presenter?.onCreate()
        return view
    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume()
    }

    override fun onStart() {
        super.onStart()
        presenter?.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter?.onDestroy()
    }

    override fun <T : View> findView(viewId: Int): T? {
        return activity?.findViewById(viewId)
    }

}