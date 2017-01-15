package de.hadizadeh.positioning.roommodel.model;

/**
 * Handles material types
 */
public class Material {
    /**
     * Default colors of the materials, if there is no texture
     */
    protected static String[][] defaultMaterialValues = new String[][]{{"eraser", "#ffffff", "#000000"},
            {"wall", "#696969", "#ffffff"}, {"furniture", "#d2b48c", "#000000"}, {"window", "#4169e1", "#ffffff"}, {"door", "#8b4513", "#ffffff"},
            {"stairs", "#87ceeb", "#000000"}, {"elevator", "#0000cd", "#ffffff"}, {"escalator", "#ffe4b5", "#000000"}};

    protected String name;
    protected String presentationName;
    protected String color;
    protected String textColor;
    protected Object texture;

    /**
     * Creates a material
     *
     * @param name material name
     */
    public Material(String name) {
        this.name = name;
    }

    /**
     * Creates a material
     *
     * @param name             name
     * @param presentationName presentation name which will be shown to the user
     * @param color            default background color if there is no texture
     * @param textColor        text color
     */
    public Material(String name, String presentationName, String color, String textColor) {
        this.name = name;
        this.presentationName = presentationName;
        this.color = color;
        this.textColor = textColor;
        loadTexture();
    }

    /**
     * Returns the name of the material
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the default background color
     *
     * @return default background color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the default background color
     *
     * @param color default background color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Returns the text color
     *
     * @return text color
     */
    public String getTextColor() {
        return textColor;
    }

    /**
     * Sets the text color
     *
     * @param textColor text color
     */
    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    /**
     * Returns the presentation name which will be shown to the user
     *
     * @return presentation name which will be shown to the user
     */
    public String getPresentationName() {
        return presentationName;
    }

    /**
     * Sets the presentation name which will be shown to the user
     *
     * @param presentationName presentation name which will be shown to the user
     */
    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

    /**
     * Returns the texture object (can be of different types on android and javafx)
     *
     * @return texture object
     */
    public Object getTexture() {
        return texture;
    }

    /**
     * Loads the texture from file
     */
    protected void loadTexture() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Material material = (Material) o;
        return name.equals(material.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Returns all default material colors
     *
     * @return default material colors
     */
    public static String[][] getDefaultMaterialValues() {
        return defaultMaterialValues;
    }
}
