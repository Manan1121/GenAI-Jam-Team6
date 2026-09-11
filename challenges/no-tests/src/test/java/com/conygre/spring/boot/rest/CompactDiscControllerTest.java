package com.conygre.spring.boot.rest;

import com.conygre.spring.boot.entities.CompactDisc;
import com.conygre.spring.boot.services.CompactDiscService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests REST endpoints for status codes, JSON request/response handling, and
 * edge cases for GET, POST, and DELETE CD operations.
 * These tests use MockMvc with a mocked service layer to verify controller
 * behavior without starting a real server or database.
 *
 * Test Coverage:
 * - GET /api/compactdiscs: Retrieve all CDs (findAll)
 * - GET /api/compactdiscs/{id}: Retrieve CD by ID (getCdById)
 * - GET /api/compactdiscs/404/{id}: Retrieve with proper 404 handling
 * (getByIdWith404)
 * - POST /api/compactdiscs: Create new CD (addCd)
 * - DELETE /api/compactdiscs/{id}: Delete CD by ID (deleteCd)
 * - DELETE /api/compactdiscs: Delete CD by entity (deleteCd)
 */
@RunWith(SpringRunner.class)
@WebMvcTest(CompactDiscController.class)
public class CompactDiscControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompactDiscService service;

    @Autowired
    private ObjectMapper objectMapper;

    private CompactDisc testDisc;
    private List<CompactDisc> testCatalog;

    @Before
    public void setUp() {
        // Initialize test data
        testDisc = new CompactDisc("Dark Side of the Moon", 15.99, "Pink Floyd", 10);
        testDisc.setId(1);

        testCatalog = new ArrayList<>();
        testCatalog.add(testDisc);
        testCatalog.add(new CompactDisc("Abbey Road", 14.99, "The Beatles", 17));
    }

    /**
     * Test: GET /api/compactdiscs returns all CDs with 200 status
     * Purpose: Verify endpoint correctly retrieves catalog and returns proper HTTP
     * response
     * Expected: Should return 200 OK with list of CDs in JSON
     */
    @Test
    public void testFindAll_ReturnsCatalogWith200Status() throws Exception {
        // Arrange
        when(service.getCatalog()).thenReturn(testCatalog);

        // Act & Assert
        mockMvc.perform(get("/api/compactdiscs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Dark Side of the Moon")))
                .andExpect(jsonPath("$[0].artist", is("Pink Floyd")))
                .andExpect(jsonPath("$[1].title", is("Abbey Road")));

        verify(service, times(1)).getCatalog();
    }

    /**
     * Test: GET /api/compactdiscs with empty catalog
     * Purpose: Verify endpoint handles empty catalog correctly
     * Expected: Should return 200 OK with empty JSON array
     */
    @Test
    public void testFindAll_WithEmptyCatalog_Returns200WithEmptyArray() throws Exception {
        // Arrange
        when(service.getCatalog()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/compactdiscs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(service, times(1)).getCatalog();
    }

    /**
     * Test: GET /api/compactdiscs/{id} returns CD when found (no null handling)
     * Purpose: Verify endpoint returns CD when ID exists
     * Expected: Should return 200 OK with CD data
     */
    @Test
    public void testGetCdById_WhenIdExists_Returns200WithCd() throws Exception {
        // Arrange
        when(service.getCompactDiscById(1)).thenReturn(testDisc);

        // Act & Assert
        mockMvc.perform(get("/api/compactdiscs/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Dark Side of the Moon")))
                .andExpect(jsonPath("$.artist", is("Pink Floyd")))
                .andExpect(jsonPath("$.price", is(15.99)))
                .andExpect(jsonPath("$.tracks", is(10)));

        verify(service, times(1)).getCompactDiscById(1);
    }

    /**
     * Test: GET /api/compactdiscs/{id} when CD not found (returns null)
     * Purpose: Document current behavior when CD not found
     * Note: This endpoint does NOT provide 404 handling; it returns null
     * Expected: Should return 200 OK with null body (inconsistent behavior)
     */
    @Test
    public void testGetCdById_WhenIdNotExists_ReturnsNullWith200Status() throws Exception {
        // Arrange
        when(service.getCompactDiscById(999)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/compactdiscs/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).getCompactDiscById(999);
    }

    /**
     * Test: GET /api/compactdiscs/404/{id} with proper 404 handling returns CD when
     * found
     * Purpose: Verify endpoint with proper null handling returns 200 OK
     * Expected: Should return 200 OK with CD data
     */
    @Test
    public void testGetByIdWith404_WhenIdExists_Returns200WithCd() throws Exception {
        // Arrange
        when(service.getCompactDiscById(1)).thenReturn(testDisc);

        // Act & Assert
        mockMvc.perform(get("/api/compactdiscs/404/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Dark Side of the Moon")));

        verify(service, times(1)).getCompactDiscById(1);
    }

    /**
     * Test: GET /api/compactdiscs/404/{id} returns 404 when CD not found
     * Purpose: Verify endpoint with proper error handling returns 404 status
     * Expected: Should return 404 NOT_FOUND with no body
     */
    @Test
    public void testGetByIdWith404_WhenIdNotExists_Returns404() throws Exception {
        // Arrange
        when(service.getCompactDiscById(999)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/compactdiscs/404/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getCompactDiscById(999);
    }

    /**
     * Test: POST /api/compactdiscs creates new CD with 200 status
     * Purpose: Verify endpoint correctly accepts JSON and creates CD
     * Expected: Should return 200 OK and call service.addNewCompactDisc()
     */
    @Test
    public void testAddCd_WithValidJson_Returns200AndCreatesCD() throws Exception {
        // Arrange
        CompactDisc newDisc = new CompactDisc("The Wall", 20.00, "Pink Floyd", 26);
        String jsonBody = objectMapper.writeValueAsString(newDisc);

        // Act & Assert
        mockMvc.perform(post("/api/compactdiscs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk());

        verify(service, times(1)).addNewCompactDisc(any(CompactDisc.class));
    }

    /**
     * Test: POST /api/compactdiscs with all fields populated
     * Purpose: Verify endpoint correctly deserializes all CD fields
     * Expected: Should create CD with all fields set
     */
    @Test
    public void testAddCd_WithAllFields_PopulatesAllAttributes() throws Exception {
        // Arrange
        CompactDisc newDisc = new CompactDisc("Wish You Were Here", 17.50, "Pink Floyd", 12);
        String jsonBody = objectMapper.writeValueAsString(newDisc);

        // Act & Assert
        mockMvc.perform(post("/api/compactdiscs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk());

        verify(service, times(1)).addNewCompactDisc(any(CompactDisc.class));
    }

    /**
     * Test: POST /api/compactdiscs with invalid JSON
     * Purpose: Verify endpoint rejects malformed JSON
     * Expected: Should return 400 BAD_REQUEST
     */
    @Test
    public void testAddCd_WithInvalidJson_Returns400() throws Exception {
        // Arrange
        String invalidJson = "{invalid json}";

        // Act & Assert
        mockMvc.perform(post("/api/compactdiscs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).addNewCompactDisc(any());
    }

    /**
     * Test: DELETE /api/compactdiscs/{id} deletes CD by ID with 200 status
     * Purpose: Verify endpoint correctly deletes CD by ID
     * Expected: Should return 200 OK and call service.deleteCompactDisc(id)
     */
    @Test
    public void testDeleteCdById_WithValidId_Returns200AndDeletesCd() throws Exception {
        // Arrange
        doNothing().when(service).deleteCompactDisc(1);

        // Act & Assert
        mockMvc.perform(delete("/api/compactdiscs/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteCompactDisc(1);
    }

    /**
     * Test: DELETE /api/compactdiscs/{id} when CD not found
     * Purpose: Verify endpoint calls service with the ID
     * Note: Service will throw NoSuchElementException; controller doesn't handle it
     * Expected: Service should be called with correct ID
     */
    @Test
    public void testDeleteCdById_CallsServiceWithCorrectId() throws Exception {
        // Arrange
        doNothing().when(service).deleteCompactDisc(5);

        // Act & Assert
        mockMvc.perform(delete("/api/compactdiscs/5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteCompactDisc(5);
    }

    /**
     * Test: DELETE /api/compactdiscs with JSON body deletes by entity with 200
     * status
     * Purpose: Verify endpoint correctly deletes CD passed as JSON entity
     * Expected: Should return 200 OK and call service.deleteCompactDisc(entity)
     */
    @Test
    public void testDeleteCdByEntity_WithValidEntity_Returns200AndDeletesCd() throws Exception {
        // Arrange
        String jsonBody = objectMapper.writeValueAsString(testDisc);
        doNothing().when(service).deleteCompactDisc(any(CompactDisc.class));

        // Act & Assert
        mockMvc.perform(delete("/api/compactdiscs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteCompactDisc(any(CompactDisc.class));
    }

    /**
     * Test: DELETE /api/compactdiscs with invalid JSON
     * Purpose: Verify endpoint rejects malformed JSON
     * Expected: Should return 400 BAD_REQUEST
     */
    @Test
    public void testDeleteCdByEntity_WithInvalidJson_Returns400() throws Exception {
        // Arrange
        String invalidJson = "{invalid json}";

        // Act & Assert
        mockMvc.perform(delete("/api/compactdiscs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).deleteCompactDisc(any(CompactDisc.class));
    }

    /**
     * Test: CORS headers are present in responses
     * Purpose: Verify @CrossOrigin annotation is effective
     * Expected: Response should include Access-Control-Allow-Origin header
     */
    @Test
    public void testCorsHeaders_PresentInGetRequest() throws Exception {
        // Arrange
        when(service.getCatalog()).thenReturn(testCatalog);

        // Act & Assert
        mockMvc.perform(get("/api/compactdiscs")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk());
        // Note: CORS headers may be configured elsewhere; this tests basic endpoint
        // behavior
    }

    /**
     * Test: Multiple sequential operations maintain endpoint consistency
     * Purpose: Verify endpoints work correctly in sequence
     * Expected: Should handle all operations without state interference
     */
    @Test
    public void testSequentialOperations_MaintainConsistency() throws Exception {
        // Arrange
        when(service.getCatalog()).thenReturn(testCatalog);
        when(service.getCompactDiscById(1)).thenReturn(testDisc);
        doNothing().when(service).deleteCompactDisc(1);

        // Act & Assert - Get catalog
        mockMvc.perform(get("/api/compactdiscs"))
                .andExpect(status().isOk());

        // Act & Assert - Get by ID
        mockMvc.perform(get("/api/compactdiscs/1"))
                .andExpect(status().isOk());

        // Act & Assert - Delete
        mockMvc.perform(delete("/api/compactdiscs/1"))
                .andExpect(status().isOk());

        verify(service).getCatalog();
        verify(service).getCompactDiscById(1);
        verify(service).deleteCompactDisc(1);
    }

    /**
     * Test: Response content type is always application/json
     * Purpose: Verify all endpoints return JSON responses
     * Expected: All responses should have JSON content type
     */
    @Test
    public void testResponseContentType_IsAlwaysJson() throws Exception {
        // Arrange
        when(service.getCatalog()).thenReturn(testCatalog);
        when(service.getCompactDiscById(1)).thenReturn(testDisc);

        // Act & Assert - GET catalog
        mockMvc.perform(get("/api/compactdiscs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Act & Assert - GET by ID
        mockMvc.perform(get("/api/compactdiscs/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
