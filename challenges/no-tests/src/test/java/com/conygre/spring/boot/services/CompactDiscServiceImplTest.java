package com.conygre.spring.boot.services;

import com.conygre.spring.boot.entities.CompactDisc;
import com.conygre.spring.boot.repos.CompactDiscRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests service-layer business logic for catalog retrieval, create/update, and
 * delete behavior, including not-found and null input paths.
 * These are isolated unit tests using JUnit and Mockito, with the repository
 * mocked so only CompactDiscServiceImpl logic is validated.
 *
 * Test Coverage:
 * - getCatalog(): Retrieves all CDs with transactional support
 * - getCompactDiscById(int id): Finds CD by ID (found and not found cases)
 * - addNewCompactDisc(CompactDisc disc): Creates new CD with forced id=0
 * - updateCompactDisc(CompactDisc disc): Updates existing CD
 * - deleteCompactDisc(int id): Deletes by ID (including bug case:
 * NoSuchElementException)
 * - deleteCompactDisc(CompactDisc disc): Deletes by entity
 */
@RunWith(MockitoJUnitRunner.class)
public class CompactDiscServiceImplTest {

    @Mock
    private CompactDiscRepository repository;

    @InjectMocks
    private CompactDiscServiceImpl service;

    private CompactDisc testDisc;

    @Before
    public void setUp() {
        // Initialize test data
        testDisc = new CompactDisc("Dark Side of the Moon", 15.99, "Pink Floyd", 10);
        testDisc.setId(1);
    }

    /**
     * Test: getCatalog() successfully returns all CDs
     * Purpose: Verify that service correctly retrieves catalog with transactional
     * support
     * Expected: Should return the iterable of CDs from repository
     */
    @Test
    public void testGetCatalog_ReturnsAllCDs() {
        // Arrange
        List<CompactDisc> mockCatalog = new ArrayList<>();
        mockCatalog.add(testDisc);
        mockCatalog.add(new CompactDisc("Abbey Road", 14.99, "The Beatles", 17));
        when(repository.findAll()).thenReturn(mockCatalog);

        // Act
        Iterable<CompactDisc> result = service.getCatalog();

        // Assert
        assertNotNull("Catalog should not be null", result);
        List<CompactDisc> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertEquals("Catalog should contain 2 CDs", 2, resultList.size());
        verify(repository, times(1)).findAll();
    }

    /**
     * Test: getCatalog() returns empty iterable when no CDs exist
     * Purpose: Verify service handles empty catalog gracefully
     * Expected: Should return empty list without error
     */
    @Test
    public void testGetCatalog_WithEmptyDatabase_ReturnsEmptyIterable() {
        // Arrange
        when(repository.findAll()).thenReturn(new ArrayList<>());

        // Act
        Iterable<CompactDisc> result = service.getCatalog();

        // Assert
        assertNotNull("Catalog should not be null even when empty", result);
        List<CompactDisc> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertTrue("Catalog should be empty", resultList.isEmpty());
        verify(repository, times(1)).findAll();
    }

    /**
     * Test: getCompactDiscById() returns CD when found
     * Purpose: Verify service retrieves and unwraps Optional correctly
     * Expected: Should return the CompactDisc entity
     */
    @Test
    public void testGetCompactDiscById_WhenIdExists_ReturnsCd() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.of(testDisc));

        // Act
        CompactDisc result = service.getCompactDiscById(1);

        // Assert
        assertNotNull("CD should be found", result);
        assertEquals("CD ID should match", 1, result.getId());
        assertEquals("CD title should match", "Dark Side of the Moon", result.getTitle());
        assertEquals("CD artist should match", "Pink Floyd", result.getArtist());
        verify(repository, times(1)).findById(1);
    }

    /**
     * Test: getCompactDiscById() returns null when CD not found
     * Purpose: Verify service returns null for non-existent ID (current behavior)
     * Note: This is inconsistent with fail-fast principle but documents actual
     * behavior
     * Expected: Should return null
     */
    @Test
    public void testGetCompactDiscById_WhenIdNotExists_ReturnsNull() {
        // Arrange
        when(repository.findById(999)).thenReturn(Optional.empty());

        // Act
        CompactDisc result = service.getCompactDiscById(999);

        // Assert
        assertNull("CD should not be found, returning null", result);
        verify(repository, times(1)).findById(999);
    }

    /**
     * Test: addNewCompactDisc() forces ID to 0 before saving
     * Purpose: Verify service generates new entities by clearing ID
     * Expected: ID should be set to 0, then saved with repository
     */
    @Test
    public void testAddNewCompactDisc_ForcesIdToZero_ThenSaves() {
        // Arrange
        CompactDisc newDisc = new CompactDisc("The Wall", 20.00, "Pink Floyd", 26);
        newDisc.setId(999); // Set non-zero ID
        CompactDisc savedDisc = new CompactDisc("The Wall", 20.00, "Pink Floyd", 26);
        savedDisc.setId(5);
        when(repository.save(any(CompactDisc.class))).thenReturn(savedDisc);

        // Act
        CompactDisc result = service.addNewCompactDisc(newDisc);

        // Assert
        assertEquals("CD ID should be forced to 0 before save", 0, newDisc.getId());
        assertNotNull("Should return saved CD", result);
        assertEquals("Returned CD should have generated ID", 5, result.getId());
        verify(repository, times(1)).save(newDisc);
    }

    /**
     * Test: addNewCompactDisc() with null input
     * Purpose: Verify service handles null input gracefully (defensive test)
     * Expected: Should throw NullPointerException or handle gracefully
     */
    @Test
    public void testAddNewCompactDisc_WithNullInput_ThrowsException() {
        // Act & Assert
        try {
            service.addNewCompactDisc(null);
            fail("Should throw exception for null input");
        } catch (Exception e) {
            assertTrue("Exception should occur for null input", true);
        }
    }

    /**
     * Test: updateCompactDisc() calls repository save
     * Purpose: Verify service correctly updates existing CD
     * Expected: Should call repository save with the CD entity
     */
    @Test
    public void testUpdateCompactDisc_CallsRepositorySave() {
        // Arrange
        testDisc.setTitle("Updated Title");
        testDisc.setPrice(18.99);
        when(repository.save(testDisc)).thenReturn(testDisc);

        // Act
        CompactDisc result = service.updateCompactDisc(testDisc);

        // Assert
        assertNotNull("Should return updated CD", result);
        assertEquals("Title should be updated", "Updated Title", result.getTitle());
        assertEquals("Price should be updated", 18.99, result.getPrice(), 0.01);
        verify(repository, times(1)).save(testDisc);
    }

    /**
     * Test: updateCompactDisc() with null input
     * Purpose: Verify service behavior with null parameter
     * Expected: Should handle or throw exception
     */
    @Test
    public void testUpdateCompactDisc_WithNullInput_ThrowsException() {
        // Arrange
        when(repository.save(null)).thenThrow(new NullPointerException());

        // Act & Assert
        try {
            service.updateCompactDisc(null);
            fail("Should throw exception for null input");
        } catch (Exception e) {
            assertTrue("Exception should occur for null input", true);
        }
    }

    /**
     * Test: deleteCompactDisc(int id) throws NoSuchElementException when CD not
     * found
     * Purpose: Document and test the BUG in the implementation
     * The bug: Uses .get() on Optional without checking if present
     * Expected: Should throw NoSuchElementException
     */
    @Test
    public void testDeleteCompactDiscById_WhenIdNotExists_ThrowsNoSuchElementException() {
        // Arrange
        when(repository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            service.deleteCompactDisc(999);
            fail("Should throw NoSuchElementException when CD not found");
        } catch (java.util.NoSuchElementException e) {
            // Expected behavior - this is the documented bug
            assertTrue("NoSuchElementException should be thrown", true);
        }
        verify(repository, times(1)).findById(999);
    }

    /**
     * Test: deleteCompactDisc(int id) successfully deletes when CD found
     * Purpose: Verify service correctly deletes CD by ID
     * Expected: Should find CD and call delete on repository
     */
    @Test
    public void testDeleteCompactDiscById_WhenIdExists_DeletesSuccessfully() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.of(testDisc));
        doNothing().when(repository).delete(testDisc);

        // Act
        service.deleteCompactDisc(1);

        // Assert
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).delete(testDisc);
    }

    /**
     * Test: deleteCompactDisc(CompactDisc disc) deletes by entity
     * Purpose: Verify service correctly deletes CD passed as entity
     * Expected: Should call repository delete with the entity
     */
    @Test
    public void testDeleteCompactDiscByEntity_DeletesSuccessfully() {
        // Arrange
        doNothing().when(repository).delete(testDisc);

        // Act
        service.deleteCompactDisc(testDisc);

        // Assert
        verify(repository, times(1)).delete(testDisc);
    }

    /**
     * Test: deleteCompactDisc(CompactDisc disc) with null entity
     * Purpose: Verify service handles null entity input
     * Expected: Should throw exception or handle gracefully
     */
    @Test
    public void testDeleteCompactDiscByEntity_WithNullEntity_ThrowsException() {
        // Arrange
        doThrow(new IllegalArgumentException()).when(repository).delete(null);

        // Act & Assert
        try {
            service.deleteCompactDisc((CompactDisc) null);
            fail("Should throw exception for null entity");
        } catch (Exception e) {
            assertTrue("Exception should occur for null entity", true);
        }
    }

    /**
     * Test: Multiple sequential operations maintain consistency
     * Purpose: Verify service state across multiple operations
     * Expected: Operations should not interfere with each other
     */
    @Test
    public void testMultipleOperations_MaintainConsistency() {
        // Arrange
        CompactDisc disc1 = new CompactDisc("Album1", 10.00, "Artist1", 5);
        CompactDisc disc2 = new CompactDisc("Album2", 15.00, "Artist2", 8);
        when(repository.findAll()).thenReturn(new ArrayList<>());
        when(repository.save(any(CompactDisc.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.findById(1)).thenReturn(Optional.of(disc1));

        // Act
        service.getCatalog();
        service.addNewCompactDisc(disc1);
        CompactDisc found = service.getCompactDiscById(1);

        // Assert
        assertNotNull("CD should be found after add", found);
        verify(repository, times(1)).findAll();
        verify(repository, times(1)).save(disc1);
        verify(repository, times(1)).findById(1);
    }
}
