package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.LoginResult;
import org.lac.bionimbus.shared.LoginResult.Responses;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginDialog
{

    private DialogBox                  loginDialog   = new DialogBox();
    private VerticalPanel              mainPanel     = new VerticalPanel();
    private HorizontalPanel            hPanel1       = new HorizontalPanel();
    private HorizontalPanel            hPanel2       = new HorizontalPanel();
    private final Label                usernameLabel = new Label("Username:\t");
    private final Label                passwordLabel = new Label("Password:\t");
    private final Button               loginButton   = new Button("Login");
    private final Button               cancelButton  = new Button("Cancel");
    private final TextBox              nameField     = new TextBox();
    private final PasswordTextBox      passwordField = new PasswordTextBox();
    private final ClientInterfaceAsync rpc           = GWT.create(ClientInterface.class);
    CistrackUI                         ui;
    private Command                    c;
    private AsyncCallback<LoginResult> handleLogin;

    LoginDialog(CistrackUI ui, Command c)
    {
        // We can add style names to widgets
        this.ui = ui;
        this.c = c;

        loginButton.addStyleName("sendButton");

        hPanel1.add(usernameLabel);
        hPanel1.add(nameField);
        hPanel2.add(passwordLabel);
        hPanel2.add(passwordField);

        mainPanel.add(hPanel1);
        mainPanel.add(hPanel2);

        HorizontalPanel cancelRegisterButtonPanel = new HorizontalPanel();
        cancelRegisterButtonPanel.setSpacing(4);

        setLoginCallback();

        if (CistrackUI.OPENID_ENABLED)
        {
            mainPanel.add(loginButton);
            mainPanel.add(new HTML("or use an OpenID "));
            OpenidPanel openidPanel = new OpenidPanel(
                    Window.Location.getHref(), new OpenidHandler()
                    {
                        public void onFailure(String caught)
                        {
                            new ErrorBox(caught);
                        }

                        public void onSuccess(String openid)
                        {
                            rpc.login(openid, handleLogin);
                        }
                    });
            openidPanel.addProvider("Google",
                    "https://www.google.com/accounts/o8/id");
            openidPanel.addProvider("Launchpad",
                    "https://login.launchpad.net/+openid");
            openidPanel.addProvider("AOL/AIM",
                    "http://api.screenname.aol.com/auth/login");
            openidPanel.addProvider("Yahoo!", "http://me.yahoo.com/");
            mainPanel.add(openidPanel);
        }
        else
        {
            cancelRegisterButtonPanel.add(loginButton);
        }

        cancelRegisterButtonPanel.add(cancelButton);

        /*
         cancelRegisterButtonPanel.add(new Button("Register", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                new AddUser(Personnel.USER_WITHLOGIN, true);
            }
        }));
        */

        mainPanel.add(cancelRegisterButtonPanel);

        showDialog();

        loginButton.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                processLogin();
            }
        });

        cancelButton.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                loginDialog.hide();
            }
        });

    }

    public void showDialog()
    {
        loginDialog.add(mainPanel);
        // Focus the cursor on the name field when the app loads
        loginDialog.center();
        loginDialog.setText("Login to Bionimbus");
        loginDialog.show();
        nameField.setFocus(true);

        passwordField.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(KeyUpEvent event)
            {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
                    processLogin();
            }
        });
    }

    private void setLoginCallback()
    {
        handleLogin = new AsyncCallback<LoginResult>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox("Login failed.  Please contact administrator!"
                        + caught);
            }

            public void onSuccess(LoginResult callbackResult)
            {
                if (callbackResult.response == Responses.Success
                        || callbackResult.response == Responses.No_Openid)
                {
                    CistrackUI.username = callbackResult.username;
                    if (CistrackUI.username.equals("dave") && CistrackUI.userMenu != null )
                    {
                        CistrackUI.userMenu.addItem("Add User",
                                CistrackUI.newUser);
                    }
                    if ( loginDialog != null )
                    {
                        loginDialog.hide();
                    }
                    if (c != null)
                    {
                        c.execute();
                    }
                    if (callbackResult.response == Responses.No_Openid)
                    {
                        // TODO: create new dialogbox with just the openid panel then
                        // use that to register an openID to user or they can cancel
                    }
                }
                else
                {
                    new FailedLogin(callbackResult.response);
                }
            }
        };
    }

    private void processLogin()
    {
        rpc.login(nameField.getText(), passwordField.getText(), handleLogin);
    }

}
