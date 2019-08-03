package http.view;

public class StaticViewResolver implements ViewResolver {
    @Override
    public boolean isSupports(String path) {
        return !path.endsWith(".html");
    }

    @Override
    public View resolveViewName(String viewName) {
        return new StaticResourceView();
    }
}
