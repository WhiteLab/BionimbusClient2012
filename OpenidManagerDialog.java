package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.OpenidResult;
import org.lac.bionimbus.shared.OpenidResult.OIDResponses;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;

public class OpenidManagerDialog extends DialogBox
{
    private OpenidManagerPanel                manager;
    private final static OpenidInterfaceAsync rpc = GWT.create(OpenidInterface.class);

    OpenidManagerDialog(final String username, String URL)
    {
        setText("Manage OpenIDs");

        manager = new OpenidManagerPanel(URL, new OpenidManagerHandler()
        {

            public void remove(final String openid)
            {
                rpc.removeOpenid(openid, new AsyncCallback<OpenidResult>()
                {
                    public void onFailure(Throwable caught)
                    {
                        new ErrorBox(caught.getMessage());
                    }

                    public void onSuccess(OpenidResult result)
                    {
                        manager.removeId(openid);
                    }

                });
            }

            public void removeLast(String openid)
            {
                new ErrorBox("You must have at least one OpenID");
            }

            public void add(final String openid)
            {
                rpc.addOpenid(username, openid,
                        new AsyncCallback<OpenidResult>()
                        {
                            public void onFailure(Throwable caught)
                            {
                                new ErrorBox(caught.getMessage());
                            }

                            public void onSuccess(OpenidResult result)
                            {
                                if (result.response == OIDResponses.Duplicate)
                                {
                                    new ErrorBox(
                                            "This OpenID is already in use.");
                                }
                                else if (result.response == OIDResponses.Verification_Error)
                                {
                                    new ErrorBox(
                                            "The OpenID could not be verified");
                                }
                                else if (result.response == OIDResponses.Insert_Error)
                                {
                                    new ErrorBox(
                                            "There was an error inserting the OpenID.");

                                }
                                else if (result.response == OIDResponses.Success)
                                {
                                    manager.addId(openid);
                                }
                            }
                        });
            }

            public void onFailure(String message)
            {
                new ErrorBox(message);
            }

        });

        rpc.getOpenids(username, new AsyncCallback<String[]>()
        {

            public void onFailure(Throwable caught)
            {
                new ErrorBox(caught.getMessage());
            }

            public void onSuccess(String[] results)
            {
                for (String result : results)
                {
                    manager.addId(result);
                }
            }
        });

        manager.addProvider("Google", "https://www.google.com/accounts/o8/id");
        manager.addProvider("Launchpad", "https://login.launchpad.net/+openid");
        manager.addProvider("AOL/AIM",
                "http://api.screenname.aol.com/auth/login");
        manager.addProvider("Yahoo!", "http://me.yahoo.com/");
        add(manager);
        manager.add(new Button("done", new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                hide();
            }
        }));

        show();
        center();
    }
}
