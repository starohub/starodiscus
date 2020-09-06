/* SH
 *
 * Modified by Staro Hub @ 2020 [ https://github.com/starohub ]
 *
 * Ref: https://github.com/mozilla/rhino/blob/Rhino1_7R3_RELEASE/src/org/mozilla/javascript/annotations/JSConstructor.java
 *
 * SH
 */

package com.starohub.discus.annotations;

import java.lang.annotation.*;

/**
 * An annotation that marks a Java method as JavaScript constructor. This can
 * be used as an alternative to the <code>jsConstructor</code> naming convention desribed in
 * {@link org.mozilla.javascript.ScriptableObject#defineClass(org.mozilla.javascript.Scriptable, java.lang.Class)}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface JSConstructor {
}
