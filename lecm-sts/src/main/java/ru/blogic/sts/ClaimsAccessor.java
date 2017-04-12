package ru.blogic.sts;

import com.sun.xml.ws.api.security.trust.Claims;
import org.oasis_open.docs.wsfed.authorization._200706.ClaimType;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by pkupershteyn on 13.01.2016.
 * Класс для поиска \ доступа утверждений в объекте {@link com.sun.xml.ws.api.security.trust.Claims}.
 */
public class ClaimsAccessor {
    private Map<String, ClaimType> claimsMap = new LinkedHashMap<String, ClaimType>();
    private static final JAXBContext CLAIMS_CONTEXT;

    static {
        try {
            CLAIMS_CONTEXT = JAXBContext.newInstance(ClaimType.class);
        } catch ( JAXBException e ) {
            throw new RuntimeException(e);
        }
    }

    public ClaimsAccessor(Claims claims) {
        try {
            Unmarshaller unmarshaller = CLAIMS_CONTEXT.createUnmarshaller();

            for ( Object claim : claims.getAny() ) {
                ClaimType claimType = unmarshaller.unmarshal((Node) claim, ClaimType.class).getValue();
                claimsMap.put(claimType.getUri(), claimType);
            }
        } catch ( JAXBException e ) {
            throw new RuntimeException(e);
        }

    }

    public ClaimType findByUri(String uri) {
        return claimsMap.get(uri);
    }

    public Collection<ClaimType> getClaims() {
        return claimsMap.values();
    }
}
