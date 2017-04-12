package ru.blogic.dss.provider;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.blogic.dss.api.DssConfigService;
import ru.blogic.dss.api.dto.dsspolicy.DssPolicy;
import ru.blogic.dss.mapper.DssPolicyMapper;
import ru.cryptopro.dss.services._2014._06.ISignServiceGetPolicyDssFaultFaultFaultMessage;
import ru.cryptopro.dss.services.schemas._2014._06.DSSPolicy;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by pkupershteyn on 11.03.2016.
 * Бин с методом кэширующегося и блокируемого вызова DSSPolicy
 */
@Singleton
@Lock(LockType.READ)
public class DssPolicyCachedProvider {

    @Inject
    private DssPolicyMapper dssPolicyMapper;

    @Inject
    private DssConfigService dssConfigService;

    private LoadingCache<Object, DssPolicy> dssPolicyCache;

    private static final Object POLICY_CACHE_KEY = new Object();

    public DssPolicy getDssPolicy(final WsPortProvider wsPortProvider) throws ISignServiceGetPolicyDssFaultFaultFaultMessage{
        if (dssPolicyCache == null) {
            setDssPolicyInCache(wsPortProvider);
        }
        return dssPolicyCache.getUnchecked(POLICY_CACHE_KEY);
    }

    private void setDssPolicyInCache(final WsPortProvider wsPortProvider) throws ISignServiceGetPolicyDssFaultFaultFaultMessage{
        if (dssPolicyCache == null) {
            int dssPolicyCacheTtlMinutes = dssConfigService.getDssPolicyCacheTtlMinutes();
            dssPolicyCache = CacheBuilder.newBuilder()
                    .expireAfterWrite(dssPolicyCacheTtlMinutes, TimeUnit.MINUTES)
                    .build(new CacheLoader<Object, DssPolicy>() {
                        @Override
                        public DssPolicy load(Object key) throws ISignServiceGetPolicyDssFaultFaultFaultMessage {
                            return getPolicyDirect(wsPortProvider);
                        }
                    });
        }
    }

    @Lock(LockType.WRITE)
    public DssPolicy getPolicyDirect(WsPortProvider wsPortProvider) throws ISignServiceGetPolicyDssFaultFaultFaultMessage{
            DSSPolicy dssPolicy = wsPortProvider.getSignPort().getPolicy();
            return dssPolicyMapper.from(dssPolicy);
    }
}
