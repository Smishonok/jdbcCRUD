package com.valentinNikolaev.jdbcCrud.models;

import java.util.Scanner;

public class Region {
    private long   id;
    private String name;

    public Region(long id, String name) {
        this.id   = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataForSerialisation() {
        return "Region id:" + this.getId() + ";" + "Region name:" + this.getName() + ";";
    }

    public static Region parse(String regionData) {
        long id;
        String name;

        Scanner scanner = new Scanner(regionData);
        scanner.useDelimiter(";");

        scanner.findInLine("Region id:");
        if (scanner.hasNextLong()) {
            id = scanner.nextLong();
        } else {
            throw new IllegalArgumentException("Can`t parse region id.");
        }

        scanner.findInLine("Region name:");
        if (scanner.hasNext()) {
            name = scanner.next();
        } else {
            throw new IllegalArgumentException("Can`t parse region name.");
        }

        return new Region(id, name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (this.hashCode() != obj.hashCode()) {
            return false;
        }

        if (! (obj instanceof Region)) {
            return false;
        }

        Region comparingObj = (Region) obj;
        return this.name.equals(comparingObj.name);
    }

    @Override
    public String toString() {
        return "Region{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
