package de.hadizadeh.positioning.roommodel;

import de.hadizadeh.positioning.roommodel.model.ContentElement;
import de.hadizadeh.positioning.roommodel.model.MapSegment;
import de.hadizadeh.positioning.roommodel.model.Material;

import java.util.ResourceBundle;

public abstract class Map {
    public static final int SEGMENTS_PER_METER = 2;

    public enum Position {
        TOP,BOTTOM,LEFT,RIGHT
    }
    protected int floors;
    protected int floorHeight;
    protected int rows;
    protected int columns;
    protected double width;
    protected double length;
    protected MapSegment[][][] mapSegments;


    protected Material selectedMaterial;
    protected ContentElement selectedContent;
    protected int currentFloor;


    public Map(int rows, int columns, int floors, int floorHeight) {
        this.floors = floors;
        this.floorHeight = floorHeight;
        this.rows = rows;
        this.columns = columns;
        this.length = rows * MapSegment.getSize();
        this.width = columns * MapSegment.getSize();

        mapSegments = new MapSegment[floors][rows][columns];
        for (int floor = 0; floor < floors; floor++) {
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    mapSegments[floor][row][column] = createMapSegment();
                }
            }
        }
    }

    public MapSegment[][][] getMapSegments() {
        return mapSegments;
    }

    public void setMapSegments(MapSegment[][][] mapSegments) {
        this.mapSegments = mapSegments;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getFloors() {
        return floors;
    }

    public int getFloorHeight() {
        return floorHeight;
    }

    public void setFloorHeight(int floorHeight) {
        this.floorHeight = floorHeight;
    }

    public int calculateRow(double y) {
        int row = (int) (y / MapSegment.getSize());
        if (row < rows) {
            return row;
        }
        return -1;
    }

    public int calculateColumn(double x) {
        int column = (int) (x / MapSegment.getSize());
        if (column < columns) {
            return column;
        }
        return -1;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setSelectedMaterial(Material selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
    }

    public void setSelectedContent(ContentElement selectedContent) {
        this.selectedContent = selectedContent;
    }

    public void resize() {
        this.length = rows * MapSegment.getSize();
        this.width = columns * MapSegment.getSize();
    }

    public void addFloor() {
        addFloor(-1);
    }

    public void addFloor(int copyFloorNumber) {
        floors++;
        MapSegment[][][] changedMapSegments = new MapSegment[floors][rows][columns];
        for (int floor = 0; floor < floors; floor++) {
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    if (floor >= floors - 1) {
                        if (copyFloorNumber < 0) {
                            changedMapSegments[floor][row][column] = createMapSegment();
                        } else {
                            changedMapSegments[floor][row][column] = copyMapSegment(mapSegments[copyFloorNumber][row][column]);
                        }
                    } else {
                        changedMapSegments[floor][row][column] = mapSegments[floor][row][column];
                    }
                }
            }
        }
        mapSegments = changedMapSegments;
    }

    public void removeFloor(int floorNumber) {
        floors--;
        MapSegment[][][] changedMapSegments = new MapSegment[floors][rows][columns];
        int changedFloorIndex = 0;
        for (int floor = 0; floor < floors + 1; floor++) {
            if (floor != floorNumber) {
                for (int row = 0; row < rows; row++) {
                    for (int column = 0; column < columns; column++) {
                        changedMapSegments[changedFloorIndex][row][column] = mapSegments[floor][row][column];
                    }
                }
                changedFloorIndex++;
            }
        }
        mapSegments = changedMapSegments;
    }

    public boolean addMapSegments(int amount, int position) {
        return changeMapSegments(true, amount, position);
    }

    public boolean removeMapSegments(int amount, int position) {
        return changeMapSegments(false, amount, position);
    }

    protected boolean changeMapSegments(boolean add, int amount, int position) {
        int changedRows = rows;
        int changedColumns = columns;
        int changeValue = amount;
        if (!add) {
            changeValue *= -1;
        }
        if(position < Position.LEFT.ordinal()) {
            changedRows += changeValue;
        } else {
            changedColumns += changeValue;
        }
        if(changedRows > 0 && changedColumns > 0) {
            int copyRowIndex = 0;
            int copyColumnIndex = 0;
            MapSegment[][][] changedMapSegments = new MapSegment[floors][changedRows][changedColumns];

            int maxRowDelimiter = changedRows;
            int maxColummDelimiter = changedColumns;
            int compareRows = rows;
            int compareColums = columns;
            if (!add) {
                maxRowDelimiter = rows;
                maxColummDelimiter = columns;
                compareRows = changedRows;
                compareColums = changedColumns;
            }
            for (int floor = 0; floor < floors; floor++) {
                copyRowIndex = 0;
                for (int row = 0; row < maxRowDelimiter; row++) {
                    copyColumnIndex = 0;
                    for (int column = 0; column < maxColummDelimiter; column++) {
                        if (position == Position.TOP.ordinal() && row < amount || position == Position.BOTTOM.ordinal() && row >= compareRows || position == Position.LEFT.ordinal() && column < amount || position == Position.RIGHT.ordinal() && column >= compareColums) {
                            if (add) {
                                changedMapSegments[floor][row][column] = createMapSegment();
                            }
                        } else {
                            if (add) {
                                changedMapSegments[floor][row][column] = copyMapSegment(mapSegments[floor][copyRowIndex][copyColumnIndex]);
                            } else {
                                changedMapSegments[floor][copyRowIndex][copyColumnIndex] = copyMapSegment(mapSegments[floor][row][column]);
                            }
                            copyColumnIndex++;
                        }
                    }
                    if (copyColumnIndex > 0) {
                        copyRowIndex++;
                    }
                }
            }
            rows = changedRows;
            columns = changedColumns;
            mapSegments = changedMapSegments;
            resize();
            return true;
        }
        return false;
    }

    protected void render(Object canvas, int startRow, int startColumn, int visibleRows, int visibleColumns) {
            int maxRow = startRow + visibleRows;
            int maxColumn = startColumn + visibleColumns;
            int renderRow = 0;
            int renderColumn = 0;
            for (int row = 0; row < rows; row++) {
                renderColumn = 0;
                if (row >= startRow && row <= maxRow) {
                    for (int column = 0; column < columns; column++) {
                        if (column >= startColumn && column <= maxColumn) {
                            mapSegments[currentFloor][row][column].render(canvas,row, column,  renderRow, renderColumn);
                            renderColumn++;
                        }
                    }
                    renderRow++;
                }
            }
    }

    public abstract Object getCanvas();

    public abstract void render();

    public abstract void render(int startRow, int startColumn, int visibleRows, int visibleColumns);

    public abstract void render(Object mapCanvas, int startRow, int startColumn, int visibleRows, int visibleColumns, Object currentXLb, Object currentYLb, Object currentMaterialLb, Object currentContentLb, Object resourceBundle);

    public abstract MapSegment createMapSegment();

    public abstract MapSegment copyMapSegment(MapSegment mapSegment);
}
