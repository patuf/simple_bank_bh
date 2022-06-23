package com.simplebank.accounts.acc.transactionoutbox;

import com.simplebank.accounts.acc.AccountRepository;
import com.simplebank.accounts.acc.AccountService;
import com.simplebank.accounts.customer.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.support.MutableMessage;
import org.springframework.messaging.Message;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestTransactionOutboxMessageHandlers {

    @Mock
    private CreateTransactionCommandRepository troutRepo;
    @Mock
    private RestTemplate restTmpl;
    @Mock
    private KafkaTemplate<Long, CreateTransactionCommand> kafkaTmpl;

    @InjectMocks
    private TransactionOutboxMessageHandlerHttp mhHttp;
    @InjectMocks
    private TransactionOutboxMessageHandlerKafka mhKafka;

    private List<CreateTransactionCommand> ctCommands;
    private Message<List<CreateTransactionCommand>> testMsg;

    @BeforeEach
    public void setup() {
        ctCommands = Arrays.asList(
                new CreateTransactionCommand(1L, 1L, 201.6, LocalDateTime.now())
        );
        testMsg = new MutableMessage<>(ctCommands);
        ReflectionTestUtils.setField(mhHttp, "uri", "http://localhost");
        ReflectionTestUtils.setField(mhKafka, "kafkaTopic", "createTransactionCommand");
    }

    @DisplayName("Test handling a message from the transaction outbox, using the HTTP polling publisher")
    @Test
    public void testHandleMessageHttp() {

//        given(restTmpl.postForLocation(any(String.class), eq(ctCommands.get(0)))).willReturn(URI.create("http://localhost"));

        mhHttp.handleMessage(testMsg);

        verify(restTmpl, times(1)).postForLocation(any(String.class), eq(ctCommands.get(0)));
        verify(troutRepo, times(1)).delete(ctCommands.get(0));
    }

    @DisplayName("Test handling a message from the transaction outbox, using the HTTP polling publisher - with sending exception")
    @Test
    public void testHandleMessageHttpException() {

        given(restTmpl.postForLocation(any(String.class), eq(ctCommands.get(0)))).willThrow(new RuntimeException("Test Exception"));

        mhHttp.handleMessage(testMsg);

        verify(restTmpl, times(1)).postForLocation(any(String.class), eq(ctCommands.get(0)));
        verify(troutRepo, times(1)).delete(ctCommands.get(0));
    }

    @DisplayName("Test handling a message from the transaction outbox, using the HTTP polling publisher")
    @Test
    public void testHandleMessageKafka() {

//        given(kafkaTmpl.send("createTransactionCommand", ctCommands.get(0))).willReturn(null);

        mhKafka.handleMessage(testMsg);

        verify(kafkaTmpl, times(1)).send("createTransactionCommand", ctCommands.get(0));
        verify(troutRepo, times(1)).delete(ctCommands.get(0));
    }

    @DisplayName("Test handling a message from the transaction outbox, using the HTTP polling publisher - with sending exception")
    @Test
    public void testHandleMessageKafkaException() {

        given(kafkaTmpl.send("createTransactionCommand", ctCommands.get(0))).willThrow(new RuntimeException("Test Exception"));

        mhKafka.handleMessage(testMsg);

        verify(kafkaTmpl, times(1)).send("createTransactionCommand", ctCommands.get(0));
        verify(troutRepo, times(1)).delete(ctCommands.get(0));
    }
}
