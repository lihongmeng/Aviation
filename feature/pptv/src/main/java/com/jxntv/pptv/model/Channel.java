package com.jxntv.pptv.model;

import java.util.ArrayList;
import java.util.List;

public final class Channel extends Tag {
  private List<Media> media;

  public List<Media> getMedia() {
    if (media == null) return new ArrayList<>();
    return media;
  }

  public void setMedia(List<Media> media) {
    this.media = media;
  }
}
