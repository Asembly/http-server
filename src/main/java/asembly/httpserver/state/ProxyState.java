package asembly.httpserver.state;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ProxyState extends ChannelState{
    private final SocketChannel client;
    private final ClientState clientState;

    public ProxyState(ByteBuffer requestOutput, ClientState clientState, SocketChannel client)
    {
        super(ByteBuffer.allocate(8192), requestOutput);
        this.client = client;
        this.clientState = clientState;
    }

    public SocketChannel getClient()
    {
        return client;
    }

    public ClientState getClientState()
    {
        return clientState;
    }

}
