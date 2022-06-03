package com.github.kriaktus.restaurantvoting.util.validation;

import com.github.kriaktus.restaurantvoting.HasId;
import com.github.kriaktus.restaurantvoting.error.AppException;
import com.github.kriaktus.restaurantvoting.error.IllegalRequestDataException;
import lombok.experimental.UtilityClass;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.time.Clock;
import java.time.LocalTime;
import java.util.Optional;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@UtilityClass
public class ValidationUtil {

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must be new (id=null)");
        }
    }

    //  Conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
    public static void assureIdConsistent(HasId bean, int id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must has id=" + id);
        }
    }

    public static void checkModification(int count) {
        if (count == 0) {
            throw new IllegalRequestDataException("Entity for modify not found");
        }
    }

    public static void checkModification(int count, int id) {
        if (count == 0) {
            throw new IllegalRequestDataException("Entity with id=" + id + " not found");
        }
    }

    public static <T> T checkNotFoundWithId(T object, int id) {
        checkNotFoundWithMessage(object != null, "Entity with id=" + id + " not found");
        return object;
    }

    public static <T> T checkNotFoundWithMessage(T object, String message) {
        checkNotFoundWithMessage(object != null, message);
        return object;
    }

    public static <T> void checkNotFoundWithMessage(boolean found, String message) {
        if (!found) throw new IllegalRequestDataException(message);
    }

    public static <T> T checkNotFoundWithId(Optional<T> optional, int id) {
        return checkNotFoundWithMessage(optional, "Entity with id=" + id + " not found");
    }

    public static <T> T checkNotFoundWithMessage(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> {
            throw new IllegalRequestDataException(message);
        });
    }

    public static void compareCurrentTimeWith(LocalTime localTime, boolean isAfter, String message, Clock clock) {
        if (LocalTime.now(clock).isAfter(localTime) ^ !isAfter)
            throw new AppException(HttpStatus.NOT_ACCEPTABLE, message, ErrorAttributeOptions.of(MESSAGE));
    }

    //  https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }
}