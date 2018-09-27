package ru.azhuk.zhukcachingservice.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.azhuk.zhukcachingservice.ZhukCachingServiceApplication;

@EnableWebMvc
@ControllerAdvice
public class ExceptionHandlers {

    private static final Logger logger = LoggerFactory.getLogger(ZhukCachingServiceApplication.class);


    @ExceptionHandler(Throwable.class)
    public void handleThrowable(final Throwable ex) throws Throwable {
        logger.error("INTERNAL_SERVER_ERROR: " + ex.getMessage());
        throw ex;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleArgumentNotValid(final MethodArgumentNotValidException ex) throws MethodArgumentNotValidException {
        logger.error("WRONG_INPUT_PARAMETERS: " + ex.getMessage());
        throw ex;
    }

}