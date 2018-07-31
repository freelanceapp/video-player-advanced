package jmm.com.videoplayer.model;

public class Favrt {

    String thumb;
    String name;
    String folder;
    String time;
    String date;

    public Favrt(String thumb, String name, String folder, String time, String date) {
        this.thumb = thumb;
        this.name = name;
        this.folder = folder;
        this.time = time;
        this.date = date;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
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
}
