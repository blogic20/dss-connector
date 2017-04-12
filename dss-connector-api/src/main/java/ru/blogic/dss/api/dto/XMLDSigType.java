package ru.blogic.dss.api.dto;

/**
 * @author dgolubev
 */
public enum XMLDSigType {
    /**
     * Вложенная XMLDSig подпись (элемент подписи добавляется в корнеаой элемент подписанной XML)
     */
    XML_ENVELOPED,
    /**
     * Присоединённая XMLDSig подпись (подвписываемые данные встраиваются в XML-структуру подписи)
     */
    XML_ENVELOPING,
    /**
     * XMLDSig подпись по шаблону
     */
    XML_TEMPLATE
}
