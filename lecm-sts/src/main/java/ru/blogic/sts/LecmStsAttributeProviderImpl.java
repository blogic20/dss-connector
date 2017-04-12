package ru.blogic.sts;

import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.api.security.trust.STSAttributeProvider;
import org.oasis_open.docs.wsfed.authorization._200706.ClaimType;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.api.dto.DssConfirmableAction;
import ru.blogic.dss.api.dto.MfaPolicyState;
import ru.blogic.dss.common.Constants;
import ru.blogic.dss.common.util.SecurityUtils;
import ru.blogic.dss.common.util.ServiceLocator;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

/**
 * Данный провайдер добавляет в токен безопасности атрибуты, необходимые для работы с сервисами КриптоПро DSS.
 *
 * @author dgolubev
 */
public class LecmStsAttributeProviderImpl implements STSAttributeProvider {

    private static final QName QNAME_NAME = new QName("http://schemas.xmlsoap.org/ws/2005/05/identity/claims", "name");
    private static final QName QNAME_ROLE = new QName("http://schemas.microsoft.com/ws/2008/06/identity/claims", "role");

    public enum Role {
        Admins,
        Users
    }

    private Role resolveUserRole(String name) {
        final DssConfigService dssConfigService = ServiceLocator.getService(DssConfigService.class);

        String dssOperator = dssConfigService.getDssOperator();
        if (dssOperator != null && dssOperator.equals(name)) {
            return Role.Admins;
        }

        return Role.Users;
    }

    @Override
    public Map<QName, List<String>> getClaimedAttributes(Subject subject, String appliesTo, String tokenType, Claims claims) {
        final Map<QName, List<String>> attrs = new HashMap<QName, List<String>>();

        final String name = SecurityUtils.getCallerName();
        attrs.put(QNAME_NAME, singletonList(name));

        final Role userRole = resolveUserRole(name);
        attrs.put(QNAME_ROLE, singletonList(userRole.name()));

        final DssConfigService dssConfigService = ServiceLocator.getService(DssConfigService.class);

        addActionPolicyIfSet(attrs, DssConfirmableAction.SIGN_DOCUMENT, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.SIGN_DOCUMENTS, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.DECRYPT_DOCUMENT, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.CREATE_REQUEST, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.CHANGE_PIN, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.RENEW_CERTIFICATE, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.HOLD_CERTIFICATE, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.REVOKE_CERTIFICATE, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.DELETE_CERTIFICATE, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.UNHOLD_CERTIFICATE, dssConfigService);
        addActionPolicyIfSet(attrs, DssConfirmableAction.ISSUE, dssConfigService);

        ClaimsAccessor claimsAccessor = new ClaimsAccessor(claims);

        if (Constants.ECHO_CLAIMS_DIALECT.equals(claims.getDialect())) {
            echoClaims(attrs, claimsAccessor);
        }
        //Копировать утверждение delegateuserid только для администратора
        copyClaimIfAdminsRole(claimsAccessor, Constants.URI_CPRO_IDENTITY_CLAIMS_DELEGATE_USER_ID, userRole, attrs);

        return attrs;
    }

    private void copyClaimIfAdminsRole(ClaimsAccessor claimsAccessor, String claimUri, Role userRole, Map<QName, List<String>> attrs) {
        ClaimType claimType;
        if (( claimType = claimsAccessor.findByUri(Constants.URI_CPRO_IDENTITY_CLAIMS_DELEGATE_USER_ID) ) != null && userRole.equals(Role.Admins)) {
            copyClaim(claimType, attrs);
        }
    }

    private static void echoClaims(Map<QName, List<String>> attrs, ClaimsAccessor claims) {
        for (ClaimType claimType : claims.getClaims()) {
            copyClaim(claimType, attrs);
        }
    }

    private static void copyClaim(ClaimType claimType, Map<QName, List<String>> attrs) {
        attrs.put(toQName(claimType.getUri()), Collections.singletonList(claimType.getValue()));
    }

    private static QName toQName(String uri) {
        final String[] parts = uri.split("/(?=[^/]+$)");
        return new QName(parts[0], parts[1]);
    }

    private void addActionPolicyIfSet(Map<QName, List<String>> attrs, DssConfirmableAction action, DssConfigService config) {
        final MfaPolicyState state = config.getPolicyState(action);
        if (state != MfaPolicyState.NOT_SET) {
            final QName name = new QName("http://dss.cryptopro.ru/identity/claims/action", action.getDssName());
            final Boolean required = state == MfaPolicyState.ON;
            attrs.put(name, singletonList("tfa:" + required.toString()));
        }
    }
}
