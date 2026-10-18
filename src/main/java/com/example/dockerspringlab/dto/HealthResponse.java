package com.example.dockerspringlab.dto;

import java.time.LocalDateTime;

public class HealthResponse {

    private String status;
    private String appName;
    private String javaVersion;
    private LocalDateTime timestamp;

    public HealthResponse() {
    }

    public HealthResponse(String status, String appName, String javaVersion, LocalDateTime timestamp) {
        this.status = status;
        this.appName = appName;
        this.javaVersion = javaVersion;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HealthResponse{" +
                "status='" + status + '\'' +
                ", appName='" + appName + '\'' +
                ", javaVersion='" + javaVersion + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
