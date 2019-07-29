package com.itsp.attendance;

public class Subject
{
    protected final static String ATTENDANCE_LABEL = "Attended:";
    protected final static String TOTAL_LABEL = "Total:";
    private String code;
    private String attendance;
    private String total;
    private String thumbnail;

    public Subject()
    {
    }

    public Subject(String code, String attendance, String total, String thumbnail)
    {
        this.code = code;
        this.attendance = attendance;
        this.total = total;
        this.thumbnail = thumbnail;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getAttendance()
    {
        return attendance;
    }

    public void setAttendance(String attendance)
    {
        this.attendance = attendance;
    }

    public String getTotal()
    {
        return total;
    }

    public void setTotal(String total)
    {
        this.total = total;
    }

    public String getThumbnail()
    {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
    }
}
