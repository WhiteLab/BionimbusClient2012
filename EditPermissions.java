package org.lac.bionimbus.client;

import java.util.HashMap;
import java.util.Vector;

import org.lac.bionimbus.shared.BNPermissions;
import org.lac.bionimbus.shared.BNPermissions.PermissionsEntity;
import org.lac.bionimbus.shared.BNPermissions.RW;
import org.lac.bionimbus.shared.Group;
import org.lac.bionimbus.shared.Supergroup;
import org.lac.bionimbus.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EditPermissions extends DialogBox
{
    private final ClientInterfaceAsync    rpc              = GWT.create(ClientInterface.class);

    private ListBox                       allGroups        = new ListBox(true);
    private ListBox                       groupsWithAccess = new ListBox(true);
    private ListBox                       usersForGroup    = new ListBox(true);

    private HashMap<String, Vector<User>> users_in_group   = new HashMap<String, Vector<User>>();

    int                                   id;

    PermissionsEntity                     pe;
    RW                                    readwrite;

    EditPermissions(int _id, PermissionsEntity _pe, RW _readwrite, String for_a)
    {
        id = _id;
        pe = _pe;
        readwrite = _readwrite;

        setText("Setting permissions for " + for_a);

        VerticalPanel vp = new VerticalPanel();

        HorizontalPanel hp = new HorizontalPanel();

        allGroups.addClickHandler(new ClickHandler()
        {
            long prev = System.currentTimeMillis();

            public void onClick(ClickEvent e)
            {
                int index = allGroups.getSelectedIndex();
                String id = allGroups.getValue(index);

                String name = allGroups.getItemText(index);

                Vector<User> users = users_in_group.get(name);

                usersForGroup.clear();

                if (users != null)
                {
                    for (User u : users)
                    {
                        usersForGroup.addItem(u.name);
                    }
                }
                else
                {
                    new ErrorBox("No object for " + name);
                }

                long curr = System.currentTimeMillis();

                if (curr - prev < 450)
                {
                    groupsWithAccess.addItem(name, id);
                }
                prev = curr;
            }
        });

        groupsWithAccess.addClickHandler(new ClickHandler()
        {
            long prev = System.currentTimeMillis();

            public void onClick(ClickEvent e)
            {
                long curr = System.currentTimeMillis();
                if (curr - prev < 450)
                {
                    int index = groupsWithAccess.getSelectedIndex();
                    groupsWithAccess.removeItem(index);
                }
                prev = curr;
            }
        });

        hp.add(groupsWithAccess);
        hp.add(allGroups);
        hp.add(usersForGroup);

        vp.add(hp);

        vp.add(new Button("Cancel", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                hide();
            }
        }));

        vp.add(new Button("Ok", new ClickHandler()
        {
            public void onClick(ClickEvent e)
            {
                setPermissions();
            }
        }));

        add(vp);

        AsyncCallback<BNPermissions> callback = new AsyncCallback<BNPermissions>()
        {
            public void onFailure(Throwable e)
            {
                new ErrorBox("peachey " + e);
            }

            public void onSuccess(BNPermissions bnp)
            {
                Supergroup sg = bnp.supergroup;
                fillWith(groupsWithAccess, sg.groups);
                fillWith(allGroups, bnp.groups);
            }
        };

        try
        {
            rpc.getPermissionsFor(pe, id, callback);
        }
        catch (Exception e)
        {
            new ErrorBox(e);
        }
        show();
        center();
    }

    void setPermissions()
    {
        int count = groupsWithAccess.getItemCount();

        Vector<Integer> newGroups = new Vector<Integer>();

        for (int a = 0; a < count; ++a)
        {
            newGroups.add(new Integer(groupsWithAccess.getValue(a)));
        }

        AsyncCallback<Integer> callback = new AsyncCallback<Integer>()
        {
            public void onFailure(Throwable e)
            {
                new ErrorBox("peachey " + e);
            }

            public void onSuccess(Integer result)
            {
                new ErrorBox("Permissions successfully changed", "Success!");
                hide();
            }
        };

        rpc.setPermissions(id, pe, readwrite, newGroups, callback);
    }

    void fillWith(ListBox box, Vector<Group> groupList)
    {
        box.clear();
        /*Collections.sort(groupList, new Comparator<Group>()
        {
            public int compare(Group a, Group b)
            {
                return a.name.compareTo(b.name);
            }
        });*/
        for (Group g : groupList)
        {
            box.addItem(g.name, "" + g.id);
            if (box == allGroups)
            {
                users_in_group.put(g.name, g.users);
            }
        }
    }
}
