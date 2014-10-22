package ca.omny.logger;

public interface OmnyLogger {

    void debug(Object message);

    void error(Object message);

    void fatal(Object message);

    void info(Object message);

    void warn(Object message);
    
}
