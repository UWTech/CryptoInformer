package com.example.cryptoinformer.ui.crypto_news.crypto_rss;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RSSRetriver {

    protected List<String> feedTitles;
    protected List<String> rssLinks;
    protected List<String> RSSFeeds;
    protected Map<String, String> titleLinkMap;

    public RSSRetriver() {
        this.feedTitles = new ArrayList<String>();
        this.rssLinks = new ArrayList<String>();
        this.titleLinkMap = new Hashtable<>();
        this.RSSFeeds = new ArrayList<String>();

        // TODO:: set this with dependency injection
        // TODO:: add more RSS feeds
        this.RSSFeeds.add("https://cointelegraph.com/rss/tag/blockchain");
    }

    public List<String> getTitles() {
        return this.feedTitles;
    }

    public List<String> getRssLinks() {
        return this.rssLinks;
    }

    public Set<String> getTitleMapKeyset() {
        return this.titleLinkMap.keySet();
    }

    public String getTitleRecord(String title) {
        return this.titleLinkMap.get(title);
    }

    public void getRSSFeeds() {
        for (String feed: this.RSSFeeds) {
            try {
                URL url = new URL(feed);
                getRSSFeed(url);
            } catch (Exception e) {
                System.out.println("Failed to retrieve RSS feed for: " + feed);
                e.printStackTrace();
            }
        }

        // map the titles to their respective links
        mapTitles();
    }

    // TODO:: make async
    private void getRSSFeed(URL url) throws IOException, XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();

        // set RSS feed to extract from
        xpp.setInput(url.openConnection().getInputStream(), "UTF_8");

        /* DOM parsing flags logic code adapted from example
         *  https://www.youtube.com/watch?v=Lnan_DJU7DI
         */
        boolean parsingRecord = false;

        // Returns the type of current event: START_TAG, END_TAG, START_DOCUMENT, END_DOCUMENT etc..
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            // indicates the entry to an RSS feed element in the DOM tree
            if (eventType == XmlPullParser.START_TAG) {
                // item tag indicate starting position in the DOM tree for a record
                // of interest in the RSS feed
                if (xpp.getName().equalsIgnoreCase("item")) {
                    parsingRecord = true;
                }
                // represents the title of a given article
                else if (xpp.getName().equalsIgnoreCase("title")) {
                    if (parsingRecord) {
                        String title = xpp != null ? xpp.nextText() : "None";
                        System.out.println("title: " + title);
                        this.feedTitles.add(title);
                    }
                }
                // represents the hyperlink to the article
                else if (xpp.getName().equalsIgnoreCase("link")) {
                    if (parsingRecord) {
                        String link = xpp != null ? xpp.nextText() : "None";
                        System.out.println("link: " + link);
                        this.rssLinks.add(link);
                    }
                }
            }
            //END_TAG and type is item means we are done processing this record in the DOM tree
            else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                parsingRecord = false;
            }

            // navigate to the next element in the DOM tree
            eventType = xpp.next();
        }
    }

    private void mapTitles() {
        /**
         * maps the title of the article to the link of the article
         */

        for (int i = 0; i < this.feedTitles.size(); i++) {
            this.titleLinkMap.put(this.feedTitles.get(i), this.rssLinks.get(i));
        }
    }
}
