package app.sonu.com.musicplayer.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import javax.inject.Scope;

/**
 * Created by sonu on 9/3/17.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerAdapter {
}
