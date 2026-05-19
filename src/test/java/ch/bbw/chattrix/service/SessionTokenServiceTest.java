package ch.bbw.chattrix.service;

import ch.bbw.chattrix.entity.SessionToken;
import ch.bbw.chattrix.entity.User;
import ch.bbw.chattrix.repository.SessionTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionTokenServiceTest {

    @Mock
    private SessionTokenRepository sessionTokenRepository;

    @InjectMocks
    private SessionTokenService sessionTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
    }

    @Test
    void shouldDeleteExistingTokensAndSaveNewOne() {
        when(sessionTokenRepository.save(any(SessionToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessionToken result = sessionTokenService.createForUser(user);

        verify(sessionTokenRepository, times(1)).deleteByUser(user);
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void shouldGenerateUniqueToken() {
        when(sessionTokenRepository.save(any(SessionToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessionToken first = sessionTokenService.createForUser(user);
        SessionToken second = sessionTokenService.createForUser(user);

        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first.getToken(), second.getToken());
    }

    @Test
    void shouldReturnSessionToken() {
        String token = "abc123";
        SessionToken sessionToken = mock(SessionToken.class);

        when(sessionTokenRepository.findByToken(token))
                .thenReturn(Optional.of(sessionToken));

        Optional<SessionToken> result = sessionTokenService.findByToken(token);

        assertTrue(result.isPresent());
        assertEquals(sessionToken, result.get());

        verify(sessionTokenRepository).findByToken(token);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        when(sessionTokenRepository.findByToken("missing"))
                .thenReturn(Optional.empty());

        Optional<SessionToken> result = sessionTokenService.findByToken("missing");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCallRepository() {
        String token = "delete-me";

        sessionTokenService.deleteByToken(token);

        verify(sessionTokenRepository, times(1)).deleteByToken(token);
    }

    @Test
    void shouldPersistTokenWithCorrectUser() {
        ArgumentCaptor<SessionToken> captor = ArgumentCaptor.forClass(SessionToken.class);

        when(sessionTokenRepository.save(any(SessionToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        sessionTokenService.createForUser(user);

        verify(sessionTokenRepository).save(captor.capture());

        SessionToken saved = captor.getValue();
        assertEquals(user, saved.getUser());
        assertNotNull(saved.getToken());
        assertNotNull(saved.getCreatedAt());
    }
}
