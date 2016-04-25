package pl.rafalmag.subtitledownloader.annotations;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@BindingAnnotation
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nResources {
}