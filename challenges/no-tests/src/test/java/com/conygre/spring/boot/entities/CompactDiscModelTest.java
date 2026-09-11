package com.conygre.spring.boot.entities;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the CompactDisc entity as a data model, covering constructors,
 * getters/setters, and track list relationship behavior.
 * These are direct JUnit unit tests that validate object state changes without
 * using Spring context or database access.
 *
 * Test Coverage:
 * - Default constructor: Creates entity with default values
 * - Parameterized constructor: Creates entity with specified values
 * - Getters/Setters: Verify all properties can be read and written
 * - OneToMany relationship: Verify Track collection management
 */
public class CompactDiscModelTest {

    private CompactDisc testDisc;

    @Before
    public void setUp() {
        testDisc = new CompactDisc();
    }

    /**
     * Test: Default constructor creates CompactDisc with default values
     * Purpose: Verify no-arg constructor works correctly
     * Expected: Should create entity with null/zero values
     */
    @Test
    public void testDefaultConstructor_CreatesEmptyEntity() {
        // Arrange & Act
        CompactDisc disc = new CompactDisc();

        // Assert
        assertNotNull("CompactDisc should be created", disc);
        assertEquals("Default ID should be 0", 0, disc.getId());
        assertNull("Default title should be null", disc.getTitle());
        assertNull("Default artist should be null", disc.getArtist());
        assertNull("Default price should be null", disc.getPrice());
        assertNull("Default tracks should be null", disc.getTracks());
        assertNotNull("TrackTitles list should be initialized", disc.getTrackTitles());
        assertTrue("TrackTitles list should be empty", disc.getTrackTitles().isEmpty());
    }

    /**
     * Test: Parameterized constructor with title, price, artist, tracks
     * Purpose: Verify constructor correctly initializes all properties
     * Expected: Should create entity with all values set correctly
     */
    @Test
    public void testParameterizedConstructor_InitializesAllFields() {
        // Arrange & Act
        CompactDisc disc = new CompactDisc("Dark Side of the Moon", 15.99, "Pink Floyd", 10);

        // Assert
        assertNotNull("CompactDisc should be created", disc);
        assertEquals("Title should be set", "Dark Side of the Moon", disc.getTitle());
        assertEquals("Price should be set", 15.99, disc.getPrice(), 0.01);
        assertEquals("Artist should be set", "Pink Floyd", disc.getArtist());
        assertEquals("Tracks should be set", 10, disc.getTracks().intValue());
    }

    /**
     * Test: setId() and getId() methods
     * Purpose: Verify ID property can be set and retrieved
     * Expected: Should set and return the ID correctly
     */
    @Test
    public void testIdSetterGetter_WorksCorrectly() {
        // Arrange & Act
        testDisc.setId(42);

        // Assert
        assertEquals("ID should be set to 42", 42, testDisc.getId());
    }

    /**
     * Test: setTitle() and getTitle() methods
     * Purpose: Verify title property can be set and retrieved
     * Expected: Should set and return the title correctly
     */
    @Test
    public void testTitleSetterGetter_WorksCorrectly() {
        // Arrange & Act
        testDisc.setTitle("The Wall");

        // Assert
        assertEquals("Title should be set", "The Wall", testDisc.getTitle());
    }

    /**
     * Test: setTitle() with empty string
     * Purpose: Verify title can be set to empty string
     * Expected: Should accept and return empty string
     */
    @Test
    public void testTitleSetterGetter_WithEmptyString() {
        // Arrange & Act
        testDisc.setTitle("");

        // Assert
        assertEquals("Title should be empty string", "", testDisc.getTitle());
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
        testDisc.setTitle(longTitle);

        // Assert
        assertEquals("Title should accept long string", longTitle, testDisc.getTitle());
    }

    /**
     * Test: setArtist() and getArtist() methods
     * Purpose: Verify artist property can be set and retrieved
     * Expected: Should set and return the artist correctly
     */
    @Test
    public void testArtistSetterGetter_WorksCorrectly() {
        // Arrange & Act
        testDisc.setArtist("The Beatles");

        // Assert
        assertEquals("Artist should be set", "The Beatles", testDisc.getArtist());
    }

    /**
     * Test: setPrice() and getPrice() methods
     * Purpose: Verify price property can be set and retrieved
     * Expected: Should set and return the price correctly
     */
    @Test
    public void testPriceSetterGetter_WorksCorrectly() {
        // Arrange & Act
        testDisc.setPrice(19.99);

        // Assert
        assertEquals("Price should be set", 19.99, testDisc.getPrice(), 0.01);
    }

    /**
     * Test: setPrice() with zero value
     * Purpose: Verify price can be set to zero
     * Expected: Should accept and return zero
     */
    @Test
    public void testPriceSetterGetter_WithZero() {
        // Arrange & Act
        testDisc.setPrice(0.0);

        // Assert
        assertEquals("Price should be zero", 0.0, testDisc.getPrice(), 0.01);
    }

    /**
     * Test: setPrice() with negative value
     * Purpose: Verify price accepts negative values (no validation at model level)
     * Expected: Should accept and return negative value
     */
    @Test
    public void testPriceSetterGetter_WithNegativeValue() {
        // Arrange & Act
        testDisc.setPrice(-10.0);

        // Assert
        assertEquals("Price should accept negative value", -10.0, testDisc.getPrice(), 0.01);
    }

    /**
     * Test: setPrice() with very large value
     * Purpose: Verify price can handle large numbers
     * Expected: Should accept and return large value
     */
    @Test
    public void testPriceSetterGetter_WithLargeValue() {
        // Arrange & Act
        testDisc.setPrice(999999.99);

        // Assert
        assertEquals("Price should accept large value", 999999.99, testDisc.getPrice(), 0.01);
    }

    /**
     * Test: setTracks() and getTracks() methods
     * Purpose: Verify tracks property can be set and retrieved
     * Expected: Should set and return the tracks count correctly
     */
    @Test
    public void testTracksSetterGetter_WorksCorrectly() {
        // Arrange & Act
        testDisc.setTracks(15);

        // Assert
        assertEquals("Tracks should be set", 15, testDisc.getTracks().intValue());
    }

    /**
     * Test: setTracks() with zero
     * Purpose: Verify tracks can be set to zero
     * Expected: Should accept and return zero
     */
    @Test
    public void testTracksSetterGetter_WithZero() {
        // Arrange & Act
        testDisc.setTracks(0);

        // Assert
        assertEquals("Tracks should be zero", 0, testDisc.getTracks().intValue());
    }

    /**
     * Test: setTracks() with negative value
     * Purpose: Verify tracks accepts negative values (no validation at model level)
     * Expected: Should accept and return negative value
     */
    @Test
    public void testTracksSetterGetter_WithNegativeValue() {
        // Arrange & Act
        testDisc.setTracks(-5);

        // Assert
        assertEquals("Tracks should accept negative value", -5, testDisc.getTracks().intValue());
    }

    /**
     * Test: setTracks() with very large value
     * Purpose: Verify tracks can handle large numbers
     * Expected: Should accept and return large value
     */
    @Test
    public void testTracksSetterGetter_WithLargeValue() {
        // Arrange & Act
        testDisc.setTracks(99999);

        // Assert
        assertEquals("Tracks should accept large value", 99999, testDisc.getTracks().intValue());
    }

    /**
     * Test: getTrackTitles() returns initialized empty list
     * Purpose: Verify OneToMany relationship is properly initialized
     * Expected: Should return empty ArrayList, not null
     */
    @Test
    public void testGetTrackTitles_ReturnsInitializedList() {
        // Act
        List<Track> tracks = testDisc.getTrackTitles();

        // Assert
        assertNotNull("Track list should not be null", tracks);
        assertTrue("Track list should be empty initially", tracks.isEmpty());
    }

    /**
     * Test: Adding tracks to CompactDisc
     * Purpose: Verify OneToMany relationship allows adding tracks
     * Expected: Should add track to the list and maintain reference
     */
    @Test
    public void testAddTracks_ToCompactDisc() {
        // Arrange
        testDisc.setId(1);
        Track track1 = new Track("Track 1");
        track1.setCdId(1);
        Track track2 = new Track("Track 2");
        track2.setCdId(1);

        // Act
        testDisc.getTrackTitles().add(track1);
        testDisc.getTrackTitles().add(track2);

        // Assert
        assertEquals("Should have 2 tracks", 2, testDisc.getTrackTitles().size());
        assertTrue("Track 1 should be in list", testDisc.getTrackTitles().contains(track1));
        assertTrue("Track 2 should be in list", testDisc.getTrackTitles().contains(track2));
    }

    /**
     * Test: Removing tracks from CompactDisc
     * Purpose: Verify OneToMany relationship allows removing tracks
     * Expected: Should remove track from the list
     */
    @Test
    public void testRemoveTracks_FromCompactDisc() {
        // Arrange
        Track track1 = new Track("Track 1");
        testDisc.getTrackTitles().add(track1);

        // Act
        testDisc.getTrackTitles().remove(track1);

        // Assert
        assertTrue("Track list should be empty after removal", testDisc.getTrackTitles().isEmpty());
    }

    /**
     * Test: Multiple properties can be set independently
     * Purpose: Verify all properties maintain independence
     * Expected: Setting one property should not affect others
     */
    @Test
    public void testMultipleProperties_SetIndependently() {
        // Arrange & Act
        testDisc.setId(1);
        testDisc.setTitle("Test Album");
        testDisc.setArtist("Test Artist");
        testDisc.setPrice(12.50);
        testDisc.setTracks(8);

        // Assert
        assertEquals("ID should be 1", 1, testDisc.getId());
        assertEquals("Title should match", "Test Album", testDisc.getTitle());
        assertEquals("Artist should match", "Test Artist", testDisc.getArtist());
        assertEquals("Price should match", 12.50, testDisc.getPrice(), 0.01);
        assertEquals("Tracks should match", 8, testDisc.getTracks().intValue());
    }

    /**
     * Test: Constructor assigns to correct variables
     * Purpose: Verify parameterized constructor parameter order is correct
     * Note: Constructor signature is (String title, double price, String artist,
     * int tracks)
     * Expected: Parameters should be assigned to correct fields
     */
    @Test
    public void testConstructorParameterMapping_IsCorrect() {
        // Arrange & Act
        CompactDisc disc = new CompactDisc("Album Title", 25.50, "Artist Name", 12);

        // Assert
        assertEquals("First param should be title", "Album Title", disc.getTitle());
        assertEquals("Second param should be price", 25.50, disc.getPrice(), 0.01);
        assertEquals("Third param should be artist", "Artist Name", disc.getArtist());
        assertEquals("Fourth param should be tracks", 12, disc.getTracks().intValue());
    }

    /**
     * Test: Setting property to null
     * Purpose: Verify properties can be set to null (if nullable in DB)
     * Expected: Should accept null values
     */
    @Test
    public void testPropertiesSetToNull_AreAccepted() {
        // Arrange & Act
        testDisc.setTitle("Original");
        testDisc.setTitle(null);
        testDisc.setArtist(null);
        testDisc.setPrice(null);
        testDisc.setTracks(null);

        // Assert
        assertNull("Title should be null", testDisc.getTitle());
        assertNull("Artist should be null", testDisc.getArtist());
        assertNull("Price should be null", testDisc.getPrice());
        assertNull("Tracks should be null", testDisc.getTracks());
    }
}
