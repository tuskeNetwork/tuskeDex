package tuskex.desktop.components.paymentmethods;

import tuskex.core.account.witness.AccountAgeWitnessService;
import tuskex.core.locale.Res;
import tuskex.core.locale.TradeCurrency;
import tuskex.core.payment.PaymentAccount;
import tuskex.core.util.coin.CoinFormatter;
import tuskex.core.util.validation.InputValidator;
import tuskex.desktop.components.InputTextField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static tuskex.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static tuskex.desktop.util.FormBuilder.addInputTextField;
import static tuskex.desktop.util.FormBuilder.addTopLabelTextField;

public abstract class GeneralAccountNumberForm extends PaymentMethodForm {

    GeneralAccountNumberForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService, InputValidator inputValidator, GridPane gridPane, int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        InputTextField accountNrInputTextField = addInputTextField(gridPane, ++gridRow, Res.get("payment.account.no"));
        accountNrInputTextField.setValidator(inputValidator);
        accountNrInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            setAccountNumber(newValue);
            updateFromInputs();
        });

        addTradeCurrency();

        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    public void addTradeCurrency() {
        final TradeCurrency singleTradeCurrency = paymentAccount.getSingleTradeCurrency();
        final String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "";
        addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), nameAndCode);
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(getAccountNr());
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"), Res.get(paymentAccount.getPaymentMethod().getId()));
        TextField field = addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.no"), getAccountNr()).second;
        field.setMouseTransparent(false);

        final String nameAndCode = paymentAccount.getSingleTradeCurrency() != null ? paymentAccount.getSingleTradeCurrency().getNameAndCode() : "";
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), nameAndCode);

        addLimitations(true);
    }


    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && inputValidator.validate(getAccountNr()).isValid
                && paymentAccount.getTradeCurrencies().size() > 0);
    }

    abstract void setAccountNumber(String newValue);

    abstract String getAccountNr();
}
