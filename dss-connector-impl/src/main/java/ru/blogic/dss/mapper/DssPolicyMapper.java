package ru.blogic.dss.mapper;

import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfKeyValueOfstringArrayOfstringty7Ep6D1;
import ru.blogic.dss.api.dto.DssAction;
import ru.blogic.dss.api.dto.EkuTemplate;
import ru.blogic.dss.api.dto.SignatureType;
import ru.blogic.dss.api.dto.dsspolicy.DssActionInfo;
import ru.blogic.dss.api.dto.dsspolicy.DssCAPolicy;
import ru.blogic.dss.api.dto.dsspolicy.DssCspPolicy;
import ru.blogic.dss.api.dto.dsspolicy.DssPolicy;
import ru.blogic.dss.api.dto.dsspolicy.SubjectNameComponent;
import ru.blogic.dss.api.dto.dsspolicy.TspService;
import ru.cryptopro.dss.services.schemas._2014._06.DSSAction;
import ru.cryptopro.dss.services.schemas._2014._06.DSSCAPolicy;
import ru.cryptopro.dss.services.schemas._2014._06.DSSCSPPolicy;
import ru.cryptopro.dss.services.schemas._2014._06.DSSPolicy;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class DssPolicyMapper extends NullSafeMapper<DssPolicy, DSSPolicy> {

    @Inject
    private MfaPolicyMapper mfaPolicyMapper;

    @Inject
    private DssCaTypeMapper dssCaTypeMapper;

    @Inject
    private SignatureTypeMapper signatureTypeMapper;

    @Inject
    private PinCodeModeMapper pinCodeModeMapper;

    @Override
    protected DssPolicy nullSafeFrom(DSSPolicy dssPolicy) {
        DssPolicy dssPolicyBean = new DssPolicy();
        // Set action policy
        List<DssActionInfo> dssActionInfos = new ArrayList<DssActionInfo>();
        if (!dssPolicy.getActionPolicy().isNil()) {
            for (DSSAction dssAction : dssPolicy.getActionPolicy().getValue().getDSSAction()) {
                DssActionInfo dssActionInfo = new DssActionInfo();

                String dssActionName = dssAction.getAction().size() > 0 ? dssAction.getAction().get(0) : "";
                dssActionInfo.setAction(DssAction.fromValue(dssActionName));

                dssActionInfo.setDisplayName(dssAction.getDisplayName().getValue());
                dssActionInfo.setMfaRequired(dssAction.isMfaRequired());
                dssActionInfo.setUri(dssAction.getUri().getValue());

            }
            ;
        }
        dssPolicyBean.setActionPolicy(dssActionInfos);
        //Set signature types
        List<SignatureType> allowedSignatureTypes = new ArrayList<SignatureType>();
        if (!dssPolicy.getAllowedSignatureTypes().isNil()) {
            dssPolicyBean.setAllowedSignatureTypes(signatureTypeMapper.to(dssPolicy.getAllowedSignatureTypes().getValue().getSignatureType()));
        }


        //Set CA policy
        List<DssCAPolicy> caPolicyDto = new ArrayList<DssCAPolicy>();
        dssPolicyBean.setCAPolicy(caPolicyDto);
        if (!dssPolicy.getCAPolicy().isNil()) {
            for (DSSCAPolicy policy : dssPolicy.getCAPolicy().getValue().getDSSCAPolicy()) {
                DssCAPolicy dsscaPolicyBean = new DssCAPolicy();
                dsscaPolicyBean.setActive(policy.isActive());
                dsscaPolicyBean.setName(policy.getName().getValue());
                dsscaPolicyBean.setAllowUserMode(policy.isAllowUserMode());
                dsscaPolicyBean.setCAType(dssCaTypeMapper.to(policy.getCAType()));
                dsscaPolicyBean.setID(policy.getID());
                dsscaPolicyBean.setSNChangesEnable(policy.isSNChangesEnable());
                List<EkuTemplate> ekuTemplates = new ArrayList<EkuTemplate>();
                dsscaPolicyBean.setEkuTemplates(ekuTemplates);
                if (!policy.getEKUTemplates().isNil()) {
                    for (ArrayOfKeyValueOfstringArrayOfstringty7Ep6D1.KeyValueOfstringArrayOfstringty7Ep6D1 ekuTemplateFromDss : policy.getEKUTemplates().getValue().getKeyValueOfstringArrayOfstringty7Ep6D1()) {
                        ekuTemplates.add(new EkuTemplate(ekuTemplateFromDss.getKey(), ekuTemplateFromDss.getValue().getString()));
                    }
                }

                List<SubjectNameComponent> namePolicy = new ArrayList<SubjectNameComponent>();
                dsscaPolicyBean.setNamePolicy(namePolicy);
                if (!policy.getNamePolicy().isNil()) {
                    for (ru.cryptopro.dss.services.schemas._2014._06.SubjectNameComponent subjectNameComponent : policy.getNamePolicy().getValue().getSubjectNameComponent()) {
                        SubjectNameComponent subjectNameComponentBean = new SubjectNameComponent();
                        subjectNameComponentBean.setName(subjectNameComponent.getName().getValue());
                        subjectNameComponentBean.setOID(subjectNameComponent.getOID().getValue());
                        subjectNameComponentBean.setOrder(subjectNameComponent.getOrder());
                        subjectNameComponentBean.setRequired(subjectNameComponent.isIsRequired());
                        subjectNameComponentBean.setStringIdentifier(subjectNameComponent.getStringIdentifier().getValue());
                        subjectNameComponentBean.setValue(subjectNameComponent.getValue().getValue());

                        namePolicy.add(subjectNameComponentBean);
                    }
                }
                caPolicyDto.add(dsscaPolicyBean);
            }
        }
        //set CSPsPolicy
        List<DssCspPolicy> CSPsPolicy = new ArrayList<DssCspPolicy>();
        dssPolicyBean.setCSPsPolicy(CSPsPolicy);
        if (!dssPolicy.getCSPsPolicy().isNil()) {
            for (DSSCSPPolicy dsscspPolicy : dssPolicy.getCSPsPolicy().getValue().getDSSCSPPolicy()) {
                DssCspPolicy dssCspPolicyBean = new DssCspPolicy();
                dssCspPolicyBean.setAlias(dsscspPolicy.getAlias().getValue());
                dssCspPolicyBean.setID(dsscspPolicy.getID());
                List<String> hashAlgorithms = new ArrayList<String>();
                if (!dsscspPolicy.getHashAlgorithms().isNil()) {
                    Collections.copy(hashAlgorithms, dsscspPolicy.getHashAlgorithms().getValue().getString());
                }
                dssCspPolicyBean.setHashAlgorithms(hashAlgorithms);
                dssCspPolicyBean.setKeyLength(dsscspPolicy.getKeyLength());
                dssCspPolicyBean.setProviderName(dsscspPolicy.getProviderName().getValue());
                dssCspPolicyBean.setProviderType(dsscspPolicy.getProviderType());

                CSPsPolicy.add(dssCspPolicyBean);
            }
        }

        //Set pinCodeMode
        dssPolicyBean.setPinCodeMode(pinCodeModeMapper.to(dssPolicy.getPinCodeMode()));
        //Set transactionConfirmation
        dssPolicyBean.setTransactionConfirmation(mfaPolicyMapper.to(dssPolicy.getTransactionConfirmation()));
        //Set TspServices
        List<TspService> tspServices = new ArrayList<TspService>();
        dssPolicyBean.setTspServices(tspServices);
        if (!dssPolicy.getTspServices().isNil()) {
            for (ru.cryptopro.dss.services.schemas._2014._06.TspService tspService : dssPolicy.getTspServices().getValue().getTspService()) {
                TspService tspServiceBean = new TspService();
                tspServiceBean.setName(tspService.getName().getValue());
                tspServiceBean.setTitle(tspService.getTitle().getValue());
                tspServiceBean.setUrl(tspService.getUrl().getValue());
                tspServices.add(tspServiceBean);
            }
        }

        return dssPolicyBean;
    }

    @Override
    protected DSSPolicy nullSafeTo(DssPolicy source) {
        throw new UnsupportedOperationException();
    }
}
