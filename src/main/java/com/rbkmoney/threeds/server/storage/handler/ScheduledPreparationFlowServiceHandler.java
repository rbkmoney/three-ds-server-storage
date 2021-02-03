package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.schedule.ContextValidationResponse;
import com.rbkmoney.damsel.schedule.ExecuteJobRequest;
import com.rbkmoney.damsel.schedule.ScheduledJobExecutorSrv;
import com.rbkmoney.damsel.schedule.ValidationResponseStatus;
import com.rbkmoney.damsel.schedule.ValidationSuccess;
import com.rbkmoney.damsel.threeds.server.storage.InitRBKMoneyPreparationFlowRequest;
import com.rbkmoney.damsel.threeds.server.storage.PreparationFlowInitializerSrv;
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

    private final ThriftDeserializer<InitRBKMoneyPreparationFlowRequest> preparationFlowRequestDeserializer;
    private final PreparationFlowInitializerSrv.Iface preparationFlowServiceHandler;

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

        log.info("Execute scheduled job, providerId={}", request.getProviderId());

        preparationFlowServiceHandler.initRBKMoneyPreparationFlow(request);

        return ByteBuffer.wrap(executeJobRequest.getServiceExecutionContext());
    }
}
