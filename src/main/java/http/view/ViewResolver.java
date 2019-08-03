package http.view;

public interface ViewResolver {
    boolean isSupports(String path);

    View resolveViewName(String viewName);
}
