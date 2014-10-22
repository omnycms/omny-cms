package ca.omny.logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class LogProducer {
    
    @Produces  
    public OmnyLogger produceLogger(InjectionPoint injectionPoint) {  
        return new SimpleLogger(injectionPoint.getMember().getDeclaringClass().getName());  
    } 
}
