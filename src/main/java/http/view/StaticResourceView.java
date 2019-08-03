package http.view;

import http.HttpRequest;
import http.HttpResponse;
import utils.FileIoUtils;

public class StaticResourceView implements View {
    private static final String DEFAULT_STATIC_PATH = "./static";

    @Override
    public void render(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        if (path.endsWith(".css")) {
            response.addHeader("Content-Type", "text/css");
        } else if (path.endsWith(".js")) {
            response.addHeader("Content-Type", "application/javascript");
        }
        byte[] body = FileIoUtils.loadFileFromClasspath(DEFAULT_STATIC_PATH + path);
        response.response200Header(body.length);
        response.responseBody(body);
    }
}
