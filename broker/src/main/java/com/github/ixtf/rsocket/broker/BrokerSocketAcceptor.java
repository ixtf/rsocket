package com.github.ixtf.rsocket.broker;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BrokerSocketAcceptor implements SocketAcceptor {
    private static final Map<String, RSocket> MAP = new ConcurrentHashMap<>();
    private static final RSocket brokerRSocket = new BrokerRSocket();

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
        final var service = setup.getDataUtf8();
        final var wrapSendingSocket = wrapSendingSocket(sendingSocket, service);
        MAP.put(service, wrapSendingSocket);
        log.debug("add " + service);

        return Mono.just(brokerRSocket);
    }

    private RSocket wrapSendingSocket(RSocket sendingSocket, String service) {
        final var rSocket = new ErrorOnDisconnectRSocket(sendingSocket);
        rSocket.onClose().doFinally(s -> {
            MAP.remove(service);
            log.info("Closing socket for {}", service);
        });
        return rSocket;
    }

    private static class BrokerRSocket implements RSocket {
        @Override
        public Mono<Payload> requestResponse(Payload payload) {
            try {
                final var service = payload.getMetadataUtf8();
                final var rSocket = MAP.get(service);
                payload.retain()
                return rSocket.requestResponse(payload);
            } catch (Throwable e) {
                log.error("", e);
                payload.release();
                return Mono.error(new RuntimeException("TODO: fill out values", e)); //TODO:
            }
        }
    }
}
