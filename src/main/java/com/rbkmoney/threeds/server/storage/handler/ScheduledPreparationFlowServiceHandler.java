package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.schedule.*;
import com.rbkmoney.damsel.three_ds_server_storage.InitRBKMoneyPreparationFlowRequest;
import com.rbkmoney.damsel.three_ds_server_storage.PreparationFlowServiceSrv;
import com.rbkmoney.threeds.server.storage.deserializer.ThriftDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledPreparationFlowServiceHandler implements ScheduledJobExecutorSrv.Iface {

    private static final ByteBuffer OK = ByteBuffer.wrap(new byte[0]);

    private final ThriftDeserializer<InitRBKMoneyPreparationFlowRequest> preparationFlowRequestDeserializer;
    private final PreparationFlowServiceSrv.Iface preparationFlowServiceHandler;

    @Override
    public ContextValidationResponse validateExecutionContext(ByteBuffer byteBuffer) {
        return new ContextValidationResponse()
                .setResponseStatus(ValidationResponseStatus.success(
                        new ValidationSuccess()));
    }

    @Override
    public ByteBuffer executeJob(ExecuteJobRequest executeJobRequest) throws TException {
        InitRBKMoneyPreparationFlowRequest request = preparationFlowRequestDeserializer.deserialize(
                executeJobRequest.getServiceExecutionContext(),
                new InitRBKMoneyPreparationFlowRequest());

        log.info("Execute scheduled job for providerId={}", request.getProviderId());
        preparationFlowServiceHandler.initRBKMoneyPreparationFlow(request);

        return OK;
    }
}
