package com.wolfheros.wmedia.util;

import com.wolfheros.wmedia.value.ItemEpisode;
import com.wolfheros.wmedia.value.Items;
import java.util.List;
import java.util.Map;

public class CustomerJSON {
    public static String episodeToJSON(ItemEpisode itemEpisode) {
        return "{\"src0\":\"" + itemEpisode.getResource_url() + "\",\"subsrc\":\"" + itemEpisode.getBbr() + "\",\"caption\":\"" + itemEpisode.getResource_name() + "\",\"src2\":\"" + itemEpisode.getKey() + "\"}";
    }

    public static String listToJSON(List<ItemEpisode> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                stringBuilder.append(episodeToJSON(list.get(i)));
            } else {
                stringBuilder.append(episodeToJSON(list.get(i))).append(",");
            }
        }
        return stringBuilder.toString();
    }

    public static String mapToJSON(Map<Integer, List<ItemEpisode>> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map.size() < 1) {
            stringBuilder.append("\"Seasons\":").append("null");
        } else if (map.size() < 2) {
            stringBuilder.append("\"Seasons\":").append('{').append('\"').append("S1").append('\"').append(':').append('[').append(listToJSON(map.get(1))).append(']').append('}');
        } else {
            stringBuilder.append("\"Seasons\":{");
            for (int i = 1; i <= map.size(); i++) {
                if (i == map.size()) {
                    stringBuilder.append('\"').append("S").append(i).append('\"').append(':').append('[').append(listToJSON(map.get(Integer.valueOf(i)))).append(']');
                } else {
                    stringBuilder.append('\"').append("S").append(i).append('\"').append(':').append('[').append(listToJSON(map.get(Integer.valueOf(i)))).append(']').append(',');
                }
            }
            stringBuilder.append("}");
        }
        return stringBuilder.toString();
    }

    public static String itemToJSON(Items items) {
        return ("{\"Name\":\"" + items.getName() + "\",\"Media_url\":\"" + items.getMedia_url() + "\",\"Image_url\":\"" + items.getImage_url() + "\",") + mapToJSON(items.getSourceMap()) + "}";
    }

    public static String itemsListToJSOn(List<Items> list) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!list.isEmpty()) {
            stringBuilder.append('[');
            for (int i = 0; i < list.size(); i++) {
                if (i == list.size() - 1) {
                    stringBuilder.append(itemToJSON(list.get(i)));
                } else {
                    stringBuilder.append(itemToJSON(list.get(i))).append(',');
                }
            }
            stringBuilder.append(']');
        } else {
            stringBuilder.append("获取到数据为：none");
        }
        return stringBuilder.toString();
    }
}