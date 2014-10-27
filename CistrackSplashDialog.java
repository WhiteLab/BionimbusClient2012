package org.lac.bionimbus.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DialogBox;

public class CistrackSplashDialog extends DialogBox
{

    public CistrackSplashDialog(Command publicData, Command login)
    {
        this.add(new CistrackSplashPanel(publicData, login));
        this.center();
        this.setText("Welcome to Bionimbus");
        this.show();
        this.setModal(false);

    }

}
