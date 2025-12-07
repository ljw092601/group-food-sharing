package com.database.group_food.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class GeometryUtil {

    // GWGS84, 표준 GPS 좌표계를 사용하도록 설정
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public Point createPoint(double longitude, double latitude) {
        // JTS의 Point는 (x, y) 즉 (경도, 위도) 순서
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}