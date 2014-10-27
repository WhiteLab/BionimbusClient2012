package org.lac.bionimbus.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ErrorBox extends DialogBox
{

    // private final static ClientInterfaceAsync rpc =
    // GWT.create(ClientInterface.class);
    private String        errLabel = "";
    private VerticalPanel vp       = new VerticalPanel();

    public ErrorBox()
    {
        this("Error");
    }

    public ErrorBox(String err)
    {
        super();
        vp.setSpacing(5);

        errLabel = err;
        setText("Error");

        vp.add(new HTML(err.replace("\n", "<br>")));
        vp.add(new Button("Ok", new ClickHandler()
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

    // Behaves more like a confirmation box if this constructor is used
    public ErrorBox(String err, String b1Text, String b2Text, final DialogBox w)
    {
        super();
        vp.setSpacing(5);

        errLabel = err;
        setText("Confirm action");

        vp.add(new Label(errLabel));

        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(5);

        hp.add(new Button(b1Text, new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                // TODO:Do Something
                w.show();
                hide();
            }
        }));

        hp.add(new Button(b2Text, new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                hide();
            }
        }));

        vp.add(hp);
        add(vp);
        center();
        show();
    }

    public void addWidget(Widget w)
    {
        vp.add(w);
    }

    public ErrorBox(String title, String msg, Button... buttons)
    {
        super(false, true);
        vp.setSpacing(5);

        setText(title);

        vp.add(new HTML(msg.replace("\n", "<br>")));

        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(5);
        vp.add(hp);
        add(vp);
        center();
        show();

        if (buttons.length == 0)
        {
            vp.add(new Button("Ok", new ClickHandler()
            {
                public void onClick(ClickEvent e)
                {
                    hide();
                }
            }));
        }

        for (Button b : buttons)
        {
            b.addClickHandler(new ClickHandler()
            {
                public void onClick(ClickEvent e)
                {
                    hide();
                }
            });
            hp.add(b);
        }

    }

    private String printStackTrace(StackTraceElement[] stackTrace)
    {
        String output = "";
        for (StackTraceElement line : stackTrace)
        {
            output += line.toString() + "\n";
        }
        return output;
    }

    public ErrorBox(Throwable t)
    {
        // TODO mail the developers?
        // TODO: figure out how to print the stack trace
        this(
                "Sorry, there was a bionimbus internal error.  Please send the contents of this dialog box to the developers.");
        TextArea ta = new TextArea();

        String s = t.getMessage();

        ta.setText(s + printStackTrace(t.getStackTrace()));

        addWidget(ta);
    }
}
