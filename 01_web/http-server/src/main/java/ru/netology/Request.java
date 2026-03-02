package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;
    private final String rawPath;
    private final String queryString;
    private final List<NameValuePair> queryParams;

    public Request(String rawRequestLine) throws URISyntaxException {
        String[] parts = rawRequestLine.split(" ");
        this.method = parts[0];
        this.rawPath = parts[1];

        URI uri = new URI(rawPath);
        this.path = uri.getPath();
        this.queryString = uri.getQuery();

        if (queryString != null && !queryString.isEmpty()) {
            this.queryParams = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
        } else {
            this.queryParams = List.of();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getRawPath() {
        return rawPath;
    }

    public String getQueryParam(String name) {
        return queryParams.stream()
                .filter(p -> p.getName().equals(name))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElse(null);
    }

    public Map<String, String> getQueryParams() {
        return queryParams.stream()
                .collect(Collectors.toMap(
                        NameValuePair::getName,
                        NameValuePair::getValue,
                        (v1, v2) -> v2
                ));
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", queryString='" + queryString + '\'' +
                ", queryParams=" + queryParams +
                '}';
    }
}
