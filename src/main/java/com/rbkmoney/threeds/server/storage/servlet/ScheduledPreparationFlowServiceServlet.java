package com.rbkmoney.threeds.server.storage.servlet;

import com.rbkmoney.damsel.schedule.ScheduledJobExecutorSrv;
import com.rbkmoney.threeds.server.storage.handler.ScheduledPreparationFlowServiceHandler;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@RequiredArgsConstructor
@WebServlet("/three-ds-server-storage/scheduled-preparation-flow")
public class ScheduledPreparationFlowServiceServlet extends GenericServlet {

    private final ScheduledPreparationFlowServiceHandler handler;

    private Servlet servlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        servlet = new THServiceBuilder()
                .build(ScheduledJobExecutorSrv.Iface.class, handler);
    }

    @Override
    public void service(
            ServletRequest request,
            ServletResponse response) throws ServletException, IOException {
        servlet.service(request, response);
    }
}