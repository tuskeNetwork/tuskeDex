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

package tuskex.core.arbitration;

import com.google.common.collect.Lists;
import tuskex.common.crypto.PubKeyRing;
import tuskex.core.support.dispute.arbitration.arbitrator.Arbitrator;
import tuskex.network.p2p.NodeAddress;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Disabled;

import java.util.Date;

@SuppressWarnings({"SameParameterValue", "UnusedAssignment"})
public class ArbitratorTest {

    @Disabled("TODO InvalidKeySpecException at tuskex.common.crypto.Sig.getPublicKeyFromBytes(Sig.java:135)")
    public void testRoundtrip() {
        Arbitrator arbitrator = getArbitratorMock();


        Arbitrator newVo = Arbitrator.fromProto(arbitrator.toProtoMessage().getArbitrator());
    }

    public static Arbitrator getArbitratorMock() {
        return new Arbitrator(new NodeAddress("host", 1000),
                new PubKeyRing(getBytes(100), getBytes(100)),
                Lists.newArrayList(),
                new Date().getTime(),
                getBytes(100),
                "registrationSignature",
                null, null, null);
    }

    public static byte[] getBytes(@SuppressWarnings("SameParameterValue") int count) {
        return RandomUtils.nextBytes(count);
    }
}
