/***********************************************************************
* Copyright (c) 2013-2016 Commonwealth Computer Research, Inc.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Apache License, Version 2.0
* which accompanies this distribution and is available at
* http://www.opensource.org/licenses/apache2.0.php.
*************************************************************************/

package org.apache.spark.sql

import java.awt.geom.AffineTransform

import com.vividsolutions.jts.geom._
import org.apache.spark.sql.udaf.ConvexHull
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.GeodeticCalculator
import org.geotools.referencing.crs.DefaultGeographicCRS
import org.geotools.referencing.operation.transform.AffineTransform2D

object SQLSpatialFunctions {
  // Geometry constructors

  // Geometry editors
  val ST_Translate: (Geometry, Double, Double) => Geometry =
    (g, deltaX, deltaY) => translate(g, deltaX, deltaY)

  // Spatial relationships
  val ST_Contains:   (Geometry, Geometry) => Boolean = (geom1, geom2) => geom1.contains(geom2)
  val ST_Covers:     (Geometry, Geometry) => Boolean = (geom1, geom2) => geom1.covers(geom2)
  val ST_Crosses:    (Geometry, Geometry) => Boolean = (geom1, geom2) => geom1.crosses(geom2)
  val ST_Disjoint:   (Geometry, Geometry) => Boolean = (geom1, geom2) => geom1.disjoint(geom2)
  val ST_Equals:     (Geometry, Geometry) => Boolean = (geom1, geom2) => geom1.equals(geom2)
  val ST_Intersects: (Geometry, Geometry) => Boolean = (geom1, geom2) => geom1.intersects(geom2)
  val ST_Overlaps:   (Geometry, Geometry) => Boolean = (geom1, geom2) => geom1.overlaps(geom2)
  val ST_Touches:    (Geometry, Geometry) => Boolean = (geom1, geom2) => geom1.touches(geom2)
  val ST_Within:     (Geometry, Geometry) => Boolean = (geom1, geom2) => geom1.within(geom2)

  val ST_Centroid: Geometry => Point = g => g.getCentroid
  val ST_DistanceSpheroid: (Geometry, Geometry) => java.lang.Double = (s, e) => fastDistance(s, e)

  // Geometry Processing
  val ch = new ConvexHull

  def registerFunctions(sqlContext: SQLContext): Unit = {
    // Register geometry accessors
    SQLSpatialAccessorFunctions.registerAccessorFunctions(sqlContext)

    // Register geometry editors
    sqlContext.udf.register("st_translate", ST_Translate)

    // Register spatial relationships
    sqlContext.udf.register("st_contains"    , ST_Contains)
    sqlContext.udf.register("st_covers"      , ST_Covers)
    sqlContext.udf.register("st_crosses"     , ST_Crosses)
    sqlContext.udf.register("st_disjoint"    , ST_Disjoint)
    sqlContext.udf.register("st_equals"      , ST_Equals)
    sqlContext.udf.register("st_intersects"  , ST_Intersects)
    sqlContext.udf.register("st_overlaps"    , ST_Overlaps)
    sqlContext.udf.register("st_touches"     , ST_Touches)
    sqlContext.udf.register("st_within"      , ST_Within)

    sqlContext.udf.register("st_centroid"      , ST_Centroid)
    sqlContext.udf.register("st_distanceSpheroid"  , ST_DistanceSpheroid)

    // Register geometry Processing
    sqlContext.udf.register("st_convexhull", ch)
  }

  @transient private val geoCalcs = new ThreadLocal[GeodeticCalculator] {
    override def initialValue(): GeodeticCalculator = new GeodeticCalculator(DefaultGeographicCRS.WGS84)
  }

  def fastDistance(s: Geometry, e: Geometry): Double = {
    val calc = geoCalcs.get()
    val c1 = s.getCentroid.getCoordinate
    calc.setStartingGeographicPoint(c1.x, c1.y)
    val c2 = e.getCentroid.getCoordinate
    calc.setDestinationGeographicPoint(c2.x, c2.y)
    calc.getOrthodromicDistance
  }

  def translate(g: Geometry, deltax: Double, deltay: Double): Geometry = {
    val affineTransform = AffineTransform.getTranslateInstance(deltax, deltay)
    val transform = new AffineTransform2D(affineTransform)
    JTS.transform(g, transform)
  }
}
