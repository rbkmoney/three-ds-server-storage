package com.rbkmoney.threeds.server.storage.utils;

import com.rbkmoney.damsel.threeds.server.storage.Action;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class HideCardRange {

    private String startRange;
    private String endRange;
    private Action action;
    private String acsStartProtocolVersion;
    private String acsEndProtocolVersion;
    private String dsStartProtocolVersion;
    private String dsEndProtocolVersion;
    private String acsInformationIndicator;
    private String threeDsMethodUrl;

}