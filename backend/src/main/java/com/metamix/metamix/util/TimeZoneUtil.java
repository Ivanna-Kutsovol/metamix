package com.metamix.metamix.util;

import net.iakovlev.timeshape.TimeZoneEngine;
import java.time.ZoneId;

public class TimeZoneUtil {

    private static final TimeZoneEngine engine = TimeZoneEngine.initialize();

    public static ZoneId getZoneId(double lat, double lon) {
        return engine.query(lat, lon).orElse(ZoneId.of("UTC"));
    }
}

