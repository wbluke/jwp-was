package webserver;

import controller.Controller;
import controller.ControllerMapper;
import http.HttpRequest;
import http.requestline.path.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import utils.FileIoUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RequestHandler implements Runnable {

    private static final ControllerMapper CONTROLLER_MAPPER = new ControllerMapper();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            HttpRequest httpRequest = readHttpRequest(bufferedReader);
            DataOutputStream dos = new DataOutputStream(out);

            if (httpRequest.hasPathFileExtension()) {
                byte[] body = FileIoUtils.loadFileFromClasspath(httpRequest.getFilePath());

                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            Path requestPath = httpRequest.getPath();
            Controller controller = CONTROLLER_MAPPER.findController(requestPath.getUri());
            controller.execute(httpRequest);

            response200Header(dos);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
        }
    }

    private HttpRequest readHttpRequest(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        HttpRequest httpRequest = new HttpRequest(line);

        while (!StringUtils.isEmpty(line)) {
            log.debug(line);
            line = bufferedReader.readLine();
            httpRequest.registerHeader(line);
        }
        return httpRequest;
    }

    private void response200Header(DataOutputStream dos) {
        response200Header(dos, 0);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
