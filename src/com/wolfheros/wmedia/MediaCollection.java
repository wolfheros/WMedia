package com.wolfheros.wmedia;

import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.ItemEpisode;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MediaCollection implements Callable<Map<Integer, List<ItemEpisode>>> {
    private String home_page;
    private final Map<Integer, List<ItemEpisode>> linkMap = new LinkedHashMap();
    private Items mItems;
    private int mapKey = 1;

    private MediaCollection() {
    }

    public MediaCollection(Items items) {
        this.mItems = items;
    }

    public Map<Integer, List<ItemEpisode>> call() throws InterruptedException {
        Util.threadSleep();
        String url = this.mItems.getMedia_url();
        if (this.home_page == null) {
            this.home_page = url;
        }
        getEachEpisode(url);
        return this.linkMap;
    }

    /* access modifiers changed from: package-private */
    public void getEachEpisode(String url) {
        Util.logOutput("NEXT MEDIA START: ");
        try {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            Document document = Jsoup.connect(url).get();
            InfoThreadProvider.getInstance(document, this.mItems).getInfo();
            Elements elements = document.getElementsByTag(StaticValues.ARTICLE).first().getElementsByClass(StaticValues.POST_CONTENT).first().getElementsByClass(StaticValues.ENTRY);
            this.linkMap.put(this.mapKey, getPlayList(elements.first().getElementsByClass("wp-playlist").first()));
            Element elementSeason = elements.first().getElementsByClass("page-links").first();
            if (elementSeason != null) {
                String nextSeason = getSeasonLink(elementSeason);
                Util.logOutput(this.mItems.getName() + " NEXT SEASON: " + nextSeason);
                if (nextSeason != null) {
                    this.mapKey++;
                    getEachEpisode(nextSeason);
                    return;
                }
                return;
            }
            Util.logOutput("NO page-links class, CURRENT SEASON: " + this.mapKey);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private List<ItemEpisode> getPlayList(Element element) {
        Util.logOutput("GET EACH EPISODE: ");
        List<ItemEpisode> list = new LinkedList<>();
        try {
            Iterator it = ((JSONArray) new JSONParser().parse((Reader) new StringReader(((JSONObject) new JSONParser().parse((Reader) new StringReader(element.getElementsByTag("script").first().data()))).get("tracks").toString()))).iterator();
            while (it.hasNext()) {
                Object o = it.next();
                ItemEpisode item = new ItemEpisode();
                JSONObject j = (JSONObject) o;
                item.setBbr(j.get(StaticValues.BBR).toString());
                item.setResource_url(j.get(StaticValues.RESOURCE_URL).toString());
                item.setKey(j.get(StaticValues.KEY_VALUE).toString());
                item.setResource_name(j.get(StaticValues.RESOURCE_NAME).toString());
                list.add(item);
            }
        } catch (Exception ioException) {
            Util.logOutput("FAILED GET EPISODE");
            ioException.printStackTrace();
        }
        Util.logOutput("GET" + list.size() + "EPISODES");
        return list;
    }

    /* access modifiers changed from: package-private */
    public String getSeasonLink(Element element) {
        String currentSeason = element.getElementsByTag("span").first().text();
        if (Util.isNumber(currentSeason)) {
            currentSeason = "/" + currentSeason + "/";
        }
        Iterator it = element.getElementsByTag("a").iterator();
        while (it.hasNext()) {
            String nextSeason = ((Element) it.next()).attr("href");
            if (nextSeason != null && !this.home_page.equals(nextSeason) && Util.isBigger(nextSeason, currentSeason)) {
                return nextSeason;
            }
        }
        return null;
    }

    public Map<Integer, List<ItemEpisode>> getLinkMap() {
        return this.linkMap;
    }
}