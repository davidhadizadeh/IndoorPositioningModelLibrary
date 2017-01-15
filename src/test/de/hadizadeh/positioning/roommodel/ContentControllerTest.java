package test.de.hadizadeh.positioning.roommodel;

import de.hadizadeh.positioning.model.MappingPoint;
import de.hadizadeh.positioning.roommodel.ContentController;
import de.hadizadeh.positioning.roommodel.model.ContentElement;
import junit.framework.TestCase;
import org.junit.Test;

public class ContentControllerTest extends TestCase {

    private ContentController contentController;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        contentController = new ContentController();
        contentController.preloadAllContents("");
    }

    @Test(expected = NullPointerException.class)
    public void testGetContentElement() throws Exception {
        assertNull(contentController.getContentElement(0));
    }

    public void testRemoveAllPositions() throws Exception {
        contentController.removeAllPositions();
    }

    public void testAddPosition() throws Exception {
        contentController.addPosition(new ContentElement(), new MappingPoint(0, 0, 0));
    }

    public void testGetContent() throws Exception {
        assertNull(contentController.getContent(new MappingPoint(0, 0, 0)));
    }

    public void testGetContents() throws Exception {
        assertNull(contentController.getContents("en"));
    }

    public void testSave() throws Exception {
        contentController.save("");
    }
}