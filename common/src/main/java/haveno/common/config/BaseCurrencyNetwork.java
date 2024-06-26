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

package haveno.common.config;

import lombok.Getter;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.utils.MonetaryFormat;

public enum BaseCurrencyNetwork {
    TSK_MAINNET(new XmrMainNetParams(), "TSK", "MAINNET", "Tuske"),
    XMR_STAGENET(new XmrStageNetParams(), "TSK", "STAGENET", "Tuske"),
    XMR_LOCAL(new XmrTestNetParams(), "TSK", "TESTNET", "Tuske");

    @Getter
    private final NetworkParameters parameters;
    @Getter
    private final String currencyCode;
    @Getter
    private final String network;
    @Getter
    private final String currencyName;

    BaseCurrencyNetwork(NetworkParameters parameters, String currencyCode, String network, String currencyName) {
        this.parameters = parameters;
        this.currencyCode = currencyCode;
        this.network = network;
        this.currencyName = currencyName;
    }

    public boolean isMainnet() {
        return "TSK_MAINNET".equals(name());
    }

    public boolean isTestnet() {
        return "XMR_LOCAL".equals(name());
    }

    public boolean isStagenet() {
        return "XMR_STAGENET".equals(name());
    }

    public long getDefaultMinFeePerVbyte() {
        return 15;  // 2021-02-22 due to mempool congestion, increased from 2
    }

    private static final MonetaryFormat XMR_MONETARY_FORMAT = new MonetaryFormat().minDecimals(2).repeatOptionalDecimals(2, 3).noCode().code(0, "TSK");

    private static class XmrMainNetParams extends MainNetParams {
        @Override
        public MonetaryFormat getMonetaryFormat() {
            return XMR_MONETARY_FORMAT;
        }
    }

    private static class XmrTestNetParams extends RegTestParams {
        @Override
        public MonetaryFormat getMonetaryFormat() {
            return XMR_MONETARY_FORMAT;
        }
    }

    private static class XmrStageNetParams extends MainNetParams {
        @Override
        public MonetaryFormat getMonetaryFormat() {
            return XMR_MONETARY_FORMAT;
        }
    }
}
