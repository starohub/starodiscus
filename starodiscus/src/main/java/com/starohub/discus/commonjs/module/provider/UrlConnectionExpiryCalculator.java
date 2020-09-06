/* SH
 *
 * Modified by Staro Hub @ 2020 [ https://github.com/starohub ]
 *
 * Ref: https://github.com/mozilla/rhino/blob/Rhino1_7R3_RELEASE/src/org/mozilla/javascript/commonjs/module/provider/UrlConnectionExpiryCalculator.java
 *
 * SH
 */

package com.starohub.discus.commonjs.module.provider;

import java.net.URLConnection;

/**
 * Implemented by objects that can be used as heuristic strategies for 
 * calculating the expiry of a cached resource in cases where the server of the
 * resource doesn't provide explicit expiry information.
 * @author Attila Szegedi
 * @version $Id: UrlConnectionExpiryCalculator.java,v 1.3 2011/04/07 20:26:12 hannes%helma.at Exp $
 */
public interface UrlConnectionExpiryCalculator
{
    /**
     * Given a URL connection, returns a calculated heuristic expiry time (in
     * terms of milliseconds since epoch) for the resource.
     * @param urlConnection the URL connection for the resource
     * @return the expiry for the resource
     */
    public long calculateExpiry(URLConnection urlConnection);
}
