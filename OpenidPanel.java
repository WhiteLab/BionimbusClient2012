package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.OpenidResult;
import org.lac.bionimbus.shared.OpenidResult.OIDResponses;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OpenidPanel extends VerticalPanel
{
    private static final int           POLL_RATE = 1000;
    private final OpenidInterfaceAsync rpc       = GWT.create(OpenidInterface.class);

    private String                     returnURL;
    private OpenidHandler              handler;
    private String                     key;

    private ListBox                    providers = new ListBox();

    private class ProviderResponse implements AsyncCallback<OpenidResult>
    {
        public void onFailure(Throwable caught)
        {
            handler.onFailure(caught.getMessage());
        }

        public void onSuccess(OpenidResult result)
        {
            if (result.response == OIDResponses.Success)
            {
                key = result.handle;
                Window.open(result.url, "Verify your OpenID", null);
                pollForCompletion.scheduleRepeating(POLL_RATE);
            }
            else if (result.response == OIDResponses.No_Provider)
            {
                handler.onFailure("The OpenID provider could not be located.");
            }
            else
            {
                handler.onFailure("There was an error.");
            }
        }
    }

    private ProviderResponse onProviderResponse = new ProviderResponse();

    private class Poll extends Timer
    {
        public void run()
        {
            rpc.openidComplete(key, new AsyncCallback<OpenidResult>()
            {
                public void onFailure(Throwable caught)
                {
                    handler.onFailure(caught.getMessage());
                }

                public void onSuccess(OpenidResult result)
                {
                    if (result != null
                            && result.response != OIDResponses.Waiting)
                    {
                        if (result.response == OIDResponses.Success)
                        {
                            handler.onSuccess(result.openid);
                            pollForCompletion.cancel();
                        }
                    }
                }
            });
        }
    }

    private Poll pollForCompletion = new Poll();

    OpenidPanel(String returnURL, OpenidHandler handler)
    {
        this.returnURL = returnURL;
        this.handler = handler;

        Button login = new Button("Login", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                contactProvider();
            }
        });

        add(providers);
        add(login);
    }

    public void addProvider(String name, String URL)
    {
        providers.addItem(name, URL);
    }

    private void contactProvider()
    {
        String providerUrl = providers.getValue(providers.getSelectedIndex());
        rpc.openidServer(providerUrl, returnURL, onProviderResponse);
    }
}
