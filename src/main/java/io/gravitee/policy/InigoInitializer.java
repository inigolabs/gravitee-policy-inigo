package io.gravitee.policy;

import com.inigolabs.Foreign;
import com.inigolabs.Inigo;
import io.gravitee.policy.api.PolicyContext;

public class InigoInitializer implements PolicyContext {

    @Override
    public void onActivation() throws Exception {
        Inigo.DownloadLibrary();
        Inigo.LibraryPath = "/opt/graviteeio-gateway/plugins-ext/libinigo.so";

        new Foreign();
    }

    @Override
    public void onDeactivation() throws Exception {}
}
