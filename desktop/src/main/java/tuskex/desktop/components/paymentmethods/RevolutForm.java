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

import tuskex.common.util.Tuple2;
import tuskex.core.account.witness.AccountAgeWitnessService;
import tuskex.core.locale.Res;
import tuskex.core.payment.PaymentAccount;
import tuskex.core.payment.RevolutAccount;
import tuskex.core.payment.payload.PaymentAccountPayload;
import tuskex.core.payment.payload.RevolutAccountPayload;
import tuskex.core.payment.validation.RevolutValidator;
import tuskex.core.util.coin.CoinFormatter;
import tuskex.core.util.validation.InputValidator;
import tuskex.desktop.components.InputTextField;
import tuskex.desktop.util.FormBuilder;
import tuskex.desktop.util.Layout;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;

import static tuskex.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static tuskex.desktop.util.FormBuilder.addCompactTopLabelTextFieldWithCopyIcon;
import static tuskex.desktop.util.FormBuilder.addTopLabelFlowPane;

@Slf4j
public class RevolutForm extends PaymentMethodForm {
    private final RevolutAccount account;
    private final RevolutValidator validator;

    public static int addFormForBuyer(GridPane gridPane, int gridRow,
                                      PaymentAccountPayload paymentAccountPayload) {
        Tuple2<String, String> tuple = ((RevolutAccountPayload) paymentAccountPayload).getRecipientsAccountData();
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, tuple.first, tuple.second);
        return gridRow;
    }

    public RevolutForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService,
                       RevolutValidator revolutValidator, InputValidator inputValidator, GridPane gridPane,
                       int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.account = (RevolutAccount) paymentAccount;
        this.validator = revolutValidator;
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        InputTextField userNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.account.username"));
        userNameInputTextField.setValidator(validator);
        userNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            account.setUsername(newValue.trim());
            updateFromInputs();
        });

        addCurrenciesGrid(true);
        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    private void addCurrenciesGrid(boolean isEditable) {
        FlowPane flowPane = addTopLabelFlowPane(gridPane, ++gridRow,
                Res.get("payment.supportedCurrencies"), Layout.FLOATING_LABEL_DISTANCE * 3,
                Layout.FLOATING_LABEL_DISTANCE * 3).second;

        if (isEditable)
            flowPane.setId("flow-pane-checkboxes-bg");
        else
            flowPane.setId("flow-pane-checkboxes-non-editable-bg");

        account.getSupportedCurrencies().forEach(e ->
                fillUpFlowPaneWithCurrencies(isEditable, flowPane, e, account));
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(account.getUsername());
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(account.getPaymentMethod().getId()));

        String userName = account.getUsername();
        TextField userNameTf = addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.username"), userName).second;
        userNameTf.setMouseTransparent(false);

        addLimitations(true);
        addCurrenciesGrid(false);
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && validator.validate(account.getUsername()).isValid
                && account.getTradeCurrencies().size() > 0);
    }
}
