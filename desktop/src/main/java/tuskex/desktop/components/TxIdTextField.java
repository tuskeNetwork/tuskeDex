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

package tuskex.desktop.components;

import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import tuskex.common.UserThread;
import tuskex.common.util.Utilities;
import tuskex.core.locale.Res;
import tuskex.core.trade.Trade;
import tuskex.core.user.BlockChainExplorer;
import tuskex.core.user.Preferences;
import tuskex.core.tsk.wallet.TskWalletService;
import tuskex.desktop.components.indicator.TxConfidenceIndicator;
import tuskex.desktop.util.GUIUtil;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;
import monero.daemon.model.MoneroTx;
import monero.wallet.model.MoneroWalletListener;

import javax.annotation.Nullable;

public class TxIdTextField extends AnchorPane {
    @Setter
    private static Preferences preferences;
    @Setter
    private static TskWalletService tskWalletService;

    @Getter
    private final TextField textField;
    private final Tooltip progressIndicatorTooltip;
    private final TxConfidenceIndicator txConfidenceIndicator;
    private final Label copyIcon, blockExplorerIcon, missingTxWarningIcon;

    private MoneroWalletListener walletListener;
    private ChangeListener<Number> tradeListener;
    private Trade trade;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    public TxIdTextField() {
        txConfidenceIndicator = new TxConfidenceIndicator();
        txConfidenceIndicator.setFocusTraversable(false);
        txConfidenceIndicator.setMaxSize(20, 20);
        txConfidenceIndicator.setId("funds-confidence");
        txConfidenceIndicator.setLayoutY(1);
        txConfidenceIndicator.setProgress(0);
        txConfidenceIndicator.setVisible(false);
        AnchorPane.setRightAnchor(txConfidenceIndicator, 0.0);
        AnchorPane.setTopAnchor(txConfidenceIndicator, 3.0);
        progressIndicatorTooltip = new Tooltip("-");
        txConfidenceIndicator.setTooltip(progressIndicatorTooltip);

        copyIcon = new Label();
        copyIcon.setLayoutY(3);
        copyIcon.getStyleClass().addAll("icon", "highlight");
        copyIcon.setTooltip(new Tooltip(Res.get("txIdTextField.copyIcon.tooltip")));
        AwesomeDude.setIcon(copyIcon, AwesomeIcon.COPY);
        AnchorPane.setRightAnchor(copyIcon, 30.0);

        Tooltip tooltip = new Tooltip(Res.get("txIdTextField.blockExplorerIcon.tooltip"));

        blockExplorerIcon = new Label();
        blockExplorerIcon.getStyleClass().addAll("icon", "highlight");
        blockExplorerIcon.setTooltip(tooltip);
        AwesomeDude.setIcon(blockExplorerIcon, AwesomeIcon.EXTERNAL_LINK);
        blockExplorerIcon.setMinWidth(20);
        AnchorPane.setRightAnchor(blockExplorerIcon, 52.0);
        AnchorPane.setTopAnchor(blockExplorerIcon, 4.0);

        missingTxWarningIcon = new Label();
        missingTxWarningIcon.getStyleClass().addAll("icon", "error-icon");
        AwesomeDude.setIcon(missingTxWarningIcon, AwesomeIcon.WARNING_SIGN);
        missingTxWarningIcon.setTooltip(new Tooltip(Res.get("txIdTextField.missingTx.warning.tooltip")));
        missingTxWarningIcon.setMinWidth(20);
        AnchorPane.setRightAnchor(missingTxWarningIcon, 52.0);
        AnchorPane.setTopAnchor(missingTxWarningIcon, 4.0);
        missingTxWarningIcon.setVisible(false);
        missingTxWarningIcon.setManaged(false);

        textField = new JFXTextField();
        textField.setId("address-text-field");
        textField.setEditable(false);
        textField.setTooltip(tooltip);
        AnchorPane.setRightAnchor(textField, 80.0);
        AnchorPane.setLeftAnchor(textField, 0.0);
        textField.focusTraversableProperty().set(focusTraversableProperty().get());
        getChildren().addAll(textField, missingTxWarningIcon, blockExplorerIcon, copyIcon, txConfidenceIndicator);
    }

    public void setup(@Nullable String txId) {
        setup(txId, null);
    }

    public void setup(@Nullable String txId, Trade trade) {
        this.trade = trade;
        if (walletListener != null) {
            tskWalletService.removeWalletListener(walletListener);
            walletListener = null;
        }
        if (tradeListener != null) {
            trade.getDepositTxsUpdateCounter().removeListener(tradeListener);
            tradeListener = null;
        }

        if (txId == null) {
            textField.setText(Res.get("shared.na"));
            textField.setId("address-text-field-error");
            blockExplorerIcon.setVisible(false);
            blockExplorerIcon.setManaged(false);
            copyIcon.setVisible(false);
            copyIcon.setManaged(false);
            txConfidenceIndicator.setVisible(false);
            missingTxWarningIcon.setVisible(true);
            missingTxWarningIcon.setManaged(true);
            return;
        }

        // subscribe for tx updates
        if (trade == null) {
            walletListener = new MoneroWalletListener() {
                @Override
                public void onNewBlock(long height) {
                    updateConfidence(txId, trade, false, height);
                }
            };
            tskWalletService.addWalletListener(walletListener); // TODO: this only listens for new blocks, listen for double spend
        } else {
            tradeListener = (observable, oldValue, newValue) -> {
                updateConfidence(txId, trade, null, null);
            };
            trade.getDepositTxsUpdateCounter().addListener(tradeListener);
        }

        textField.setText(txId);
        textField.setOnMouseClicked(mouseEvent -> openBlockExplorer(txId));
        blockExplorerIcon.setOnMouseClicked(mouseEvent -> openBlockExplorer(txId));
        copyIcon.setOnMouseClicked(e -> Utilities.copyToClipboard(txId));
        txConfidenceIndicator.setVisible(true);

        // update off main thread
        new Thread(() -> updateConfidence(txId, trade, true, null)).start();
    }

    public void cleanup() {
        if (tskWalletService != null && walletListener != null) {
            tskWalletService.removeWalletListener(walletListener);
            walletListener = null;
        }
        if (tradeListener != null) {
            trade.getDepositTxsUpdateCounter().removeListener(tradeListener);
            tradeListener = null;
        }
        trade = null;
        textField.setOnMouseClicked(null);
        blockExplorerIcon.setOnMouseClicked(null);
        copyIcon.setOnMouseClicked(null);
        textField.setText("");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void openBlockExplorer(String txId) {
        if (preferences != null) {
            BlockChainExplorer blockChainExplorer = preferences.getBlockChainExplorer();
            GUIUtil.openWebPage(blockChainExplorer.txUrl + txId, false);
        }
    }

    private synchronized void updateConfidence(String txId, Trade trade, Boolean useCache, Long height) {
        MoneroTx tx = null;
        try {
            if (trade == null) {
                tx = useCache ? tskWalletService.getDaemonTxWithCache(txId) : tskWalletService.getDaemonTx(txId);
                tx.setNumConfirmations(tx.isConfirmed() ? (height == null ? tskWalletService.getConnectionService().getLastInfo().getHeight() : height) - tx.getHeight(): 0l); // TODO: don't set if tx.getNumConfirmations() works reliably on non-local testnet
            } else {
                if (txId.equals(trade.getMaker().getDepositTxHash())) tx = trade.getMakerDepositTx();
                else if (txId.equals(trade.getTaker().getDepositTxHash())) tx = trade.getTakerDepositTx();
            }
        } catch (Exception e) {
            // do nothing
        }
        updateConfidence(tx, trade);
    }

    private void updateConfidence(MoneroTx tx, Trade trade) {
        UserThread.execute(() -> {
            GUIUtil.updateConfidence(tx, trade, progressIndicatorTooltip, txConfidenceIndicator);
            if (txConfidenceIndicator.getProgress() != 0) {
                AnchorPane.setRightAnchor(txConfidenceIndicator, 0.0);
            }
            if (txConfidenceIndicator.getProgress() >= 1.0 && walletListener != null) {
                tskWalletService.removeWalletListener(walletListener); // unregister listener
                walletListener = null;
            }
        });
    }
}
