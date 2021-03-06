package http.response;

import http.cookie.Cookie;
import http.request.HttpRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static http.response.HttpResponseHeaderKeys.LOCATION_HEADER_KEY;
import static http.response.HttpStatusCode.FOUND;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {

    private final HttpRequest httpRequest;
    private final HttpResponseMetaData metaData;
    private final Map<String, Object> model = new HashMap<>();

    public static HttpResponse from(HttpRequest httpRequest) {
        return new HttpResponse(httpRequest, new HttpResponseMetaData());
    }

    public void setModel(String key, Object value) {
        model.put(key, value);
    }

    public void renderTemplate(String location) {
        try {
            String renderedPage = TemplateRenderer.render(location, model);
            updateResponseBodyContent(renderedPage.getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void redirect(String location) {
        metaData.updateStatusCode(FOUND);
        metaData.putResponseHeader(LOCATION_HEADER_KEY, location);
    }

    public void addCookie(Cookie cookie) {
        metaData.addCookie(cookie);
    }

    public void setLoginCookie(boolean logined) {
        Cookie cookie = createLoginCookie(logined);
        addCookie(cookie);
    }

    public void setLoginCookie(boolean logined, String path) {
        if (path == null) {
            setLoginCookie(logined);
            return;
        }

        Cookie cookie = createLoginCookie(logined);
        cookie.setPath(path);
        addCookie(cookie);
    }

    public void updateResponseBodyContent(byte[] responseBody) {
        metaData.updateContentType(httpRequest.getMimeType(), responseBody.length);
        metaData.updateResponseBody(responseBody);
    }

    public void flush(OutputStream outputStream) throws IOException {
        DataOutputStream dos = new DataOutputStream(outputStream);

        metaData.writeResponseLine(dos, httpRequest.getProtocolSpec());
        metaData.writeResponseHeaders(dos);
        metaData.writeResponseBody(dos);
        dos.flush();
    }

    private Cookie createLoginCookie(boolean logined) {
        return new Cookie("logined", String.valueOf(logined));
    }
}
