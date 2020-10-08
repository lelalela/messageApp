package app.messages;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityChecker {
	
	@Pointcut("@annotation(SecurityCheck)")
	public void checkMethodSecurity() {}
	
	@Around
	
}
