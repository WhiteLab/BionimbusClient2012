package org.lac.bionimbus.client;

public interface OpenidHandler
{
    public void onFailure(String message);

    public void onSuccess(String openid);
}
