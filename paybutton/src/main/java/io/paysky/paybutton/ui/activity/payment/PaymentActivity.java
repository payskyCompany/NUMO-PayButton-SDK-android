package io.paysky.paybutton.ui.activity.payment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import io.paysky.paybutton.R;

import io.paysky.paybutton.data.model.PaymentData;
import io.paysky.paybutton.data.network.ApiConnection;
import io.paysky.paybutton.ui.base.BaseActivity;
import io.paysky.paybutton.ui.fragment.manualpayment.ManualPaymentFragment;
import io.paysky.paybutton.ui.fragment.qr.QrCodePaymentFragment;
import io.paysky.paybutton.util.AllURLsStatus;
import io.paysky.paybutton.util.AppConstant;
import io.paysky.paybutton.util.AppUtils;
import io.paysky.paybutton.util.DialogUtils;
import io.paysky.paybutton.util.LocaleHelper;
import io.paysky.paybutton.util.PrefsUtils;
import io.paysky.paybutton.util.TransactionManager;
import me.grantland.widget.AutofitHelper;

public class PaymentActivity extends BaseActivity implements View.OnClickListener {

    //GUI.
    private ImageView headerBackImage;
    private LinearLayout cardPaymentLayout;
    private LinearLayout qrPaymentLayout;
    private TextView currencyTextView;
    private TextView amountTextView;
    private TextView merchantNameTextView, tvMerchantText,tvPowerByText, tvAmountText, tvTitle;
    private ImageView poweredByImageView;
    //Objects,
    public static Bitmap qrBitmap;
    private PaymentData paymentData;

    private AllURLsStatus allURLsStatus;
    private int urlStatus;
    private static boolean isFirst = true;

    //Variables.
    private static boolean NORMAL_CLOSE = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefsUtils.initialize(this);
//        LocaleHelper.setLocale(this, LocaleHelper.getLocale());
        makeActivityFullScreen();
        AppUtils.preventScreenshot(this);
        setContentView(R.layout.activity_pay);
        hideActionBar();
        initView();
        paymentData = getIntent().getExtras().getParcelable(AppConstant.BundleKeys.PAYMENT_DATA);
        urlStatus = getIntent().getExtras().getInt(AppConstant.BundleKeys.URL_ENUM_KEY);
        allURLsStatus = AllURLsStatus.values()[urlStatus];

        // show activity layout.
        AutofitHelper.create(merchantNameTextView);
        merchantNameTextView.setText(paymentData.merchantName);
        String amount = AppUtils.currencyFormat(paymentData.amountFormatted);
        paymentData.executedTransactionAmount = amount;
        amountTextView.setText(amount);
        if (LocaleHelper.getLocale().equals("en"))
            currencyTextView.setText(paymentData.currencyName);
        else currencyTextView.setText("د.ل");

        showPaymentBasedOnPaymentOptions(paymentData.paymentMethod);

    }


    public void makeActivityFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    public void hideActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
    }


    //GUI Methods.
    private void initView() {
        headerBackImage = findViewById(R.id.header_back_imageView);
        headerBackImage.setOnClickListener(this);
        merchantNameTextView = findViewById(R.id.pb_merchant_name_textView);
        currencyTextView = findViewById(R.id.currency_textView);
        amountTextView = findViewById(R.id.amount_textView);
        tvTitle = findViewById(R.id.tvTitle);
        tvAmountText = findViewById(R.id.tvAmountText);
        tvPowerByText = findViewById(R.id.tvPowerByText);
        tvMerchantText = findViewById(R.id.tvMerchantText);
        TextView languageTextView = findViewById(R.id.language_textView);
        poweredByImageView = findViewById(R.id.iv_powered_by);
        languageTextView.setOnClickListener(this);
        TextView termsTextView = findViewById(R.id.terms_conditions_textView);
        termsTextView.setOnClickListener(this);
        termsTextView.setOnClickListener(this);
        cardPaymentLayout = findViewById(R.id.card_payment_layout);
        qrPaymentLayout = findViewById(R.id.qr_payment_layout);
        cardPaymentLayout.setOnClickListener(this);
        qrPaymentLayout.setOnClickListener(this);
        setLang();
    }


    private void setLang() {
        if (ApiConnection.LANG.equals("ar")) {
            tvMerchantText.setText("التاجر");
            tvTitle.setText("نموذج الدفع السريع");
            tvAmountText.setText("المبلغ");
            tvPowerByText.setText("تم التطوير بواسطة باى سكاى");
        }
        CountDownTimer timer = new CountDownTimer(100, 1000) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                if (isFirst) {
                    LocaleHelper.changeAppLanguage(PaymentActivity.this);
                    NORMAL_CLOSE = false;
                    recreate();
                    isFirst = false;
                } else {
                    cancel();
                }

            }

        }.start();
//        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                LocaleHelper.changeAppLanguage(PaymentActivity.this);
//                NORMAL_CLOSE = false;
//                recreate();
//            }
//        }, 300);

    }

    public void showPaymentBasedOnPaymentOptions(int paymentOptions) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
        switch (paymentOptions) {
            case 0:
                // card payment enabled.
                cardPaymentLayout.setVisibility(View.VISIBLE);
                cardPaymentLayout.setOnClickListener(this);
                showCardPaymentFragment(bundle);
                changePaymentOptionButton(1);
                break;
            case 1:
                // wallet payment enabled.
                qrPaymentLayout.setVisibility(View.VISIBLE);
                qrPaymentLayout.setOnClickListener(this);
                showQrPaymentFragment(bundle);
                changePaymentOptionButton(2);
                break;
            case 2:
                // both payments enabled.
                cardPaymentLayout.setVisibility(View.VISIBLE);
                cardPaymentLayout.setOnClickListener(this);
                qrPaymentLayout.setVisibility(View.VISIBLE);
                qrPaymentLayout.setOnClickListener(this);
                showCardPaymentFragment(bundle);
                break;
        }
    }


    public void showCardPaymentFragment(Bundle bundle) {
        replaceFragmentAndRemoveOldFragment(ManualPaymentFragment.class, bundle);
    }


    public void showQrPaymentFragment(Bundle bundle) {
        replaceFragmentAndRemoveOldFragment(QrCodePaymentFragment.class, bundle);
    }


    public Context getContext() {
        return this;
    }

    public void setHeaderIcon(int icon) {
        headerBackImage.setImageResource(icon);
    }


    public void setHeaderIconClickListener(View.OnClickListener clickListener) {
        headerBackImage.setOnClickListener(clickListener);
    }


    public void replaceFragmentAndRemoveOldFragment(Class<? extends Fragment> fragmentClass, Bundle bundle) {
        replaceFragment(fragmentClass, bundle, false);
    }


    public void replaceFragmentAndAddOldToBackStack(Class<? extends Fragment> fragmentClass, Bundle bundle) {
        replaceFragment(fragmentClass, bundle, true);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.header_back_imageView) {
            onBackPressed();
        } else if (i == R.id.language_textView) {
            // change app language.
            LocaleHelper.changeAppLanguage(this);
            NORMAL_CLOSE = false;
            recreate();
//            Bundle bundle = new Bundle();
//            bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
//            startActivity(new Intent(this, PaymentActivity.class).putExtras(bundle));
//            finish();
        } else if (i == R.id.terms_conditions_textView) {
            // show terms dialog.
            DialogUtils.showTermsAndConditionsDialog(this);
        } else if (i == R.id.card_payment_layout) {
            changePaymentOptionButton(1);
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
            showCardPaymentFragment(bundle);
        } else if (i == R.id.qr_payment_layout) {
            changePaymentOptionButton(2);
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
            showQrPaymentFragment(bundle);
        }
    }

    private void changePaymentOptionButton(int type) {
        if (type == 1) {
            // manual payment.
            cardPaymentLayout.setBackgroundResource(R.drawable.payment_option_selected);
            TextView manualTextView = cardPaymentLayout.findViewById(R.id.card_payment_textView);
            manualTextView.setTextColor(getResources().getColor(android.R.color.white));
            if (LocaleHelper.getLocale().equals("ar")) {
                manualTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_card_white, 0);
                manualTextView.setText("بطاقة");
            } else {
                manualTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_white, 0, 0, 0);
            }
            qrPaymentLayout.setBackgroundResource(R.drawable.payment_option_unselected);
            TextView qrTextView = qrPaymentLayout.findViewById(R.id.qr_payment_textView);
            qrTextView.setTextColor(getResources().getColor(R.color.font_gray_color3));
            if (LocaleHelper.getLocale().equals("ar")) {
                manualTextView.setText("بطاقة");
                qrTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wallet_gray, 0);
            } else {
                qrTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet_gray, 0, 0, 0);
            }
        } else {
            // qr payment.
            cardPaymentLayout.setBackgroundResource(R.drawable.payment_option_unselected);
            TextView manualTextView = cardPaymentLayout.findViewById(R.id.card_payment_textView);
            manualTextView.setTextColor(getResources().getColor(R.color.font_gray_color3));
            if (LocaleHelper.getLocale().equals("ar")) {
                manualTextView.setText("بطاقة");
                manualTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_card_black, 0);
            } else {
                manualTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_black, 0, 0, 0);
            }
            qrPaymentLayout.setBackgroundResource(R.drawable.payment_option_selected);
            TextView qrTextView = qrPaymentLayout.findViewById(R.id.qr_payment_textView);
            qrTextView.setTextColor(getResources().getColor(android.R.color.white));
            if (LocaleHelper.getLocale().equals("ar")) {
                manualTextView.setText("بطاقة");
                qrTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wallet_white, 0);
            } else {
                qrTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet_white, 0, 0, 0);
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (NORMAL_CLOSE) {
            qrBitmap = null;
            isFirst = true;
            TransactionManager.sendTransactionEvent();
        } else {
            PaymentActivity.NORMAL_CLOSE = true;
        }
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    public void showManualPayment() {
        cardPaymentLayout.performClick();
    }

    public void hidePaymentOptions() {
        qrPaymentLayout.setVisibility(View.GONE);
        cardPaymentLayout.setVisibility(View.GONE);
    }


}
