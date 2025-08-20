package net.r4mble.types;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Payment {
    private String url;
    private String code;
    private String card;

    public String getUrl() {
        return url;
    }

    public String getCode() {
        return code;
    }

    public String getCardName() {
        return URLDecoder.decode(card, StandardCharsets.UTF_8);
    }
}
