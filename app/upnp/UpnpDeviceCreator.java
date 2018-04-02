/*
 * Copyright 2018 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package upnp;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UDN;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Created by alex on 17/04/17
 **/
@SuppressWarnings("unchecked")
public class UpnpDeviceCreator implements Callable<Void> {

    private final String suffix;
    private final int port;
    private final Path datumPath;
    private final UpnpService upnpService;

    public UpnpDeviceCreator(String suffix, int port, Path datumPath, UpnpService upnpService) {
        this.suffix = suffix;
        this.port = port;
        this.datumPath = datumPath;
        this.upnpService = upnpService;
    }

    @Override
    public Void call() throws IOException, ValidationException {
        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier("Flac Manager"));
        final String serviceName = "FlacManagerService" + suffix;
        DeviceType deviceType = new UDADeviceType(serviceName, 1);
        DeviceDetails deviceDetails = new DeviceDetails(
                "Flac Manager",
                new ManufacturerDetails("Alex Jones"),
                new ModelDetails(serviceName, "Flac Manager Server", "v1"));
        Icon icon = new Icon("image/png", 48, 48, 8,
                FlacManagerService.class.getClassLoader().getResource("upnp/icon.png"));
        LocalService flacManagerService =
                new AnnotationLocalServiceBinder().read(
                        FlacManagerService.class,
                        new UDAServiceId(serviceName),
                        new UDAServiceType(serviceName),
                        true,
                        Collections.emptySet());
        flacManagerService.setManager(new DefaultServiceManager(flacManagerService, FlacManagerService.class));
        LocalDevice device = new LocalDevice(identity, deviceType, deviceDetails, icon, flacManagerService);
        upnpService.getRegistry().addDevice(device);
        runAction(flacManagerService, "SetPort", port);
        runAction(flacManagerService, "SetDatumFilename", datumPath.getFileName().toString());
        return null;
    }

    private void runAction(LocalService service, String actionName, Object value) {
        Optional<Action> maybeSetPortAction = Arrays.
                stream(service.getActions()).
                filter(action -> actionName.equals(action.getName())).
                findFirst();
        maybeSetPortAction.ifPresent(setPortAction -> {
            ActionArgumentValue arg = new ActionArgumentValue(setPortAction.getFirstInputArgument(), value);
            final ActionInvocation actionInvocation = new ActionInvocation(setPortAction, new ActionArgumentValue[]{arg});
            ActionCallback callback = new ActionCallback.Default(actionInvocation, upnpService.getControlPoint());
            callback.run();
        });

    }
}
