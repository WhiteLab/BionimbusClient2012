package org.lac.bionimbus.client;

public interface OpenidManagerHandler
{
    void remove(String openid);

    void removeLast(String openid);

    void add(String openid);

    void onFailure(String message);

}
