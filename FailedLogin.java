package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.LoginResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FailedLogin extends DialogBox
{
    private VerticalPanel panel = null;

    public FailedLogin(LoginResult.Responses response)
    {
        super(false);
        setText("Login Result");
        panel = new VerticalPanel();
        add(panel);

        String message = "";

        switch (response)
        {
            case No_Openid:
                message = "<br/>This account does not have an OpenID associated with it.";

            case No_Such_User:
                message = "<br/>Login failed. No Such user";
                break;

            case Wrong_password:
                message = "<br/>Wrong password";
                break;

            case Expired:
                message = "<br/>Account locked due to inactivity. Contact an administrator.";
                break;

            case Verify:
                message = "<br/>Login failed as the account is not verified.<br/>"
                        + "Please click verify account to start using<br/> your Bionimbus account<br/><br/>";
                break;

            case Nonassociated_Openid:
                message = "<br/>There is no account for this OpenID.<br/>  "
                        + "Please sign in with your Bionimbus account and <br/>"
                        + "add this OpenID or Register a new account.";
                break;

            default:
                message = "Login failed";
        }

        InlineHTML loginRes = new InlineHTML(message);

        //TODO: what happens if the result is not one of these??????

        panel.add(loginRes); //TODO: could be null 

        HorizontalPanel verifyUserForgotPasswordPanel = new HorizontalPanel();
        verifyUserForgotPasswordPanel.setSpacing(4);
        panel.add(verifyUserForgotPasswordPanel);

        verifyUserForgotPasswordPanel.add(new Button("Ok", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                hide();
            }
        }));

        if (response == LoginResult.Responses.Verify)
        {
            verifyUserForgotPasswordPanel.add(new Button("Verify Account",
                    new ClickHandler()
                    {
                        public void onClick(ClickEvent e)
                        {
                            String html = "Please enter the username <br/> you registered your account with"
                                    + " to receive a<br/> new verification link.";
                            String emailBody = "Please click on the link below to verify your Bionimbus account :";
                            String emailSubject = "Activate your new Bionimbus account";
                            new VerifyUser(new InlineHTML(html), emailBody,
                                    emailSubject);
                        }
                    }));
        }

        if (response == LoginResult.Responses.Wrong_password)
        {
            verifyUserForgotPasswordPanel.add(new Button("Forgot Password",
                    new ClickHandler()
                    {
                        public void onClick(ClickEvent e)
                        {
                            String html = "Please enter the username <br/> you registered your account with"
                                    + " to receive a<br/> link to reset your password.";
                            String emailBody = "Please click on the link below to reset your Bionimbus account password :";
                            String emailSubject = "Reset your Bionimbus account password";
                            new ForgotPassword(new InlineHTML(html), emailBody,
                                    emailSubject);
                        }
                    }));
        }

        center();
    }
}
