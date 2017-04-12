package ru.blogic.dss.domain;

import com.sun.xml.ws.api.security.trust.Status;
import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.security.trust.elements.Entropy;
import com.sun.xml.ws.security.trust.elements.Lifetime;
import com.sun.xml.ws.security.trust.elements.RequestedAttachedReference;
import com.sun.xml.ws.security.trust.elements.RequestedProofToken;
import com.sun.xml.ws.security.trust.elements.RequestedSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestedUnattachedReference;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RequestSecurityTokenResponseType;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestSecurityTokenResponseImpl;
import org.oasis_open.docs.ws_sx.ws_trust._200802.InteractiveChallengeResponseType;
import org.oasis_open.docs.ws_sx.ws_trust._200802.InteractiveChallengeType;
import org.oasis_open.docs.ws_sx.ws_trust._200802.ObjectFactory;

import javax.xml.bind.JAXBElement;
import java.net.URI;
import java.util.List;

/**
 * @author dgolubev
 */
public class DssRequestSecurityTokenResponseImpl extends RequestSecurityTokenResponseImpl {

    private InteractiveChallengeType challenge;
    private InteractiveChallengeResponseType challengeResponse;

    private URI requestType;

    public DssRequestSecurityTokenResponseImpl(RequestSecurityTokenResponseType rstrType) throws Exception {
        super(rstrType);
        List<Object> list = rstrType.getAny();
        for (Object object : list) {
            if (!(object instanceof JAXBElement)) {
                getAny().add(object);
            } else {
                JAXBElement obj = (JAXBElement) object;
                String local = obj.getName().getLocalPart();
                if (local.equalsIgnoreCase("InteractiveChallenge")) {
                    InteractiveChallengeType challengeType
                            = (InteractiveChallengeType) obj.getValue();
                    setChallenge(challengeType);
                }
                if (local.equalsIgnoreCase("InteractiveChallengeResponse")) {
                    InteractiveChallengeResponseType challengeResponseType
                            = (InteractiveChallengeResponseType) obj.getValue();
                    setChallengeResponse(challengeResponseType);
                }
            }
        }
    }

    public DssRequestSecurityTokenResponseImpl() {
        super();
    }

    public DssRequestSecurityTokenResponseImpl(URI tokenType, URI context, RequestedSecurityToken token, AppliesTo scopes, RequestedAttachedReference attached, RequestedUnattachedReference unattached, RequestedProofToken proofToken, Entropy entropy, Lifetime lifetime, Status status) {
        super(tokenType, context, token, scopes, attached, unattached, proofToken, entropy, lifetime, status);
    }

    public InteractiveChallengeType getChallenge() {
        return challenge;
    }

    public void setChallenge(InteractiveChallengeType challenge) {
        this.challenge = challenge;
        JAXBElement<InteractiveChallengeType> sElement = new ObjectFactory().createInteractiveChallenge(challenge);
        getAny().add(sElement);
    }

    public InteractiveChallengeResponseType getChallengeResponse() {
        return challengeResponse;
    }

    public void setChallengeResponse(InteractiveChallengeResponseType challengeResponse) {
        this.challengeResponse = challengeResponse;
        JAXBElement<InteractiveChallengeResponseType> sElement = new ObjectFactory().createInteractiveChallengeResponse(challengeResponse);
        getAny().add(sElement);
    }

    public URI getRequestType() {
        return this.requestType;
    }

    public void setRequestType(URI requestType) {
        String rtString = requestType.toString();
        this.requestType = requestType;
        JAXBElement<String> rtElement = new com.sun.xml.ws.security.trust.impl.wssx.bindings.ObjectFactory().createRequestType(rtString);
        getAny().add(rtElement);
    }

}
