package com.github.ixtf.rsocket.client;

import com.github.ixtf.japp.core.J;
import io.netty.handler.ssl.SslContextBuilder;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.Resume;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.ByteBufPayload;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;
import reactor.util.retry.Retry;

import java.time.Duration;

public class Client {

    public static void main(String[] args) throws Throwable {
        final var connector = RSocketConnector.create()
                .setupPayload(ByteBufPayload.create("TestService"))
                .acceptor((setup, sendingSocket) -> Mono.just(new RSocket() {
                    @Override
                    public Mono<Payload> requestResponse(Payload payload) {
                        payload.release();
                        return Mono.just(ByteBufPayload.create("TestService requestResponse"));
                    }
                }))
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .reconnect(Retry.indefinitely())
                .keepAlive(Duration.ofDays(10), Duration.ofHours(1));

        final var clientTransport = TcpClientTransport.create(TcpClient.create().port(7878).secure(sslContextSpec -> {
            final var certFile = J.getFile("/Users/jzb/Desktop/cert.pem");
            sslContextSpec.sslContext(SslContextBuilder.forClient().trustManager(certFile));
        }));

        registerService(connector, clientTransport);
        Thread.currentThread().join();
    }

    private static RSocket service;

    private static void registerService(RSocketConnector connector, TcpClientTransport clientTransport) {
        if (service == null) {
            service = connector.connect(clientTransport).block();
            service.onClose().subscribe(it -> {
                System.out.println("onClose");
                service = null;
                registerService(connector, clientTransport);
            });
        }
    }
}
