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

package tuskex.core.payment;

import tuskex.core.api.model.PaymentAccountFormField;
import tuskex.core.locale.TraditionalCurrency;
import tuskex.core.locale.TradeCurrency;
import tuskex.core.payment.payload.OKPayAccountPayload;
import tuskex.core.payment.payload.PaymentAccountPayload;
import tuskex.core.payment.payload.PaymentMethod;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// Cannot be deleted as it would break old trade history entries
@Deprecated
@EqualsAndHashCode(callSuper = true)
public final class OKPayAccount extends PaymentAccount {

    public static final List<TradeCurrency> SUPPORTED_CURRENCIES = List.of(
            new TraditionalCurrency("AED"),
            new TraditionalCurrency("ARS"),
            new TraditionalCurrency("AUD"),
            new TraditionalCurrency("BRL"),
            new TraditionalCurrency("CAD"),
            new TraditionalCurrency("CHF"),
            new TraditionalCurrency("CNY"),
            new TraditionalCurrency("DKK"),
            new TraditionalCurrency("EUR"),
            new TraditionalCurrency("GBP"),
            new TraditionalCurrency("HKD"),
            new TraditionalCurrency("ILS"),
            new TraditionalCurrency("INR"),
            new TraditionalCurrency("JPY"),
            new TraditionalCurrency("KES"),
            new TraditionalCurrency("MXN"),
            new TraditionalCurrency("NOK"),
            new TraditionalCurrency("NZD"),
            new TraditionalCurrency("PHP"),
            new TraditionalCurrency("PLN"),
            new TraditionalCurrency("SEK"),
            new TraditionalCurrency("SGD"),
            new TraditionalCurrency("USD")
    );

    public OKPayAccount() {
        super(PaymentMethod.OK_PAY);
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new OKPayAccountPayload(paymentMethod.getId(), id);
    }

    @Override
    public @NotNull List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }

    @Override
    public @NotNull List<PaymentAccountFormField.FieldId> getInputFieldIds() {
        throw new RuntimeException("Not implemented");
    }

    public void setAccountNr(String accountNr) {
        ((OKPayAccountPayload) paymentAccountPayload).setAccountNr(accountNr);
    }

    public String getAccountNr() {
        return ((OKPayAccountPayload) paymentAccountPayload).getAccountNr();
    }
}
