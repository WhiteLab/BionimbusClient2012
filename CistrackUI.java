package org.lac.bionimbus.client;

import java.util.Vector;

import org.lac.bionimbus.shared.Personnel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CistrackUI implements EntryPoint
{
    static private MenuBar              menu               = null;
    static String                       guest              = "guest";

    static String                       username           = guest;

    static String                       loginText          = "Login";
    static String                       logoutText         = "Logout";
    // public int userId;

    //CistrackUI             self;

    /**
     * This is the entry point method.
     */

    static Panel                        basePanel;
    String                              keyGenSeqRequestId = "";

    static private MenuItem             loginItem          = null;

    static private CistrackSplashDialog splash             = null;

    static final boolean                OPENID_ENABLED     = false;

    static boolean isGuest()
    {
        return username.equals(guest);
    }

    static void clear()
    {
        if (splash != null)
        {
            splash.hide();
        }
        if (basePanel != null)
        {
            RootPanel.get().remove(basePanel);
            basePanel = null;
        }

    }

    static void afterLogin(Command openid)
    {
        if (loginItem != null)
        {
            loginItem.setText(logoutText);
            if (OPENID_ENABLED)
            {
                loginItem.getParentMenu().addItem("Manage OpenIDs", openid);
            }
        }
        // after login most users will want to see their data
        clear();
        basePanel = new ExperimentUnitTable(false);

    }

    static VerticalPanel displayPanel = new VerticalPanel();

    static Command       newUser      = new Command()
                                      {

                                          public void execute()
                                          {
                                              new AddUser(
                                                      Personnel.USER_WITHLOGIN,
                                                      true);
                                          }
                                      };

    public void onModuleLoad()
    {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler()
        {
            public void onUncaughtException(Throwable e)
            {
                new ErrorBox(e);
            }
        });

        String query = Window.Location.getQueryString();
        if (query.length() > 0)
        {
            query = query.substring(1);
        }

        Command publicData = new Command()
        {
            public void execute()
            {
                try
                {
                    clear();
                    basePanel = new ExperimentUnitTable(true);
                }
                catch (Exception e)
                {
                    Window.alert("Exception : " + e);
                    e.printStackTrace(System.out);
                }
            }
        };

        Command insulators = new Command()
        {
            public void execute()
            {
                try
                {
                    clear();
                    basePanel = new Insulators("guest");
                }
                catch (Exception e)
                {
                    Window.alert("Exception : " + e);
                    e.printStackTrace(System.out);
                }
            }
        };

        final Command addOpenid = new Command()
        {
            public void execute()
            {
                new OpenidManagerDialog(username, Window.Location.getHref());
            }
        };

        Command myData = new Command()
        {
            public void execute()
            {
                try
                {
                    if (username.equals("guest"))
                    {
                        new LoginDialog(CistrackUI.this, this);
                    }
                    else
                    {
                        // by default users will see their data after login
                        afterLogin(addOpenid);
                    }
                }
                catch (Exception e)
                {
                    Window.alert("Exception : " + e);
                    e.printStackTrace(System.out);
                }
            }
        };

        Command login = new Command()
        {
            public void execute()
            {
                try
                {
                    if (isGuest() && loginItem.getText().equals(loginText))
                    {
                        new LoginDialog(CistrackUI.this, this);

                    }
                    else if (loginItem.getText().equals(loginText))
                    {

                        afterLogin(addOpenid);
                    }
                    else
                    {

                        // same functionality as bellow change if we need state
                        Window.Location.reload();
                        /*
                        loginItem.setText(loginText);
                        username = guest;
                        clear();
                        */
                    }

                }
                catch (Exception e)
                {
                    Window.alert("Exception : " + e);
                    e.printStackTrace(System.out);
                }
            }
        };

        Command timeCourse = new Command()
        {
            public void execute()
            {
                clear();
                basePanel = new TimeCourse();
            }
        };

        Command projects = new Command()
        {
            public void execute()
            {
                clear();
                basePanel = new ProjectEdit();
            }
        };

        Command stats = new Command()
        {
            public void execute()
            {
                new Statistics();
            }
        };

        Command kgTable = new Command()
        {
            public void execute()
            {
                if (username.equals("guest"))
                {
                    new LoginDialog(CistrackUI.this, this);
                }
                else
                {
                    clear();
                    KeyGen kg = new KeyGen();
                    basePanel = kg;
                    // if an id is specified in the url, then use it as a default value
                    if (!keyGenSeqRequestId.equals(""))
                        kg.setRequestId(keyGenSeqRequestId);
                    //                    basePanel = new KeyGen(username);
                }
            }
        };

        Command experimentCreator = new Command()
        {
            public void execute()
            {
                if (username.equals("guest"))
                {
                    new LoginDialog(CistrackUI.this, this);
                }
                else
                {
                    clear();
                    basePanel = new ExperimentCreator();
                }
            }
        };

        Command configCreator = new Command()
        {
            public void execute()
            {
                if (username.equals("guest"))
                {
                    new LoginDialog(CistrackUI.this, this);
                }
                else
                {
                    clear();
                    basePanel = new ConfigCreator();
                }
            }
        };

        Command sequenceRequestCreate = new Command()
        {
            public void execute()
            {
                if (username.equals("guest"))
                {
                    new LoginDialog(CistrackUI.this, this);
                }
                else
                {
                    clear();
                    basePanel = new RequestOfferUI();
                }
            }
        };

        Command sequenceRequestView = new Command()
        {
            public void execute()
            {
                String[] VIEW_REQUESTS_GROUP = new String[] { "mdomanus2",
                        "edybdahl", "lherend1", "acd", "lthimothy", "dave",
                        "jtuteja", "tweshoo", "msrmbrown" };
                boolean canViewRequests = java.util.Arrays.asList(
                        VIEW_REQUESTS_GROUP).contains(username);

                if (username.equals("guest"))
                {
                    new LoginDialog(CistrackUI.this, this);
                }
                else if (canViewRequests)
                {
                    clear();
                    basePanel = new ViewRequestOffer();
                }
            }
        };

        Command goHome = new Command()
        {
            public void execute()
            {
                Window.open("http://www.bionimbus.org", "_self", "");
            }
        };

        if (OpenidVerifier.handleRedirect(Window.Location.getHref(),
                Window.Location.getParameterMap(), new OpenidHandler()
                {
                    public void onFailure(String message)
                    {
                        close();
                    }

                    public void onSuccess(String openid)
                    {
                        close();
                    }
                }))
            ;

        else if (query.equals("timecourse"))
        {
            timeCourse.execute();
        }
        else if (query.equals("public"))
        {
            publicData.execute();
        }
        else if (query.equals("mydata"))
        {
            myData.execute();
        }
        else if (query.startsWith("makeKeys"))
        {
            if (query.contains("="))
            {
                keyGenSeqRequestId = query.split("=")[1];
            }
            kgTable.execute();
        }
        else if (query.equals("stats"))
        {
            stats.execute();
        }
        else if (query.equals("insulators"))
        {
            insulators.execute();
        }
        else if (query.equals("experimentcreator"))
        {
            experimentCreator.execute();
        }
        else if (query.equals("configcreator"))
        {
            configCreator.execute();
        }
        else if (query.equals("createseqrequest"))
        {
            sequenceRequestCreate.execute();
        }
        else if (query.equals("viewseqrequest"))
        {
            sequenceRequestView.execute();
        }
        else if (query.contains("verifyuser"))
        {
            //TODO: don't roll internal login into the main action loop 
            try
            {
                String[] queryPieces = query.split("&");
                String key = "", userid = "";
                String[] urlPieces = Window.Location.getHref().split("\\?");
                String url = urlPieces[0];
                //                new ErrorBox(url);
                for (int i = 0; i < queryPieces.length; i++)
                {
                    String[] values = queryPieces[i].split("=");
                    if (i == 0)
                    {
                        key = values[1];
                    }
                    else if (i == 1)
                    {
                        userid = values[1];
                    }
                }
                new VerifyUser(Long.parseLong(key), Integer.parseInt(userid),
                        url);
            }
            catch (Exception e)
            {
                new ErrorBox(e);
            }

        }
        else if (query.contains("forgotpassword"))
        {
            //TODO: don't cut and paste code like a high-school freshman 
            try
            {
                String[] queryPieces = query.split("&");
                String key = "", userid = "";
                String[] urlPieces = Window.Location.getHref().split("\\?");
                String url = urlPieces[0];
                for (int i = 0; i < queryPieces.length; i++)
                {
                    String[] values = queryPieces[i].split("=");
                    if (i == 0)
                    {
                        key = values[1];
                    }
                    else if (i == 1)
                    {
                        userid = values[1];
                    }
                }
                new ForgotPassword(Long.parseLong(key),
                        Integer.parseInt(userid), url);
            }
            catch (Exception e)
            {
                new ErrorBox(e);
            }

        }
        else
        {
            MenuBar home = new MenuBar(true);
            home.addItem("Return to Bionimbus Home", goHome);

            MenuBar publicMenu = new MenuBar(true);
            publicMenu.addItem("Timecourse", timeCourse);
            publicMenu.addItem("Public Data", publicData);
            publicMenu.addItem("Insulators", insulators);

            MenuBar regMenu = new MenuBar(true);
            regMenu.addItem("My Data", myData);
            regMenu.addItem("Make Bionimbus Keys", kgTable);
            regMenu.addItem("Bionimbus Statistics", stats);
            //regMenu.addItem("Experiment Creator", experimentCreator);
            regMenu.addItem("Sequence Config Creator", configCreator);
            regMenu.addItem("Create Seq Request", sequenceRequestCreate);
            //regMenu.addItem("Projects", projects);

            //regMenu.addItem("View Seq Requests", sequenceRequestView);
            //Button login = new Button(); 

            userMenu = new MenuBar(true);
            loginItem = userMenu.addItem(loginText, login);

            menu = new MenuBar();
            menu.addItem("Bionimbus Home", home);
            menu.addItem("Public", publicMenu);
            menu.addItem("Registered", regMenu);
            menu.addItem("User", userMenu);

            RootPanel.get().add(menu);

            //basePanel = new CistrackSplashPanel(publicData, login);
            splash = new CistrackSplashDialog(publicData, login);
            //RootPanel.get().add(basePanel);
        }
    }

    static MenuBar              userMenu;
    static ClientInterfaceAsync rpc = GWT.create(ClientInterface.class);

    static Vector<Integer> vecFiller(int i)
    {
        Vector<Integer> vec = new Vector<Integer>();
        if (i >= 0)
            vec.add(i);
        return vec;
    }

    public static void makeDownload(final String filename, int experiment,
            int unit, int file)
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                // TODO : do something when rpc fails
                new ErrorBox("Experiment Detail RPC failed" + caught);
            }

            public void onSuccess(String result)
            {
                // TODO: how do we not hard-code the following URL?
                String URL = "/Download?dl=" + result + "&fn=" + filename
                        + ".tgz";
                Frame fileDownloadFrame = new Frame(URL);
                fileDownloadFrame.setSize("0px", "0px");
                fileDownloadFrame.setVisible(false);
                RootPanel panel = RootPanel.get("__gwt_downloadFrame");
                while (panel.getWidgetCount() > 0)
                    panel.remove(0);
                panel.add(fileDownloadFrame);
                // Window.alert( "Done!" );
            }
        };

        try
        {
            rpc.createDownload(filename, vecFiller(experiment),
                    vecFiller(unit), vecFiller(file), 5, callback);
        }
        catch (Exception e)
        {
            Window.alert("Error contacting server");
        }
    }

    native private static void close()/*-{
                                      $wnd.close();
                                      }-*/;

}
