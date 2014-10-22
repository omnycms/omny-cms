package ca.omny.routing;

public interface IRoute<T> {

    String getPath();
    
    public T getObject();
}
