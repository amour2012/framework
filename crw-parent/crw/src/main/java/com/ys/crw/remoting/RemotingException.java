package com.ys.crw.remoting;

/**
 * @author oscar.wu
 *
 */
public class RemotingException extends RuntimeException {

	private static final long serialVersionUID = 2094267698835101247L;

	public RemotingException() {
        super();
    }

    public RemotingException(String message) {
        super(message);
    }
}
