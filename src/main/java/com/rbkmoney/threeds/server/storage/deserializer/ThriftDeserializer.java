package com.rbkmoney.threeds.server.storage.deserializer;

import com.rbkmoney.threeds.server.storage.exception.DeserializationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;

@Slf4j
public abstract class ThriftDeserializer<T extends TBase> {

    public T deserialize(byte[] bin, T thrift) {
        try {
            new TDeserializer().deserialize(thrift, bin);
        } catch (TException e) {
            log.error("Exception when trying to deserialize bin", e);
            throw new DeserializationException(e);
        }

        return thrift;
    }
}