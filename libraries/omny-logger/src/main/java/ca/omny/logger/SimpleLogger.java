package ca.omny.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleLogger implements OmnyLogger {
    
    Logger logger; 
    
    public SimpleLogger(String loggerName) {
       logger = Logger.getLogger(loggerName);
    }
    
    @Override
    public void info(Object message) {
        this.log(Level.INFO, message);
    }
    
    @Override
    public void warn(Object message) {
        this.log(Level.WARNING, message);
    }
    
    @Override
    public void debug(Object message) {
        this.log(Level.WARNING, message);
    }
    
    @Override
    public void error(Object message) {
        this.log(Level.SEVERE, message);
    }
    
    @Override
    public void fatal(Object message) {
        this.log(Level.SEVERE, message);
    }
    
    private void log(Level logLevel, Object message) {
        String resultMessage = message.toString();
        logger.log(logLevel, resultMessage);
    }
}
