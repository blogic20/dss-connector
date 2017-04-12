package ru.blogic.dss;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

/**
 * Created by pkupershteyn on 10.08.2016.
 * Metro в текущей рализации имеет баг: при получаении в ответ на зашиврованны пакет незашифрованного ответа с ошибкой
 * - результарующий exception от Metro - "Invalid Security Header", т.е. Metro всё равно пытается его дешифровать.
 * Этот tube призван решить проблему: он должен быть расположен перед SecurityTube, и тогда он, при обнаружении SOAP сообщения с ошибкой
 * генерирует читаемый exception.
 * Вызов его factory (@see {@link DssProcessResponseFaultTubeFactory}) должен быть расположен в metro-default.xml
 * (metro\tubelines\tubeline\client-side) НИЖЕ чем вызов com.sun.xml.wss.provider.wsit.SecurityTubeFactory
 *
 */
public class DssProcessResponseFaultTube extends AbstractFilterTubeImpl {

    enum Side {
        Client,
        Server
    }

    private final Side side;

    public Side getSide() {
        return side;
    }

    public DssProcessResponseFaultTube(Tube next, Side side) {
        super(next);
        this.side = side;
    }

    public DssProcessResponseFaultTube(DssProcessResponseFaultTube that, TubeCloner cloner) {
        super(that, cloner);
        this.side = that.getSide();
    }

    @Override
    public AbstractTubeImpl copy(TubeCloner tubeCloner) {
        return new DssProcessResponseFaultTube(this, tubeCloner);
    }

    @Override
    public NextAction processResponse(Packet response) {
        Message message = response.getMessage();
        if (message == null || !message.isFault()) {
            return super.processResponse(response);
        }
        try {
            // Прямой вызов message.readAsSOAPMessage() вызывает NPE, поэтому сначала вызываем copy()
            SOAPFault fault = message.copy().readAsSOAPMessage().getSOAPBody().getFault();
            throw new SOAPFaultException(fault);
        } catch (SOAPException e) {
            throw new RuntimeException("Could not parse SOAP fault",e);
        }


    }

    @Override
    public NextAction processException(Throwable t) {
        return super.processException(t);
    }
}
