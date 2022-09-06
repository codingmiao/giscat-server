package org.wowtools.giscatserver.dataset.postgis;

import org.locationtech.jts.io.WKBReader;
import org.wowtools.giscatserver.dataset.api.DataSetCtx;

/**
 * @author liuyu
 * @date 2022/9/1
 */
public class PostgisDataSetCtx extends DataSetCtx {
    public final WKBReader wkbReader = new WKBReader();
}
