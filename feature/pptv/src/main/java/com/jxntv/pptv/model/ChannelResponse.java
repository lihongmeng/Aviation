package com.jxntv.pptv.model;

import java.util.List;

public final class ChannelResponse {
  private List<Banner> header;
  private List<Tag> tag;
  private List<Channel> plate;


  public List<Banner> getHeader() {
    return header;
  }

  public void setHeader(List<Banner> header) {
    this.header = header;
  }

  public List<Tag> getTag() {
    return tag;
  }

  public void setTag(List<Tag> hotTags) {
    this.tag = hotTags;
  }

  public List<Channel> getPlate() {
    return plate;
  }

  public void setPlate(List<Channel> plate) {
    this.plate = plate;
  }
}
