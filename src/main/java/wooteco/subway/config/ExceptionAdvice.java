package wooteco.subway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.auth.application.InvalidTokenException;
import wooteco.subway.member.application.DuplicateEmailException;
import wooteco.subway.member.application.EmailNotFoundException;
import wooteco.subway.member.application.InvalidPasswordException;
import wooteco.subway.path.application.InvalidPathException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionAdvice {
    private final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception e) {
        logger.error("method argument not valid exception occurred. message=[{}]", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage("관리자에게 문의해주세요."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleBindException(MethodArgumentNotValidException e) {
        logger.error("method argument not valid exception occurred. message=[{}]", e.getMessage(), e);

        String message = e.getBindingResult().getAllErrors().stream()
                .filter(err -> err != null && err.getCodes() != null)
                .filter(err -> err.getCodes().length > 1)
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(message));
    }

    @ExceptionHandler({InvalidPathException.class, DuplicateEmailException.class})
    public ResponseEntity<ErrorMessage> handleInvalidPathException(Exception e) {
        logger.error("method argument not valid exception occurred. message=[{}]", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler({InvalidPasswordException.class, InvalidTokenException.class, EmailNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleAuthException(Exception e) {
        logger.error("method argument not valid exception occurred. message=[{}]", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage(e.getMessage()));
    }
}
