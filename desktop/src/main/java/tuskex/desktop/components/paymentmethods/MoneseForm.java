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

package tuskex.desktop.components.paymentmethods;

import tuskex.core.account.witness.AccountAgeWitnessService;
import tuskex.core.locale.Res;
import tuskex.core.payment.MoneseAccount;
import tuskex.core.payment.PaymentAccount;
import tuskex.core.payment.payload.MoneseAccountPayload;
import tuskex.core.payment.payload.PaymentAccountPayload;
import tuskex.core.util.coin.CoinFormatter;
import tuskex.core.util.validation.InputValidator;
import tuskex.desktop.components.InputTextField;
import tuskex.desktop.util.FormBuilder;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import static tuskex.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static tuskex.desktop.util.FormBuilder.addCompactTopLabelTextFieldWithCopyIcon;

public class MoneseForm extends PaymentMethodForm {
    private final MoneseAccount account;

    public static int addFormForBuyer(GridPane gridPane, int gridRow,
                                      PaymentAccountPayload paymentAccountPayload) {
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, 0, Res.get("payment.account.owner"),
                ((MoneseAccountPayload) paymentAccountPayload).getHolderName());
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, gridRow, 1, Res.get("payment.mobile"),
                ((MoneseAccountPayload) paymentAccountPayload).getMobileNr());
        return gridRow;
    }

    public MoneseForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService,
                      InputValidator inputValidator, GridPane gridPane,
                      int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.account = (MoneseAccount) paymentAccount;
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        InputTextField holderNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.account.owner"));
        holderNameInputTextField.setValidator(inputValidator);
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            account.setHolderName(newValue.trim());
            updateFromInputs();
        });

        InputTextField mobileNrInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.mobile"));
        mobileNrInputTextField.setValidator(inputValidator);
        mobileNrInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            account.setMobileNr(newValue.trim());
            updateFromInputs();
        });

        addCurrenciesGrid(true);
        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    private void addCurrenciesGrid(boolean isEditable) {
        FlowPane flowPane = FormBuilder.addTopLabelFlowPane(gridPane, ++gridRow,
                Res.get("payment.supportedCurrencies"), 20, 20).second;

        if (isEditable) {
            flowPane.setId("flow-pane-checkboxes-bg");
        } else {
            flowPane.setId("flow-pane-checkboxes-non-editable-bg");
        }

        account.getSupportedCurrencies().forEach(currency ->
                fillUpFlowPaneWithCurrencies(isEditable, flowPane, currency, account));
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(account.getMobileNr());
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(account.getPaymentMethod().getId()));
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner"), account.getHolderName())
                .second.setMouseTransparent(false);
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.mobile"), account.getMobileNr())
                .second.setMouseTransparent(false);
        addLimitations(true);
        addCurrenciesGrid(false);
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && inputValidator.validate(account.getHolderName()).isValid
                && inputValidator.validate(account.getMobileNr()).isValid
                && account.getTradeCurrencies().size() > 0);
    }
}
