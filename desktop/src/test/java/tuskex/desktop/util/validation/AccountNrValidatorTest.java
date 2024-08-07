package tuskex.desktop.util.validation;

import tuskex.core.locale.Res;
import tuskex.core.payment.validation.AccountNrValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountNrValidatorTest {

    @BeforeEach
    public void setup() {
        Locale.setDefault(new Locale("en", "US"));
        Res.setBaseCurrencyCode("TSK");
        Res.setBaseCurrencyName("Monero");
    }

    @Test
    public void testValidationForArgentina() {
        AccountNrValidator validator = new AccountNrValidator("AR");

        assertTrue(validator.validate("4009041813520").isValid);
        assertTrue(validator.validate("035-005198/5").isValid);
    }
}
