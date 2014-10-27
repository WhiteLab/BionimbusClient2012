package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.OpenidResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OpenidInterfaceAsync
{
    // openid services
    void openidServer(String name, AsyncCallback<OpenidResult> callback);

    void openidServer(String providerUrl, String returnToUrl,
            AsyncCallback<OpenidResult> callback);

    void verifiedUser(String url, AsyncCallback<OpenidResult> callback);

    void openidComplete(String key, AsyncCallback<OpenidResult> callback);

    void addOpenid(String username, String openid,
            AsyncCallback<OpenidResult> asyncCallback);

    void removeOpenid(String openid, AsyncCallback<OpenidResult> asyncCallback);

    void getOpenids(String username, AsyncCallback<String[]> callback);
}
