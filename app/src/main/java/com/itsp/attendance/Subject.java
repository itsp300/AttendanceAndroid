package com.itsp.attendance;

public class Subject {
    private String code;
    private String attendance;
    private String total;
    private String thumbnail;

    protected final static String ATTENDANCE_LABEL = "Attended:";
    protected final static String TOTAL_LABEL = "Total:";

    public Subject() {}

    public Subject(String code, String attendance, String total, String thumbnail) {
        this.code = code;
        this.attendance = attendance;
        this.total = total;
        this.thumbnail = thumbnail;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotal() {
        return total;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
