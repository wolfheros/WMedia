package com.wolfheros.wmedia;

import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.Items;

import java.net.SocketTimeoutException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MediaSelection {
    private static final String ATTR_STYLE = "style";
    private static final String CONTAINER = "container";
    private static final String DATA_HREF = "data-href";
    private static final String MAIN = "main";
    private static final String POST_BOX_CONTAINER = "post-box-container";
    private static final String POST_BOX_LIST = "post-box-list";
    private static final String POST_BOX_META = "post-box-meta";
    private static final String POST_BOX_TEXT = "post-box-text";
    private boolean doc_null;
    private String home_page;
    private Set<Items> itemsSet;
    private boolean last_page;

    private MediaSelection() {
        this.last_page = false;
        this.doc_null = false;
        this.itemsSet = null;
    }

    private MediaSelection(String url) {
        this.last_page = false;
        this.doc_null = false;
        this.itemsSet = null;
        this.itemsSet = new HashSet<>();
        this.home_page = url;
    }

    public static MediaSelection newInstance(String home_page2) {
        return new MediaSelection(home_page2);
    }

    /* access modifiers changed from: package-private */
    public Set<Items> run() {
        return urlSelector();
    }

    private Set<Items> urlSelector() {
        try {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            Document document = Jsoup.connect(this.home_page).userAgent("Mozilla").get();
            if (document != null) {
                this.doc_null = false;
                this.itemsSet.addAll(getMediaContent(document));
                String nextPage = getNextPage(document);
                if (!this.last_page && nextPage != null) {
                    Util.threadSleep();
                    this.home_page = nextPage;
                    Util.logOutput("NEXT WEBSITE \n" + nextPage + "\n CURRENT ITEMS " + this.itemsSet.size());
                    urlSelector();
                }
            }
        } catch (SocketTimeoutException e) {
            urlSelector();
            return this.itemsSet;
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
        return this.itemsSet;
    }

    /* access modifiers changed from: package-private */
    public Set<Items> getMediaContent(Document document) {
        HashSet<Items> hashSet = new HashSet<>();
        for (Element item : document.getElementsByAttributeStarting(DATA_HREF)) {
            for (Element element : item.getElementsByClass(POST_BOX_CONTAINER)) {
                Items items = new Items();
                Element elementContent = element.getElementsByClass(POST_BOX_TEXT).first().getElementsByAttribute("href").last();
                Element elementImage = element.getElementsByAttribute(ATTR_STYLE).first();
                Elements elementsTag = element.getElementsByClass(POST_BOX_META).first().getElementsByTag("a");
                if (elementContent == null || elementImage == null) {
                    break;
                }
                ArrayList<String> stringArrayList = new ArrayList<>();
                for (Element value : elementsTag) {
                    stringArrayList.add(value.text());
                }
                if (stringArrayList.isEmpty()) {
                    stringArrayList.add("OTHER_TV");
                }
                items.setStringList(stringArrayList);
                items.setName(elementContent.text());
                items.setMedia_url(elementContent.attr("href"));
                items.setImage_url(Util.cutUri(elementImage.toString()));
                hashSet.add(items);
            }
        }
        return hashSet;
    }

    /* access modifiers changed from: package-private */
    public String getNextPage(Document document) {
        try {
            Element element = document.getElementById(CONTAINER)
                    .getElementById(MAIN)
                    .getElementsByClass("pagination_wrap")
                    .first()
                    .getElementsByClass("navigation pagination")
                    .first()
                    .getElementsByClass("nav-links")
                    .first()
                    .getElementsByClass("next page-numbers")
                    .get(0);
            if (!Util.isEqual(element.attr("href"), this.home_page)) {
                this.last_page = false;
                return element.attr("href");
            }
            this.last_page = true;
            return null;
        }catch (IndexOutOfBoundsException ioException) {
            return null;
        }
    }
}