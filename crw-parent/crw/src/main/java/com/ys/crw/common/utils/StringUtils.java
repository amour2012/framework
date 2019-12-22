package com.ys.crw.common.utils;

import java.io.PrintWriter;

import com.ys.crw.common.io.UnsafeStringWriter;

/**
 * @author oscar.wu
 *
 */
public class StringUtils {
	public static String toString(Throwable e) {
    	UnsafeStringWriter w = new UnsafeStringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }
}
