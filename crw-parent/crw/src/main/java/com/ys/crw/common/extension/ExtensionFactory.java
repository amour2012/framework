package com.ys.crw.common.extension;

import java.util.List;

/**
 * @author oscar.wu
 *
 */
public interface ExtensionFactory {

    /**
     * Get extension.
     * 
     * @param type object type.
     * @return object instance.
     */
    <T> List<T> getExtension(Class<T> type);

}