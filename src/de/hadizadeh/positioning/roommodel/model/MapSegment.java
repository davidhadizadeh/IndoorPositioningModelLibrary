package de.hadizadeh.positioning.roommodel.model;

/**
 * Handles a single map segment of a map, stores data and visualizes them
 */
public abstract class MapSegment {
    /**
     * coleor of the outer stroke
     */
    public static String strokeColor = "#A8A8A8";
    /**
     * width of the outer stroke line
     */
    public static double strokeLineWidth = 0.4;
    /**
     * color of a stoke with medium width
     */
    public static String mediumStrokeColor = "#333333";
    /**
     * color of a stoke with bold width
     */
    public static String boldStrokeColor = "#000000";
    /**
     * size of a stoke with bold width
     */
    public static double boldStrokeLineWidth = 2.0;
    /**
     * Size of a content symbol in relation to the segment
     */
    public static double contentSizeFactor = 1.6;


    private static double minSize = 10;
    private static double maxSize = 100;

    /**
     * Size of a map segment
     */
    protected static double size = 30;
    protected Material material;
    protected ContentElement content;

    /**
     * Creates a map segment
     */
    public MapSegment() {

    }

    /**
     * Creates a new map segment out of an existing map segment and copies all data from the existing map segment
     *
     * @param copy existing map segment
     */
    public MapSegment(MapSegment copy) {
        this.material = copy.material;
        this.content = copy.content;
    }

    /**
     * Renders the map segment
     *
     * @param graphic        graphic object to paint
     * @param originalRow    row number of the map segment
     * @param originalColumn column number of the map segment
     * @param renderRow      row position of the map segment (can be scrolled elsewhere)
     * @param renderColumn   column position of the map segment (can be scrolled elsewhere)
     */
    public abstract void render(Object graphic, int originalRow, int originalColumn, int renderRow, int renderColumn);

    /**
     * Returns the material of the full map segment
     *
     * @return material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material of the full map segment
     *
     * @param material material
     */
    public void setMaterial(Material material) {
        this.material = material;
    }


    /**
     * Returns the connected content of the map segment
     *
     * @return content
     */
    public ContentElement getContent() {
        return content;
    }

    /**
     * Sets the connected content of the map segment
     *
     * @param content content
     */
    public void setContent(ContentElement content) {
        this.content = content;
    }

    /**
     * Returns the size of every map segments
     *
     * @return size
     */
    public static double getSize() {
        return size;
    }

    /**
     * Sets the size of every map segments
     *
     * @param size size
     */
    public static void setSize(double size) {
        MapSegment.size = size;
        if (MapSegment.size < MapSegment.minSize) {
            MapSegment.size = MapSegment.minSize;
        } else if (MapSegment.size > MapSegment.maxSize) {
            MapSegment.size = MapSegment.maxSize;
        }
    }

    /**
     * Returns the minimum size of possible zooming
     *
     * @return minimum size
     */
    public static double getMinSize() {
        return minSize;
    }

    /**
     * Returns the maximum size of possible zooming
     *
     * @return maximum size
     */
    public static double getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the minimum size of possible zooming
     *
     * @param minSize minimum size
     */
    public static void setMinSize(double minSize) {
        MapSegment.minSize = minSize;
    }

    /**
     * Sets the maximum size of possible zooming
     *
     * @param maxSize maximum size
     */
    public static void setMaxSize(double maxSize) {
        MapSegment.maxSize = maxSize;
    }
}
