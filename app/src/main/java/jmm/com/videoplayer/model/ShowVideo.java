package jmm.com.videoplayer.model;

public class ShowVideo {

    String thumb;
    String data;
    String id;
    String time;
    String date;
    String folder;
    String name;

    public ShowVideo(String thumb, String date, String id, String time, String data, String folder,String name) {
        this.thumb = thumb;
        this.data = data;
        this.id = id;
        this.time = time;
        this.date = date;
        this.folder = folder;
        this.name = name;
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
}
