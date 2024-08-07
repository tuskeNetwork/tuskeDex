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
import tuskex.core.account.witness.AccountAgeWitnessService;
import tuskex.core.monetary.Price;
import tuskex.core.monetary.Volume;
import tuskex.core.provider.price.MarketPrice;
import tuskex.core.provider.price.PriceFeedService;
import tuskex.core.trade.ClosedTradableFormatter;
import tuskex.core.trade.ClosedTradableManager;
import tuskex.core.trade.ClosedTradableUtil;
import tuskex.core.trade.Tradable;
import tuskex.core.trade.Trade;
import tuskex.core.trade.TradeManager;
import tuskex.core.user.Preferences;
import tuskex.core.util.PriceUtil;
import tuskex.core.util.VolumeUtil;
import tuskex.desktop.common.model.ActivatableDataModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ClosedTradesDataModel extends ActivatableDataModel {

    final ClosedTradableManager closedTradableManager;
    final ClosedTradableFormatter closedTradableFormatter;
    private final Preferences preferences;
    private final PriceFeedService priceFeedService;
    final AccountAgeWitnessService accountAgeWitnessService;
    private final ObservableList<ClosedTradesListItem> list = FXCollections.observableArrayList();
    private final ListChangeListener<Tradable> tradesListChangeListener;
    private final TradeManager tradeManager;

    @Inject
    public ClosedTradesDataModel(ClosedTradableManager closedTradableManager,
                                 ClosedTradableFormatter closedTradableFormatter,
                                 Preferences preferences,
                                 PriceFeedService priceFeedService,
                                 AccountAgeWitnessService accountAgeWitnessService,
                                 TradeManager tradeManager) {
        this.closedTradableManager = closedTradableManager;
        this.closedTradableFormatter = closedTradableFormatter;
        this.preferences = preferences;
        this.priceFeedService = priceFeedService;
        this.accountAgeWitnessService = accountAgeWitnessService;
        this.tradeManager = tradeManager;

        tradesListChangeListener = change -> applyList();
    }

    @Override
    protected void activate() {
        applyList();
        closedTradableManager.getObservableList().addListener(tradesListChangeListener);
    }

    @Override
    protected void deactivate() {
        closedTradableManager.getObservableList().removeListener(tradesListChangeListener);
    }

    ObservableList<ClosedTradesListItem> getList() {
        return list;
    }

    List<Tradable> getListAsTradables() {
        return list.stream().map(ClosedTradesListItem::getTradable).collect(Collectors.toList());
    }

    BigInteger getTotalAmount() {
        return ClosedTradableUtil.getTotalAmount(getListAsTradables());
    }

    Optional<Volume> getVolumeInUserFiatCurrency(BigInteger amount) {
        return getVolume(amount, preferences.getPreferredTradeCurrency().getCode());
    }

    Optional<Volume> getVolume(BigInteger amount, String currencyCode) {
        MarketPrice marketPrice = priceFeedService.getMarketPrice(currencyCode);
        if (marketPrice == null) {
            return Optional.empty();
        }

        Price price = PriceUtil.marketPriceToPrice(marketPrice);
        return Optional.of(VolumeUtil.getVolume(amount, price));
    }

    BigInteger getTotalTxFee() {
        return ClosedTradableUtil.getTotalTxFee(getListAsTradables());
    }

    BigInteger getTotalTradeFee() {
        return closedTradableManager.getTotalTradeFee(getListAsTradables());
    }

    boolean isCurrencyForTradeFeeBtc(Tradable item) {
        return item != null;
    }

    private void applyList() {
        list.clear();
        list.addAll(
                closedTradableManager.getObservableList().stream()
                        .map(tradable -> new ClosedTradesListItem(tradable, closedTradableFormatter, closedTradableManager))
                        .collect(Collectors.toList())
        );
        // We sort by date, the earliest first
        list.sort((o1, o2) -> o2.getTradable().getDate().compareTo(o1.getTradable().getDate()));
    }

    public void onMoveTradeToPendingTrades(Trade trade) {
        tradeManager.onMoveClosedTradeToPendingTrades(trade);
    }
}
