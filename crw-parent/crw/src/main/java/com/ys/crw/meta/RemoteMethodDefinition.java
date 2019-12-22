package com.ys.crw.meta;

import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author oscar.wu
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoteMethodDefinition {
	private Method method;
	private int regionParamIndex;
	private boolean center;
	
}
