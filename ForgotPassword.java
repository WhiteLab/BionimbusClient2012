package org.lac.bionimbus.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

public class ForgotPassword extends VerifyUser
{
    public ForgotPassword(InlineHTML html, String emailBody, String emailSubject)
    {
        super(html, emailBody, emailSubject);
    }

    public ForgotPassword(long tempkey, int userid, String url) throws Exception
    {
        super(tempkey, userid, url);
    }

    void setAction()
    {
        setText("Reset Password");
        action = forgotPasswordActionString;
    }

    void showResult(int result)
    {
        if (result > -1)
        {
            hide();
            new ErrorBox(
                    "Reset Password",
                    "Email link successfully generated. Please check your email to reset your password",
                    new Button("Ok"));
        }
        else
        {
            new ErrorBox(
                    "The username entered is not valid. Please check the information entered and try again");
        }

    }

    void actionOnVerification(int result, final int userid)
    {
        if (result < 0)
        {
            new ErrorBox(
                    "The username and the key do not match. Please check the link"
                            + " or try generating another verification link through the login screen");
        }
        setText("Reset Password");

        final PasswordTextBox password = new PasswordTextBox();
        final PasswordTextBox confirmPassword = new PasswordTextBox();
        String passwordRules = "*Contain a minimum of 8 characters. *Not be based on personal information (e.g. birthdate, favorite team)"
                + ". *Not contain dictionary words. *Contain at least one upper case character, one lower case character, and one number";
        Label passwordRestrictions = new Label(passwordRules);
        HorizontalPanel hp = new HorizontalPanel();
        HorizontalPanel hp1 = new HorizontalPanel();
        Grid g = new Grid(2, 2);

        hp1.setSpacing(4);
        hp.add(passwordRestrictions);
        hp.setWidth("350px");
        panel.add(hp);
        panel.add(g);
        g.setWidget(0, 0, new Label("Enter new password "));
        g.setWidget(0, 1, password);
        g.setWidget(1, 0, new Label("Confirm password "));
        g.setWidget(1, 1, confirmPassword);
        panel.add(hp1);
        hp1.add(new Button("Ok", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                updatePassword(password.getText(), confirmPassword.getText(),
                        userid);
            }
        }));
        hp1.add(new Button("Cancel", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                hide();
            }
        }));
        center();
    }

    private void updatePassword(String password, String confirmPassword,
            final int userid)
    {
        if (password.length() < 8)
        {
            new ErrorBox("Password should be at least 8 characters in length!");
            return;
        }

        if (!password.matches(".*[A-Z]+.*"))
        {
            new ErrorBox("Password must contain an upper case character!");
            return;
        }

        if (!password.matches(".*[a-z]+.*"))
        {
            new ErrorBox("Password must contain a lower case character!");
            return;
        }

        if (!password.matches(".*[0-9]+.*"))
        {
            new ErrorBox("Password must contain a number!");
            return;
        }
        if (!password.equals(confirmPassword))
        {
            new ErrorBox("Passwords do not match!");
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
                showResetPasswordResult((int) result);
            }
        };

        try
        {
            CistrackUI.rpc.resetPassword(password, userid, callback);
        }
        catch (Exception e)
        {
            new ErrorBox(e);
        }
    }

    private void showResetPasswordResult(int result)
    {
        if (result > -1)
        {
            new ErrorBox("Reset Password", "Password successfully reset. Please use the new"
                            + " password to login. Follow the link below to " 
                            + "the Bionimbus homepage : <br> <a href = " + url + ">Bionimbus Home</a>", new Button("Ok"));
            hide();
        }
        else
        {
            new ErrorBox("Password reset failed");
        }
    }

}
