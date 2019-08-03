package http.view;

import http.HttpRequest;
import http.HttpResponse;

import java.util.Map;

public interface View {
    void render(HttpRequest request, HttpResponse response) throws Exception;
}
