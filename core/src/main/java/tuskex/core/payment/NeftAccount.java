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

import tuskex.core.payment.payload.NeftAccountPayload;
import tuskex.core.payment.payload.PaymentAccountPayload;
import tuskex.core.payment.payload.PaymentMethod;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class NeftAccount extends IfscBasedAccount {

    public NeftAccount() {
        super(PaymentMethod.NEFT);
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new NeftAccountPayload(paymentMethod.getId(), id);
    }

    public String getMessageForBuyer() {
        return "payment.neft.info.buyer";
    }

    public String getMessageForSeller() {
        return "payment.neft.info.seller";
    }

    public String getMessageForAccountCreation() {
        return "payment.neft.info.account";
    }

}
