package com.data.example.wikisearch;

/**
 * Created by SamradhShukla
 * on 20/08/18.
 */

class WikiItem {

  final int     index;
  final String  title;
  final String  desc;
  final String  thumbUrl;
  final long    pageId;

  WikiItem(int index, String title, String desc, String thumbUrl, long pageId) {

    this.index    = index;
    this.title    = title;
    this.desc     = desc;
    this.thumbUrl = thumbUrl;
    this.pageId   = pageId;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("index: ").append(index);
    sb.append(", title: ").append(title);
    sb.append(", desc: ").append(desc);
    sb.append(", thumbUrl: ").append(thumbUrl);
    sb.append(", pageId: ").append(pageId);
    return sb.toString();
  }
}
