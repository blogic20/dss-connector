package ru.blogic.dss.mapper;

import ru.blogic.dss.api.dto.SignerInfo;
import ru.blogic.dss.common.util.CollectionUtilsExt;
import ru.blogic.dss.common.util.Grouping;
import ru.blogic.dss.common.util.MapUtilsExt;
import ru.blogic.dss.common.util.Transformer;
import ru.cryptopro.dss.services.schemas._2014._06.ArrayOfSignerInfo;
import ru.cryptopro.dss.services.schemas._2014._06.SignersInfo;

import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dgolubev
 */
public class SignersInfoMapper extends NullSafeMapper<List<SignerInfo>, SignersInfo> {

    @Inject
    private CertificateInfoMapper certificateInfoMapper;

    SignersInfoMapper() {
    }

    @Override
    protected List<SignerInfo> nullSafeFrom(SignersInfo source) {
        final JAXBElement<ArrayOfSignerInfo> wrappedSignersList = source.getSignerInfoList();
        if (wrappedSignersList == null || wrappedSignersList.getValue() == null) {
            return new ArrayList<SignerInfo>();
        }

        final List<ru.cryptopro.dss.services.schemas._2014._06.SignerInfo> signersInfo
                = wrappedSignersList.getValue().getSignerInfo();

        final Map<String, ru.cryptopro.dss.services.schemas._2014._06.SignerInfo> byId = MapUtilsExt.byKey(
                signersInfo,
                new Grouping<String, ru.cryptopro.dss.services.schemas._2014._06.SignerInfo>() {
                    @Override
                    public String group(ru.cryptopro.dss.services.schemas._2014._06.SignerInfo value) {
                        return value.getId().getValue();
                    }
                });

        final Set<String> parentIds = CollectionUtilsExt.transformToSet(
                signersInfo,
                new Transformer<ru.cryptopro.dss.services.schemas._2014._06.SignerInfo, String>() {
                    @Override
                    public String transform(ru.cryptopro.dss.services.schemas._2014._06.SignerInfo value) {
                        return value.getParentId().getValue();
                    }
                });

        ru.cryptopro.dss.services.schemas._2014._06.SignerInfo unreferenced = null;
        for (ru.cryptopro.dss.services.schemas._2014._06.SignerInfo signer : signersInfo) {
            if (!parentIds.contains(signer.getId().getValue())) {
                unreferenced = signer;
                break;
            }
        }

        final SignerInfo tail = transform(byId, unreferenced);
        return CollectionUtilsExt.toList(tail);
    }

    @Override
    protected SignersInfo nullSafeTo(List<SignerInfo> source) {
        throw new UnsupportedOperationException();
    }

    private SignerInfo transform(Map<String, ru.cryptopro.dss.services.schemas._2014._06.SignerInfo> signers,
                                 ru.cryptopro.dss.services.schemas._2014._06.SignerInfo source) {
        final ru.cryptopro.dss.services.schemas._2014._06.SignerInfo parent = signers.get(source.getParentId().getValue());

        return new SignerInfo(
                source.getId().getValue(),
                source.getIndex(),
                certificateInfoMapper.from(source.getSignerCertificateInfo().getValue()),
                parent != null ? transform(signers, parent) : null
        );
    }
}
