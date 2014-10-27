package org.lac.bionimbus.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OpenidManagerPanel extends VerticalPanel
{
    private VerticalPanel        idList = new VerticalPanel();
    private Button               remove;
    private OpenidManagerHandler handler;
    private OpenidPanel          openidPanel;

    OpenidManagerPanel(String returnURL, final OpenidManagerHandler handler)
    {
        this.handler = handler;

        remove = new Button("remove ids", new ClickHandler()
        {

            public void onClick(ClickEvent event)
            {
                removeIds();
            }

        });

        openidPanel = new OpenidPanel(returnURL, new OpenidHandler()
        {
            public void onFailure(String message)
            {
                handler.onFailure(message);
            }

            public void onSuccess(String openid)
            {
                handler.add(openid);
            }
        });

        add(idList);
        add(remove);
        add(openidPanel);
    }

    public void addId(String openid)
    {
        idList.add(new CheckBox(openid));
        noIds();
    }

    public void removeId(String openid)
    {
        for (int i = 0; i < idList.getWidgetCount(); i++)
        {
            CheckBox box = (CheckBox) idList.getWidget(i);
            if (box.getText().equals(openid))
            {
                idList.remove(i);
            }
        }
        noIds();
    }

    public void addProvider(String name, String URL)
    {
        openidPanel.addProvider(name, URL);
    }

    private void noIds()
    {
        if (idList.getWidgetCount() == 0)
        {
            remove.setVisible(false);
            return;
        }
        remove.setVisible(true);
    }

    private void removeIds()
    {
        int count = 0;
        for (int i = 0; i < idList.getWidgetCount(); i++)
        {
            CheckBox box = (CheckBox) idList.getWidget(i);
            if (box.getValue())
            {
                count++;
                if (idList.getWidgetCount() == 1
                        || idList.getWidgetCount() == count)
                {
                    handler.removeLast(box.getText());
                }
                else
                {
                    handler.remove(box.getText());
                }
            }
        }
    }
}
