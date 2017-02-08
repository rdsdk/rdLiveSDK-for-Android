package com.rd.mix;

/**
 * Created by JIAN on 2017/1/19.
 */

public class MixInfo {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MixInfo(String duration, String path, String name) {
        this.duration = duration;
        this.path = path;
        this.name = name;
    }

    private String duration;
    private String name;
    private String path;

    @Override
    public String toString() {
        return "MixInfo{" +
                "duration='" + duration + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
