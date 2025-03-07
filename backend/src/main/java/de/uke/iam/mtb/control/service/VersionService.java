package de.uke.iam.mtb.control.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VersionService {

  @Value("${spring.application.version}")
  private String appVersion;

  public String getVersionFromPOM() {
    return appVersion;
  }
}
