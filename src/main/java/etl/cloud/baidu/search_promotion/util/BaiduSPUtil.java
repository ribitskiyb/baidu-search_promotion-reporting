package etl.cloud.baidu.search_promotion.util;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public final class BaiduSPUtil {

    public static void checkNotNullOrEmpty(String arg) {
        if (isBlank(arg)) {
            throw new IllegalArgumentException("argument can't be neither null nor empty.");
        }
    }

    public static List<String> stringToList(String listAsString) {
        return Arrays.asList(listAsString.trim().split("\\s*,\\s*"));
    }

}
