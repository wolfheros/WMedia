package com.wolfheros.wmedia;

import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;
import org.jsoup.nodes.Document;

public class InfoThreadProvider {
    private static InfoThreadProvider infoThreadProvider;
    private Document doc;
    private Items it;

    private InfoThreadProvider(Document document, Items items) {
        this.doc = document;
        this.it = items;
    }

    public void setIt(Items items) {
        this.it = items;
    }

    private void setmUri(Document document) {
        this.doc = document;
    }

    public static InfoThreadProvider getInstance(Document document, Items items) {
        if (infoThreadProvider == null) {
            synchronized (InfoThreadProvider.class) {
                if (infoThreadProvider == null) {
                    infoThreadProvider = new InfoThreadProvider(document, items);
                }
            }
        } else {
            infoThreadProvider.setmUri(document);
            infoThreadProvider.setIt(items);
        }
        return infoThreadProvider;
    }

    public void getInfo() {
        try {
            this.it.setImage_url(this.doc.select("main").first()
                    .getElementsByClass("type-post").first()
                    .getElementsByClass(StaticValues.POST_CONTENT).first()
                    .getElementsByClass(StaticValues.ENTRY).first()
                    .getElementsByClass("doulist-item").first()
                    .getElementsByClass("mod").first()
                    .getElementsByClass("doulist-subject").first()
                    .getElementsByClass("post").select("img").first()
                    .absUrl("src"));
        } catch (NullPointerException nullPointerException) {
            Util.logOutput("获取:\n" + this.it.getName() + " 照片过程中出现错误!\n");
            nullPointerException.printStackTrace();
        }
    }
}