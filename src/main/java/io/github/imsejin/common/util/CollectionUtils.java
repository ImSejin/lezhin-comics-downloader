package io.github.imsejin.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CollectionUtils {

    private CollectionUtils() {}

    /**
     * List를 인덱스가 key인 Map으로 변경한다.
     *
     * @param list list
     * @param <V>  type of element in the list
     * @return map with index as key and element
     */
    public static <V> Map<Integer, V> toMap(List<V> list) {
        return list.stream()
                .collect(HashMap<Integer, V>::new,
                        (map, streamValue) -> map.put(map.size(), streamValue),
                        (map, map2) -> {});
    }

}
