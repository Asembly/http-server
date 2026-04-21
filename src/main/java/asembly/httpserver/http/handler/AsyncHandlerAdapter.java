package asembly.httpserver.http.handler;

import asembly.httpserver.http.Request;
import asembly.httpserver.http.Response;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class AsyncHandlerAdapter implements Handler{

    private final AsyncHandler asyncHandler;

    public AsyncHandlerAdapter(AsyncHandler asyncHandler)
    {
       this.asyncHandler = asyncHandler;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public Response handleSync(Request request) {
        throw new UnsupportedOperationException("SyncHandler does not support async handling");
    }

    @Override
    public void handleAsync(Request request, SelectionKey clientKey) throws IOException {
        asyncHandler.handle(request, clientKey);
    }
}
