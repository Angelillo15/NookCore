package com.nookure.core.boot.paper.annotation;

public enum Features {
  CORE("NookCore-Core"),
  CONFIG("NookCore-Config"),
  DATABASE("NookCore-Database"),
  EVENT("NookCore-Event"),
  LOGGER("NookCore-Logger"),
  MESSENGER("NookCore-Messenger"),
  PLAYER("NookCore-Player");

  private final String artifactId;

  Features(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getArtifactId() {
    return artifactId;
  }
}
