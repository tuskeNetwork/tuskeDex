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
import tuskex.core.payment.payload.ChaseQuickPayAccountPayload;
import tuskex.core.payment.payload.PaymentAccountPayload;
import tuskex.core.payment.payload.PaymentMethod;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.List;

// Removed due to QuickPay becoming Zelle
// Cannot be deleted as it would break old trade history entries
@Deprecated
@EqualsAndHashCode(callSuper = true)
public final class ChaseQuickPayAccount extends PaymentAccount {

    public static final List<TradeCurrency> SUPPORTED_CURRENCIES = List.of(new TraditionalCurrency("USD"));

    public ChaseQuickPayAccount() {
        super(PaymentMethod.CHASE_QUICK_PAY);
        setSingleTradeCurrency(SUPPORTED_CURRENCIES.get(0));
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new ChaseQuickPayAccountPayload(paymentMethod.getId(), id);
    }

    @Override
    public @NonNull List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }

    @Override
    public @NonNull List<PaymentAccountFormField.FieldId> getInputFieldIds() {
        throw new RuntimeException("Not implemented");
    }

    public void setEmail(String email) {
        ((ChaseQuickPayAccountPayload) paymentAccountPayload).setEmail(email);
    }

    public String getEmail() {
        return ((ChaseQuickPayAccountPayload) paymentAccountPayload).getEmail();
    }

    public void setHolderName(String holderName) {
        ((ChaseQuickPayAccountPayload) paymentAccountPayload).setHolderName(holderName);
    }

    public String getHolderName() {
        return ((ChaseQuickPayAccountPayload) paymentAccountPayload).getHolderName();
    }
}
