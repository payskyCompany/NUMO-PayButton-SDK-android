package io.paysky.paybutton.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import io.paysky.paybutton.R;

import io.paysky.paybutton.ui.activity.payment.PaymentActivity;
import io.paysky.paybutton.ui.dialog.InfoDialog;
import io.paysky.paybutton.ui.mvp.BaseView;
import io.paysky.paybutton.util.AppUtils;
import io.paysky.paybutton.util.ToastUtils;

/**
 * Created by Paysky-202 on 5/14/2018.
 */

public class BaseFragment extends Fragment implements BaseView {

    public ProgressDialog progressDialog;
    public PaymentActivity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (PaymentActivity) getActivity();
    }

    @Override
    public void showProgress() {
        showProgress(getString(R.string.please_wait));
    }

    public void showProgress(@StringRes int message) {
        if (isDetached())return;
        showProgress(getString(message));
    }

    @Override
    public void dismissProgress() {
        if (progressDialog == null) {
            return;
        }
        
        // Check if fragment is still attached and activity is valid
        if (isDetached() || getActivity() == null || getActivity().isFinishing()) {
            progressDialog = null;
            return;
        }
        
        // Use handler to ensure dismissal happens on UI thread
        final ProgressDialog dialog = progressDialog;
        final PaymentActivity currentActivity = activity;
        progressDialog = null; // Clear reference immediately to prevent double dismissal
        
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    // Double check activity state before dismissing using stored reference
                    if (currentActivity == null || currentActivity.isFinishing() || isDetached()) {
                        return;
                    }
                    
                    // Check if dialog is still showing before dismissing
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (IllegalArgumentException e) {
                    // Ignore "View not attached to window manager" exception
                    // This happens when activity/fragment is destroyed during dismissal
                    // Silently catch and ignore - no need to log
                } catch (Exception e) {
                    // Safely handle any other exceptions during dismissal
                    // Only log if it's not the window manager exception
                    if (!(e instanceof IllegalArgumentException) || 
                        !e.getMessage().contains("not attached to window manager")) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void showToast(int message) {
        ToastUtils.showToast(getActivity(), message);
    }

    public void showProgress(String message) {
        try {
            // Check if fragment is still attached and activity is valid
            if (isDetached() || getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            
            // Dismiss existing dialog if any
            if (progressDialog != null && progressDialog.isShowing()) {
                dismissProgress();
            }
            
            progressDialog = AppUtils.createProgressDialog(getActivity(), message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            // Safely handle any exceptions during showing
            e.printStackTrace();
            progressDialog = null;
        }
    }

    public String getText(TextView textView) {
        return textView.getText().toString().trim();
    }


    public boolean isInternetAvailable() {
        return AppUtils.isInternetAvailable(activity);
    }

    @Override
    public void showNoInternetDialog() {
        new InfoDialog(activity).setDialogTitle(R.string.error)
                .setDialogText(R.string.check_internet_connection)
                .showAgreeButton(R.string.ok, null).showDialog();
    }

    public boolean isEmpty(String text) {
        return text.isEmpty();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Dismiss dialog in onDestroyView to prevent WindowLeaked error
        dismissProgressSafely();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Final cleanup - dismiss any remaining dialogs
        dismissProgressSafely();
    }

    /**
     * Safely dismiss progress dialog without checks - used during cleanup
     */
    private void dismissProgressSafely() {
        if (progressDialog != null) {
            final ProgressDialog dialog = progressDialog;
            progressDialog = null; // Clear reference immediately
            
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (IllegalArgumentException e) {
                // Ignore "View not attached to window manager" exception
                // This is expected when activity/fragment is being destroyed
            } catch (Exception e) {
                // Ignore any other exceptions during cleanup
            }
        }
    }
}
