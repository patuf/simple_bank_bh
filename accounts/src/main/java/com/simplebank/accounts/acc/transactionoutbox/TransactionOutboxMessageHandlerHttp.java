package com.simplebank.accounts.acc.transactionoutbox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TransactionOutboxMessageHandlerHttp implements MessageHandler {
    private final Log log = LogFactory.getLog(getClass());

    @Value("${service.transaction.uri.transactions.create}")
    private String uri;
    @Autowired
    private CreateTransactionCommandRepository troutRepo;
    @Autowired
    private RestTemplate restTmpl;

    @Override
    @Transactional
    public void handleMessage(Message<?> message) throws MessagingException {
        log.debug("Handling message using http handler. Uri is " + uri);
        List<CreateTransactionCommand> payloads = (List<CreateTransactionCommand>) message.getPayload();
        for (CreateTransactionCommand payload: payloads) {
            try {
                restTmpl.postForLocation(uri, payload);
                log.debug("Message sent to http endpoint: New account transaction for customerId: " + payload.getCustomerId());
            } catch (Throwable ex) {
                log.error(String.format("CreateTransaction command for accountId %d was not sent due to failure! Retries anb DLQ are not implemented - sorry!", payload.getAccountId()), ex);
            }
            troutRepo.delete(payload);
        }
    }
}
