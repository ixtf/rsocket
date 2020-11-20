package test;

import com.github.ixtf.japp.core.J;
import io.netty.handler.ssl.SslContextBuilder;
import io.rsocket.Payload;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.loadbalance.LoadbalanceRSocketClient;
import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.ByteBufPayload;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TestClient {
    public static void main(String[] args) {
        final var connector = RSocketConnector.create()
                .setupPayload(ByteBufPayload.create("TestClient"))
                .payloadDecoder(PayloadDecoder.ZERO_COPY);
        final var clientTransport = TcpClientTransport.create(TcpClient.create().port(7878).secure(sslContextSpec -> {
            final var certFile = J.getFile("/Users/jzb/Desktop/cert.pem");
            sslContextSpec.sslContext(SslContextBuilder.forClient().trustManager(certFile));
        }));
        final var loadbalanceTarget = LoadbalanceTarget.from("TestClient", clientTransport);
        final var client = LoadbalanceRSocketClient.create(connector, Mono.just(List.of(loadbalanceTarget)));
        final var payload = client.requestResponse(Mono.just(ByteBufPayload.create("", "TestService")))
                .doOnError(Throwable::printStackTrace)
                .block();
        System.out.println(payload.getDataUtf8());



//        final var client = rSocketConnector
//                .connect(TcpClientTransport.create(7878))
//                .block();
//        client.requestResponse(ByteBufPayload.create("", "TestService"))
//                .doOnError(Throwable::printStackTrace)
//                .subscribe(payload -> {
//                    final var s = UTF_8.decode(payload.getData());
//                    System.out.println(s);
//                });

//        client.onClose().block();
    }
}
