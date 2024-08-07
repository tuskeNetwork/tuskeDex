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
import tuskex.core.locale.TradeCurrency;
import tuskex.core.payment.PaymentAccount;
import tuskex.core.payment.USPostalMoneyOrderAccount;
import tuskex.core.payment.payload.PaymentAccountPayload;
import tuskex.core.payment.payload.USPostalMoneyOrderAccountPayload;
import tuskex.core.payment.validation.USPostalMoneyOrderValidator;
import tuskex.core.util.coin.CoinFormatter;
import tuskex.core.util.validation.InputValidator;
import tuskex.desktop.components.InputTextField;
import tuskex.desktop.util.FormBuilder;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import static tuskex.desktop.util.FormBuilder.addCompactTopLabelTextArea;
import static tuskex.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static tuskex.desktop.util.FormBuilder.addCompactTopLabelTextFieldWithCopyIcon;
import static tuskex.desktop.util.FormBuilder.addTopLabelTextArea;
import static tuskex.desktop.util.FormBuilder.addTopLabelTextField;

public class USPostalMoneyOrderForm extends PaymentMethodForm {
    private final USPostalMoneyOrderAccount usPostalMoneyOrderAccount;
    private final USPostalMoneyOrderValidator usPostalMoneyOrderValidator;
    private TextArea postalAddressTextArea;

    public static int addFormForBuyer(GridPane gridPane, int gridRow,
                                      PaymentAccountPayload paymentAccountPayload) {
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, Res.get("payment.account.owner"),
                ((USPostalMoneyOrderAccountPayload) paymentAccountPayload).getHolderName());
        TextArea textArea = addCompactTopLabelTextArea(gridPane, ++gridRow, Res.get("payment.postal.address"), "").second;
        textArea.setMinHeight(70);
        textArea.setEditable(false);
        textArea.setId("text-area-disabled");
        textArea.setText(((USPostalMoneyOrderAccountPayload) paymentAccountPayload).getPostalAddress());
        return gridRow;
    }

    public USPostalMoneyOrderForm(PaymentAccount paymentAccount,
                                  AccountAgeWitnessService accountAgeWitnessService, USPostalMoneyOrderValidator usPostalMoneyOrderValidator,
                                  InputValidator inputValidator, GridPane gridPane, int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.usPostalMoneyOrderAccount = (USPostalMoneyOrderAccount) paymentAccount;
        this.usPostalMoneyOrderValidator = usPostalMoneyOrderValidator;
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        InputTextField holderNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow,
                Res.get("payment.account.owner"));
        holderNameInputTextField.setValidator(inputValidator);
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            usPostalMoneyOrderAccount.setHolderName(newValue);
            updateFromInputs();
        });

        postalAddressTextArea = addTopLabelTextArea(gridPane, ++gridRow,
                Res.get("payment.postal.address"), "").second;
        postalAddressTextArea.setMinHeight(70);
        //postalAddressTextArea.setValidator(usPostalMoneyOrderValidator);
        postalAddressTextArea.textProperty().addListener((ov, oldValue, newValue) -> {
            usPostalMoneyOrderAccount.setPostalAddress(newValue);
            updateFromInputs();
        });


        TradeCurrency singleTradeCurrency = usPostalMoneyOrderAccount.getSingleTradeCurrency();
        String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "null";
        addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"),
                nameAndCode);
        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(usPostalMoneyOrderAccount.getPostalAddress());
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(usPostalMoneyOrderAccount.getPaymentMethod().getId()));
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner"),
                usPostalMoneyOrderAccount.getHolderName());
        TextArea textArea = addCompactTopLabelTextArea(gridPane, ++gridRow, Res.get("payment.postal.address"), "").second;
        textArea.setText(usPostalMoneyOrderAccount.getPostalAddress());
        textArea.setMinHeight(70);
        textArea.setEditable(false);
        TradeCurrency singleTradeCurrency = usPostalMoneyOrderAccount.getSingleTradeCurrency();
        String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "null";
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), nameAndCode);
        addLimitations(true);
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && usPostalMoneyOrderValidator.validate(usPostalMoneyOrderAccount.getPostalAddress()).isValid
                && !usPostalMoneyOrderAccount.getPostalAddress().isEmpty()
                && inputValidator.validate(usPostalMoneyOrderAccount.getHolderName()).isValid
                && usPostalMoneyOrderAccount.getTradeCurrencies().size() > 0);
    }
}
