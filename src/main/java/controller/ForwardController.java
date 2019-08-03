package controller;

import http.HttpRequest;
import http.HttpResponse;

public class ForwardController extends AbstractController {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.forward(removeSuffix(request.getPath()));
    }

    private String removeSuffix(String viewName) {
        return viewName.replace(".html", "");
    }
}
