package com.data.example.wikisearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SamradhShukla
 * on 20/08/18.
 */

public class WikiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final String TAG       = "WikiAdapter";
  private static final String WIKI_URL  = "https://en.wikipedia.org/wiki?curid=";

  private List<WikiItem>  wikiItems;
  private Context         context;

  WikiAdapter(Context context, List<WikiItem> wikiItems) {

    this.wikiItems  = wikiItems;
    this.context    = context;
  }

  void setList(List<WikiItem> wikiItems) {
    this.wikiItems  = wikiItems;
    notifyDataSetChanged();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view               = inflater.inflate(R.layout.wiki_item, parent, false);
    return new WikiHolder(view);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    WikiHolder wikiHolder = (WikiHolder) holder;
    wikiHolder.bindData(position);
  }

  @Override
  public int getItemCount() {

    return wikiItems.size();
  }

  private class WikiHolder extends RecyclerView.ViewHolder {

    private View      rootView;
    private ImageView image;
    private TextView  title;
    private TextView  desc;

    WikiHolder(View itemView) {
      super(itemView);

      rootView  = itemView;
      image     = itemView.findViewById(R.id.wiki_image);
      title     = itemView.findViewById(R.id.wiki_item_title);
      desc      = itemView.findViewById(R.id.wiki_item_desc);
    }

    void bindData(final int position) {

      final WikiItem wikiItem = wikiItems.get(position);

      title.setText(wikiItem.title);
      desc.setText(wikiItem.desc);

      rootView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Log.i(TAG, "item clicked at position: " + position);

          if (Utility.isNetworkAvailable(context)) {
            openPage(wikiItem.title, wikiItem.pageId);
          } else {
            Toast.makeText(context, R.string.internet_check, Toast.LENGTH_SHORT).show();
          }
        }
      });

      if (wikiItem.thumbUrl != null && !wikiItem.thumbUrl.isEmpty()) {
        Picasso.with(context)
            .load(wikiItem.thumbUrl)
            .placeholder(R.drawable.ic_image_default)
            .into(image);

      } else {
        image.setImageResource(R.drawable.ic_image_default);
      }
    }

    private void openPage(String title, long pageId) {

      Intent intent = new Intent(context, WikiActivity.class);
      Bundle bundle = new Bundle();
      bundle.putString(WikiActivity.TITLE, title);
      bundle.putString(WikiActivity.URL, WIKI_URL + pageId);
      intent.putExtras(bundle);
      context.startActivity(intent);
    }
  }
}
