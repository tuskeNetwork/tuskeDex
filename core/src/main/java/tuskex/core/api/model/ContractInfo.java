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

package tuskex.core.api.model;

import tuskex.common.Payload;
import tuskex.core.payment.payload.PaymentAccountPayload;
import tuskex.core.proto.CoreProtoResolver;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * A lightweight Trade Contract constructed from a trade's json contract.
 * Many fields in the core Contract are ignored, but can be added as needed.
 */
@Getter
public class ContractInfo implements Payload {

    private final String buyerNodeAddress;
    private final String sellerNodeAddress;
    private final String arbitratorNodeAddress;
    private final boolean isBuyerMakerAndSellerTaker;
    private final String makerAccountId;
    private final String takerAccountId;
    private final PaymentAccountPayload makerPaymentAccountPayload;
    private final PaymentAccountPayload takerPaymentAccountPayload;
    private final String makerPayoutAddressString;
    private final String takerPayoutAddressString;

    public ContractInfo(String buyerNodeAddress,
                        String sellerNodeAddress,
                        String arbitratorNodeAddress,
                        boolean isBuyerMakerAndSellerTaker,
                        String makerAccountId,
                        String takerAccountId,
                        PaymentAccountPayload makerPaymentAccountPayload,
                        PaymentAccountPayload takerPaymentAccountPayload,
                        String makerPayoutAddressString,
                        String takerPayoutAddressString) {
        this.buyerNodeAddress = buyerNodeAddress;
        this.sellerNodeAddress = sellerNodeAddress;
        this.arbitratorNodeAddress = arbitratorNodeAddress;
        this.isBuyerMakerAndSellerTaker = isBuyerMakerAndSellerTaker;
        this.makerAccountId = makerAccountId;
        this.takerAccountId = takerAccountId;
        this.makerPaymentAccountPayload = makerPaymentAccountPayload;
        this.takerPaymentAccountPayload = takerPaymentAccountPayload;
        this.makerPayoutAddressString = makerPayoutAddressString;
        this.takerPayoutAddressString = takerPayoutAddressString;
    }


    // For transmitting TradeInfo messages when no contract is available.
    public static Supplier<ContractInfo> emptyContract = () ->
            new ContractInfo("",
                    "",
                    "",
                    false,
                    "",
                    "",
                    null,
                    null,
                    "",
                    "");

    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static ContractInfo fromProto(tuskex.proto.grpc.ContractInfo proto) {
        CoreProtoResolver coreProtoResolver = new CoreProtoResolver();
        return new ContractInfo(proto.getBuyerNodeAddress(),
                proto.getSellerNodeAddress(),
                proto.getArbitratorNodeAddress(),
                proto.getIsBuyerMakerAndSellerTaker(),
                proto.getMakerAccountId(),
                proto.getTakerAccountId(),
                proto.getMakerPaymentAccountPayload() == null ? null : PaymentAccountPayload.fromProto(proto.getMakerPaymentAccountPayload(), coreProtoResolver),
                proto.getTakerPaymentAccountPayload() == null ? null : PaymentAccountPayload.fromProto(proto.getTakerPaymentAccountPayload(), coreProtoResolver),
                proto.getMakerPayoutAddressString(),
                proto.getTakerPayoutAddressString());
    }

    @Override
    public tuskex.proto.grpc.ContractInfo toProtoMessage() {
        tuskex.proto.grpc.ContractInfo.Builder builder = tuskex.proto.grpc.ContractInfo.newBuilder()
                .setBuyerNodeAddress(buyerNodeAddress)
                .setSellerNodeAddress(sellerNodeAddress)
                .setArbitratorNodeAddress(arbitratorNodeAddress)
                .setIsBuyerMakerAndSellerTaker(isBuyerMakerAndSellerTaker)
                .setMakerAccountId(makerAccountId)
                .setTakerAccountId(takerAccountId)
                .setMakerPayoutAddressString(makerPayoutAddressString)
                .setTakerPayoutAddressString(takerPayoutAddressString);
       if (makerPaymentAccountPayload != null) builder.setMakerPaymentAccountPayload((protobuf.PaymentAccountPayload) makerPaymentAccountPayload.toProtoMessage());
       if (takerPaymentAccountPayload != null) builder.setTakerPaymentAccountPayload((protobuf.PaymentAccountPayload) takerPaymentAccountPayload.toProtoMessage());
       return builder.build();
    }
}
