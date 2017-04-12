package ru.blogic.dss.mapper;

import ru.blogic.dss.api.dto.XMLDSigType;

import java.util.Map;

/**
 * @author dgolubev
 */
public class XmlDSigTypeMapper extends ExplicitMapper<XMLDSigType, String> {

    XmlDSigTypeMapper() {
    }

    @Override
    protected void map(Map<XMLDSigType, String> mappings) {
        mappings.put(XMLDSigType.XML_ENVELOPED, "XMLEnveloped");
        mappings.put(XMLDSigType.XML_ENVELOPING, "XMLEnveloping");
        mappings.put(XMLDSigType.XML_TEMPLATE, "XMLTemplate");
    }
}
