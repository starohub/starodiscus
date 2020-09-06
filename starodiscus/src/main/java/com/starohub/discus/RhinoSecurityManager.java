/* SH
 *
 * Modified by Staro Hub @ 2020 [ https://github.com/starohub ]
 *
 * Ref: https://github.com/mozilla/rhino/blob/Rhino1_7R3_RELEASE/src/org/mozilla/javascript/RhinoSecurityManager.java
 *
 * SH
 */

package com.starohub.discus;

/**
 * A <code>java.lang.SecurityManager</code> subclass that provides access to 
 * the current top-most script class on the execution stack. This can be used 
 * to get the class loader or protection domain of the script that triggered 
 * the current action. It is required for JavaAdapters to have the same 
 * <code>ProtectionDomain</code> as the script code that created them. 
 * Embeddings that implement their own SecurityManager can use this as base class.
 */
public class RhinoSecurityManager extends SecurityManager {

    /**
     * Get the class of the top-most stack element representing a script.
     * @return The class of the top-most script in the current stack, 
     *         or null if no script is currently running
     */
    protected Class getCurrentScriptClass() {
        Class[] context = getClassContext();
        for (Class c : context) {
            if (c != InterpretedFunction.class && NativeFunction.class.isAssignableFrom(c) ||
                    PolicySecurityController.SecureCaller.class.isAssignableFrom(c)) {
                return c;
            }
        }
        return null;
    }

}