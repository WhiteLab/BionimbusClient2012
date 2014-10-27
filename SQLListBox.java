package org.lac.bionimbus.client;

import java.util.Vector;

import org.lac.bionimbus.shared.SQLListBoxQuery;
import org.lac.bionimbus.shared.SQLListItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SQLListBox extends UserStringListBox
{

    private final ClientInterfaceAsync rpc    = GWT.create(ClientInterface.class);
    private SQLListBoxQuery.items      type;

    private Vector<SQLListBox>         clones = new Vector<SQLListBox>();
    private Integer                    pickThisIndex;

    public SQLListBox(SQLListBoxQuery.items type)
    {
        this(type, null);
    }

    public SQLListBox(SQLListBoxQuery.items type, String[] params)
    {
        super();
        this.type = type;
        refresh(params);
        // setStyleName("gwt-ListBox");
    }

    public SQLListBox(SQLListBox source)
    {
        super();
        this.type = source.getType();
        source.addClone(this);
        setEnabled(false);
    }

    public void addClone(SQLListBox listbox)
    {
        clones.add(listbox);
    }

    public void refreshSaveSelected(Integer _pickThisIndex)
    {
        if (_pickThisIndex == null)
            _pickThisIndex = getSelectedIndex();
        pickThisIndex = _pickThisIndex;
        refresh(null);
    }

    public void refresh()
    {
        refresh(null);
    }

    public void refresh(String[] params)
    {
        setEnabled(false);
        rpc.getListItems(type, params, makeRpcCallback(getSelectedValue()));
    }

    public int getSelectedValue()
    {
        int index = getSelectedIndex();
        if (index == -1)
            return -1;
        else
            return Integer.parseInt(getValue(index));
    }

    public void setSelectedValue(int val)
    {
        String v = val + "";
        for (int i = 0; i < getItemCount(); i++)
        {
            if (getValue(i).equals(v))
            {
                setSelectedIndex(i);
                return;
            }
        }
    }

    /*public void setSelectedValueDeferred(int val)
    {
        deferredValue = val;
    }*/

    private AsyncCallback<Vector<SQLListItem>> makeRpcCallback(final int s)
    {
        return new AsyncCallback<Vector<SQLListItem>>()
        {
            public void onFailure(Throwable caught)
            {
                setEnabled(false);
                // throw new Exception("cannot create listbox");
            }

            public void onSuccess(Vector<SQLListItem> callbackResult)
            {
                int i = 0;

                clear();
                addDefaultItem();

                for (SQLListItem item : callbackResult)
                {
                    addItem(item.getText(), "" + item.getValue());
                    i++;
                }

                if (pickThisIndex != null)
                {
                    setSelectedIndex(pickThisIndex);
                }

                setEnabled(true);
                refreshClones();
            }
        };
    }

    private void refreshClones()
    {
        for (SQLListBox box : clones)
        {
            box.clear();
            for (int i = 0; i < getItemCount(); i++)
            {
                box.addItem(getItemText(i), getValue(i));
            }
            box.setSelectedIndex(getSelectedIndex());
            box.setEnabled(true);
        }
    }

    public SQLListBoxQuery.items getType()
    {
        return type;
    }

    // public static SQLListBox newDistinctPersonnel() throws Exception {
    // return new SQLListBox(SQLListBoxQuery.SQLLIST_PERSONNEL_DISTINCT);
    // }
    //
    // public static SQLListBox newFileType(int catalogId) throws Exception {
    // return new SQLListBox(SQLListBoxQuery.SQLLIST_FILETYPE, {""+ catalogId});
    // }
    //
    // public static void refreshFileType(SQLListBox listbox, int catalogId)
    // throws Exception {
    // if (listbox.getType() == SQLListBoxQuery.SQLLIST_FILETYPE) {
    // listbox.refresh(catalogId);
    // } else {
    // throw new Exception("incorrect sqllistbls -lox type");
    // }
    // }
    //
    // public static SQLListBox newDonor() throws Exception {
    // return new SQLListBox(SQLListBoxQuery.SQLLIST_DONOR);
    // }
    //
    // public static SQLListBox newFacility() {
    // return new SQLListBox(SQLListBoxQuery.FACILITY);
    // }
    //
    // public static SQLListBox newOrganism() {
    // return new SQLListBox(SQLListBoxQuery.ORGANISM);
    // }

}
