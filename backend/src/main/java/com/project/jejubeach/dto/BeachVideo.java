package com.project.jejubeach.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeachVideo {
  private String id;
  private String filename;
  private String title;
  private String description;
  private String videoUrl;

  public BeachVideo(String id, String filename, String title, String description, String videoUrl) {
    this.id = id;
    this.filename = filename;
    this.title = title;
    this.description = description;
    this.videoUrl = videoUrl;
  }

}
