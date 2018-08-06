package jmm.com.videoplayer.model;

public class ShowVideo {

    String thumb;
    String data;
    String id;
    String time;
    String date;
    String folder;
    String name;
    String resolution;
    String size;
    boolean isFavrt;

    public ShowVideo() {
    }

    public ShowVideo(String thumb, String resolution,  String time, String data, String folder, String name,String size) {
        this.thumb = thumb;
        this.data = data;
        this.id = id;
        this.time = time;
        this.resolution = resolution;
        this.folder = folder;
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isFavrt() {
        return isFavrt;
    }

    public void setFavrt(boolean favrt) {
        isFavrt = favrt;
    }
}
