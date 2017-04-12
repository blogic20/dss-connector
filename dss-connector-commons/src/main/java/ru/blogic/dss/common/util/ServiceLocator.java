package ru.blogic.dss.common.util;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/**
 * @author dgolubev
 */
public class ServiceLocator {

    private static final InitialContext ctx;

    static {
        try {
            ctx = new InitialContext();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getService(Class<T> api) {
        return getService(api, (api.isAnnotationPresent(Local.class) || api.isAnnotationPresent(LocalBean.class)));
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> api, boolean local) {
        Object obj;
        final String name = api.getName();
        try {
            obj = ctx.lookup(local ? "ejblocal:" + name : name);
        } catch (NamingException e) {
            throw new RuntimeException(String.format("Unable to obtain the%s service by the interface %s:",
                    local ? " local" : "", api.getName()), e);
        }
        return (T) PortableRemoteObject.narrow(obj, api);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(String name) {
        try {
            return (T) ctx.lookup(name);
        } catch (NamingException e) {
            throw new RuntimeException(String.format("Unable to obtain the service by the name %s:", name), e);
        }
    }
}
