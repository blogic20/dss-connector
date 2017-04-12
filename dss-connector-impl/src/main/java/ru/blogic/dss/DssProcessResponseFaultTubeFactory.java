package ru.blogic.dss;

import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;

import javax.xml.ws.WebServiceException;

/**
 * Created by pkupershteyn on 10.08.2016.
 * Factory для @see {@link DssProcessResponseFaultTube}
 */
public class DssProcessResponseFaultTubeFactory implements TubeFactory {
    @Override
    public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
        return new DssProcessResponseFaultTube(context.getTubelineHead(), DssProcessResponseFaultTube.Side.Client);
    }

    @Override
    public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
        return new DssProcessResponseFaultTube(context.getTubelineHead(), DssProcessResponseFaultTube.Side.Server);
    }
}
