package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.OpenidResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("greet")
public interface OpenidInterface extends RemoteService
{
    // openid services
    OpenidResult openidServer(String name);

    OpenidResult openidServer(String providerUrl, String returnToUrl);

    OpenidResult verifiedUser(String url);

    OpenidResult openidComplete(String key);

    OpenidResult addOpenid(String username, String openid);

    OpenidResult removeOpenid(String openid);

    String[] getOpenids(String username);
}
