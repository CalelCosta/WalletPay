package recargapay.wallet.adapters.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import recargapay.wallet.application.dto.request.AuthenticationRequestDTO;
import recargapay.wallet.application.dto.response.AuthenticationResponseDTO;
import recargapay.wallet.application.service.impl.AuthenticationServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationServiceImpl authenticationService;

    @Test
    public void createAuthenticationToken_success() {
        // Arrange
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO();
        requestDTO.setUsername("admin");
        requestDTO.setPassword("admin123");

        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO("\"sample-token\"", "", 600);
        ResponseEntity<AuthenticationResponseDTO> expectedResponse = ResponseEntity.ok(responseDTO);
        when(authenticationService.createAuthenticationToken(requestDTO)).thenReturn(expectedResponse);

        ResponseEntity<AuthenticationResponseDTO> actualResponse = authenticationController.createAuthenticationToken(requestDTO);

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertEquals("\"sample-token\"", actualResponse.getBody().token());

        verify(authenticationService, times(1)).createAuthenticationToken(requestDTO);
    }
}
