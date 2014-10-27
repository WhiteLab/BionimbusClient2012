package org.lac.bionimbus.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

public class VerifyUser extends DialogBox
{
    protected VerticalPanel panel                      = null;
    public static String    forgotPasswordActionString = "forgotpassword";
    public static String    verifyUserActionString     = "verifyuser";
    protected String        action                     = "";
    protected String        url                        = null;
//    protected final String  PROTOCOL                   = "https://";

    public VerifyUser(long tempkey, int userid, String url) throws Exception
    {
        super(false);
        panel = new VerticalPanel();
        add(panel);
        this.url = url;
        verifyUser(tempkey, userid);
    }

    // Constructor to re-verify a user
    public VerifyUser(InlineHTML html, final String emailBody,
            final String emailSubject)
    {
        super(false);
        setAction();
        final TextBox uName = new TextBox();

        Grid g = new Grid(2, 2);
        g.setCellSpacing(4);
        g.setWidget(0, 0, new InlineHTML("Username"));
        g.setWidget(0, 1, uName);
        panel = new VerticalPanel();
        add(panel);
        panel.add(html);
        panel.add(g);

        g.setWidget(1, 0, new Button("Ok", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                generateEmailLink(uName.getText(), emailBody, emailSubject);
            }
        }));
        g.setWidget(1, 1, new Button("Cancel", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                hide();
            }
        }));
        center();
    }

    void generateEmailLink(String uName, String emailBody, String emailSubject)
    {
        if (uName.equals(""))
        {
            new ErrorBox("Please enter a username");
            return;
        }

        AsyncCallback<Integer> callback = new AsyncCallback<Integer>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox(caught);
            }

            public void onSuccess(Integer result)
            {
                showResult((int) result);
            }
        };

        try
        {
            CistrackUI.rpc.createEmailWithUrl(emailBody, action, emailSubject,
                    uName, callback);
        }
        catch (Exception e)
        {
            new ErrorBox(e);
        }

    }

    public void verifyUser(long key, final int userid) throws Exception
    {
        AsyncCallback<Integer> callback = new AsyncCallback<Integer>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox(caught);
            }

            public void onSuccess(Integer result)
            {
                actionOnVerification((int) result, userid);
            }
        };

        try
        {
            CistrackUI.rpc.verifyUser(key, userid, callback);
        }
        catch (Exception e)
        {
            new ErrorBox(e);
        }
    }

    void setAction()
    {
        setText("Verify User");
        action = verifyUserActionString;
    }

    void showResult(int result)
    {
        if (result > -1)
        {
            hide();
            new ErrorBox(
                    "User Verification",
                    "Verification email successfully generated. Please check your email to verify your account",
                    new Button("Ok"));
        }
        else
        {
            new ErrorBox(
                    "The username entered is not valid. Please check the information entered and try again");
        }

    }

    void actionOnVerification(int result, int userid)
    {
        if (result > -1)
        {
            new ErrorBox("Add User", "User verified successfully. Please click the link below to"
                        + "reach the Bionimbus homepage :<br> <a href=" + url + ">Bionimbus Home</a>"
                        , new Button("Ok")); 
        }
        else if (result == -1)
        {
            new ErrorBox(
                    "Key expired. Please try generating another verification link through the login screen");
        }
        else if (result == -2)
        {
            new ErrorBox(
                    "The username and the key do not match. Please check the link"
                            + " or try generating another verification link through the login screen");
        }
    }

}
