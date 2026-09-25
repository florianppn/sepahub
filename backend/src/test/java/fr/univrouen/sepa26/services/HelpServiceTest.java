package fr.univrouen.sepa26.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.univrouen.sepa26.dto.EndpointDoc;

/**
 * Tests unitaires pour {@link HelpService}.
 *
 * @author Florian Pépin
 * @version 2.0
 */
public class HelpServiceTest {

    private IHelpService helpService;

    @BeforeEach
    void setUp() {
        helpService = new HelpService();
    }

    @Test
    void testGetEndpointsReturnsNonEmptyList() {
        List<EndpointDoc> endpoints = helpService.getEndpoints();
        assertNotNull(endpoints);
        assertEquals(8, endpoints.size(), "Le catalogue doit documenter exactement 8 routes");

        for (EndpointDoc doc : endpoints) {
            assertNotNull(doc.url(), "L'URL ne doit pas être nulle");
            assertNotNull(doc.method(), "La méthode ne doit pas être nulle");
            assertNotNull(doc.format(), "Le format ne doit pas être nul");
            assertNotNull(doc.operation(), "L'opération ne doit pas être nulle");
            assertNotNull(doc.description(), "La description ne doit pas être nulle");
            assertEquals(doc.format(), doc.getReturn(), "getReturn() doit être identique à format()");
        }
    }

    @Test
    void testEndpointsContainKeyRoutes() {
        List<EndpointDoc> endpoints = helpService.getEndpoints();
        assertTrue(endpoints.stream().anyMatch(e -> e.url().equals("/") && e.method().equals("GET")));
        assertTrue(endpoints.stream().anyMatch(e -> e.url().equals("/help") && e.method().equals("GET")));
        assertTrue(endpoints.stream().anyMatch(e -> e.url().equals("/sepa26/insert") && e.method().equals("POST")));
        assertTrue(endpoints.stream().anyMatch(e -> e.url().equals("/sepa26/delete/{id}") && e.method().equals("DELETE")));
        assertTrue(endpoints.stream().anyMatch(e -> e.url().equals("/sepa26/search") && e.method().equals("GET")));
    }
}
