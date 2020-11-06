package com.rbkmoney.threeds.server.storage.servlet;

import com.rbkmoney.damsel.three_ds_server_storage.PreparationFlowInitializerSrv;
import com.rbkmoney.threeds.server.storage.handler.PreparationFlowServiceHandler;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@RequiredArgsConstructor
@WebServlet("/three-ds-server-storage/preparation-flow")
public class PreparationFlowServiceServlet extends GenericServlet {

    private final PreparationFlowServiceHandler handler;

    private Servlet servlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        servlet = new THServiceBuilder()
                .build(PreparationFlowInitializerSrv.Iface.class, handler);
    }

    @Override
    public void service(
            ServletRequest request,
            ServletResponse response) throws ServletException, IOException {
        servlet.service(request, response);
    }
}