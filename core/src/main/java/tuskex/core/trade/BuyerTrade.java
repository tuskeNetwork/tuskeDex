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

package tuskex.core.trade;

import tuskex.core.offer.Offer;
import tuskex.core.trade.protocol.ProcessModel;
import tuskex.core.tsk.wallet.TskWalletService;
import tuskex.network.p2p.NodeAddress;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public abstract class BuyerTrade extends Trade {
    BuyerTrade(Offer offer,
               BigInteger tradeAmount,
               long tradePrice,
               TskWalletService tskWalletService,
               ProcessModel processModel,
               String uid,
               @Nullable NodeAddress takerNodeAddress,
               @Nullable NodeAddress makerNodeAddress,
               @Nullable NodeAddress arbitratorNodeAddress) {
        super(offer,
                tradeAmount,
                tradePrice,
                tskWalletService,
                processModel,
                uid,
                takerNodeAddress,
                makerNodeAddress,
                arbitratorNodeAddress);
    }

    @Override
    public BigInteger getPayoutAmountBeforeCost() {
        checkNotNull(getAmount(), "Invalid state: getTradeAmount() = null");
        return getAmount().add(getBuyerSecurityDepositBeforeMiningFee());
    }

    @Override
    public boolean confirmPermitted() {
        return true;
    }
}
