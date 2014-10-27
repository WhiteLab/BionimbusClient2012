package org.lac.bionimbus.client;

import org.lac.bionimbus.shared.ExperimentDetailRes;
import org.lac.bionimbus.shared.UnitRole;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class UploadApplet extends VerticalPanelWithHeader
{
    private Grid                       grid           = null;

    public UploadApplet(String username)
    {
	super("Upload files");
	grid = new Grid(2, 1);
	HTML appletHtml = new HTML("<br><br><br>Please note:<br><br>1. The application might take time to load, please wait<br>2. You will need to accept the certificate if required<br><br><br><br><br><br><br><applet code= 'edu.uic.ncdm.cistrack.CRDU.class' archive = '/Bionimbus/cu_signed.jar' width='1' height='1' ><param name = 'userdir' value = '" + username +"'><param name = 'internal' value = '4dbfba1ac202f63d7dcdb1ed36954a85'><param name = 'title' value = 'Cistrack Upload'><param name = 'serverlist' value = 'badserver;testing.cistrack.org' ></applet>");
	grid.setWidget(0, 0, appletHtml);
	add(grid);
	RootPanel.get().add(this);
    }

}
