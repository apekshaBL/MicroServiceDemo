package logging;

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

    // This constructor will print a massive alert in your logs when the app starts.
    // If you see this, you know 100% that Spring Boot found this file!
    public LoggingAspect() {
        System.out.println("🚨🚨🚨 INVISIBLE MANAGER (LOGGING ASPECT) IS AWAKE AND WATCHING! 🚨🚨🚨");
    }

    // BULLETPROOF POINTCUT: Watches EVERYTHING inside the "controller" and "service" folders
    @Around("execution(* auth_service.controller..*(..)) || execution(* auth_service.service..*(..))")
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