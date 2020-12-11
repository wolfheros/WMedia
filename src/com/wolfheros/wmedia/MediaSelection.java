package com.wolfheros.wmedia;

import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.Items;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
    private List<Items> itemsList;
    private boolean last_page;

    private MediaSelection() {
        this.last_page = false;
        this.doc_null = false;
        this.itemsList = null;
    }

    private MediaSelection(String url) {
        this.last_page = false;
        this.doc_null = false;
        this.itemsList = null;
        this.itemsList = new LinkedList();
        this.home_page = url;
    }

    public static MediaSelection newInstance(String home_page2) {
        return new MediaSelection(home_page2);
    }

    /* access modifiers changed from: package-private */
    public List<Items> run() {
        return urlSelector();
    }

    private List<Items> urlSelector() {
        try {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            Document document = Jsoup.connect(this.home_page).get();
            if (document != null) {
                this.doc_null = false;
                this.itemsList.addAll(getMediaContent(document));
                String nextPage = getNextPage(document);
                if (!this.last_page && nextPage != null) {
                    Util.threadSleep();
                    this.home_page = nextPage;
                    Util.logOutput("下一网站地址: " + nextPage + "\n当前获取资源数量：" + this.itemsList.size());
                    urlSelector();
                }
            } else if (!this.doc_null) {
                Util.logOutput("获取document失败");
                this.doc_null = true;
                urlSelector();
            }
        } catch (SocketTimeoutException e) {
            urlSelector();
            return this.itemsList;
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
        return this.itemsList;
    }

    /* access modifiers changed from: package-private */
    public LinkedList<Items> getMediaContent(Document document) {
        LinkedList<Items> list = new LinkedList<>();
        Iterator it = document.getElementsByAttributeStarting(DATA_HREF).iterator();
        while (it.hasNext()) {
            Iterator it2 = ((Element) it.next()).getElementsByClass(POST_BOX_CONTAINER).iterator();
            while (it2.hasNext()) {
                Element eList = (Element) it2.next();
                Items items = new Items();
                Element elementContent = eList.getElementsByClass(POST_BOX_TEXT).first().getElementsByAttribute("href").last();
                Element elementImage = eList.getElementsByAttribute(ATTR_STYLE).first();
                Elements elementsTag = eList.getElementsByClass(POST_BOX_META).first().getElementsByTag("a");
                if (elementContent == null || elementImage == null) {
                    break;
                }
                ArrayList<String> stringArrayList = new ArrayList<>();
                Iterator it3 = elementsTag.iterator();
                while (it3.hasNext()) {
                    stringArrayList.add(((Element) it3.next()).text());
                }
                if (stringArrayList.size() == 0) {
                    stringArrayList.add("OTHER_TV");
                }
                items.setStringList(stringArrayList);
                items.setName(elementContent.text());
                items.setMedia_url(elementContent.attr("href"));
                items.setImage_url(Util.cutUri(elementImage.toString()));
                list.add(items);
            }
        }
        return list;
    }

    /* access modifiers changed from: package-private */
    public String getNextPage(Document document) {
        Element element = document.getElementById(CONTAINER).getElementById(MAIN).getElementsByClass("pagination_wrap").first().getElementsByAttribute("href").last();
        if (!Util.isEqual(element.attr("href"), this.home_page)) {
            this.last_page = false;
            return element.attr("href");
        }
        this.last_page = true;
        return null;
    }
}