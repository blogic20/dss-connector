package ru.blogic.dss.service.impl;

import org.apache.commons.beanutils.PropertyUtils;
import ru.blogic.dss.api.exception.DssServiceException;
import ru.cryptopro.dss.services.schemas._2014._06.DssFault;

import javax.xml.ws.WebFault;

/**
 * Created by pkupershteyn on 26.02.2016.
 */
public class DssServiceExceptionFactory {

    private static String DSS_FAULT="DssFault";
    private static String DSS_FAULT_FAULTINFO_PROPERTY="faultInfo";

    static DssServiceException newInstance(String message, Throwable cause) {
        String dssFault = getDssFault(cause);
        if (dssFault != null) {
            message = dssFault;
        }else{
            message+=": "+cause.getMessage();
        }
        return new DssServiceException(message, cause);
    }

    static DssServiceException newInstance(Throwable cause) {
        String dssFault = getDssFault(cause);
        if (dssFault != null) {
            return new DssServiceException(dssFault, cause);
        }
        return new DssServiceException(cause);
    }

    private static String getDssFault(Throwable cause) {
        if (cause != null && cause.getClass().isAnnotationPresent(WebFault.class)) {
            WebFault annotation = cause.getClass().getAnnotation(WebFault.class);
            if (DSS_FAULT.equals(annotation.name())) {
                try {
                    DssFault fault = (DssFault) PropertyUtils.getProperty(cause, DSS_FAULT_FAULTINFO_PROPERTY);
                    return fault.getMessage().getValue();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;

    }
}
