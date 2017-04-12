package ru.blogic.dss.wstrust;

import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponseCollection;
import com.sun.xml.ws.security.trust.impl.wssx.WSTrustElementFactoryImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestSecurityTokenResponseCollectionImpl;
import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import com.sun.xml.ws.security.trust.util.TrustNamespacePrefixMapper;
import org.oasis_open.docs.ws_sx.ws_trust._200802.InteractiveChallengeType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.blogic.dss.domain.DssRequestSecurityTokenResponseImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

/**
 * @author dgolubev
 */
public class DssWSTrustElementFactoryImpl extends WSTrustElementFactoryImpl {

    private static JAXBContext EXTENDED_CONTEXT;

    static {
        try {
            EXTENDED_CONTEXT = JAXBContext.newInstance(
                    "org.oasis_open.docs.ws_sx.ws_trust._200802" +
                            ":org.oasis_open.docs.wsfed.authorization._200706" +
                            ":com.sun.xml.ws.security.trust.impl.wssx.bindings" +
                            ":com.sun.xml.ws.security.secconv.impl.wssx.bindings" +
                            ":com.sun.xml.ws.security.secext10" +
                            ":com.sun.xml.security.core.ai" +
                            ":com.sun.xml.security.core.dsig" +
                            ":com.sun.xml.ws.policy.impl.bindings");
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    DssWSTrustElementFactoryImpl() {
    }

    @Override
    public RequestSecurityTokenResponseCollection createRSTRCollectionFrom(Element elem) {
        try {
            NodeList interactiveChallenges = elem.getElementsByTagName("InteractiveChallenge");
            if (interactiveChallenges.getLength() > 0) {
                JAXBContext jaxbContext = JAXBContext.newInstance(InteractiveChallengeType.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement interactiveChallengeType = (JAXBElement) jaxbUnmarshaller.unmarshal(interactiveChallenges.item(0));
                InteractiveChallengeType type = (InteractiveChallengeType) interactiveChallengeType.getValue();
                DssRequestSecurityTokenResponseImpl rstr = new DssRequestSecurityTokenResponseImpl();
                rstr.setChallenge(type);
                RequestSecurityTokenResponseCollectionImpl rstrc = new RequestSecurityTokenResponseCollectionImpl();
                rstrc.addRequestSecurityTokenResponse(rstr);
                return rstrc;
            }
            return super.createRSTRCollectionFrom(elem);

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Marshaller getMarshaller() {
        try {
            final Marshaller marshaller = EXTENDED_CONTEXT.createMarshaller();
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new TrustNamespacePrefixMapper());
            return marshaller;
        } catch (PropertyException e) {
            throw new RuntimeException(LogStringsMessages.WST_0003_ERROR_CREATING_WSTRUSTFACT(), e);
        } catch (JAXBException jbe) {
            throw new RuntimeException(LogStringsMessages.WST_0003_ERROR_CREATING_WSTRUSTFACT(), jbe);
        }
    }
}
