package com.ys.crw.rpc.protocol;

import com.ys.crw.rpc.Exporter;
import com.ys.crw.rpc.Function;

/**
 * @author oscar.wu
 *
 */
public abstract class AbstractExporter implements Exporter {    
    private final Function function;

    private volatile boolean unexported = false;

    public AbstractExporter(Function function) {
        if (function == null)
            throw new IllegalStateException("function == null");
        if (function.getMethod() == null)
            throw new IllegalStateException("function method == null");
        if (function.getUrl() == null)
            throw new IllegalStateException("function url == null");
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    public void unexport() {
        if (unexported) {
            return ;
        }
        unexported = true;
        getFunction().destroy();
    }

    public String toString() {
        return getFunction().toString();
    }
}
