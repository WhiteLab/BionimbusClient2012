package org.lac.bionimbus.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.ListBox;

public class Utils
{

    public static String ntoS(Object o)
    {
        return o == null ? "" : o.toString();
    }

    public static void fillBarcode(ListBox l)
    {
        l.addItem("--None--");
        //fetchURLInto("/Bionimbus/barcodes.csv", l);
        fetchURLInto("http://127.0.0.1:8888/barcodes.csv", l);
    }

    /*  public static String getBarcodeList()
      {
          String str = "";
          //str = fetchURLIntoStr("/Bionimbus/barcodes.csv");
          str = fetchURLIntoStr("http://127.0.0.1:8888/barcodes.csv");
          System.out.println("get list" + str);
          return str;
      }*/

    public static String beforeComma(String str)
    {
        return (str.split(","))[0];
    }

    static void fetchURLInto(String u, final ListBox l)
    /* throws MalformedURLException,IOException*/
    {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, u);
        try
        {
            rb.sendRequest(null, new RequestCallback()
            {
                public void onError(final Request request,
                        final Throwable exception)
                {
                    new Error("Callback error " + exception.getMessage());
                }

                public void onResponseReceived(final Request request,
                        final Response response)
                {
                    String txt = response.getText();

                    String[] tokens = txt.split("\n");

                    for (String t : tokens)
                    {
                        t = beforeComma(t);
                        l.addItem(t);
                        //if (t != "")
                        //  System.out.println(t);
                    }
                }
            });
        }
        catch (final Exception e)
        {
            new Error("Call error " + e.getMessage());
        }
    }

    /*   static String fetchURLIntoStr(String u)*/
    /* throws MalformedURLException,IOException*/
    /*   {
           final String str = "";
           RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, u);
           try
           {
               rb.sendRequest(null, new RequestCallback()
               {
                   public void onError(final Request request,
                           final Throwable exception)
                   {
                       new Error("Callback error " + exception.getMessage());
                   }

                   public void onResponseReceived(final Request request,
                           final Response response)
                   {
                       String txt = response.getText();

                       String[] tokens = txt.split("\n");

                       for (String t : tokens)
                       {
                           t = beforeComma(t);
                           //l.addItem(t);
                           str.concat(t);
                           str.concat(",");
                           //System.out.println(t);
                       }
                       System.out.println("fetch" + str);
                   }
               });
           }
           catch (final Exception e)
           {
               new Error("Call error " + e.getMessage());
           }
           return str;
       }*/
}
