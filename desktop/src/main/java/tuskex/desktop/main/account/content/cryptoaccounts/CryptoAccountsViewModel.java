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

package tuskex.desktop.main.account.content.cryptoaccounts;

import com.google.inject.Inject;
import tuskex.core.payment.PaymentAccount;
import tuskex.desktop.common.model.ActivatableWithDataModel;
import tuskex.desktop.common.model.ViewModel;
import javafx.collections.ObservableList;

class CryptoAccountsViewModel extends ActivatableWithDataModel<CryptoAccountsDataModel> implements ViewModel {

    @Inject
    public CryptoAccountsViewModel(CryptoAccountsDataModel dataModel) {
        super(dataModel);
    }

    @Override
    protected void activate() {
    }

    @Override
    protected void deactivate() {
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // UI actions
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void onSaveNewAccount(PaymentAccount paymentAccount) {
        dataModel.onSaveNewAccount(paymentAccount);
    }

    public void onUpdateAccount(PaymentAccount paymentAccount) {
        dataModel.onUpdateAccount(paymentAccount);
    }

    public boolean onDeleteAccount(PaymentAccount paymentAccount) {
        return dataModel.onDeleteAccount(paymentAccount);
    }

    public void onSelectAccount(PaymentAccount paymentAccount) {
        dataModel.onSelectAccount(paymentAccount);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////////////////////

    ObservableList<PaymentAccount> getPaymentAccounts() {
        return dataModel.paymentAccounts;
    }
}
