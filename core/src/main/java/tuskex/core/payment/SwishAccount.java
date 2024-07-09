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
import tuskex.core.payment.payload.PaymentAccountPayload;
import tuskex.core.payment.payload.PaymentMethod;
import tuskex.core.payment.payload.SwishAccountPayload;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public final class SwishAccount extends PaymentAccount {

    public static final List<TradeCurrency> SUPPORTED_CURRENCIES = List.of(new TraditionalCurrency("SEK"));

    public SwishAccount() {
        super(PaymentMethod.SWISH);
        setSingleTradeCurrency(SUPPORTED_CURRENCIES.get(0));
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new SwishAccountPayload(paymentMethod.getId(), id);
    }

    @Override
    public @NonNull List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }

    @Override
    public @NonNull List<PaymentAccountFormField.FieldId> getInputFieldIds() {
        throw new RuntimeException("Not implemented");
    }

    public void setMobileNr(String mobileNr) {
        ((SwishAccountPayload) paymentAccountPayload).setMobileNr(mobileNr);
    }

    public String getMobileNr() {
        return ((SwishAccountPayload) paymentAccountPayload).getMobileNr();
    }

    public void setHolderName(String holderName) {
        ((SwishAccountPayload) paymentAccountPayload).setHolderName(holderName);
    }

    public String getHolderName() {
        return ((SwishAccountPayload) paymentAccountPayload).getHolderName();
    }
}
