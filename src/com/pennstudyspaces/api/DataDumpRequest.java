package com.pennstudyspaces.api;


public class DataDumpRequest implements ApiRequest {
    private static final String DUMP   = "showall",
                               FORMAT = "format";
    
    private String format;
    
    public DataDumpRequest() {
        this("json");
    }
    
    public DataDumpRequest(String format) {
        if (!"json".equals(format))
            throw new IllegalArgumentException("unsupported format");

        this.format = format;
    }
    
    @Override
    public String toString() {
        return DUMP + "=1&" + FORMAT + "=" + this.format;
    }

    @Override
    public String createRequest() {
        return API_URL + this.toString();
    }
}
