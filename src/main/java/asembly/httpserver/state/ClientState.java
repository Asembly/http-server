package asembly.httpserver.state;

import java.nio.ByteBuffer;

public class ClientState extends ChannelState{

    public ClientState()
    {
        super(ByteBuffer.allocate(8192), null);
    }

}
