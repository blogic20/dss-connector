package ru.blogic.dss.wstrust;

import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.trust.TrustPlugin;
import com.sun.xml.ws.security.trust.impl.client.STSIssuedTokenProviderImpl;
import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import com.sun.xml.wss.SubjectAccessor;

import javax.security.auth.Subject;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dgolubev
 */
public class DssIssuedTokenProvider extends STSIssuedTokenProviderImpl {

    private static final Logger logger = Logger.getLogger(DssIssuedTokenProvider.class.getName());

    private TrustPlugin trustPlugin = new DssTrustPluginImpl();

    public DssIssuedTokenProvider() {
    }

    @Override
    public void issue(IssuedTokenContext ctx) throws WSTrustException {
        STSIssuedTokenConfiguration config = (STSIssuedTokenConfiguration) ctx.getSecurityPolicy().get(0);
        ctx.setTokenIssuer(config.getSTSEndpoint());
        boolean shareToken = "true".equals(config.getOtherOptions().get(STSIssuedTokenConfiguration.SHARE_TOKEN));
        boolean renewExpiredToken = "true".equals(config.getOtherOptions().get(STSIssuedTokenConfiguration.RENEW_EXPIRED_TOKEN));
        String maxClockSkew = (String) config.getOtherOptions().get(STSIssuedTokenConfiguration.MAX_CLOCK_SKEW);
        Subject subject = SubjectAccessor.getRequesterSubject();
        if (shareToken && subject != null) {
            Set pcs = subject.getPrivateCredentials(IssuedTokenContext.class);
            for (Object obj : pcs) {
                IssuedTokenContext cached = (IssuedTokenContext) obj;

                // Check if the token is expired
                Calendar c = new GregorianCalendar();
                long offset = c.get(Calendar.ZONE_OFFSET);
                if (c.getTimeZone().inDaylightTime(c.getTime())) {
                    offset += c.getTimeZone().getDSTSavings();
                }
                long beforeTime = c.getTimeInMillis();
                long currentTime = beforeTime - offset;
                if (maxClockSkew != null) {
                    currentTime = currentTime - Long.parseLong(maxClockSkew);
                }
                c.setTimeInMillis(currentTime);
                Date currentTimeInDateFormat = c.getTime();
                if (cached.getExpirationTime() != null && currentTimeInDateFormat.after(cached.getExpirationTime())) {
                    // Remove the expired context
                    subject.getPrivateCredentials().remove(cached);

                    //if renewExpiredToke="true" is not set
                    if (!renewExpiredToken) {
                        logger.log(Level.SEVERE,
                                LogStringsMessages.WST_0046_TOKEN_EXPIRED(cached.getCreationTime(), cached.getExpirationTime(), currentTimeInDateFormat));
                        throw new WSTrustException(LogStringsMessages.WST_0046_TOKEN_EXPIRED(cached.getCreationTime(), cached.getExpirationTime(), currentTimeInDateFormat));
                    }
                } else if (cached.getTokenIssuer().equals(ctx.getTokenIssuer())) {
                    updateContext(cached, ctx);
                    return;
                }
            }
        }

        trustPlugin.process(ctx);
        if (shareToken) {
            if (subject == null) {
                subject = new Subject();
            }
            subject.getPrivateCredentials().add(ctx);
            SubjectAccessor.setRequesterSubject(subject);
        }
    }

    private void updateContext(IssuedTokenContext cached, IssuedTokenContext ctx) {
        ctx.setUnAttachedSecurityTokenReference(cached.getUnAttachedSecurityTokenReference());
        ctx.setSecurityToken(cached.getSecurityToken());
        ctx.setRequestorCertificate(cached.getRequestorCertificate());
        ctx.setProofKeyPair(cached.getProofKeyPair());
        ctx.setProofKey(cached.getProofKey());
        ctx.setExpirationTime(cached.getExpirationTime());
        ctx.setCreationTime(cached.getCreationTime());
        ctx.setAttachedSecurityTokenReference(cached.getAttachedSecurityTokenReference());
    }
}
