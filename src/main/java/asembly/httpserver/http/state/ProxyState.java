package asembly.httpserver.http.state;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ProxyState extends ClientState {
    private final SocketChannel client;
    private final ClientState clientState;

    public ProxyState(ByteBuffer requestOutput, ClientState clientState, SocketChannel client)
    {
        super();
        this.client = client;
        this.clientState = clientState;
        setOutput(requestOutput);
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
