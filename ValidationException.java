package org.lac.bionimbus.client;

public class ValidationException extends Throwable
{

    String message;

    ValidationException(String s)
    {
        message = s;
    }
}
