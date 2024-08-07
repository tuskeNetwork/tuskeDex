/*
 * This file is part of Tuskex.
 *
 * Tuskex is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Tuskex is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Tuskex. If not, see <http://www.gnu.org/licenses/>.
 */

package tuskex.core.trade.messages;

import tuskex.common.proto.ProtoUtil;

public enum TradeProtocolVersion {
    MULTISIG_2_3;

    public static TradeProtocolVersion fromProto(
            protobuf.TradeProtocolVersion tradeProtocolVersion) {
        return ProtoUtil.enumFromProto(TradeProtocolVersion.class, tradeProtocolVersion.name());
    }

    public static protobuf.TradeProtocolVersion toProtoMessage(TradeProtocolVersion tradeProtocolVersion) {
        return protobuf.TradeProtocolVersion.valueOf(tradeProtocolVersion.name());
    }
}