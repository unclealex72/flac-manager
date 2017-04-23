/*
 * Copyright 2017 Alex Jones
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

package client;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.RemoteService;
import scala.Function1;
import scala.concurrent.Promise;

/**
 * A class to retrieve the URL and datum filename from a Upnp server. Written in
 * Java to avoid Scala type inference ugliness
 * Created by alex on 17/04/17
 **/
class ServerDetailsHelper {

    /**
     * Execute a Get command
     * @param controlPoint The Upnp control point
     * @param service The Upnp service
     * @param propertyName The name of the property to get.
     * @param promise A promise to be used to store the result.
     * @param valueAdaptor A function used to turn a string into the correct type.
     * @param <T> The type of the returned property.
     */
    public static <T> void executeGetter(
            ControlPoint controlPoint,
            RemoteService service,
            String propertyName,
            Promise<T> promise,
            Function1<String, T> valueAdaptor) {
        final Action<RemoteService> action = service.getAction("Get" + propertyName);
        final ActionInvocation<RemoteService> invocation = new ActionInvocation<>(action);
        controlPoint.execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                String value = invocation.getOutput(propertyName).getValue().toString();
                promise.success(valueAdaptor.apply(value));
            }
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                promise.failure(new Exception(defaultMsg));
            }
        });
    }
}
