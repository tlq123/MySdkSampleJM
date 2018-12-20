package com.turing.turingsdksample.demo_common;

/**
 * 点播 请求状态
 */
public class RequestInfo {
    private String apiKey;
    private String deviceId;
    private int type;
    private Status status;


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    public class Status{
        private String title;
        private int mediaId;
        private int play;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getMediaId() {
            return mediaId;
        }

        public void setMediaId(int mediaId) {
            this.mediaId = mediaId;
        }

        public int getPlay() {
            return play;
        }

        public void setPlay(int play) {
            this.play = play;
        }

    }
}
