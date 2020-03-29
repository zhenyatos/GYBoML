package ru.spbstu.gyboml.core.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.utils.XmlReader;

class CircleNode {

    private final float x;
    private final float y;
    private final float r;

    // cache heap objects
    private final CircleShape circleShape = new CircleShape();
    private final Vector2 position = new Vector2();

    CircleNode(XmlReader.Element data) {
        x = data.getFloatAttribute("x");
        y = data.getFloatAttribute("y");
        r = data.getFloatAttribute("r");
    }

    CircleShape getCircleShape(float scale) {
        position.set(x * scale, y * scale);
        circleShape.setRadius(r * scale);
        circleShape.setPosition(position);
        return circleShape;
    }

    void dispose()
    {
        circleShape.dispose();
    }

}