package com.data.example.wikisearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by SamradhShukla
 * on 20/08/18.
 */

class Utility {

  static boolean isNetworkAvailable(Context context) {

    ConnectivityManager connectivityManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetworkInfo = null;
    if (connectivityManager != null) {
      activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    }

    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

}
