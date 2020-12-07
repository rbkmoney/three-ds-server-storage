package com.rbkmoney.threeds.server.storage.servlet;

import com.rbkmoney.damsel.threeds.server.storage.CardRangesStorageSrv;
import com.rbkmoney.threeds.server.storage.handler.CardRangesStorageHandler;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@RequiredArgsConstructor
@WebServlet("/three-ds-server-storage/card-ranges")
public class CardRangesStorageServlet extends GenericServlet {

    private final CardRangesStorageHandler handler;

    private Servlet servlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        servlet = new THServiceBuilder()
                .build(CardRangesStorageSrv.Iface.class, handler);
    }

    @Override
    public void service(
            ServletRequest request,
            ServletResponse response) throws ServletException, IOException {
        servlet.service(request, response);
    }
}

