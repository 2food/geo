(ns geo.jts
  "Wrapper for the vividsolutions JTS spatial library. Constructors for points,
  coordinate sequences, rings, polygons, multipolygons, and so on."
  (:import (com.vividsolutions.jts.geom Coordinate
                                        Point
                                        LinearRing
                                        Polygon
                                        MultiPolygon
                                        PrecisionModel
                                        GeometryFactory)))

(def ^GeometryFactory gf
  (GeometryFactory.))

(defn coordinate
  "Creates a Cooordinate."
  ([x y]
   (Coordinate. x y)))

(defn point
  "Creates a Point from a Coordinate, or an x,y pair."
  ([x y]
   (point (coordinate x y)))
  ([^Coordinate coordinate]
   (.createPoint gf coordinate)))

(defn coordinate-sequence
  "Given a list of Coordinates, generates a CoordinateSequence."
  [coordinates]
  (.. gf getCoordinateSequenceFactory create
    (into-array Coordinate coordinates)))

(defn linear-ring
  "Given a list of Coordinates, creates a LinearRing."
  [coordinates]
  (.createLinearRing gf (into-array Coordinate coordinates)))

(defn linear-ring-wkt
  "Makes a LinearRing from a WKT-style data structure: a flat sequence of
  coordinate pairs, e.g. [0 0, 1 0, 0 2, 0 0]"
  [coordinates]
  (->> coordinates
    (partition 2)
    (map (partial apply coordinate))
    linear-ring))

(defn polygon
  "Given a LinearRing shell, and a list of LinearRing holes, generates a
  polygon."
  ([shell]
   (polygon shell nil))
  ([shell holes]
   (.createPolygon gf shell (into-array LinearRing holes))))

(defn polygon-wkt
  "Generates a polygon from a WKT-style data structure: a sequence of
  [outer-ring hole1 hole2 ...], where outer-ring and each hole is a flat list
  of coordinate pairs, e.g.

  [[0 0 10 0 10 10 0 0]
   [1 1  9 1  9  9 1 1]]"
  [rings]
  (let [rings (map linear-ring-wkt rings)]
    (polygon (first rings) (rest rings))))

(defn multi-polygon
  "Given a list of polygons, generates a MultiPolygon."
  [polygons]
  (.createMultiPolygon gf (into-array Polygon polygons)))

(defn multi-polygon-wkt
  "Creates a MultiPolygon from a WKT-style data structure, e.g. [[[0 0 1 0 2 2
  0 0]] [5 5 10 10 6 2]]"
  [wkt]
  (multi-polygon (map polygon-wkt wkt)))
