package com.companyslack.app.aop.company;

import com.companyslack.app.security.SecurityUtils;
import com.companyslack.app.repository.UserRepository;
import com.companyslack.app.domain.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Filter;
import java.util.Optional;

@Aspect
@Component
public class CompanyAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private final String fieldName =  "companyId";

    private final Logger log = LoggerFactory.getLogger(CompanyAspect.class);

    /**
     * Run method if User service is hit.
     * Filter users based on which company the user is associated with.
     * Skip filter if user has no company
     */
    @Before("execution(* com.companyslack.app.service.UserService.*(..)) || execution(* com.companyslack.app.service.ChannelsService.*(..))")
    public void beforeExecution() throws Throwable {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();

        if(login.isPresent()) {
			User user = userRepository.findOneByLogin(login.get()).get();

			if (user.getCompany() != null) {
				Filter filter = entityManager.unwrap(Session.class).enableFilter("COMPANY_FILTER");
				filter.setParameter(fieldName, user.getCompany().getId());
			}
		}
    }
}
