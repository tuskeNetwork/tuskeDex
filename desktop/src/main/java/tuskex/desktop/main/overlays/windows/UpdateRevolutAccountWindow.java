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

package tuskex.desktop.main.overlays.windows;

import tuskex.core.locale.Res;
import tuskex.core.payment.RevolutAccount;
import tuskex.core.payment.validation.RevolutValidator;
import tuskex.core.user.User;
import tuskex.desktop.components.InputTextField;
import tuskex.desktop.main.overlays.Overlay;
import tuskex.desktop.util.Layout;
import javafx.scene.Scene;

import static tuskex.desktop.util.FormBuilder.addInputTextField;
import static tuskex.desktop.util.FormBuilder.addLabel;

public class UpdateRevolutAccountWindow extends Overlay<UpdateRevolutAccountWindow> {
    private final RevolutValidator revolutValidator;
    private final RevolutAccount revolutAccount;
    private final User user;
    private InputTextField userNameInputTextField;

    public UpdateRevolutAccountWindow(RevolutAccount revolutAccount, User user) {
        super();
        this.revolutAccount = revolutAccount;
        this.user = user;
        type = Type.Attention;
        hideCloseButton = true;
        revolutValidator = new RevolutValidator();
        actionButtonText = Res.get("shared.save");
    }

    @Override
    protected void setupKeyHandler(Scene scene) {
        // We do not support enter or escape here
    }

    @Override
    public void show() {
        if (headLine == null)
            headLine = Res.get("payment.revolut.addUserNameInfo.headLine");

        width = 868;
        createGridPane();
        addHeadLine();
        addContent();
        addButtons();
        applyStyles();
        display();
    }

    private void addContent() {
        addLabel(gridPane, ++rowIndex, Res.get("payment.account.revolut.addUserNameInfo", Res.get("payment.revolut.info"), revolutAccount.getAccountName()));
        userNameInputTextField = addInputTextField(gridPane, ++rowIndex, Res.get("payment.account.username"), Layout.COMPACT_FIRST_ROW_DISTANCE);
        userNameInputTextField.setValidator(revolutValidator);
        userNameInputTextField.textProperty().addListener((observable, oldValue, newValue) ->
                actionButton.setDisable(!revolutValidator.validate(newValue).isValid));
    }

    @Override
    protected void addButtons() {
        super.addButtons();

        // We do not allow close in case the userName is not correctly added so we
        // overwrote the default handler
        actionButton.setOnAction(event -> {
            String userName = userNameInputTextField.getText();
            if (revolutValidator.validate(userName).isValid) {
                revolutAccount.setUsername(userName);
                user.requestPersistence();
                closeHandlerOptional.ifPresent(Runnable::run);
                hide();
            }
        });
        actionButton.setDisable(true);
    }

}

