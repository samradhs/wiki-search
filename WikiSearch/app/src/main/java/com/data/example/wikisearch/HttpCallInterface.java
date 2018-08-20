package com.data.example.wikisearch;

import java.util.ArrayList;

/**
 * Created by SamradhShukla
 * on 20/08/18.
 */

interface HttpCallInterface {

  void onHttpResponse(ArrayList<WikiItem> items);
  void onError();
}
