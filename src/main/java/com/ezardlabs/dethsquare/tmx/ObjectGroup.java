package com.ezardlabs.dethsquare.tmx;

/**
 * Created by Benjamin on 2016-04-26.
 */
public class ObjectGroup {
    private String name;
    private TMXObject[] objects;

    public ObjectGroup(String name, TMXObject[] objects) {
        this.name = name;
        this.objects = objects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TMXObject[] getObjects() {
        return objects;
    }

    public void setObjects(TMXObject[] objects) {
        this.objects = objects;
    }
}
