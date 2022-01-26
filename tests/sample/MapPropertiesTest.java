package sample;

import org.junit.Test;

public class MapPropertiesTest {

    @Test
    public void load() {
        MapProperties properties = new MapProperties(true);

        System.out.println(properties.getWidth());
    }
}