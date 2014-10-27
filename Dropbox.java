package org.lac.bionimbus.client;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Dropbox extends DialogBox
{
    private final ClientInterfaceAsync rpc         = GWT.create(ClientInterface.class);

    private Vector<Integer>            experiments = new Vector<Integer>();
    private Vector<Integer>            units       = new Vector<Integer>();
    private Vector<Integer>            files       = new Vector<Integer>();

    private Grid                       contents    = new Grid(2, 1);
    //private Button                     create;
    //private Button                     cloud;
    //private Button                     cancel;
    private TextBox                    tf          = new TextBox();

    Dropbox()
    {
        super(false, false);
        setText("Dropbox");
        VerticalPanel vp = new VerticalPanel();

        vp.add(tf);
        vp.add(contents);

        vp.add(new Button("Create", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                makeDownload();
            }
        }));

        vp.add(new Button("Push to the cloud", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                cloudPush();
            }
        }));

        vp.add(new Button("Cancel", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                hide();
            }
        }));

        add(vp);
        center();
        show();
    }

    void makeDownload()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox(caught);
            }

            public void onSuccess(String result)
            {

                String srv = Window.Location.getHostName() + ":"
                        + Window.Location.getPort();
                String URL = "http://" + srv + "/Download?dl=" + result;
                Window.alert("Your download URL: " + URL);
                hide();
            }
        };

        try
        {
            rpc.createDownload(tf.getText(), experiments, units, files, -1,
                    callback);
        }
        catch (Exception e)
        {
            new Error(e);
        }
    }

    public void hide()
    {
        super.hide();
        thisBox = null;
    }

    void cloudPush()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            public void onFailure(Throwable caught)
            {
                new ErrorBox(caught);
            }

            public void onSuccess(String result)
            {
                new ErrorBox("Cloud Publish",
                        "Your data will appear in glusterfs shortly.");
                hide();
            }
        };

        try
        {
            rpc.cloudPublish("", experiments, units, files, callback);
        }
        catch (Exception e)
        {
            new Error(e);
        }
    }

    int rownum = 0;

    void addVec(String name, Vector<Integer> parts)
    {
        for (Integer i : parts)
        {
            contents.setWidget(rownum, 0, new Button("x"));
            contents.setText(rownum, 1, name + " " + i);
            rownum++;
        }
    }

    void refresh()
    {
        int rows = experiments.size() + units.size() + files.size();
        contents.resize(rows, 2);
        rownum = 0;
        addVec("Experiment", experiments);
        addVec("Unit", units);
        addVec("File", files);
    }

    static Dropbox thisBox;

    static void make()
    {
        if (thisBox == null)
        {
            thisBox = new Dropbox();
        }
    }

    static void adder(Vector<Integer> vec, int val)
    {
        vec.add(val);
        thisBox.refresh();
    }

    static void addExperiment(int ex)
    {
        make();
        adder(thisBox.experiments, ex);

    }

    static void addUnit(int ex)
    {
        make();
        adder(thisBox.units, ex);
    }

    static void addFile(int ex)
    {
        make();
        adder(thisBox.files, ex);
    }
}
