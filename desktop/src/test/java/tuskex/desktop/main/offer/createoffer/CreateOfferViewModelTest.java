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

package tuskex.desktop.main.offer.createoffer;

import tuskex.common.config.Config;
import tuskex.core.account.witness.AccountAgeWitnessService;
import tuskex.core.locale.Country;
import tuskex.core.locale.CryptoCurrency;
import tuskex.core.locale.GlobalSettings;
import tuskex.core.locale.Res;
import tuskex.core.offer.CreateOfferService;
import tuskex.core.offer.OfferDirection;
import tuskex.core.offer.OfferUtil;
import tuskex.core.payment.PaymentAccount;
import tuskex.core.payment.payload.PaymentMethod;
import tuskex.core.payment.validation.SecurityDepositValidator;
import tuskex.core.payment.validation.TskValidator;
import tuskex.core.provider.price.MarketPrice;
import tuskex.core.provider.price.PriceFeedService;
import tuskex.core.trade.statistics.TradeStatisticsManager;
import tuskex.core.user.Preferences;
import tuskex.core.user.User;
import tuskex.core.util.coin.CoinFormatter;
import tuskex.core.util.coin.ImmutableCoinFormatter;
import tuskex.core.util.validation.AmountValidator8Decimals;
import tuskex.core.util.validation.AmountValidator4Decimals;
import tuskex.core.util.validation.InputValidator;
import tuskex.core.tsk.model.TskAddressEntry;
import tuskex.core.tsk.wallet.TskWalletService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

import static tuskex.desktop.maker.PreferenceMakers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateOfferViewModelTest {

    private CreateOfferViewModel model;
    private final CoinFormatter coinFormatter = new ImmutableCoinFormatter(
            Config.baseCurrencyNetworkParameters().getMonetaryFormat());

    @BeforeEach
    public void setUp() {
        final CryptoCurrency tsk = new CryptoCurrency("TSK", "monero");
        GlobalSettings.setDefaultTradeCurrency(tsk);
        Res.setup();

        final TskValidator btcValidator = new TskValidator();
        final AmountValidator8Decimals priceValidator8Decimals = new AmountValidator8Decimals();
        final AmountValidator4Decimals priceValidator4Decimals = new AmountValidator4Decimals();

        TskAddressEntry addressEntry = mock(TskAddressEntry.class);
        TskWalletService tskWalletService = mock(TskWalletService.class);
        PriceFeedService priceFeedService = mock(PriceFeedService.class);
        User user = mock(User.class);
        PaymentAccount paymentAccount = mock(PaymentAccount.class);
        Preferences preferences = mock(Preferences.class);
        SecurityDepositValidator securityDepositValidator = mock(SecurityDepositValidator.class);
        AccountAgeWitnessService accountAgeWitnessService = mock(AccountAgeWitnessService.class);
        CreateOfferService createOfferService = mock(CreateOfferService.class);
        OfferUtil offerUtil = mock(OfferUtil.class);
        var tradeStats = mock(TradeStatisticsManager.class);

        when(tskWalletService.getOrCreateAddressEntry(anyString(), any())).thenReturn(addressEntry);
        when(tskWalletService.getBalanceForSubaddress(any(Integer.class))).thenReturn(BigInteger.valueOf(10000000L));
        when(priceFeedService.updateCounterProperty()).thenReturn(new SimpleIntegerProperty());
        when(priceFeedService.getMarketPrice(anyString())).thenReturn(
                new MarketPrice("USD",
                        12684.0450,
                        Instant.now().getEpochSecond(),
                        true));
        when(user.findFirstPaymentAccountWithCurrency(any())).thenReturn(paymentAccount);
        when(paymentAccount.getPaymentMethod()).thenReturn(PaymentMethod.ZELLE);
        when(user.getPaymentAccountsAsObservable()).thenReturn(FXCollections.observableSet());
        when(securityDepositValidator.validate(any())).thenReturn(new InputValidator.ValidationResult(false));
        when(accountAgeWitnessService.getMyTradeLimit(any(), any(), any())).thenReturn(100000000L);
        when(preferences.getUserCountry()).thenReturn(new Country("ES", "Spain", null));
        when(createOfferService.getRandomOfferId()).thenReturn(UUID.randomUUID().toString());
        when(tradeStats.getObservableTradeStatisticsSet()).thenReturn(FXCollections.observableSet());

        CreateOfferDataModel dataModel = new CreateOfferDataModel(createOfferService,
            null,
            offerUtil,
            tskWalletService,
            empty,
            user,
            null,
            priceFeedService,
            accountAgeWitnessService,
            coinFormatter,
            tradeStats,
            null);
        dataModel.initWithData(OfferDirection.BUY, new CryptoCurrency("TSK", "monero"));
        dataModel.activate();

        model = new CreateOfferViewModel(dataModel,
                null,
                priceValidator4Decimals,
                priceValidator8Decimals,
                btcValidator,
                securityDepositValidator,
                priceFeedService,
                null,
                null,
                preferences,
                coinFormatter,
                offerUtil);
        model.activate();
    }

    @Test
    public void testSyncMinAmountWithAmountUntilChanged() {
        assertNull(model.amount.get());
        assertNull(model.minAmount.get());

        model.amount.set("0.0");
        assertEquals("0.0", model.amount.get());
        assertNull(model.minAmount.get());

        model.amount.set("0.03");

        assertEquals("0.03", model.amount.get());
        assertEquals("0.03", model.minAmount.get());

        model.amount.set("0.0312");

        assertEquals("0.0312", model.amount.get());
        assertEquals("0.0312", model.minAmount.get());

        model.minAmount.set("0.01");
        model.onFocusOutMinAmountTextField(true, false);

        assertEquals("0.01", model.minAmount.get());

        model.amount.set("0.0301");

        assertEquals("0.0301", model.amount.get());
        assertEquals("0.01", model.minAmount.get());
    }

    @Test
    public void testSyncMinAmountWithAmountWhenZeroCoinIsSet() {
        model.amount.set("0.03");

        assertEquals("0.03", model.amount.get());
        assertEquals("0.03", model.minAmount.get());

        model.minAmount.set("0.00");
        model.onFocusOutMinAmountTextField(true, false);

        model.amount.set("0.04");

        assertEquals("0.04", model.amount.get());
        assertEquals("0.04", model.minAmount.get());

    }

    @Test
    public void testSyncMinAmountWithAmountWhenSameValueIsSet() {
        model.amount.set("0.03");

        assertEquals("0.03", model.amount.get());
        assertEquals("0.03", model.minAmount.get());

        model.minAmount.set("0.03");
        model.onFocusOutMinAmountTextField(true, false);

        model.amount.set("0.04");

        assertEquals("0.04", model.amount.get());
        assertEquals("0.04", model.minAmount.get());
    }

    @Test
    public void testSyncMinAmountWithAmountWhenHigherMinAmountValueIsSet() {
        model.amount.set("0.03");

        assertEquals("0.03", model.amount.get());
        assertEquals("0.03", model.minAmount.get());

        model.minAmount.set("0.05");
        model.onFocusOutMinAmountTextField(true, false);

        assertEquals("0.05", model.amount.get());
        assertEquals("0.05", model.minAmount.get());
    }

    @Test
    public void testSyncPriceMarginWithVolumeAndFixedPrice() {
        model.amount.set("0.01");
        model.onFocusOutPriceAsPercentageTextField(true, false); //leave focus without changing
        assertEquals("0.00", model.marketPriceMargin.get());
        assertEquals("0.00000078", model.volume.get());
        assertEquals("12684.04500000", model.price.get());
    }
}
