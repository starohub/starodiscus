/* SH
 *
 * Modified by Staro Hub @ 2020 [ https://github.com/starohub ]
 *
 * Ref: https://github.com/mozilla/rhino/blob/Rhino1_7R3_RELEASE/src/org/mozilla/javascript/commonjs/module/provider/MultiModuleScriptProvider.java
 *
 * SH
 */

package com.starohub.discus.commonjs.module.provider;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import com.starohub.discus.Context;
import com.starohub.discus.Scriptable;
import com.starohub.discus.commonjs.module.ModuleScript;
import com.starohub.discus.commonjs.module.ModuleScriptProvider;

/**
 * A multiplexer for module script providers.
 * @author Attila Szegedi
 * @version $Id: MultiModuleScriptProvider.java,v 1.4 2011/04/07 20:26:12 hannes%helma.at Exp $
 */
public class MultiModuleScriptProvider implements ModuleScriptProvider
{
    private final ModuleScriptProvider[] providers;
    
    /**
     * Creates a new multiplexing module script provider tht gathers the 
     * specified providers
     * @param providers the providers to multiplex.
     */
    public MultiModuleScriptProvider(Iterable<? extends ModuleScriptProvider> providers) {
        final List<ModuleScriptProvider> l = new LinkedList<ModuleScriptProvider>();
        for (ModuleScriptProvider provider : providers) {
            l.add(provider);
        }
        this.providers = l.toArray(new ModuleScriptProvider[l.size()]);
    }
    
    public ModuleScript getModuleScript(Context cx, String moduleId, URI uri,
                                        Scriptable paths) throws Exception {
        for (ModuleScriptProvider provider : providers) {
            final ModuleScript script = provider.getModuleScript(cx, moduleId,
                    uri, paths);
            if(script != null) {
                return script;
            }
        }
        return null;
    }
}
