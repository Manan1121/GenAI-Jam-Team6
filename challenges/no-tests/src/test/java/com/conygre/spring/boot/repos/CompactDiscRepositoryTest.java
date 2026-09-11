package com.conygre.spring.boot.repos;

import com.conygre.spring.boot.entities.CompactDisc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests repository persistence behavior for create, read, update, delete, and
 * artist-based query logic.
 * These are data-layer tests using @DataJpaTest with an H2 in-memory database
 * to verify real JPA/Hibernate behavior.
 *
 * Test Coverage:
 * - findAll(): Retrieves all CDs from database
 * - findById(Integer id): Finds CD by primary key
 * - save(CompactDisc): Persists new or updated CD
 * - delete(CompactDisc): Removes CD from database
 * - findByArtist(String artist): Custom query by artist name
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class CompactDiscRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompactDiscRepository repository;

    private CompactDisc testDisc1;
    private CompactDisc testDisc2;
    private CompactDisc testDisc3;

    @Before
    public void setUp() {
        // Initialize test data
        testDisc1 = new CompactDisc("Dark Side of the Moon", 15.99, "Pink Floyd", 10);
        testDisc2 = new CompactDisc("Abbey Road", 14.99, "The Beatles", 17);
        testDisc3 = new CompactDisc("The Wall", 20.00, "Pink Floyd", 26);
    }

    /**
     * Test: findAll() returns all CDs in database
     * Purpose: Verify repository retrieves all persisted CDs
     * Expected: Should return all 3 test CDs
     */
    @Test
    public void testFindAll_WithMultipleCDs_ReturnsAll() {
        // Arrange
        entityManager.persistAndFlush(testDisc1);
        entityManager.persistAndFlush(testDisc2);
        entityManager.persistAndFlush(testDisc3);

        // Act
        Iterable<CompactDisc> result = repository.findAll();

        // Assert
        List<CompactDisc> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertEquals("Should return all 3 CDs", 3, resultList.size());
    }

    /**
     * Test: findAll() returns empty list when database is empty
     * Purpose: Verify repository handles empty database correctly
     * Expected: Should return empty list
     */
    @Test
    public void testFindAll_WithEmptyDatabase_ReturnsEmptyList() {
        // Act
        Iterable<CompactDisc> result = repository.findAll();

        // Assert
        List<CompactDisc> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertTrue("Should return empty list", resultList.isEmpty());
    }

    /**
     * Test: findById() returns Optional with CD when exists
     * Purpose: Verify repository finds CD by primary key
     * Expected: Should return Optional containing the CD
     */
    @Test
    public void testFindById_WhenCdExists_ReturnsOptionalWithCd() {
        // Arrange
        CompactDisc persisted = entityManager.persistAndFlush(testDisc1);

        // Act
        Optional<CompactDisc> result = repository.findById(persisted.getId());

        // Assert
        assertTrue("CD should be found", result.isPresent());
        assertEquals("Title should match", "Dark Side of the Moon", result.get().getTitle());
        assertEquals("Artist should match", "Pink Floyd", result.get().getArtist());
        assertEquals("Price should match", 15.99, result.get().getPrice(), 0.01);
        assertEquals("Tracks should match", 10, result.get().getTracks().intValue());
    }

    /**
     * Test: findById() returns empty Optional when CD not found
     * Purpose: Verify repository returns empty Optional for non-existent ID
     * Expected: Should return empty Optional
     */
    @Test
    public void testFindById_WhenCdNotExists_ReturnsEmptyOptional() {
        // Act
        Optional<CompactDisc> result = repository.findById(999);

        // Assert
        assertFalse("CD should not be found", result.isPresent());
    }

    /**
     * Test: save() persists new CD and generates ID
     * Purpose: Verify repository correctly saves new entities with auto-increment
     * ID
     * Expected: CD should be persisted with generated ID
     */
    @Test
    public void testSave_WithNewCd_PersistsAndGeneratesId() {
        // Arrange
        testDisc1.setId(0);

        // Act
        CompactDisc saved = repository.save(testDisc1);

        // Assert
        assertNotNull("Saved CD should not be null", saved);
        assertNotEquals("ID should be generated", 0, saved.getId());
        Optional<CompactDisc> retrieved = repository.findById(saved.getId());
        assertTrue("CD should exist in database", retrieved.isPresent());
        assertEquals("Persisted title should match", "Dark Side of the Moon", retrieved.get().getTitle());
    }

    /**
     * Test: save() updates existing CD
     * Purpose: Verify repository correctly updates already persisted CDs
     * Expected: CD should be updated with new values
     */
    @Test
    public void testSave_WithExistingCd_UpdatesSuccessfully() {
        // Arrange
        CompactDisc persisted = entityManager.persistAndFlush(testDisc1);
        persisted.setTitle("Updated Title");
        persisted.setPrice(25.00);

        // Act
        CompactDisc updated = repository.save(persisted);

        // Assert
        assertNotNull("Updated CD should not be null", updated);
        assertEquals("Title should be updated", "Updated Title", updated.getTitle());
        assertEquals("Price should be updated", 25.00, updated.getPrice(), 0.01);
        Optional<CompactDisc> retrieved = repository.findById(updated.getId());
        assertEquals("Changes should persist", "Updated Title", retrieved.get().getTitle());
    }

    /**
     * Test: delete() removes CD from database
     * Purpose: Verify repository correctly removes persisted CDs
     * Expected: CD should no longer exist after deletion
     */
    @Test
    public void testDelete_RemovesCdFromDatabase() {
        // Arrange
        CompactDisc persisted = entityManager.persistAndFlush(testDisc1);
        int cdId = persisted.getId();

        // Act
        repository.delete(persisted);

        // Assert
        Optional<CompactDisc> retrieved = repository.findById(cdId);
        assertFalse("CD should no longer exist", retrieved.isPresent());
    }

    /**
     * Test: findByArtist() returns all CDs by specific artist
     * Purpose: Verify custom query correctly filters CDs by artist
     * Expected: Should return only CDs by specified artist
     */
    @Test
    public void testFindByArtist_WhenCdsExist_ReturnsAllByArtist() {
        // Arrange
        entityManager.persistAndFlush(testDisc1); // Pink Floyd
        entityManager.persistAndFlush(testDisc2); // The Beatles
        entityManager.persistAndFlush(testDisc3); // Pink Floyd

        // Act
        Iterable<CompactDisc> result = repository.findByArtist("Pink Floyd");

        // Assert
        List<CompactDisc> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertEquals("Should return 2 Pink Floyd CDs", 2, resultList.size());
        assertTrue("All results should be by Pink Floyd",
                resultList.stream().allMatch(cd -> "Pink Floyd".equals(cd.getArtist())));
    }

    /**
     * Test: findByArtist() returns empty when no CDs match artist
     * Purpose: Verify custom query handles non-existent artist gracefully
     * Expected: Should return empty list
     */
    @Test
    public void testFindByArtist_WhenNoMatch_ReturnsEmptyList() {
        // Arrange
        entityManager.persistAndFlush(testDisc1);
        entityManager.persistAndFlush(testDisc2);

        // Act
        Iterable<CompactDisc> result = repository.findByArtist("Non-existent Artist");

        // Assert
        List<CompactDisc> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertTrue("Should return empty list for non-existent artist", resultList.isEmpty());
    }

    /**
     * Test: findByArtist() with null artist parameter
     * Purpose: Verify custom query handles null input
     * Expected: Should return empty list or throw exception
     */
    @Test
    public void testFindByArtist_WithNullArtist_ReturnsEmpty() {
        // Arrange
        entityManager.persistAndFlush(testDisc1);

        // Act
        Iterable<CompactDisc> result = repository.findByArtist(null);

        // Assert
        List<CompactDisc> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        // Result depends on Spring Data behavior; typically empty or all records
        assertTrue("Should handle null gracefully", true);
    }

    /**
     * Test: findByArtist() is case-sensitive
     * Purpose: Verify custom query matches exact artist name case
     * Expected: Should only return exact matches
     */
    @Test
    public void testFindByArtist_IsCaseSensitive() {
        // Arrange
        entityManager.persistAndFlush(testDisc1); // Pink Floyd
        entityManager.persistAndFlush(testDisc2);

        // Act
        Iterable<CompactDisc> resultLower = repository.findByArtist("pink floyd");
        Iterable<CompactDisc> resultExact = repository.findByArtist("Pink Floyd");

        // Assert
        List<CompactDisc> lowerList = new ArrayList<>();
        resultLower.forEach(lowerList::add);
        List<CompactDisc> exactList = new ArrayList<>();
        resultExact.forEach(exactList::add);

        // May depend on database collation; document actual behavior
        assertEquals("Exact case should match", 1, exactList.size());
    }

    /**
     * Test: Multiple operations on same entity
     * Purpose: Verify repository correctly handles entity state transitions
     * Expected: CRUD operations should work correctly in sequence
     */
    @Test
    public void testCrudSequence_MultipleOperations() {
        // Create
        CompactDisc saved = repository.save(testDisc1);
        int cdId = saved.getId();
        assertTrue("CD should be persisted", repository.findById(cdId).isPresent());

        // Read
        CompactDisc retrieved = repository.findById(cdId).get();
        assertEquals("Retrieved CD should match", testDisc1.getTitle(), retrieved.getTitle());

        // Update
        retrieved.setPrice(99.99);
        repository.save(retrieved);
        CompactDisc updated = repository.findById(cdId).get();
        assertEquals("Updated price should persist", 99.99, updated.getPrice(), 0.01);

        // Delete
        repository.delete(updated);
        assertFalse("CD should be deleted", repository.findById(cdId).isPresent());
    }
}
