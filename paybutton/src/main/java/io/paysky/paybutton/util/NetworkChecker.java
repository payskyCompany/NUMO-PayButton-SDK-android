package io.paysky.paybutton.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import io.paysky.paybutton.BuildConfig;

/**
 * Utility class for checking network connectivity
 */
public class NetworkChecker {
    private static final String TAG = "NetworkChecker";

    /**
     * Check if internet connection is available
     * Uses modern API for Android 6.0+ and fallback for older versions
     *
     * @param context Application context
     * @return true if internet is available, false otherwise
     */
    public static boolean isInternetAvailable(Context context) {
        if (context == null) {
            Log.w(TAG, "Context is null, returning false");
            return false;
        }

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            Log.w(TAG, "ConnectivityManager is null, returning false");
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use modern API for Android 6.0+
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "No active network found");
                }
                return false;
            }

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Network capabilities are null");
                }
                return false;
            }

            boolean hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Internet available: " + hasInternet);
                Log.d(TAG, "Network type: " + getNetworkType(capabilities));
            }

            return hasInternet;
        } else {
            // Fallback for older Android versions
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = networkInfo != null && networkInfo.isConnected();

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Internet available (legacy): " + isConnected);
                if (networkInfo != null) {
                    Log.d(TAG, "Network type: " + networkInfo.getTypeName());
                }
            }

            return isConnected;
        }
    }

    /**
     * Get network type as string for logging
     */
    private static String getNetworkType(NetworkCapabilities capabilities) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return "WiFi";
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return "Cellular";
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            return "Ethernet";
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            return "VPN";
        } else {
            return "Unknown";
        }
    }

    /**
     * Check if device is connected to WiFi
     */
    public static boolean isWifiConnected(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null &&
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null &&
                    networkInfo.isConnected() &&
                    networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
    }

    /**
     * Check if device is connected to mobile data
     */
    public static boolean isMobileDataConnected(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null &&
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null &&
                    networkInfo.isConnected() &&
                    networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
    }
}
