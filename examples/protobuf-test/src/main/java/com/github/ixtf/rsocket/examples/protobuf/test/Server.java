package com.github.ixtf.rsocket.examples.protobuf.test;

import com.github.ixtf.rsocket.examples.protobuf.test.proto.SimpleServiceServer;
import io.rsocket.core.RSocketServer;
import io.rsocket.rpc.rsocket.RequestHandlingRSocket;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class Server {
    public static void main(String[] args) {
        final var serviceServer = new SimpleServiceServer(new DefaultSimpleService(), Optional.empty(), Optional.empty());
        final var closeableChannel = RSocketServer
                .create((setup, sendingSocket) -> Mono.just(new RequestHandlingRSocket(serviceServer)))
                .transport(TcpServerTransport.create(8081))
                .start()
                .block();
    }
}
