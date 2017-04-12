package ru.blogic.sts;

import com.sun.xml.ws.security.trust.sts.BaseSTSImpl;
import ru.blogic.dss.common.Constants;

import javax.annotation.Resource;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;

/**
 * @author dgolubev
 */
@WebServiceProvider(serviceName = Constants.LECM_STS_SERVICE_NAME,
        portName = Constants.LECM_STS_PORT_NAME,
        targetNamespace = Constants.URI_OASIS_WS_TRUST_200512,
        wsdlLocation = "WEB-INF/wsdl/LECM-STS.wsdl")
@ServiceMode(Service.Mode.PAYLOAD)
public class LecmStsImpl extends BaseSTSImpl implements Provider<Source> {

    @Resource
    private WebServiceContext context;

    @Override
    public Source invoke(Source rstElement) {
        return super.invoke(rstElement);
    }

    @Override
    protected MessageContext getMessageContext() {
        return context.getMessageContext();
    }
}
