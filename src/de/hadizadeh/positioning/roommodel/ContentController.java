package de.hadizadeh.positioning.roommodel;


import de.hadizadeh.positioning.content.Content;
import de.hadizadeh.positioning.content.ContentList;
import de.hadizadeh.positioning.content.MappedContentManager;
import de.hadizadeh.positioning.content.exceptions.ContentPersistenceException;
import de.hadizadeh.positioning.controller.MappedPositionManager;
import de.hadizadeh.positioning.model.MappingPoint;
import de.hadizadeh.positioning.roommodel.model.ContentElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls the loading process and actions with content elements and content groups
 */
public class ContentController {
    /**
     * Defines the size of a content group
     */
    protected final int CONTENTS_PER_ELEMENT = 7;
    /**
     * Default language
     */
    protected String defaultLanguage = "en";
    protected MappedContentManager mappedContentManager;
    protected List<String> languages;
    protected java.util.Map<String, List<ContentElement>> contentElements;
    protected String currentLanguage;
    protected int currentContentIndex;

    /**
     * Loads all contents to a map. Should be called in a thread.
     *
     * @param workingDir directory where the content is stored
     * @throws ContentPersistenceException if the content could not be loaded
     */
    public void preloadAllContents(String workingDir) throws ContentPersistenceException {
        String contentDir = workingDir + File.separator + "content";
        contentElements = new HashMap<String, List<ContentElement>>();

        languages = new ArrayList<String>();
        mappedContentManager = null;
        if (new File(contentDir).exists()) {
            mappedContentManager = new MappedContentManager(new File(workingDir, "content.xml"));
            for (File file : (new File(contentDir)).listFiles()) {
                if (file.isDirectory()) {
                    languages.add(file.getName());
                }
            }
        }
        if (languages.isEmpty()) {
            languages.add(defaultLanguage);
        }

        if (mappedContentManager != null) {
            ContentList<Content> contents = mappedContentManager.getAllContents();
            int index = 0;
            ContentElement contentElement = null;
            for (Content content : contents) {
                for (String language : languages) {
                    if (!contentElements.containsKey(language)) {
                        contentElements.put(language, new ArrayList<ContentElement>());
                    }
                    if (index % CONTENTS_PER_ELEMENT == 0) {
                        ContentElement element = new ContentElement();
                        element.setContentNumber(index / CONTENTS_PER_ELEMENT + 1);
                        element.setPositions(content.getPositions());
                        contentElements.get(language).add(element);
                    }
                    contentElement = contentElements.get(language).get(index / (CONTENTS_PER_ELEMENT));
                    Content.ContentType contentType = content.getType();
                    String contentData = content.getData();
                    String languagePath = contentDir + File.separator + language + File.separator;
                    if (contentType.equals(Content.ContentType.TEXTFILE) && contentData.contains("-title")) {
                        contentElement.setTitle(FileManager.readTextFile(languagePath + contentData));
                    } else if (contentType.equals(Content.ContentType.TEXTFILE)) {
                        contentElement.setDescription(FileManager.readTextFile(languagePath + contentData));
                    } else if (contentType.equals(Content.ContentType.URL)) {
                        contentElement.setUrl(FileManager.readTextFile(languagePath + contentData));
                    } else if (contentType.equals(Content.ContentType.AUDIO)) {
                        contentElement.setAudioFile(new File(languagePath + contentData));
                    } else if (contentType.equals(Content.ContentType.MOVIE)) {
                        contentElement.setVideoFile(new File(languagePath + contentData));
                    } else if (contentType.equals(Content.ContentType.IMAGE)) {
                        contentElement.setImageFile(new File(languagePath + contentData));
                    } else if (contentType.equals(Content.ContentType.HTML_TEXTFILE)) {
                        contentElement.setFullText(FileManager.readTextFile(languagePath + contentData));
                    }
                }
                index++;
            }
        }
    }

    /**
     * Returns a single content element
     *
     * @param index index of the content element
     * @return content element
     */
    public ContentElement getContentElement(int index) {
        if (contentElements.containsKey(currentLanguage)) {
            return contentElements.get(currentLanguage).get(index);
        } else {
            return null;
        }
    }

    /**
     * Removes all connected positions
     */
    public void removeAllPositions() {
        for (Map.Entry<String, List<ContentElement>> language : contentElements.entrySet()) {
            for (ContentElement contentElement : language.getValue()) {
                contentElement.setPositions(new ArrayList<String>());
            }
        }
    }

    /**
     * Adds a position to a content element (creates the connection)
     *
     * @param content      content element
     * @param mappingPoint position (coordinates)
     */
    public void addPosition(ContentElement content, MappingPoint mappingPoint) {
        for (Map.Entry<String, List<ContentElement>> language : contentElements.entrySet()) {
            for (ContentElement contentElement : language.getValue()) {
                if (contentElement.getContentNumber() == content.getContentNumber()) {
                    contentElement.getPositions().add(MappedPositionManager.mappingPointToName(mappingPoint));
                }
            }
        }
    }

    /**
     * Returns a single content element by its position
     *
     * @param mappingPoint position (coordinates)
     * @return content element
     */
    public ContentElement getContent(MappingPoint mappingPoint) {
        String searchPosition = MappedPositionManager.mappingPointToName(mappingPoint);
        if (contentElements.entrySet().iterator().hasNext()) {
            Map.Entry<String, List<ContentElement>> firstLanguage = contentElements.entrySet().iterator().next();
            for (ContentElement contentElement : firstLanguage.getValue()) {
                for (String position : contentElement.getPositions()) {
                    if (position.equals(searchPosition)) {
                        return contentElement;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns all contents of a language
     *
     * @param language language for filtering
     * @return contents of the language
     */
    public List<ContentElement> getContents(String language) {
        if (contentElements.containsKey(language)) {
            return contentElements.get(language);
        } else if (contentElements.containsKey(defaultLanguage)) {
            return contentElements.get(defaultLanguage);
        } else {
            return null;
        }
    }

    /**
     * Saves all contents to the files and creates the content groups
     *
     * @param savePath directory where the content should be saved
     */
    public void save(String savePath) {
        try {
            File contentPathFile = new File(savePath + File.separator + "content");
            if (!contentPathFile.exists()) {
                contentPathFile.mkdir();
            }
            if (mappedContentManager == null) {
                mappedContentManager = new MappedContentManager(new File(savePath, "content.xml"));
            }
            mappedContentManager.removeAllContent();

            boolean contentManagerDataSaved = false;
            List<Content.ContentType> contentTypes = new ArrayList<Content.ContentType>();
            List<String> multipleData = new ArrayList<String>();
            List<String> positions = new ArrayList<String>();
            for (String language : contentElements.keySet()) {
                List<String> files = new ArrayList<String>();
                String path = contentPathFile.getAbsolutePath() + File.separator + language + File.separator;
                File pathFile = new File(path);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                for (ContentElement saveElement : contentElements.get(language)) {
                    String titleData = saveElement.getContentNumber() + "-title.txt";
                    String subtitleData = saveElement.getContentNumber() + "-subtitle.txt";
                    String urlData = saveElement.getContentNumber() + "-url.txt";
                    String imageData = saveElement.getContentNumber() + ".jpg";
                    String audioData = saveElement.getContentNumber() + ".mp3";
                    String videoData = saveElement.getContentNumber() + ".mp4";
                    String fullTextData = saveElement.getContentNumber() + ".txt";
                    if (!contentManagerDataSaved) {
                        mappedContentManager.addContent(Content.ContentType.TEXTFILE, titleData);
                        mappedContentManager.addContent(Content.ContentType.TEXTFILE, subtitleData);
                        mappedContentManager.addContent(Content.ContentType.URL, urlData);
                        mappedContentManager.addContent(Content.ContentType.IMAGE, imageData);
                        mappedContentManager.addContent(Content.ContentType.AUDIO, audioData);
                        mappedContentManager.addContent(Content.ContentType.MOVIE, videoData);
                        mappedContentManager.addContent(Content.ContentType.HTML_TEXTFILE, fullTextData);
                        for (String position : saveElement.getPositions()) {
                            prepareContentPosition(contentTypes, multipleData, positions, Content.ContentType.TEXTFILE, titleData, position);
                            prepareContentPosition(contentTypes, multipleData, positions, Content.ContentType.TEXTFILE, titleData, position);
                            prepareContentPosition(contentTypes, multipleData, positions, Content.ContentType.URL, titleData, position);
                            prepareContentPosition(contentTypes, multipleData, positions, Content.ContentType.IMAGE, titleData, position);
                            prepareContentPosition(contentTypes, multipleData, positions, Content.ContentType.AUDIO, titleData, position);
                            prepareContentPosition(contentTypes, multipleData, positions, Content.ContentType.MOVIE, titleData, position);
                            prepareContentPosition(contentTypes, multipleData, positions, Content.ContentType.HTML_TEXTFILE, titleData, position);
                        }
                        mappedContentManager.addPositions(contentTypes, multipleData, positions);
                    }

                    if (saveElement.getTitle() != null) {
                        writeFile(path + titleData, saveElement.getTitle());
                        files.add(titleData);
                    }
                    if (saveElement.getDescription() != null) {
                        writeFile(path + subtitleData, saveElement.getDescription());
                        files.add(subtitleData);
                    }
                    if (saveElement.getUrl() != null) {
                        writeFile(path + urlData, saveElement.getUrl());
                        files.add(urlData);
                    }
                    if (saveElement.getFullText() != null) {
                        writeFile(path + fullTextData, saveElement.getFullText());
                        files.add(fullTextData);
                    }
                    if (saveElement.isImageUpdated()) {
                        if (saveElement.getImageFile() != null) {
                            Files.copy(saveElement.getImageFile().toPath(), new File(path + imageData).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    if (saveElement.isAudioUpdated()) {
                        if (saveElement.getAudioFile() != null) {
                            Files.copy(saveElement.getAudioFile().toPath(), new File(path + audioData).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    if (saveElement.isVideoUpdated()) {
                        if (saveElement.getVideoFile() != null) {
                            Files.copy(saveElement.getVideoFile().toPath(), new File(path + videoData).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    if (saveElement.getImageFile() != null) {
                        files.add(imageData);
                    }
                    if (saveElement.getAudioFile() != null) {
                        files.add(audioData);
                    }
                    if (saveElement.getVideoFile() != null) {
                        files.add(videoData);
                    }
                }

                for (File file : new File(path).listFiles()) {
                    if (!files.contains(file.getName())) {
                        file.delete();
                    }
                }

                contentManagerDataSaved = true;
            }
            for (File folder : contentPathFile.listFiles()) {
                if (!languages.contains(folder.getName())) {
                    FileManager.removeDirectory(folder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helps the saving process by preparing the contents for saving
     *
     * @param contentTypes existing content types
     * @param multipleData existing data of the contents
     * @param positions    existing connected positions
     * @param type         new type of the content
     * @param titleData    new title data
     * @param position     new position
     */
    protected void prepareContentPosition(List<Content.ContentType> contentTypes, List<String> multipleData, List<String> positions, Content.ContentType type, String titleData, String position) {
        contentTypes.add(type);
        multipleData.add(titleData);
        positions.add(position);
    }

    /**
     * Calculates a new and free content number for the next content
     *
     * @return content number
     */
    protected int getNewContentNumber() {
        int max = 0;
        if (contentElements.get(currentLanguage) != null) {
            for (ContentElement contentElement : contentElements.get(currentLanguage)) {
                int contentNumber = contentElement.getContentNumber();
                if (contentNumber > max) {
                    max = contentNumber;
                }
            }
        }
        return max + 1;
    }

    /**
     * Writes a text string to a file
     *
     * @param file file name
     * @param text text string
     * @throws FileNotFoundException        if the file was not found
     * @throws UnsupportedEncodingException if the utf-8 enconding could not be established
     */
    protected void writeFile(String file, String text) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println(text);
        writer.close();
    }

    /**
     * Removes a file
     *
     * @param fileName file name
     * @return true if the file could be deleted, else false
     */
    protected boolean removeFile(String fileName) {
        File file = new File(fileName);
        return file.delete();
    }

    /**
     * Converts seconds into a formatted time string of minutes and seconds
     *
     * @param seconds seconds
     * @return formatted time string
     */
    protected String getFormattedTime(int seconds) {
        StringBuffer timeString = new StringBuffer();
        int hours = seconds / (60 * 60);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        if (hours > 0) {
            timeString.append(String.format("%02d", hours));
        }
        timeString.append(String.format("%02d", minutes)).append(":").append(String.format("%02d", seconds));
        return timeString.toString();
    }
}
