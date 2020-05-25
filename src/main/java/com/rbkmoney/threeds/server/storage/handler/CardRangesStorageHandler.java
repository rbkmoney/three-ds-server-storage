package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.CardRangesStorageSrv;
import com.rbkmoney.damsel.three_ds_server_storage.GetCardRangesRequest;
import com.rbkmoney.damsel.three_ds_server_storage.GetCardRangesResponse;
import com.rbkmoney.damsel.three_ds_server_storage.InitRBKMoneyPreparationFlowRequest;
import org.apache.thrift.TException;

public class CardRangesStorageHandler implements CardRangesStorageSrv.Iface {

    @Override
    public void initRBKMoneyPreparationFlow(InitRBKMoneyPreparationFlowRequest initRBKMoneyPreparationFlowRequest) throws TException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public GetCardRangesResponse getCardRanges(GetCardRangesRequest getCardRangesRequest) throws TException {
        throw new UnsupportedOperationException("TODO");
    }
}
