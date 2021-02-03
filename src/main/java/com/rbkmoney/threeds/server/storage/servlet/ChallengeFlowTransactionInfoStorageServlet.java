package com.rbkmoney.threeds.server.storage.servlet;

import com.rbkmoney.damsel.threeds.server.storage.ChallengeFlowTransactionInfoStorageSrv;
import com.rbkmoney.threeds.server.storage.handler.ChallengeFlowTransactionInfoStorageHandler;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;

@RequiredArgsConstructor
@WebServlet("/three-ds-server-storage/challenge-flow-transaction-info")
public class ChallengeFlowTransactionInfoStorageServlet extends GenericServlet {

    private final ChallengeFlowTransactionInfoStorageHandler handler;

    private Servlet servlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        servlet = new THServiceBuilder()
                .build(ChallengeFlowTransactionInfoStorageSrv.Iface.class, handler);
    }

    @Override
    public void service(
            ServletRequest request,
            ServletResponse response) throws ServletException, IOException {
        servlet.service(request, response);
    }
}
