package io.paysky.paybutton.ui.fragment.webview;

import io.paysky.paybutton.ui.mvp.BasePresenter;

class WebPaymentPresenter extends BasePresenter<WebPaymentView> {


    public void load3dWebView() {
        view.load3dTransactionWebView();
    }
}
