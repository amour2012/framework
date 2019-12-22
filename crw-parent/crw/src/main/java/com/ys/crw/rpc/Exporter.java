package com.ys.crw.rpc;

/**
 * @author oscar.wu
 *
 */
public interface Exporter {
    Function getFunction();    
    void unexport();
}
