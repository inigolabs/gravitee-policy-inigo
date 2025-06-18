package io.gravitee.policy;

import io.gravitee.policy.api.PolicyConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InigoPolicyConfig implements PolicyConfiguration {
    private String errorKey = "failure";

    private String token = "";
    private String schema = "";
    private boolean federation = false;
    private boolean federationExample = false;
}
