package org.lac.bionimbus.client;

import java.util.List;
import java.util.Map;

import org.lac.bionimbus.shared.OpenidResult;
import org.lac.bionimbus.shared.OpenidResult.OIDResponses;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OpenidVerifier
{
    static boolean handleRedirect(String URL,
            Map<String, List<String>> parameterMap, final OpenidHandler handler)
    {
        if (parameterMap.get("openid.mode") != null
                && parameterMap.get("openid.mode").get(0).equals("id_res"))
        {
            OpenidInterfaceAsync rpc = GWT.create(OpenidInterface.class);

            rpc.verifiedUser(URL, new AsyncCallback<OpenidResult>()
            {
                public void onFailure(Throwable caught)
                {
                    handler.onFailure(caught.getMessage());
                }

                public void onSuccess(OpenidResult result)
                {
                    if (result.response == OIDResponses.Success)
                    {
                        handler.onSuccess(result.openid);
                    }
                    else if (result.response == OIDResponses.Verification_Error)
                    {
                        handler.onFailure(result.openid);
                    }
                }
            });
            return true;
        }
        return false;
    }
}
