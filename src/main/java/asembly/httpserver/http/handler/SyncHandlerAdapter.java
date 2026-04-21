package asembly.httpserver.http.handler;

import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;

import java.nio.channels.SelectionKey;

public class SyncHandlerAdapter implements Handler {

    private final SyncHandler syncHandler;

    public SyncHandlerAdapter(SyncHandler syncHandler)
    {
       this.syncHandler = syncHandler;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public Response handleSync(Request request) {
        return syncHandler.handle(request);
    }

    @Override
    public void handleAsync(Request request, SelectionKey clientKey) {
        throw new UnsupportedOperationException("AsyncHandler does not support sync handling");
    }
}
