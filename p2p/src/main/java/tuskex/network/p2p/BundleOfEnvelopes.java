/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package tuskex.network.p2p;

import tuskex.common.app.Capabilities;
import tuskex.common.app.Capability;
import tuskex.common.app.Version;
import tuskex.common.proto.ProtobufferException;
import tuskex.common.proto.network.NetworkEnvelope;
import tuskex.common.proto.network.NetworkProtoResolver;
import tuskex.network.p2p.storage.messages.BroadcastMessage;
import tuskex.network.p2p.storage.payload.CapabilityRequiringPayload;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Value
public final class BundleOfEnvelopes extends BroadcastMessage implements ExtendedDataSizePermission, CapabilityRequiringPayload {

    private final List<NetworkEnvelope> envelopes;

    public BundleOfEnvelopes() {
        this(new ArrayList<>(), Version.getP2PMessageVersion());
    }

    public BundleOfEnvelopes(List<NetworkEnvelope> envelopes) {
        this(envelopes, Version.getP2PMessageVersion());
    }

    public void add(NetworkEnvelope networkEnvelope) {
        envelopes.add(networkEnvelope);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private BundleOfEnvelopes(List<NetworkEnvelope> envelopes, String messageVersion) {
        super(messageVersion);
        this.envelopes = envelopes;
    }


    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        return getNetworkEnvelopeBuilder()
                .setBundleOfEnvelopes(protobuf.BundleOfEnvelopes.newBuilder().addAllEnvelopes(envelopes.stream()
                        .map(NetworkEnvelope::toProtoNetworkEnvelope)
                        .collect(Collectors.toList())))
                .build();
    }

    public static BundleOfEnvelopes fromProto(protobuf.BundleOfEnvelopes proto,
                                              NetworkProtoResolver resolver,
                                              String messageVersion) {
        List<NetworkEnvelope> envelopes = proto.getEnvelopesList()
                .stream()
                .map(envelope -> {
                    try {
                        return resolver.fromProto(envelope);
                    } catch (ProtobufferException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new BundleOfEnvelopes(envelopes, messageVersion);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // CapabilityRequiringPayload
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Capabilities getRequiredCapabilities() {
        return new Capabilities(Capability.BUNDLE_OF_ENVELOPES);
    }
}
