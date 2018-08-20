package com.data.example.wikisearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SamradhShukla
 * on 20/08/18.
 */

class WikiManager {

  private static final String TAG         = "WikiManager";

  // Wikipedia response keys
  private static final String QUERY       = "query";
  private static final String PAGES       = "pages";
  private static final String PAGE_ID     = "pageid";
  private static final String TITLE       = "title";
  private static final String INDEX       = "index";
  private static final String THUMBNAIL   = "thumbnail";
  private static final String SOURCE      = "source";
  private static final String TERMS       = "terms";
  private static final String DESCRIPTION = "description";

  private static WikiManager  wikiManager;
  private OkHttpClient        okHttpClient;

  static WikiManager getInstance(Context context) {

    if (wikiManager == null) wikiManager = new WikiManager(context);
    return wikiManager;
  }

  @SuppressWarnings("unused")
  private WikiManager() {}

  private WikiManager(Context context) {

    Cache cache  = new Cache(context.getCacheDir(), 1024 * 1024 * 10);
    okHttpClient = new OkHttpClient.Builder()
        .addNetworkInterceptor(new ResponseCacheInterceptor())
        .addInterceptor(new OfflineResponseCacheInterceptor(context))
        .cache(cache)
        .build();
  }

  void makeHttpCall(String word, final HttpCallInterface httpCallInterface) {

    word = word.replace(' ', '+');

    String url = "https://en.wikipedia.org//w/api.php?action=query&format=json&prop=pageimages%7Cpageterms&generator=prefixsearch&redirects=1&formatversion=2&piprop=thumbnail&pithumbsize=50&pilimit=10&wbptterms=description&gpssearch="
        + word + "&gpslimit=10";

    Request request = new Request.Builder()
        .cacheControl(new CacheControl.Builder()
          .maxStale(10, TimeUnit.DAYS)
          .build())
        .url(url).build();

    okHttpClient.newCall(request).enqueue(new Callback() {

      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        httpCallInterface.onError();
      }

      @SuppressWarnings("ConstantConditions")
      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

        if (response.body() == null) return;

        String respBodyStr = response.body().string();
        processResponse(respBodyStr, httpCallInterface);
      }
    });
  }

  private void processResponse(String respBodyStr, HttpCallInterface httpCallInterface) {

    Log.i(TAG, "processing response");
    if (respBodyStr.isEmpty()) {
      httpCallInterface.onError();
      return;
    }

    try {
      JSONObject resp = new JSONObject(respBodyStr);

      if (!resp.has(QUERY)) {
        return;
      }

      JSONObject query = (JSONObject) resp.get(QUERY);
      if (!query.has(PAGES)) {
        return;
      }

      ArrayList<WikiItem> wikiItems = parseList(query.getJSONArray(PAGES));
      Collections.sort(wikiItems, new Comparator<WikiItem>() {
        @Override
        public int compare(WikiItem wiki1, WikiItem wiki2) {

          return wiki1.index - wiki2.index;
        }
      });

      for (WikiItem wikiItem: wikiItems) {
        Log.e(TAG, wikiItem.toString());
      }

      httpCallInterface.onHttpResponse(wikiItems);

    } catch (JSONException e) {
      Log.e(TAG, e.toString());
    }
  }

  private ArrayList<WikiItem> parseList(JSONArray pages) {

    Log.i(TAG, "parsing list");

    ArrayList<WikiItem> list = new ArrayList<>();
    if (pages.length() == 0) return list;

    for (int i = 0; i < pages.length(); i++) {

      JSONObject json;

      try {
        json = pages.getJSONObject(i);
      } catch (Exception e) {
        Log.e(TAG, e.toString());
        continue;
      }

      long pageId   = json.optLong(PAGE_ID, -1);
      int index     = json.optInt(INDEX, 100);
      String title  = json.optString(TITLE, "");

      String thumbUrl = "";
      if (json.has(THUMBNAIL)) {
        JSONObject thumbnail;
        try {
          thumbnail = json.getJSONObject(THUMBNAIL);
          thumbUrl  = thumbnail.optString(SOURCE, "");
        } catch (Exception e) {
          Log.e(TAG, e.toString());
        }
      }

      String desc = "";
      if (json.has(TERMS)) {
        JSONObject terms;
        try {
          terms = json.getJSONObject(TERMS);
          JSONArray jsonArray = terms.optJSONArray(DESCRIPTION);
          if (jsonArray.length() != 0) desc = jsonArray.getString(0);
        } catch (Exception e) {
          Log.e(TAG, e.toString());
        }
      }

      list.add(new WikiItem(index, title, desc, thumbUrl, pageId));
    }

    return list;
  }

  private static class OfflineResponseCacheInterceptor implements Interceptor {

    private Context context;

    private OfflineResponseCacheInterceptor(Context context) {
      this.context = context;
    }

    @Override
    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
      Request request = chain.request();
      if (!Utility.isNetworkAvailable(context)) {
        request = request.newBuilder()
            .header("Cache-Control",
                "public, only-if-cached, max-stale=" + 2419200)
            .build();
      }

      return chain.proceed(request);
    }
  }

  private static class ResponseCacheInterceptor implements Interceptor {
    @Override
    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
      okhttp3.Response originalResponse = chain.proceed(chain.request());

      return originalResponse.newBuilder()
          .header("Cache-Control", "public, max-age=" + 60)
          .build();
    }
  }
}
