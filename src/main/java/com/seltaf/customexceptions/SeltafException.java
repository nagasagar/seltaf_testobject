package com.seltaf.customexceptions;

public class SeltafException extends Exception{

   
	private static final long serialVersionUID = 6124471507887205513L;

	public SeltafException() { }

    public SeltafException(final String message) {
        super(message);
    }

    public SeltafException(final Throwable cause) {
        super(cause);
    }

    public SeltafException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
