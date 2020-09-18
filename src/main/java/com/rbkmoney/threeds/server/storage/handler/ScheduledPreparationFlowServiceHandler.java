package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.schedule.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledPreparationFlowServiceHandler implements ScheduledJobExecutorSrv.Iface {

    @Override
    public ContextValidationResponse validateExecutionContext(ByteBuffer byteBuffer) {
        return new ContextValidationResponse()
                .setResponseStatus(ValidationResponseStatus.success(
                        new ValidationSuccess()));
    }

    @Override
    public ByteBuffer executeJob(ExecuteJobRequest executeJobRequest) {
        // TODO [a.romanov]:
        //  - deserialize request context
        //  - call PreparationFlowServiceHandler

        return ByteBuffer.wrap(new byte[0]);
    }
}
