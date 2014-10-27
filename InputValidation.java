package org.lac.bionimbus.client;

public interface InputValidation
{
    public boolean isValid();
    
    public void setFieldLabel(String label);
    
    public String getFieldLabel();
}

