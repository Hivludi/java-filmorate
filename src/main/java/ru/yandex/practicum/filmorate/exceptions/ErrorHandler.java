package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MultipleErrorsResponse collectValidationErrorsAndLog(final MethodArgumentNotValidException e) {

        List list = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        for (Object error : list) {
            log.warn("Ошибка валидации: {}", error);
        }

        return new MultipleErrorsResponse(list);
    }

    @ExceptionHandler(ReviewLikeAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleReviewLikeAlreadyExistException(ReviewLikeAlreadyExistException e) {
        return new ErrorResponse("Review like уже существует: " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final IncorrectParameterException e) {
        log.warn("Ошибка с полем \"{}\": {}", e.getParameter(), e.getMessage());
        return new ErrorResponse(String.format("Ошибка с полем \"%s\": %s", e.getParameter(), e.getMessage()));
    }

    @ExceptionHandler({ObjectNotFoundException.class,
            LikeNotFoundException.class,
            FriendNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(RuntimeException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({FilmAlreadyLikedException.class,
            UserAlreadyFriendedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExceptions(RuntimeException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    private static class ErrorResponse {
        String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    private static class MultipleErrorsResponse {
        List<String> errorsList;

        public MultipleErrorsResponse(List<String> errorsList) {
            this.errorsList = errorsList;
        }

        public List<String> getErrorsList() {
            return errorsList;
        }
    }
}