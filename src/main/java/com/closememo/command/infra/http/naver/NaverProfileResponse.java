package com.closememo.command.infra.http.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class NaverProfileResponse {

  private String resultcode;
  private String message;
  @JsonProperty("response")
  private NaverProfile naverProfile;

  @Getter
  public static class NaverProfile {
    private String id;
    private String nickname;
    private String name;
    private String email;
    private String gender;
    private String age;
    private String birthday;
    @JsonProperty("profile_image")
    private String profileImage;
    private String birthyear;
    private String mobile;
  }
}
