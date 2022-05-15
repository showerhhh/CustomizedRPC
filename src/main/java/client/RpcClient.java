package client;

import protocol.RpcRequest;

public interface RpcClient {
    public Object sendRequest(RpcRequest rpcRequest);
}
