package com.example.amrel.paybuttonexample;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import io.paysky.paybutton.data.model.SuccessfulCardTransaction;
import io.paysky.paybutton.data.model.SuccessfulWalletTransaction;
import io.paysky.paybutton.exception.TransactionException;
import io.paysky.paybutton.ui.PayButton;
import io.paysky.paybutton.util.AllURLsStatus;
import io.paysky.paybutton.util.AppUtils;
import io.paysky.paybutton.util.LocaleHelper;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    //GUI.
    private EditText merchantIdEditText, terminalIdEditText, amountEditText, secureHashKeyEditText;
    private TextView paymentStatusTextView;
    private TextView languageTextView;
    private EditText currencyEditText;
    private Spinner spinner_type;

    String[] list_to_show = {"PRODUCTION", "TESTING", "PACE_PAY"};
    AllURLsStatus[] list_to_URLS = {AllURLsStatus.PRODUCTION, AllURLsStatus.STAGINIG,
            AllURLsStatus.PACE_PAY};
    int item_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spinner_type = findViewById(R.id.spinner_type);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list_to_show);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(dataAdapter);
        spinner_type.setSelection(item_position);

        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                item_position = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // find views.
        TextView payTextView = findViewById(R.id.pay_textView);
        merchantIdEditText = findViewById(R.id.merchant_id_editText);
        terminalIdEditText = findViewById(R.id.terminal_id_editText);
        amountEditText = findViewById(R.id.amount_editText);
        paymentStatusTextView = findViewById(R.id.payment_status_textView);
        currencyEditText = findViewById(R.id.currency_editText);
        secureHashKeyEditText = findViewById(R.id.secureHash_editText);

        payTextView.setOnClickListener(v -> {
            paymentStatusTextView.setText("");
            String terminalId = terminalIdEditText.getText().toString().trim();
            String merchantId = merchantIdEditText.getText().toString().trim();
            String amount = amountEditText.getText().toString().trim();
            String secureHashKey = secureHashKeyEditText.getText().toString().trim();

            boolean hasErrors = false;
            if (terminalId.isEmpty()) {
                terminalIdEditText.setError(getString(R.string.required));
                hasErrors = true;
            }
            if (merchantId.isEmpty()) {
                merchantIdEditText.setError(getString(R.string.required));
                hasErrors = true;
            }
            if (amount.isEmpty() || amount.equals("0")) {
                amountEditText.setError(getString(R.string.required));
                hasErrors = true;
            }

            if (secureHashKey.isEmpty()) {
                secureHashKeyEditText.setError(getString(R.string.required));
                hasErrors = true;
            }

            if (hasErrors) {
                return;
            }

            // add payments data.
            PayButton payButton = new PayButton(MainActivity.this);
            payButton.setMerchantId(merchantId); // Merchant id
            payButton.setTerminalId(terminalId); // Terminal  id
            payButton.setAmount(Double.parseDouble(amount)); // Amount
            String a = currencyEditText.getText().toString();
            if (a.isEmpty()) {
                payButton.setCurrencyCode(0); // Currency Code
            } else {
                payButton.setCurrencyCode(Integer.parseInt(a)); // Currency Code
            }
            payButton.setMerchantSecureHash(secureHashKey);
            payButton.setTransactionReferenceNumber(AppUtils.generateRandomNumber());
            payButton.setProductionStatus(list_to_URLS[item_position]);
            payButton.setLang(MyLocaleHelper.getLocale());
            Log.d("LocalLang", MyLocaleHelper.getLocale());
            payButton.createTransaction(new PayButton.PaymentTransactionCallback() {
                @Override
                public void onCardTransactionSuccess(SuccessfulCardTransaction cardTransaction) {
                    paymentStatusTextView.setText(cardTransaction.toString());
                }

                @Override
                public void onWalletTransactionSuccess(SuccessfulWalletTransaction walletTransaction) {
                    paymentStatusTextView.setText(walletTransaction.toString());
                }

                @Override
                public void onError(TransactionException error) {
                    paymentStatusTextView.setText("failed by:- " + error.errorMessage);
                }
            });
        });
        TextView appVersion = findViewById(R.id.app_version_textView);
        appVersion.setText("PaySDK - PayButton module - Ver.  " + AppUtils.getVersionNumber(this));
        ImageView logoImageView = findViewById(R.id.logo_imageView);
        logoImageView.setOnLongClickListener(this);
        languageTextView = findViewById(R.id.language_textView);
        languageTextView.setOnClickListener(this);
        if (LocaleHelper.getLocale().equals("ar")) {
            languageTextView.setText(R.string.english);
            merchantIdEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            terminalIdEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            amountEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        } else {
            languageTextView.setText(io.paysky.paybutton.R.string.arabic);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        startActivity(new Intent(this, SettingActivity.class));
        return true;
    }

    @Override
    public void onClick(View view) {
        MyLocaleHelper.changeAppLanguage(this);
        recreate();
        if (LocaleHelper.getLocale().equals("ar")) {
            languageTextView.setText(R.string.english);
            merchantIdEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            terminalIdEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            amountEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        } else {
            languageTextView.setText(io.paysky.paybutton.R.string.arabic);
        }
    }

}

class MyLocaleHelper {


    public static void setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());

    }


    public static String getLocale() {
        return Locale.getDefault().getLanguage();
    }

    public static void changeAppLanguage(Context context) {
        String appLocale = Locale.getDefault().getLanguage();
        if (appLocale.equals("ar")) {
            setLocale(context, "en");
        } else {
            setLocale(context, "ar");
        }
    }
}