package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.schedule.ContextValidationResponse;
import com.rbkmoney.damsel.schedule.ExecuteJobRequest;
import com.rbkmoney.damsel.schedule.ScheduledJobExecutorSrv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledPreparationFlowServiceHandler implements ScheduledJobExecutorSrv.Iface {

    @Override
    public ContextValidationResponse validateExecutionContext(ByteBuffer byteBuffer) throws TException {
        return null; // TODO [a.romanov]: impl
    }

    @Override
    public ByteBuffer executeJob(ExecuteJobRequest executeJobRequest) throws TException {
        return null; // TODO [a.romanov]: impl
    }
}
