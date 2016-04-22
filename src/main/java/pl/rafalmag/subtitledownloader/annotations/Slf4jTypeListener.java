package pl.rafalmag.subtitledownloader.annotations;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Slf4jTypeListener implements TypeListener {

    public <I> void hear(TypeLiteral<I> aTypeLiteral, TypeEncounter<I> aTypeEncounter) {
        getAllFields(new LinkedList<>(), aTypeLiteral.getRawType())
                .stream()
                .filter(field -> field.getType() == Logger.class && field.isAnnotationPresent(InjectLogger.class))
                .forEach(field -> aTypeEncounter.register(new Slf4jMembersInjector<>(field)));
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
}