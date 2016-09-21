package com.seltaf.customexceptions;

public class NoMatchingTestData extends Exception{

   
	private static final long serialVersionUID = 6124471507887205513L;

	public NoMatchingTestData() { }

    public NoMatchingTestData(final String message) {
        super(message);
    }

    public NoMatchingTestData(final Throwable cause) {
        super(cause);
    }

    public NoMatchingTestData(final String message, final Throwable cause) {
        super(message, cause);
    }

}
