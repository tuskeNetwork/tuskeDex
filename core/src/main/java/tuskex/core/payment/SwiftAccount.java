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
import tuskex.core.locale.CurrencyUtil;
import tuskex.core.locale.TradeCurrency;
import tuskex.core.payment.payload.PaymentAccountPayload;
import tuskex.core.payment.payload.PaymentMethod;
import tuskex.core.payment.payload.SwiftAccountPayload;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

@EqualsAndHashCode(callSuper = true)
public final class SwiftAccount extends PaymentAccount {

    public static final List<TradeCurrency> SUPPORTED_CURRENCIES = new ArrayList<>(CurrencyUtil.getAllSortedFiatCurrencies(comparing(TradeCurrency::getCode)));

    private static final List<PaymentAccountFormField.FieldId> INPUT_FIELD_IDS = List.of(
            PaymentAccountFormField.FieldId.ACCOUNT_NAME,
            PaymentAccountFormField.FieldId.BANK_SWIFT_CODE,
            PaymentAccountFormField.FieldId.BANK_COUNTRY_CODE,
            PaymentAccountFormField.FieldId.BANK_NAME,
            PaymentAccountFormField.FieldId.BANK_BRANCH,
            PaymentAccountFormField.FieldId.BANK_ADDRESS,
            PaymentAccountFormField.FieldId.INTERMEDIARY_SWIFT_CODE,
            PaymentAccountFormField.FieldId.INTERMEDIARY_COUNTRY_CODE,
            PaymentAccountFormField.FieldId.INTERMEDIARY_NAME,
            PaymentAccountFormField.FieldId.INTERMEDIARY_BRANCH,
            PaymentAccountFormField.FieldId.INTERMEDIARY_ADDRESS,
            PaymentAccountFormField.FieldId.BENEFICIARY_NAME,
            PaymentAccountFormField.FieldId.BENEFICIARY_ACCOUNT_NR,
            PaymentAccountFormField.FieldId.BENEFICIARY_ADDRESS,
            PaymentAccountFormField.FieldId.BENEFICIARY_CITY,
            PaymentAccountFormField.FieldId.BENEFICIARY_PHONE,
            PaymentAccountFormField.FieldId.SPECIAL_INSTRUCTIONS,
            PaymentAccountFormField.FieldId.SALT
    );

    public SwiftAccount() {
        super(PaymentMethod.SWIFT);
        tradeCurrencies.addAll(SUPPORTED_CURRENCIES);
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new SwiftAccountPayload(paymentMethod.getId(), id);
    }

    public SwiftAccountPayload getPayload() {
        return ((SwiftAccountPayload) this.paymentAccountPayload);
    }

    @Override
    public String getMessageForBuyer() {
        return "payment.swift.info.buyer";
    }

    @Override
    public String getMessageForSeller() {
        return "payment.swift.info.seller";
    }

    @Override
    public String getMessageForAccountCreation() {
        return "payment.swift.info.account";
    }

    @Override
    public @NonNull List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }

    @Override
    public @NonNull List<PaymentAccountFormField.FieldId> getInputFieldIds() {
        return INPUT_FIELD_IDS;
    }
}
