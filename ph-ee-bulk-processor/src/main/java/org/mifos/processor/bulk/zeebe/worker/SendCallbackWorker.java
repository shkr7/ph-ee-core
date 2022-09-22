package org.mifos.processor.bulk.zeebe.worker;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.mifos.processor.bulk.camel.routes.RouteId;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.mifos.processor.bulk.camel.config.CamelProperties.BATCH_STATUS_FAILED;
import static org.mifos.processor.bulk.camel.config.CamelProperties.CALLBACK_RESPONSE_CODE;
import static org.mifos.processor.bulk.zeebe.ZeebeVariables.*;

@Component
public class SendCallbackWorker extends BaseWorker {

    @Override
    public void setup() {
        newWorker(Worker.SEND_CALLBACK, (client, job) -> {
            Map<String, Object> variables = job.getVariablesAsMap();

            int retry = (int) variables.getOrDefault(CALLBACK_RETRY, 0);

            Exchange exchange = new DefaultExchange(camelContext);
            exchange.setProperty(MAX_STATUS_RETRY,variables.get(MAX_STATUS_RETRY));
            exchange.setProperty(CALLBACK_RETRY,retry);
            exchange.setProperty(CALLBACK_URL,variables.get(CALLBACK_URL));
            exchange.setProperty(SUCCESS_RATE,variables.get(SUCCESS_RATE));
            sendToCamelRoute(RouteId.SEND_CALLBACK, exchange);

            Boolean callbackSuccess = exchange.getProperty(CALLBACK_SUCCESS, Boolean.class);
            if (callbackSuccess == null || !callbackSuccess) {
                variables.put(ERROR_CODE, exchange.getProperty(ERROR_CODE));
                variables.put(ERROR_DESCRIPTION, exchange.getProperty(ERROR_DESCRIPTION));
                logger.info("Error: {}, {}", variables.get(ERROR_CODE), variables.get(ERROR_DESCRIPTION));
            } else {
                variables.put(CALLBACK_SUCCESS,true);
            }

            variables.put(CALLBACK_RETRY,exchange.getProperty(CALLBACK_RETRY));
            variables.put(CALLBACK_RESPONSE_CODE,exchange.getProperty(CALLBACK_RESPONSE_CODE));

            logger.info("Retry: {} and Response Code {}", exchange.getProperty(CALLBACK_RETRY), exchange.getProperty(CALLBACK_RESPONSE_CODE));
            client.newCompleteCommand(job.getKey()).variables(variables).send();
        });
    }

}
