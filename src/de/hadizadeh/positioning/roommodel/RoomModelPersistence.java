package de.hadizadeh.positioning.roommodel;


import de.hadizadeh.positioning.content.exceptions.ContentPersistenceException;
import de.hadizadeh.positioning.model.MappingPoint;
import de.hadizadeh.positioning.roommodel.model.ContentElement;
import de.hadizadeh.positioning.roommodel.model.MapSegment;
import de.hadizadeh.positioning.roommodel.model.Material;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Handles the persistence layer of room models
 */
public abstract class RoomModelPersistence {

    /**
     * Saves a room model map to a file
     *
     * @param filename filename
     * @param map      room model map
     * @throws IOException                 if the file could not be created
     * @throws ContentPersistenceException if the content of the map is incorrect
     */
    public void save(String filename, Map map) throws IOException, ContentPersistenceException {
        FileOutputStream fos = new FileOutputStream(new File(filename));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("x\ty\tz\tmaterial");
        bw.newLine();
        MapSegment[][][] mapSegments = map.getMapSegments();
        int rows = map.getRows();
        int columns = map.getColumns();
        int floors = map.getFloors();
        int floorHeight = map.getFloorHeight();
        removeAllPositions();
        int zEnd = floors * floorHeight - floorHeight;
        for (int z = 0; z <= zEnd; z += floorHeight) {
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    String materialName = "";
                    if (mapSegments[z / floorHeight][row][column].getMaterial() != null) {
                        materialName = mapSegments[z / floorHeight][row][column].getMaterial().getName();
                    }
                    bw.write(column + "\t" + row + "\t" + z + "\t" + materialName);
                    if (!(z == zEnd && row == rows - 1 && column == columns - 1)) {
                        bw.newLine();
                    }
                    ContentElement content = mapSegments[z / floorHeight][row][column].getContent();
                    if (content != null) {
                        addPosition(content, new MappingPoint(column, row, z));
                    }
                }
            }
        }
        bw.close();
    }

    /**
     * Loads room model map data from a file to objects
     *
     * @param filename  persistence file name
     * @param materials available materials
     * @return room model map
     * @throws IOException                    if the file could not be read
     * @throws ArrayIndexOutOfBoundsException room model file is corrupted
     */
    public Map load(String filename, List<Material> materials) throws IOException, ArrayIndexOutOfBoundsException {
        BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
        String line;
        String lastLine = "";
        String[] parts;
        int floors = 0;
        int z = 0;
        int lastZ = -1;
        int floorHeight = 0;
        br.readLine();
        while ((line = br.readLine()) != null) {
            lastLine = line;
            parts = line.split("\t");
            z = Integer.parseInt(parts[2]);
            if (z != lastZ) {
                floorHeight = z - lastZ;
                floors++;
            }
            lastZ = z;
        }
        br.close();
        br = new BufferedReader(new FileReader(new File(filename)));
        parts = lastLine.split("\t");
        int columns = Integer.parseInt(parts[0]) + 1;
        int rows = Integer.parseInt(parts[1]) + 1;
        Map map = createMap(rows, columns, floors, floorHeight);
        MapSegment[][][] mapSegments = map.getMapSegments();

        int column = 0;
        int row = 0;
        String materialName = "";
        br.readLine();
        java.util.Map<String, Material> materialNames = new HashMap<String, Material>();
        for (Material material : materials) {
            materialNames.put(material.getName(), material);
        }
        MappingPoint mappingPoint = new MappingPoint(column, row, z);
        while ((line = br.readLine()) != null) {
            parts = line.split("\t");
            column = Integer.parseInt(parts[0]);
            row = Integer.parseInt(parts[1]);
            z = Integer.parseInt(parts[2]);
            if (parts.length > 3) {
                materialName = parts[3];
                mapSegments[z / floorHeight][row][column].setMaterial(materialNames.get(materialName));
            }
            mappingPoint.setX(column);
            mappingPoint.setY(row);
            mappingPoint.setZ(z);
            ContentElement content = getContent(mappingPoint);
            if (content != null) {
                mapSegments[z / floorHeight][row][column].setContent(content);
            }
        }
        map.setMapSegments(mapSegments);
        br.close();
        return map;
    }

    /**
     * Removes all connected positions from file
     */
    protected abstract void removeAllPositions();

    /**
     * Add a position to a content (connect)
     *
     * @param content      content
     * @param mappingPoint position (coordinates)
     */
    protected abstract void addPosition(ContentElement content, MappingPoint mappingPoint);

    /**
     * Loads a content of a position (coordinates)
     *
     * @param mappingPoint position (coordinates)
     * @return content
     */
    protected abstract ContentElement getContent(MappingPoint mappingPoint);

    /**
     * Creates a room model map of a defined size
     *
     * @param rows        amount of rows
     * @param columns     amount of columns
     * @param floors      amount of floors
     * @param floorHeight height of each floor
     * @return room model map
     */
    protected abstract Map createMap(int rows, int columns, int floors, int floorHeight);
}
