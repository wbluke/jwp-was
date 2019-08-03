package http;

import http.view.StaticViewResolver;
import http.view.TemplateViewResolver;
import http.view.View;
import http.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private static List<ViewResolver> viewResolvers =
            Arrays.asList(new StaticViewResolver(), new TemplateViewResolver());

    private HttpRequest request;

    private DataOutputStream dos = null;

    private Map<String, String> headers = new HashMap<String, String>();

    public HttpResponse(HttpRequest request, OutputStream out) {
        this.request = request;
        this.dos = new DataOutputStream(out);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private ViewResolver getViewResolver(String path) {
        return viewResolvers.stream()
                .filter(viewResolver -> viewResolver.isSupports(path))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(path + "을 처리할 ViewResolver를 찾을 수 없다."));
    }

    public void forward(String path) {
        try {
            ViewResolver viewResolver = getViewResolver(path);
            View view = viewResolver.resolveViewName(path);
            view.render(request, this);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void forwardBody(String body) {
        byte[] contents = body.getBytes();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", contents.length + "");
        response200Header(contents.length);
        responseBody(contents);
    }

    public void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            processHeaders();
            dos.writeBytes("Location: " + redirectUrl + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processHeaders() {
        try {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                dos.writeBytes(key + ": " + headers.get(key) + " \r\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
