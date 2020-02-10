package com.example.cryptoinformer.ui.crypto_news.cryptoRss;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

// code tutorial cribbed here https://www.youtube.com/watch?v=Lnan_DJU7DI

public class RSSRetriver {

    protected ArrayList<String> feedTitles;
    protected ArrayList<String> rssLinks;

    public RSSRetriver() {
        this.feedTitles = new ArrayList<String>();
        this.rssLinks = new ArrayList<String>();
    }


    public ArrayList<String> getTitles() {
        return this.feedTitles;
    }

    public ArrayList<String> getRssLinks() {
        return this.rssLinks;
    }

    public InputStream getInputStream(URL url) throws IOException {
        /**
         * gets the input stream of the supplied
         * RSS feed URL
         */

        try
        {
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public void getRSSFeed() throws IOException, XmlPullParserException {
        URL url = new URL("https://cointelegraph.com/rss/tag/blockchain");
        //creates new instance of PullParserFactory that can be used to create XML pull parsers
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        //Specifies whether the parser produced by this factory will provide support
        //for XML namespaces
        factory.setNamespaceAware(false);

        //creates a new instance of a XML pull parser using the currently configured
        //factory features
        XmlPullParser xpp = factory.newPullParser();

        // We will get the XML from an input stream
        xpp.setInput(getInputStream(url), "UTF_8");

        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
         * We should take into consideration that the rss feed name is also enclosed in a "<title>" tag.
         * Every feed begins with these lines: "<channel><title>Feed_Name</title> etc."
         * We should skip the "<title>" tag which is a child of "<channel>" tag,
         * and take into consideration only the "<title>" tag which is a child of the "<item>" tag
         *
         * In order to achieve this, we will make use of a boolean variable called "insideItem".
         */
        boolean insideItem = false;

        // Returns the type of current event: START_TAG, END_TAG, START_DOCUMENT, END_DOCUMENT etc..
        int eventType = xpp.getEventType(); //loop control variable

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            //if we are at a START_TAG (opening tag)
            if (eventType == XmlPullParser.START_TAG)
            {
                //if the tag is called "item"
                if (xpp.getName().equalsIgnoreCase("item"))
                {
                    insideItem = true;
                }
                //if the tag is called "title"
                else if (xpp.getName().equalsIgnoreCase("title"))
                {
                    if (insideItem)
                    {
                        // extract the text between <title> and </title>
                        String title = xpp != null ? xpp.nextText() : "None";
                        System.out.println("title: " + title);
                        this.feedTitles.add(title);
                    }
                }
                //if the tag is called "link"
                else if (xpp.getName().equalsIgnoreCase("link"))
                {
                    if (insideItem)
                    {
                        // extract the text between <link> and </link>
                        String link = xpp != null ? xpp.nextText() : "None";
                        System.out.println("link: " + link);
                        this.rssLinks.add(link);
                    }
                }
            }
            //if we are at an END_TAG and the END_TAG is called "item"
            else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
            {
                insideItem = false;
            }

            eventType = xpp.next(); //move to next element
        }

    }
}
