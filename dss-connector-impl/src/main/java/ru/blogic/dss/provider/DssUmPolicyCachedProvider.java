package ru.blogic.dss.provider;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.datacontract.schemas._2004._07.cryptopro_dss_common.UmsPolicy;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.api.dto.usermanagement.umspolicy.DssUmsPolicy;
import ru.blogic.dss.mapper.usermanagement.UmPolicyMapper;
import ru.cryptopro.dss.services._2015._12.IUserManagementServiceGetPolicyDssFaultFaultFaultMessage;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by pkupershteyn on 11.03.2016.
 * Бин с методом кэширующегося и блокируемого вызова DSSUmPolicy
 */
@Singleton
@Lock(LockType.READ)
public class DssUmPolicyCachedProvider {

    @Inject
    private UmPolicyMapper umPolicyMapper;

    @Inject
    private DssConfigService dssConfigService;

    private LoadingCache<Object, DssUmsPolicy> dssUmsPolicyCache;

    private static final String UMS_POLICY_CACHE_KEY = "UMS_POLICY_CACHE";

    public DssUmsPolicy getDssUmsPolicy(final WsPortProvider wsPortProvider) throws IUserManagementServiceGetPolicyDssFaultFaultFaultMessage {
        if (dssUmsPolicyCache == null) {
            setDssPolicyInCache(wsPortProvider);
        }
        return dssUmsPolicyCache.getUnchecked(UMS_POLICY_CACHE_KEY);
    }

    private void setDssPolicyInCache(final WsPortProvider wsPortProvider) throws IUserManagementServiceGetPolicyDssFaultFaultFaultMessage{
        if (dssUmsPolicyCache == null) {
            int dssPolicyCacheTtlMinutes = dssConfigService.getDssPolicyCacheTtlMinutes();
            dssUmsPolicyCache = CacheBuilder.newBuilder()
                    .expireAfterWrite(dssPolicyCacheTtlMinutes, TimeUnit.MINUTES)
                    .build(new CacheLoader<Object, DssUmsPolicy>() {
                        @Override
                        public DssUmsPolicy load(Object key) throws IUserManagementServiceGetPolicyDssFaultFaultFaultMessage {
                            return getPolicyDirect(wsPortProvider);
                        }
                    });
        }
    }

    @Lock(LockType.WRITE)
    public DssUmsPolicy getPolicyDirect(WsPortProvider wsPortProvider) throws IUserManagementServiceGetPolicyDssFaultFaultFaultMessage {
        UmsPolicy policy = wsPortProvider.getUserManagementPort().getPolicy();
        return umPolicyMapper.from(policy);
    }
}
