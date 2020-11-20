package com.github.ixtf.rsocket.broker;

import com.github.ixtf.japp.core.J;
import io.netty.handler.ssl.SslContextBuilder;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.netty.tcp.TcpServer;

import javax.net.ssl.KeyManager;

public class Broker {
    public static void main(String[] args) {
        RSocketServer.create(new BrokerSocketAcceptor())
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .interceptors(interceptorRegistry -> {
                    interceptorRegistry.forRequester(r -> {
                        System.out.println(r);
                        return r;
                    });
                })
//                .bindNow(TcpServerTransport.create(7878))
                .bindNow(TcpServerTransport.create(TcpServer.create().port(7878).secure(sslContextSpec -> {
                    final var keyFile = J.getFile("/Users/jzb/Desktop/key.pem");
                    final var certFile = J.getFile("/Users/jzb/Desktop/cert.pem");
                    sslContextSpec.sslContext(SslContextBuilder.forServer(certFile, keyFile));
                })))
                .onClose()
                .block();
    }
}
