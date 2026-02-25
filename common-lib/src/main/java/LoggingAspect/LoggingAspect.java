package LoggingAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // This universal pointcut looks for ANY class inside a "controller" or "service" package across all your microservices!
    @Around("(execution(* *..controller..*(..)) || execution(* *..service..*(..))) && !within(org.springframework..*)")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();
        log.info("▶️ START: Executing method: {}", methodName);

        long startTime = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable exception) {
            log.error("❌ ERROR: Method {} threw exception: {}", methodName, exception.getMessage());
            throw exception;
        }

        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("✅ END: Method {} executed successfully in {} ms", methodName, timeTaken);

        return result;
    }
}