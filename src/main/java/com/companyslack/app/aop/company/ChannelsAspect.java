package com.companyslack.app.aop.company;

import com.companyslack.app.repository.ChannelsRepository;
import com.companyslack.app.security.SecurityUtils;
import com.companyslack.app.repository.UserRepository;
import com.companyslack.app.domain.User;
import com.companyslack.app.domain.Channels;
import org.aspectj.lang.JoinPoint;
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
import java.util.NoSuchElementException;
import java.util.List;
import org.springframework.data.domain.Example;
import org.aspectj.lang.annotation.AfterReturning;

@Aspect
@Component
public class ChannelsAspect {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelsRepository channelsRepository;

    /**
     * Run method if Channels repository save is hit.
     * Adds tenant information to entity.
     */
    @Before(value = "execution(* com.companyslack.app.repository.ChannelsRepository.save(..)) && args(channels, ..)")
    public void onSave(JoinPoint joinPoint, Channels channels) {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();

        if(login.isPresent()) {
            User loggedInUser = userRepository.findOneByLogin(login.get()).get();

            if (loggedInUser.getCompany() != null) {
                channels.setCompany(loggedInUser.getCompany());
            }
        }
    }

    /**
     * Run method if Channels repository deleteById is hit.
     * Verify if tenant owns the channels before delete.
     */
    @Before(value = "execution(* com.companyslack.app.repository.ChannelsRepository.deleteById(..)) && args(id, ..)")
    public void onDelete(JoinPoint joinPoint, Long id) {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();

        if(login.isPresent()) {
            User loggedInUser = userRepository.findOneByLogin(login.get()).get();

            if (loggedInUser.getCompany() != null) {
                Channels channels = channelsRepository.findById(id).get();
                if(channels.getCompany() != loggedInUser.getCompany()){
                    throw new NoSuchElementException();
                }
            }
        }
    }

    /**
     * Run method if Channels repository findById is returning.
     * Adds filtering to prevent display of information from another tenant.
     */
    @Around("execution(* com.companyslack.app.repository.ChannelsRepository.findById(..)) && args(id, ..)")
    public Object onFindById(ProceedingJoinPoint pjp, Long id) throws Throwable {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();

        Optional<Channels> optional = (Optional<Channels>) pjp.proceed();
        if(login.isPresent())
        {
            User loggedInUser = userRepository.findOneByLogin(login.get()).get();

            if (loggedInUser.getCompany() != null) {
                if(optional.isPresent() && !optional.get().getCompany().equals(loggedInUser.getCompany())){
                    throw new NoSuchElementException();
                }
            }
        }
        return optional;
    }

    /**
     * Run method around Channels service findAll.
     * Adds filtering to prevent display of information from another tenant before database query (less performance hit).
     */
    @Around("execution(* com.companyslack.app.service.ChannelsService.findAll())")
    public Object onFindAll(ProceedingJoinPoint pjp) throws Throwable {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();

        if(login.isPresent())
        {
            User loggedInUser = userRepository.findOneByLogin(login.get()).get();

            if (loggedInUser.getCompany() != null) {
                Channels example = new Channels();
                example.setCompany(loggedInUser.getCompany());
                List<Channels> channels = channelsRepository.findAll(Example.of(example));
                return channels;
            }
        }
        return pjp.proceed();
    }
}
