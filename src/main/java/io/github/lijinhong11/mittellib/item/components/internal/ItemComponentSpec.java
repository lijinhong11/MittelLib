package io.github.lijinhong11.mittellib.item.components.internal;

import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiStatus.Internal
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ItemComponentSpec {
    String key();

    MCVersion requiredVersion();
}
