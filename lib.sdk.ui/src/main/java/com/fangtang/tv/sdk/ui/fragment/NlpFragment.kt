package com.fangtang.tv.sdk.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fangtang.tv.base.fragment.FBaseFragment
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.base.IBaseFragment
import com.fangtang.tv.sdk.ui.net.bean.MovieEntityNlp
import com.fangtang.tv.sdk.ui.presenter.NlpFragmentPresenter

class NlpFragment : FBaseFragment(), IBaseFragment, NlpFragmentPresenter.IView {

    private var presenter: NlpFragmentPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_nlp, container, false)
        presenter = NlpFragmentPresenter(this)
        return view
    }

    override fun onStart() {
        super.onStart()
        presenter?.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    override fun getContext(): Context? {
        return activity
    }

    override fun getNlpData(): MovieEntityNlp? {
        return activity?.intent?.getSerializableExtra("data") as MovieEntityNlp?
    }

    override fun <T : View> findView(viewId: Int): T? {
        return activity?.findViewById(viewId)
    }
}