package ru.spbstu.gyboml.core.util;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.XmlReader;

class PolygonNode {

    private final Polygon polygon;
    private final PolygonShape polygonShape = new PolygonShape(); // cache heap object

    PolygonNode(XmlReader.Element data) {
        float[] vertices = Utility.parseFloatsCSV(data.getText());
        polygon = new Polygon(vertices);
    }

    PolygonShape getPolygonShape(float scaleX, float scaleY) {
        polygon.setScale(scaleX, scaleY);
        polygonShape.set(polygon.getTransformedVertices());
        return polygonShape;
    }

    void dispose()
    {
        polygonShape.dispose();
    }

}
