package de.hadizadeh.positioning.roommodel.model;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a single content (not a content group)
 */
public class ContentElement {
    protected int contentNumber;
    protected String title;
    protected String description;
    protected String fullText;
    protected boolean imageUpdated;
    protected File imageFile;
    protected boolean audioUpdated;
    protected File audioFile;
    protected boolean videoUpdated;
    protected File videoFile;
    protected List<String> positions;
    protected String url;

    /**
     * Creates a content element
     */
    public ContentElement() {
        this.positions = new ArrayList<String>();
        this.imageUpdated = false;
        this.audioUpdated = false;
        this.videoUpdated = false;
    }

    /**
     * Creates a content element
     *
     * @param contentNumber identification number of the content
     */
    public ContentElement(int contentNumber) {
        this();
        this.contentNumber = contentNumber;
    }

    /**
     * Creates a new content out of an existing content and copies all data from the existing content
     *
     * @param copy existing content
     */
    public ContentElement(ContentElement copy) {
        this.contentNumber = copy.contentNumber;
        this.title = copy.title;
        this.description = copy.description;
        this.fullText = copy.fullText;
        this.imageUpdated = copy.imageUpdated;
        this.imageFile = copy.imageFile;
        this.audioUpdated = copy.audioUpdated;
        this.audioFile = copy.audioFile;
        this.videoUpdated = copy.videoUpdated;
        this.videoFile = copy.videoFile;
        this.positions = copy.positions;
        this.url = copy.url;
    }

    /**
     * Returns the content identification number
     *
     * @return content identification number
     */
    public int getContentNumber() {
        return contentNumber;
    }

    /**
     * Sets the content identification number
     *
     * @param contentNumber content identification number
     */
    public void setContentNumber(int contentNumber) {
        this.contentNumber = contentNumber;
    }

    /**
     * Returns the title of the content
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the content
     *
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns  the description of the content
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the content
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a long description text of the content
     *
     * @return long description text
     */
    public String getFullText() {
        return fullText;
    }

    /**
     * Sets the long description text of the content
     *
     * @param fullText long description text
     */
    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    /**
     * Delivers the information if the image has been updated
     *
     * @return true, if the image has been updated, else false
     */
    public boolean isImageUpdated() {
        return imageUpdated;
    }

    /**
     * Sets the information if the image has been updated
     *
     * @param imageUpdated true, if the image has been updated, else false
     */
    public void setImageUpdated(boolean imageUpdated) {
        this.imageUpdated = imageUpdated;
    }

    /**
     * Returns the image file of the content
     *
     * @return image file
     */
    public File getImageFile() {
        return imageFile;
    }

    /**
     * Sets the image file of the content
     *
     * @param imageFile image file
     */
    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * Delivers the information if the audio file has been updated
     *
     * @return true, if the audio file has been updated, else false
     */
    public boolean isAudioUpdated() {
        return audioUpdated;
    }

    /**
     * Sets the information if the audio file has been updated
     *
     * @param audioUpdated true, if the audio file has been updated, else false
     */
    public void setAudioUpdated(boolean audioUpdated) {
        this.audioUpdated = audioUpdated;
    }

    /**
     * Returns the audio file of the content
     *
     * @return audio file
     */
    public File getAudioFile() {
        return audioFile;
    }

    /**
     * Sets the audio file of the content
     *
     * @param audioFile audio file
     */
    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    /**
     * Delivers the information if the video file has been updated
     *
     * @return true, if the video file has been updated, else false
     */
    public boolean isVideoUpdated() {
        return videoUpdated;
    }

    /**
     * Sets the information if the video file has been updated
     *
     * @param videoUpdated true, if the video file has been updated, else false
     */
    public void setVideoUpdated(boolean videoUpdated) {
        this.videoUpdated = videoUpdated;
    }

    /**
     * Returns  video file of the content
     *
     * @return video file
     */
    public File getVideoFile() {
        return videoFile;
    }

    /**
     * Sets the video file of the content
     *
     * @param videoFile video file
     */
    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }

    /**
     * Returns the connected positions of the content
     *
     * @return connected position identifiers
     */
    public List<String> getPositions() {
        return positions;
    }

    /**
     * Sets the connected positions of the content
     *
     * @param positions connected position identifiers
     */
    public void setPositions(List<String> positions) {
        this.positions = positions;
    }

    /**
     * Returns the url of the content
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url of the content
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
