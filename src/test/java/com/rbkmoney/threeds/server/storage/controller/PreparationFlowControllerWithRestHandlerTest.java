package com.rbkmoney.threeds.server.storage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.threeds.server.storage.config.AbstractConfigWithoutDao;
import com.rbkmoney.threeds.server.storage.service.PreparationFlowService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "rest-handler.enabled=true")
@AutoConfigureMockMvc
public class PreparationFlowControllerWithRestHandlerTest extends AbstractConfigWithoutDao {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PreparationFlowService preparationFlowService;

    @Test
    public void test() throws Exception {
        PreparationFlowController.PreparationRequest preparationRequest =
                new PreparationFlowController.PreparationRequest();
        preparationRequest.setMessageVersion("2.1.0");
        preparationRequest.setProviderId("visa");

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/preparation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(preparationRequest));

        mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful());
    }
}
