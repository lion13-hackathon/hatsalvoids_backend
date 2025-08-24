package com.example.hatsalvoids.shade;


import com.example.hatsalvoids.external.vworld.VWorldApiCaller;
import com.example.hatsalvoids.external.vworld.model.response.GISBuildingWFSApiResponse;
import com.example.hatsalvoids.external.vworld.model.response.RoadAddressBuildingApiResponse;
import com.example.hatsalvoids.global.GlobalUtils;
import com.example.hatsalvoids.shade.model.FetchShadeResponse;
import com.example.hatsalvoids.shade.model.ShadeGeometryResult;
import lombok.RequiredArgsConstructor;
import net.e175.klaus.solarpositioning.AzimuthZenithAngle;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.SPA;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.proj4j.*;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShadeService {

    private final VWorldApiCaller vWorldApiCaller;

    // 그늘 구하기
    public List<FetchShadeResponse> getShades(String latitude, String longitude, double radius,
                                              String time, String zoneId) {

        ZonedDateTime when = GlobalUtils.getZonedDateTime(time, zoneId);
        List<FetchShadeResponse> results = new ArrayList<>();

        Geometry bufferPolygon = getBufferPolygon(Double.parseDouble(latitude),
                Double.parseDouble(longitude), radius);

        // TODO : 데이터베이스에서 주변 반경 내 건물들의 정보가 있는지 확인하기 -> 기준을 어떻게?

        String geometryType = "POLYGON";
        String geometryFilter = geometryType + new WKTWriter().writeFormatted(bufferPolygon).substring(
                geometryType.length() + 1,
                bufferPolygon.toString().length());

        // 현재 위치에서 주변 반경 내 건물 리스트를 "도로명주소건물 API" 호출하여 가져오기
        List<RoadAddressBuildingApiResponse> roadAddressBuildingApiResponses =
                vWorldApiCaller.getRoadAddressBuildingAroundAll(geometryFilter);

        for (RoadAddressBuildingApiResponse roadAddressBuildingApiResponse : roadAddressBuildingApiResponses) {
            List<RoadAddressBuildingApiResponse.FeatureDto> features =
                    roadAddressBuildingApiResponse.getResult().getFeatureCollection().getFeatures();

            for (RoadAddressBuildingApiResponse.FeatureDto feature : features) {

                // nestedGeometry 추출
                List<List<List<List<Double>>>> nestedGeometry = feature.getGeometry().getCoordinates();


                //  API 호출 결과의 건축관리대장번호 앞자리 19자를 추출하여 pnu 추출
                String pnu = feature.getProperties().getBd_mgt_sn().substring(0, 19);

                // pnu로 "GIS건물통합WFS조회 API" 호출해서 건축물 높이 가져오기
                GISBuildingWFSApiResponse gisBuildingWFSApiResponse = vWorldApiCaller.getGISBuildingWFS(pnu);

                if (gisBuildingWFSApiResponse.getFeatures().isEmpty()) {
                    continue; // 건물 정보가 없으면 다음 건물로
                }

                Double buildingHeightM = gisBuildingWFSApiResponse.getFeatures().get(0).getProperties().getHg();

                // 각 건물에 대해 그늘 연산 수행
                List<List<double[]>> rings = extractLinearRings(nestedGeometry);
                ShadeGeometryResult result = computeShadeGeometryEpsg5186(rings, buildingHeightM, when, zoneId);

                FetchShadeResponse response = FetchShadeResponse.of(feature.getProperties(), result);
                results.add(response);
            }
        }

        return results;
    }

    public Geometry getBufferPolygon(double latitude, double longitude, double radiusMeters) {
        GeometryFactory geometryFactory = new GeometryFactory();

        // 1) EPSG:4326 좌표 객체 생성 (lon, lat)
        ProjCoordinate srcCoord = new ProjCoordinate(longitude, latitude);
        ProjCoordinate dstCoord = new ProjCoordinate();

        // 2) CRS 및 변환 객체 생성
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem srcCrs = crsFactory.createFromName("EPSG:4326");
        CoordinateReferenceSystem dstCrs = crsFactory.createFromName("EPSG:5186");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(srcCrs, dstCrs);

        // 3) 좌표 변환 수행
        transform.transform(srcCoord, dstCoord);

        // 4) JTS Point 생성 (EPSG:5186 좌표계)
        Coordinate convertedCoord = new Coordinate(dstCoord.x, dstCoord.y);
        Point point5186 = geometryFactory.createPoint(convertedCoord);

        // 5) 버퍼 생성 (radius는 미터 단위)
        int quadrantSegments = 20;
        Geometry bufferPolygon = point5186.buffer(radiusMeters, quadrantSegments);

        return bufferPolygon;
    }

    // 깊게 중첩된 멀티폴리곤 구조에서 모든 LinearRing을 추출: [ [ [x,y], ... ], ... ]
    private List<List<double[]>> extractLinearRings(Object nestedGeometry) {
        List<List<double[]>> rings = new ArrayList<>();
        walkGeometry(nestedGeometry, rings);
        return rings;
    }

    private void walkGeometry(Object node, List<List<double[]>> rings) {
        if (node instanceof List<?> listNode) {
            if (!listNode.isEmpty() && isRing(listNode)) {
                List<double[]> ring = new ArrayList<>();
                for (Object p : listNode) {
                    List<?> pt = (List<?>) p;
                    double x = toDouble(pt.get(0));
                    double y = toDouble(pt.get(1));
                    ring.add(new double[]{x, y});
                }
                rings.add(ring);
                return;
            }
            for (Object child : listNode) {
                walkGeometry(child, rings);
            }
        }
    }

    private boolean isRing(List<?> candidate) {
        if (candidate.isEmpty()) return false;
        for (Object p : candidate) {
            if (!(p instanceof List<?> pt && pt.size() == 2 &&
                    pt.get(0) instanceof Number && pt.get(1) instanceof Number)) {
                return false;
            }
        }
        return true;
    }

    private double toDouble(Object o) {
        return ((Number) Objects.requireNonNull(o, "value")).doubleValue();
    }

    // 단순 평균 센트로이드(면적 가중 아님)
    private double[] computeCentroidXY(List<double[]> points) {
        double sx = 0.0;
        double sy = 0.0;
        int n = points.size();
        for (double[] p : points) {
            sx += p[0];
            sy += p[1];
        }
        return new double[]{sx / n, sy / n};
    }

    // EPSG:5186 -> EPSG:4326 좌표 변환 후 태양 고도/방위 계산 (SPA)
    private double[] getSolarAnglesForEpsg5186Point(double x5186, double y5186, ZonedDateTime when, String zoneId) {
        ZonedDateTime whenLocal = when.withZoneSameInstant(ZoneId.of(zoneId));
        ZonedDateTime whenKst = whenLocal.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem src = crsFactory.createFromName("EPSG:5186");
        CoordinateReferenceSystem dst = crsFactory.createFromName("EPSG:4326");
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(src, dst);
        ProjCoordinate from = new ProjCoordinate(x5186, y5186);
        ProjCoordinate to = new ProjCoordinate();
        transform.transform(from, to);
        double lon = to.x;
        double lat = to.y;

        double deltaT = DeltaT.estimate(java.util.GregorianCalendar.from(whenKst));
        AzimuthZenithAngle position = SPA.calculateSolarPosition(
                java.util.GregorianCalendar.from(whenKst),
                lat,
                lon,
                0.0,
                deltaT,
                1010.0,
                11.0
        );
        double azimuthDeg = position.getAzimuth();
        double altitudeDeg = 90.0 - position.getZenithAngle();
        return new double[]{altitudeDeg, azimuthDeg};
    }

    // 그림자 벡터 계산 (degree 입력)
    private double[] computeShadeOffset(double heightM, double altitudeDeg, double azimuthDeg, double minAltitudeDeg, double maxLengthM) {
        double useAlt = Math.max(altitudeDeg, minAltitudeDeg);
        double length = useAlt > 0.0 ? heightM / Math.tan(Math.toRadians(useAlt)) : maxLengthM;
        length = Math.min(length, maxLengthM);
        double shadeAz = (azimuthDeg + 180.0) % 360.0;
        double rad = Math.toRadians(shadeAz);
        double dx = length * Math.sin(rad);
        double dy = length * Math.cos(rad);
        return new double[]{dx, dy};
    }

    private List<double[]> shiftRingXY(List<double[]> ring, double dx, double dy) {
        List<double[]> shifted = new ArrayList<>(ring.size());
        for (double[] p : ring) {
            shifted.add(new double[]{p[0] + dx, p[1] + dy});
        }
        return shifted;
    }

    private ShadeGeometryResult computeShadeGeometryEpsg5186(List<List<double[]>> rings, double heightM, ZonedDateTime when, String zoneId) {
        if (rings.isEmpty()) {
            throw new IllegalArgumentException("유효한 LinearRing 좌표를 찾지 못했습니다.");
        }
        double[] centroid = computeCentroidXY(rings.get(0));
        double[] solar = getSolarAnglesForEpsg5186Point(centroid[0], centroid[1], when, zoneId);
        double altitude = solar[0];
        double azimuth = solar[1];
        if (altitude <= 0.0) {
            return new ShadeGeometryResult(rings, List.of());
        }
        double[] offset = computeShadeOffset(heightM, altitude, azimuth, 1.0, 10000.0);
        double dx = offset[0];
        double dy = offset[1];
        List<List<double[]>> shade = new ArrayList<>(rings.size());
        for (List<double[]> r : rings) {
            shade.add(shiftRingXY(r, dx, dy));
        }
        return new ShadeGeometryResult(rings, shade);
    }
}
