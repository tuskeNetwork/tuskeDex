package tuskex.core.payment.validation;

import com.google.inject.Inject;
import tuskex.core.locale.Res;
import tuskex.core.util.validation.InputValidator;
import tuskex.core.util.validation.RegexValidator;

public class InteracETransferQuestionValidator extends InputValidator {
    private LengthValidator lengthValidator;
    private RegexValidator regexValidator;

    @Inject
    public InteracETransferQuestionValidator(LengthValidator lengthValidator, RegexValidator regexValidator) {

        lengthValidator.setMinLength(1);
        lengthValidator.setMaxLength(160);
        this.lengthValidator = lengthValidator;

        regexValidator.setPattern("[A-Za-z0-9\\-\\_\\'\\,\\.\\? ]+");
        regexValidator.setErrorMessage(Res.get("validation.interacETransfer.invalidQuestion"));
        this.regexValidator = regexValidator;
    }

    @Override
    public ValidationResult validate(String input) {
        ValidationResult result = super.validate(input);

        if (result.isValid)
            result = lengthValidator.validate(input);
        if (result.isValid)
            result = regexValidator.validate(input);

        return result;
    }
}
