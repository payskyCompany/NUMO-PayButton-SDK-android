package io.paysky.paybutton.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.paysky.paybutton.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.paysky.paybutton.ui.dialog.InfoDialog;
import io.paysky.paybutton.ui.mvp.BaseView;
import io.paysky.paybutton.util.AppUtils;
import io.paysky.paybutton.util.ToastUtils;
/**
 * Created by Paysky-202 on 5/13/2018.
 */

public class BaseActivity extends AppCompatActivity implements BaseView {

    private ProgressDialog progressDialog;


    @Override
    protected void attachBaseContext(Context newBase) {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("medium.ttf")
                                //.setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void showProgress() {
        showProgress(getString(R.string.please_wait));
    }

    public void showProgress(@StringRes int message) {
        showProgress(getString(message));
    }

    @Override
    public void dismissProgress() {
        if (progressDialog == null) {
            return;
        }
        
        // Check if activity is finishing or destroyed
        if (isFinishing() || isDestroyed()) {
            progressDialog = null;
            return;
        }
        
        // Use handler to ensure dismissal happens on UI thread
        final ProgressDialog dialog = progressDialog;
        progressDialog = null; // Clear reference immediately to prevent double dismissal
        
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    // Double check activity state before dismissing
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    
                    // Check if dialog is still showing before dismissing
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (IllegalArgumentException e) {
                    // Ignore "View not attached to window manager" exception
                    // This happens when activity is destroyed during dismissal
                } catch (Exception e) {
                    // Safely handle any other exceptions during dismissal
                    e.printStackTrace();
                }
            }
        });
    }

    public void showProgress(String message) {
        try {
            // Check if activity is finishing or destroyed
            if (isFinishing() || isDestroyed()) {
                return;
            }
            
            // Dismiss existing dialog if any
            if (progressDialog != null && progressDialog.isShowing()) {
                dismissProgress();
            }
            
            progressDialog = AppUtils.createProgressDialog(this, message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            // Safely handle any exceptions during showing
            e.printStackTrace();
            progressDialog = null;
        }
    }

    public boolean isInternetAvailable() {
        return AppUtils.isInternetAvailable(this);
    }

    @Override
    public void showToast(@StringRes int text) {
        ToastUtils.showToast(this, text);
    }


    public void showToast(String text) {
        ToastUtils.showToast(this, text);
    }


    protected void replaceFragment(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean addOldToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        try {
            fragment = fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        fragmentTransaction.replace(R.id.fragment_frame, fragment);
        if (addOldToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void showNoInternetDialog() {
        new InfoDialog(this).setDialogTitle(R.string.error)
                .setDialogText(R.string.check_internet_connection)
                .showAgreeButton(R.string.ok, null).showDialog();
    }



}
