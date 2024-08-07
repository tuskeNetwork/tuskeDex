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

package tuskex.desktop.main.portfolio.closedtrades;

import com.google.inject.Inject;
import tuskex.core.trade.ClosedTradableFormatter;
import tuskex.desktop.common.model.ActivatableWithDataModel;
import tuskex.desktop.common.model.ViewModel;

import java.math.BigInteger;
import java.util.Map;

public class ClosedTradesViewModel extends ActivatableWithDataModel<ClosedTradesDataModel> implements ViewModel {
    private final ClosedTradableFormatter closedTradableFormatter;

    @Inject
    public ClosedTradesViewModel(ClosedTradesDataModel dataModel, ClosedTradableFormatter closedTradableFormatter) {
        super(dataModel);

        this.closedTradableFormatter = closedTradableFormatter;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Used in ClosedTradesSummaryWindow
    ///////////////////////////////////////////////////////////////////////////////////////////

    public BigInteger getTotalTradeAmount() {
        return dataModel.getTotalAmount();
    }

    public String getTotalAmountWithVolume(BigInteger totalTradeAmount) {
        return dataModel.getVolumeInUserFiatCurrency(totalTradeAmount)
                .map(volume -> closedTradableFormatter.getTotalAmountWithVolumeAsString(totalTradeAmount, volume))
                .orElse("");
    }

    public Map<String, String> getTotalVolumeByCurrency() {
        return closedTradableFormatter.getTotalVolumeByCurrencyAsString(dataModel.getListAsTradables());
    }

    public String getTotalTxFee(BigInteger totalTradeAmount) {
        BigInteger totalTxFee = dataModel.getTotalTxFee();
        return closedTradableFormatter.getTotalTxFeeAsString(totalTradeAmount, totalTxFee);
    }

    public String getTotalTradeFee(BigInteger totalTradeAmount) {
        BigInteger totalTradeFee = dataModel.getTotalTradeFee();
        return closedTradableFormatter.getTotalTradeFeeAsString(totalTradeAmount, totalTradeFee);
    }
}
