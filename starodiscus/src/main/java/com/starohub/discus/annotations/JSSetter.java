/* SH
 *
 * Modified by Staro Hub @ 2020 [ https://github.com/starohub ]
 *
 * Ref: https://github.com/mozilla/rhino/blob/Rhino1_7R3_RELEASE/src/org/mozilla/javascript/annotations/JSSetter.java
 *
 * SH
 */

package com.starohub.discus.annotations;

import java.lang.annotation.*;

/**
 * An annotation that marks a Java method as JavaScript setter. This can
 * be used as an alternative to the <code>jsSet_</code> prefix desribed in
 * {@link org.mozilla.javascript.ScriptableObject#defineClass(org.mozilla.javascript.Scriptable, java.lang.Class)}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSSetter {
    String value() default "";
}
