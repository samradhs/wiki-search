package com.data.example.wikisearch;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends FragmentActivity implements HttpCallInterface {

  private static final String TAG   = "SearchActivity";
  private static final long   DELAY = 1000;

  private WikiManager wikiManager;
  private WikiAdapter wikiAdapter;
  private Timer       timer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.i(TAG, "creating search activity");
    setContentView(R.layout.activity_search);
    wikiManager = WikiManager.getInstance(this);

    final EditText searchView = findViewById(R.id.sch_search_view);

    searchView.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
        Log.i(TAG, "onTextChanged: " + charSequence);

        if (timer != null) timer.cancel();

        if (charSequence.length() == 0) {
          changeViewsForError(false);

        } else {
          timer = new Timer();
          timer.schedule(
              new TimerTask() {
                @Override
                public void run() {
                  showProgress();
                  wikiManager.makeHttpCall(charSequence.toString(), SearchActivity.this);
                }
              },
              DELAY
          );
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });
  }

  @Override
  public void onHttpResponse(final ArrayList<WikiItem> items) {

    Log.i(TAG, "onHttpResponse: " + items.size());
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        setRecyclerView(items);
      }
    });
  }

  @Override
  public void onError() {

    Log.i(TAG, "onError");
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        changeViewsForError(true);
      }
    });
  }

  private void showProgress() {

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        findViewById(R.id.sch_req_progress).setVisibility(View.VISIBLE);
        findViewById(R.id.sch_search_results).setVisibility(View.GONE);
        findViewById(R.id.sch_search_placeholder).setVisibility(View.GONE);
      }
    });
  }

  private void changeViewsForError(boolean showToast) {

    int strId = Utility.isNetworkAvailable(this) ? R.string.general_error : R.string.internet_check;
    if (showToast) Toast.makeText(this, strId, Toast.LENGTH_SHORT).show();

    ProgressBar reqProgress     = findViewById(R.id.sch_req_progress);
    RecyclerView searchResults  = findViewById(R.id.sch_search_results);
    TextView placeHolder        = findViewById(R.id.sch_search_placeholder);

    reqProgress.setVisibility(View.GONE);
    searchResults.setVisibility(View.GONE);
    placeHolder.setVisibility(View.VISIBLE);

    EditText searchView = findViewById(R.id.sch_search_view);
    String input        = searchView.getText().toString();
    placeHolder.setText(input.isEmpty() ? R.string.sch_res_empty : strId);
  }

  private void setRecyclerView(ArrayList<WikiItem> wikiItems) {

    ProgressBar reqProgress     = findViewById(R.id.sch_req_progress);
    RecyclerView searchResults  = findViewById(R.id.sch_search_results);
    TextView placeHolder        = findViewById(R.id.sch_search_placeholder);

    reqProgress.setVisibility(View.GONE);
    searchResults.setVisibility(View.VISIBLE);
    placeHolder.setVisibility(View.GONE);

    if (wikiAdapter == null) {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
      searchResults.setLayoutManager(linearLayoutManager);
      wikiAdapter = new WikiAdapter(this, wikiItems);
      searchResults.setAdapter(wikiAdapter);

    } else {
      wikiAdapter.setList(wikiItems);
    }
  }
}
