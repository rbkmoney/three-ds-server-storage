package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.RReqTransactionInfo;
import com.rbkmoney.damsel.three_ds_server_storage.RReqTransactionInfoStorageSrv;
import org.apache.thrift.TException;

public class RReqTransactionInfoStorageHandler implements RReqTransactionInfoStorageSrv.Iface {

    @Override
    public void saveRReqTransactionInfo(RReqTransactionInfo rReqTransactionInfo) throws TException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public RReqTransactionInfo getRReqTransactionInfo(String s) throws TException {
        throw new UnsupportedOperationException("TODO");
    }
}
