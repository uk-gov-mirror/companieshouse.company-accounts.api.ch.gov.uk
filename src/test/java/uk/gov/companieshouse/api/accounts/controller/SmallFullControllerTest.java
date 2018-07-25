package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Equals;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullControllerTest {


        @Mock
        private SmallFull smallFull;

        @Mock
        private SmallFull createdSmallFull;

        @Mock
        private SmallFullService smallFullService;

        @InjectMocks
        private SmallFullController smallFullController;

        @BeforeEach
        public void setUp(){
            when(smallFullService.save(smallFull)).thenReturn(createdSmallFull);
        }

        @Test
        @DisplayName("Tests the successful creation of a smallFull resource")
        public void canCreateSmallFull() {
            ResponseEntity response = smallFullController.create(smallFull);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertTrue(new Equals(createdSmallFull).matches(response.getBody()));
        }
    }