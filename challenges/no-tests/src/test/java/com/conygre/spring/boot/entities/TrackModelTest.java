package com.conygre.spring.boot.entities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the Track entity as a plain data model, including constructors, field
 * accessors, and cdId relationship values.
 * These are fast unit tests run directly with JUnit by instantiating Track
 * objects without Spring or database dependencies.
 *
 * Test Coverage:
 * - Default constructor: Creates entity with default values
 * - Parameterized constructors: Creates entity with specified values
 * - Getters/Setters: Verify all properties can be read and written
 * - CD ID relationship: Verify foreign key management
 */
public class TrackModelTest {

    private Track track;

    @Before
    public void setUp() {
        track = new Track();
    }

    /**
     * Test: Default constructor creates Track with default values
     * Purpose: Verify no-arg constructor works correctly
     * Expected: Should create entity with null/zero values
     */
    @Test
    public void testDefaultConstructor_CreatesEmptyEntity() {
        // Arrange & Act
        Track t = new Track();

        // Assert
        assertNotNull("Track should be created", t);
        assertNull("Default ID should be null", t.getId());
        assertNull("Default title should be null", t.getTitle());
        assertEquals("Default CD ID should be 0", 0, t.getCdId());
    }

    /**
     * Test: Parameterized constructor with id, title, cdId
     * Purpose: Verify constructor correctly initializes all properties
     * Expected: Should create entity with all values set correctly
     */
    @Test
    public void testParameterizedConstructor_WithIdTitleCdId() {
        // Arrange & Act
        Track t = new Track(5, "Track Title", 1);

        // Assert
        assertNotNull("Track should be created", t);
        assertEquals("ID should be set", 5, t.getId().intValue());
        assertEquals("Title should be set", "Track Title", t.getTitle());
        assertEquals("CD ID should be set", 1, t.getCdId());
    }

    /**
     * Test: Single-argument constructor with title only
     * Purpose: Verify convenience constructor for creating track with title
     * Expected: Should create entity with title set, ID and CD ID default
     */
    @Test
    public void testSingleArgConstructor_WithTitleOnly() {
        // Arrange & Act
        Track t = new Track("Single Song");

        // Assert
        assertNotNull("Track should be created", t);
        assertNull("ID should be null by default", t.getId());
        assertEquals("Title should be set", "Single Song", t.getTitle());
        assertEquals("CD ID should be 0 by default", 0, t.getCdId());
    }

    /**
     * Test: setId() and getId() methods
     * Purpose: Verify ID property can be set and retrieved
     * Expected: Should set and return the ID correctly
     */
    @Test
    public void testIdSetterGetter_WorksCorrectly() {
        // Arrange & Act
        track.setId(123);

        // Assert
        assertEquals("ID should be set to 123", 123, track.getId().intValue());
    }

    /**
     * Test: setTitle() and getTitle() methods
     * Purpose: Verify title property can be set and retrieved
     * Expected: Should set and return the title correctly
     */
    @Test
    public void testTitleSetterGetter_WorksCorrectly() {
        // Arrange & Act
        track.setTitle("Bohemian Rhapsody");

        // Assert
        assertEquals("Title should be set", "Bohemian Rhapsody", track.getTitle());
    }

    /**
     * Test: setTitle() with empty string
     * Purpose: Verify title can be set to empty string
     * Expected: Should accept and return empty string
     */
    @Test
    public void testTitleSetterGetter_WithEmptyString() {
        // Arrange & Act
        track.setTitle("");

        // Assert
        assertEquals("Title should be empty string", "", track.getTitle());
    }

    /**
     * Test: setTitle() with very long string
     * Purpose: Verify title can handle long strings
     * Expected: Should accept and return long string
     */
    @Test
    public void testTitleSetterGetter_WithLongString() {
        // Arrange
        String longTitle = "A".repeat(255);

        // Act
        track.setTitle(longTitle);

        // Assert
        assertEquals("Title should accept long string", longTitle, track.getTitle());
    }

    /**
     * Test: setCdId() and getCdId() methods
     * Purpose: Verify CD ID (foreign key) property can be set and retrieved
     * Expected: Should set and return the CD ID correctly
     */
    @Test
    public void testCdIdSetterGetter_WorksCorrectly() {
        // Arrange & Act
        track.setCdId(42);

        // Assert
        assertEquals("CD ID should be set to 42", 42, track.getCdId());
    }

    /**
     * Test: setCdId() with zero value
     * Purpose: Verify CD ID can be set to zero (unassigned)
     * Expected: Should accept and return zero
     */
    @Test
    public void testCdIdSetterGetter_WithZero() {
        // Arrange & Act
        track.setCdId(0);

        // Assert
        assertEquals("CD ID should be zero", 0, track.getCdId());
    }

    /**
     * Test: setCdId() with negative value
     * Purpose: Verify CD ID accepts negative values (though unusual)
     * Expected: Should accept and return negative value
     */
    @Test
    public void testCdIdSetterGetter_WithNegativeValue() {
        // Arrange & Act
        track.setCdId(-1);

        // Assert
        assertEquals("CD ID should accept negative value", -1, track.getCdId());
    }

    /**
     * Test: setCdId() with large value
     * Purpose: Verify CD ID can handle large numbers
     * Expected: Should accept and return large value
     */
    @Test
    public void testCdIdSetterGetter_WithLargeValue() {
        // Arrange & Act
        track.setCdId(999999);

        // Assert
        assertEquals("CD ID should accept large value", 999999, track.getCdId());
    }

    /**
     * Test: Multiple properties can be set independently
     * Purpose: Verify all properties maintain independence
     * Expected: Setting one property should not affect others
     */
    @Test
    public void testMultipleProperties_SetIndependently() {
        // Arrange & Act
        track.setId(10);
        track.setTitle("Test Track");
        track.setCdId(5);

        // Assert
        assertEquals("ID should be 10", 10, track.getId().intValue());
        assertEquals("Title should be set", "Test Track", track.getTitle());
        assertEquals("CD ID should be 5", 5, track.getCdId());
    }

    /**
     * Test: Constructor parameter mapping is correct
     * Purpose: Verify parameterized constructor parameter order
     * Note: Constructor signature is (int id, String title, int cdId)
     * Expected: Parameters should be assigned to correct fields
     */
    @Test
    public void testConstructorParameterMapping_IsCorrect() {
        // Arrange & Act
        Track t = new Track(15, "Song Title", 3);

        // Assert
        assertEquals("First param should be ID", 15, t.getId().intValue());
        assertEquals("Second param should be title", "Song Title", t.getTitle());
        assertEquals("Third param should be CD ID", 3, t.getCdId());
    }

    /**
     * Test: Setting property to null
     * Purpose: Verify properties can be set to null (if nullable in DB)
     * Expected: Should accept null values
     */
    @Test
    public void testPropertiesSetToNull_AreAccepted() {
        // Arrange & Act
        track.setId(null);
        track.setTitle(null);

        // Assert
        assertNull("ID should be null", track.getId());
        assertNull("Title should be null", track.getTitle());
    }

    /**
     * Test: Track can be created and fully initialized
     * Purpose: Verify full track lifecycle initialization
     * Expected: Should support complete initialization workflow
     */
    @Test
    public void testTrackFullInitialization_WorksCorrectly() {
        // Arrange & Act
        Track t = new Track();
        t.setId(1);
        t.setTitle("First Track");
        t.setCdId(1);

        // Assert
        assertEquals("ID should be set", 1, t.getId().intValue());
        assertEquals("Title should be set", "First Track", t.getTitle());
        assertEquals("CD ID should be set", 1, t.getCdId());
    }

    /**
     * Test: Track's relationship to CD through cdId
     * Purpose: Verify CD foreign key relationship is maintained
     * Expected: Track should maintain reference to parent CD via cdId
     */
    @Test
    public void testTrackRelationshipToCd_ThroughCdId() {
        // Arrange & Act
        int parentCdId = 7;
        track.setCdId(parentCdId);

        // Assert
        assertEquals("Track should reference CD with ID 7", parentCdId, track.getCdId());
    }

    /**
     * Test: Multiple tracks can have same CD ID
     * Purpose: Verify multiple tracks can belong to same CD
     * Expected: Different tracks can have same cdId value
     */
    @Test
    public void testMultipleTracks_WithSameCdId() {
        // Arrange
        Track track1 = new Track("Song 1");
        Track track2 = new Track("Song 2");
        int cdId = 5;

        // Act
        track1.setCdId(cdId);
        track2.setCdId(cdId);

        // Assert
        assertEquals("Track 1 should have CD ID 5", cdId, track1.getCdId());
        assertEquals("Track 2 should have CD ID 5", cdId, track2.getCdId());
    }

    /**
     * Test: ID type is Integer (nullable) rather than int
     * Purpose: Verify ID can be null (useful for new entities)
     * Expected: ID field should be Integer type and support null
     */
    @Test
    public void testIdType_IsInteger_SupportsNull() {
        // Arrange & Act
        Track t = new Track();
        t.setId(10);
        assertNotNull("ID should be set", t.getId());
        t.setId(null);

        // Assert
        assertNull("ID should support null", t.getId());
    }
}
