package com.ys.crw.rpc;

import com.ys.crw.common.URL;

/**
 * @author oscar.wu
 *
 */
public interface Node {
    /**
     * get url.
     * 
     * @return url.
     */
    URL getUrl();
    
    /**
     * is available.
     * 
     * @return available.
     */
    boolean isAvailable();

    /**
     * destroy.
     */
    void destroy();


}
