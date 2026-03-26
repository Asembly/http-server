package org.example.enums;

public enum StatusCode {
    OK(200,"OK"),
    BAD_REQUEST(400,"Bad request");

    private final int code;
    private final String title;

    StatusCode(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public static String stringFromCode(int code)
    {
        for(StatusCode sc: values())
        {
            if(sc.code == code)
                return sc.title;
        }
        return "Unknown";
    }
}
