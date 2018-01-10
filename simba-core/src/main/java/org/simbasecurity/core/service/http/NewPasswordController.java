package org.simbasecurity.core.service.http;

import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.common.filter.action.RequestActionFactory;
import org.simbasecurity.common.request.RequestUtil;
import org.simbasecurity.core.chain.Chain;
import org.simbasecurity.core.chain.ChainContextImpl;
import org.simbasecurity.core.service.LoginMappingService;
import org.simbasecurity.core.service.SessionService;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.simbasecurity.core.service.http.NewPasswordController.SIMBA_NEW_PWD_PATH;

@org.springframework.stereotype.Controller
@RequestMapping(SIMBA_NEW_PWD_PATH)
public class NewPasswordController implements Controller {

    public static final String SIMBA_NEW_PWD_PATH = "/simba-new-pwd";

    @Autowired
    private Chain newPasswordChain;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CoreConfigurationService configurationService;

    @Autowired
    private LoginMappingService loginMappingService;

    private SimbaWebUrlResolver simbaWebUrlResolver = new SimbaWebUrlResolver();

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        RequestData requestData = RequestUtil.createRequestData(httpServletRequest, simbaWebUrlResolver.resolveSimbaWebURL(httpServletRequest));

        ChainContextImpl context = new ChainContextImpl(requestData, sessionService.getSession(requestData.getSsoToken()), configurationService, loginMappingService);
        newPasswordChain.execute(context);

        ActionDescriptor actionDescriptor = context.getActionDescriptor();

        RequestActionFactory actionFactory = new RequestActionFactory(httpServletRequest, httpServletResponse);
        actionFactory.execute(actionDescriptor);
        return null;
    }
}
