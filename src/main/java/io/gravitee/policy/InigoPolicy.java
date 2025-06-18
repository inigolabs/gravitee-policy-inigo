package io.gravitee.policy;

import com.inigolabs.Foreign;
import com.inigolabs.Inigo;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.stream.BufferedReadWriteStream;
import io.gravitee.gateway.api.stream.ReadWriteStream;
import io.gravitee.gateway.api.stream.SimpleReadWriteStream;
import io.gravitee.policy.api.PolicyChain;
import io.gravitee.policy.api.PolicyResult;
import io.gravitee.policy.api.annotations.OnRequestContent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InigoPolicy {
    private final long instance;

    public InigoPolicy(InigoPolicyConfig configuration) {
        var config = new Inigo.Config();
        config.Token = configuration.getToken();
        config.Schema = configuration.getSchema();
        config.Federation = configuration.isFederation();
        config.FederationExample = configuration.isFederationExample();

        config.Name += " : gravitee";
        if (configuration.isFederation()) {
            config.Name += " : gateway";
        }

        instance = Foreign.Create(config);
    }

    @OnRequestContent
    public ReadWriteStream<?> onRequestContent(ExecutionContext context, Request request, PolicyChain chain) {
        return new BufferedReadWriteStream() {
            private Buffer buffer = Buffer.buffer();

            @Override
            public SimpleReadWriteStream<Buffer> write(Buffer content) {
                if (content != null) {
                    buffer.appendBuffer(content);
                }
                super.write(content);
                return this;
            }

            @Override
            public void end() {
                var inigoRequest = Foreign.ProcessRequest(instance, null, request.headers(), buffer.getBytes());
                if (inigoRequest == null) {
                    log.error("Inigo request processing failed, returning error response");
                    chain.streamFailWith(PolicyResult.failure(500, "Inigo request processing failed"));
                    return;
                }
                chain.streamFailWith(PolicyResult.build(inigoRequest.StatusCode(), null, inigoRequest.Output(), null, "application/json"));
            }
        };
    }
}
