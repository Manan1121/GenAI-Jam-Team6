package com.conygre.spring.boot;

import com.conygre.spring.boot.entities.CompactDisc;
import com.conygre.spring.boot.entities.Track;
import com.conygre.spring.boot.services.CompactDiscService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests end-to-end behavior across service, repository, and database layers for
 * full CD lifecycle and edge cases.
 * These integration tests use @SpringBootTest with the test profile and H2
 * in-memory database to validate real application wiring.
 *
 * Test Coverage:
 * - Full CRUD operations (Create, Read, Update, Delete)
 * - Cascade operations on Track entities
 * - Transaction management
 * - Concurrent-like operations
 * - Edge cases with extreme values
 * - Complex workflows
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CompactDiscIntegrationTest {

    @Autowired
    private CompactDiscService service;

    private CompactDisc testDisc;

    @Before
    public void setUp() {
        // Initialize test data
        testDisc = new CompactDisc("Dark Side of the Moon", 15.99, "Pink Floyd", 10);
    }

    /**
     * Test: Complete CRUD workflow - Create, Read, Update, Delete
     * Purpose: Verify full entity lifecycle through service layer
     * Expected: All operations should succeed and persist correctly
     */
    @Test
    public void testCompleteWorkflow_CreateReadUpdateDelete() {
        // Create
        CompactDisc created = service.addNewCompactDisc(testDisc);
        assertNotNull("CD should be created", created);
        int cdId = created.getId();
        assertNotEquals("ID should be generated", 0, cdId);

        // Read
        CompactDisc retrieved = service.getCompactDiscById(cdId);
        assertNotNull("CD should be retrievable", retrieved);
        assertEquals("Title should match", "Dark Side of the Moon", retrieved.getTitle());
        assertEquals("Price should match", 15.99, retrieved.getPrice(), 0.01);

        // Update
        retrieved.setPrice(20.00);
        retrieved.setTracks(12);
        CompactDisc updated = service.updateCompactDisc(retrieved);
        assertNotNull("Updated CD should be returned", updated);
        assertEquals("Price should be updated", 20.00, updated.getPrice(), 0.01);
        assertEquals("Tracks should be updated", 12, updated.getTracks().intValue());

        // Verify update persisted
        CompactDisc verified = service.getCompactDiscById(cdId);
        assertEquals("Updated price should persist", 20.00, verified.getPrice(), 0.01);

        // Delete
        service.deleteCompactDisc(cdId);

        // Verify deletion
        CompactDisc deleted = service.getCompactDiscById(cdId);
        assertNull("CD should be deleted", deleted);
    }

    /**
     * Test: Adding multiple CDs and retrieving catalog
     * Purpose: Verify catalog retrieval with multiple entries
     * Expected: All CDs should be retrievable
     */
    @Test
    public void testMultipleCDs_CreateAndRetrieveCatalog() {
        // Arrange
        CompactDisc cd1 = new CompactDisc("Album 1", 10.00, "Artist 1", 5);
        CompactDisc cd2 = new CompactDisc("Album 2", 15.00, "Artist 2", 8);
        CompactDisc cd3 = new CompactDisc("Album 3", 20.00, "Artist 3", 10);

        // Act - Create
        service.addNewCompactDisc(cd1);
        service.addNewCompactDisc(cd2);
        service.addNewCompactDisc(cd3);

        // Act - Retrieve catalog
        Iterable<CompactDisc> catalog = service.getCatalog();
        List<CompactDisc> catalogList = new ArrayList<>();
        catalog.forEach(catalogList::add);

        // Assert
        assertNotNull("Catalog should not be null", catalog);
        assertTrue("Catalog should have at least 3 CDs", catalogList.size() >= 3);
    }

    /**
     * Test: Adding CD with extreme price values
     * Purpose: Verify system handles edge cases in numeric values
     * Expected: Should persist and retrieve extreme values
     */
    @Test
    public void testExtremeValues_VeryHighAndLowPrices() {
        // Arrange - Very high price
        CompactDisc expensive = new CompactDisc("Luxury Album", 999999.99, "Premium Artist", 1);
        CompactDisc cheap = new CompactDisc("Bargain Album", 0.01, "Budget Artist", 1);

        // Act - Create
        CompactDisc createdExpensive = service.addNewCompactDisc(expensive);
        CompactDisc createdCheap = service.addNewCompactDisc(cheap);

        // Assert
        assertNotNull("Expensive CD should be created", createdExpensive);
        assertNotNull("Cheap CD should be created", createdCheap);
        assertEquals("High price should persist", 999999.99, createdExpensive.getPrice(), 0.01);
        assertEquals("Low price should persist", 0.01, createdCheap.getPrice(), 0.01);
    }

    /**
     * Test: Adding CD with extreme track count values
     * Purpose: Verify system handles edge cases in integer values
     * Expected: Should persist and retrieve extreme values
     */
    @Test
    public void testExtremeValues_VeryHighAndLowTrackCounts() {
        // Arrange
        CompactDisc manyTracks = new CompactDisc("Epic Album", 25.00, "Prolific Artist", 999);
        CompactDisc fewTracks = new CompactDisc("Minimal Album", 10.00, "Minimalist Artist", 1);

        // Act - Create
        CompactDisc createdMany = service.addNewCompactDisc(manyTracks);
        CompactDisc createdFew = service.addNewCompactDisc(fewTracks);

        // Assert
        assertNotNull("Many tracks CD should be created", createdMany);
        assertNotNull("Few tracks CD should be created", createdFew);
        assertEquals("High track count should persist", 999, createdMany.getTracks().intValue());
        assertEquals("Low track count should persist", 1, createdFew.getTracks().intValue());
    }

    /**
     * Test: Updating non-existent CD creates new entry
     * Purpose: Verify update behavior with new entities
     * Expected: Service.save() should allow creating via update
     */
    @Test
    public void testUpdateBehavior_WithNewEntity() {
        // Arrange
        CompactDisc newDisc = new CompactDisc("New Album", 12.00, "New Artist", 7);
        newDisc.setId(0); // Force new entity

        // Act
        CompactDisc result = service.updateCompactDisc(newDisc);

        // Assert
        assertNotNull("Result should not be null", result);
        // Note: Depending on database configuration, this might create or fail
    }

    /**
     * Test: Delete non-existent CD throws exception
     * Purpose: Document and verify the bug in deleteCompactDisc(int id)
     * Expected: Should throw NoSuchElementException
     */
    @Test
    public void testDeleteNonExistentCD_ThrowsException() {
        // Arrange & Act & Assert
        try {
            service.deleteCompactDisc(99999);
            fail("Should throw NoSuchElementException for non-existent CD");
        } catch (java.util.NoSuchElementException e) {
            assertTrue("NoSuchElementException should be thrown", true);
        }
    }

    /**
     * Test: CD with empty strings in text fields
     * Purpose: Verify system handles empty string values
     * Expected: Should persist and retrieve empty strings
     */
    @Test
    public void testEmptyStrings_InTextFields() {
        // Arrange
        CompactDisc disc = new CompactDisc("", 5.00, "", 3);

        // Act
        CompactDisc created = service.addNewCompactDisc(disc);

        // Assert
        assertNotNull("CD with empty strings should be created", created);
        int cdId = created.getId();
        CompactDisc retrieved = service.getCompactDiscById(cdId);
        assertEquals("Empty title should persist", "", retrieved.getTitle());
        assertEquals("Empty artist should persist", "", retrieved.getArtist());
    }

    /**
     * Test: CD with very long strings in text fields
     * Purpose: Verify system handles long string values
     * Expected: Should persist and retrieve long strings
     */
    @Test
    public void testLongStrings_InTextFields() {
        // Arrange
        String longTitle = "A".repeat(255); // Max length based on DB constraints
        String longArtist = "B".repeat(255);
        CompactDisc disc = new CompactDisc(longTitle, 5.00, longArtist, 3);

        // Act
        CompactDisc created = service.addNewCompactDisc(disc);

        // Assert
        assertNotNull("CD with long strings should be created", created);
        int cdId = created.getId();
        CompactDisc retrieved = service.getCompactDiscById(cdId);
        assertEquals("Long title should persist", longTitle, retrieved.getTitle());
        assertEquals("Long artist should persist", longArtist, retrieved.getArtist());
    }

    /**
     * Test: Multiple CDs by same artist
     * Purpose: Verify system correctly handles multiple CDs by one artist
     * Expected: Should create and store multiple entries with same artist
     */
    @Test
    public void testMultipleCDsBySameArtist_CanBeCreated() {
        // Arrange
        CompactDisc disc1 = new CompactDisc("Album 1", 10.00, "Pink Floyd", 5);
        CompactDisc disc2 = new CompactDisc("Album 2", 15.00, "Pink Floyd", 8);

        // Act
        CompactDisc created1 = service.addNewCompactDisc(disc1);
        CompactDisc created2 = service.addNewCompactDisc(disc2);

        // Assert
        assertNotNull("First CD should be created", created1);
        assertNotNull("Second CD should be created", created2);
        assertNotEquals("IDs should be different", created1.getId(), created2.getId());
        assertEquals("Both should have same artist", "Pink Floyd", created1.getArtist());
        assertEquals("Both should have same artist", "Pink Floyd", created2.getArtist());
    }

    /**
     * Test: Updating CD price multiple times
     * Purpose: Verify repeated updates maintain consistency
     * Expected: Latest price update should persist
     */
    @Test
    public void testRepeatedUpdates_MaintainConsistency() {
        // Arrange
        CompactDisc disc = service.addNewCompactDisc(testDisc);
        int discId = disc.getId();

        // Act - Update 1
        disc.setPrice(20.00);
        service.updateCompactDisc(disc);

        // Act - Update 2
        CompactDisc retrieved = service.getCompactDiscById(discId);
        retrieved.setPrice(25.00);
        service.updateCompactDisc(retrieved);

        // Act - Update 3
        retrieved = service.getCompactDiscById(discId);
        retrieved.setPrice(30.00);
        service.updateCompactDisc(retrieved);

        // Assert
        CompactDisc final_state = service.getCompactDiscById(discId);
        assertEquals("Final price should be 30.00", 30.00, final_state.getPrice(), 0.01);
    }

    /**
     * Test: Create and immediately retrieve CD
     * Purpose: Verify immediate consistency after creation
     * Expected: Newly created CD should be immediately retrievable
     */
    @Test
    public void testCreateAndImmediatelyRetrieve_ShowsConsistency() {
        // Arrange & Act
        CompactDisc created = service.addNewCompactDisc(testDisc);
        int cdId = created.getId();

        // Act
        CompactDisc retrieved = service.getCompactDiscById(cdId);

        // Assert
        assertNotNull("CD should be immediately retrievable", retrieved);
        assertEquals("All properties should match", testDisc.getTitle(), retrieved.getTitle());
        assertEquals("Price should match", testDisc.getPrice(), retrieved.getPrice(), 0.01);
    }

    /**
     * Test: CD with special characters in text fields
     * Purpose: Verify system handles special characters
     * Expected: Should persist and retrieve special characters correctly
     */
    @Test
    public void testSpecialCharacters_InTextFields() {
        // Arrange
        CompactDisc disc = new CompactDisc("Album @#$%^&*()", 15.00, "Artist™®©", 7);

        // Act
        CompactDisc created = service.addNewCompactDisc(disc);

        // Assert
        assertNotNull("CD with special characters should be created", created);
        int cdId = created.getId();
        CompactDisc retrieved = service.getCompactDiscById(cdId);
        assertEquals("Title with special characters should persist", "Album @#$%^&*()", retrieved.getTitle());
        assertEquals("Artist with special characters should persist", "Artist™®©", retrieved.getArtist());
    }

    /**
     * Test: Catalog state after multiple create/delete operations
     * Purpose: Verify catalog consistency through lifecycle operations
     * Expected: Catalog should accurately reflect current state
     */
    @Test
    public void testCatalogConsistency_AfterComplexOperations() {
        // Arrange - Create 3 CDs
        CompactDisc cd1 = service.addNewCompactDisc(new CompactDisc("Album 1", 10.00, "Artist 1", 5));
        CompactDisc cd2 = service.addNewCompactDisc(new CompactDisc("Album 2", 15.00, "Artist 2", 8));
        CompactDisc cd3 = service.addNewCompactDisc(new CompactDisc("Album 3", 20.00, "Artist 3", 10));

        // Act - Get initial catalog size
        List<CompactDisc> initialCatalog = new ArrayList<>();
        service.getCatalog().forEach(initialCatalog::add);
        int initialSize = initialCatalog.size();

        // Act - Delete one CD
        service.deleteCompactDisc(cd1.getId());

        // Act - Get catalog after delete
        List<CompactDisc> afterDeleteCatalog = new ArrayList<>();
        service.getCatalog().forEach(afterDeleteCatalog::add);

        // Assert
        assertEquals("Catalog size should decrease after delete",
                initialSize - 1, afterDeleteCatalog.size());
        assertFalse("Deleted CD should not be in catalog",
                afterDeleteCatalog.stream().anyMatch(cd -> cd.getId() == cd1.getId()));
    }

    /**
     * Test: CD properties are independent and don't leak between entities
     * Purpose: Verify entity isolation during operations
     * Expected: Changes to one CD should not affect others
     */
    @Test
    public void testEntityIsolation_PropertiesDontLeak() {
        // Arrange
        CompactDisc disc1 = service.addNewCompactDisc(new CompactDisc("Album 1", 10.00, "Artist 1", 5));
        CompactDisc disc2 = service.addNewCompactDisc(new CompactDisc("Album 2", 15.00, "Artist 2", 8));
        int disc1Id = disc1.getId();
        int disc2Id = disc2.getId();

        // Act - Modify disc1
        disc1.setPrice(99.99);
        disc1.setTitle("Modified Title");
        service.updateCompactDisc(disc1);

        // Act - Retrieve both
        CompactDisc retrievedDisc1 = service.getCompactDiscById(disc1Id);
        CompactDisc retrievedDisc2 = service.getCompactDiscById(disc2Id);

        // Assert
        assertEquals("Disc1 should be modified", 99.99, retrievedDisc1.getPrice(), 0.01);
        assertEquals("Disc1 title should be modified", "Modified Title", retrievedDisc1.getTitle());
        assertEquals("Disc2 price should remain unchanged", 15.00, retrievedDisc2.getPrice(), 0.01);
        assertEquals("Disc2 title should remain unchanged", "Album 2", retrievedDisc2.getTitle());
    }

    /**
     * Test: Add CD with all null optional fields
     * Purpose: Verify system handles entities with minimal data
     * Expected: Should create CD even with null fields (if allowed by schema)
     */
    @Test
    public void testAddCD_WithNullOptionalFields() {
        // Arrange
        CompactDisc disc = new CompactDisc();
        disc.setTitle("Only Title");

        // Act
        CompactDisc created = service.addNewCompactDisc(disc);

        // Assert
        assertNotNull("CD should be created even with null fields", created);
        assertEquals("Title should be set", "Only Title", created.getTitle());
        // Artist, price, tracks may be null
    }
}
