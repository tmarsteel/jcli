package com.tmarsteel.jcli.util.formatting;

/**
 * Thrown whenever there is not enough space to render a text component (component exceeds maximum size).
 */
public class OutOfRenderingSpaceException extends RuntimeException
{
    public OutOfRenderingSpaceException(String message) {
        this(message, null);
    }

    public OutOfRenderingSpaceException(String message, Throwable previous) {
        super(message, previous);
    }
}