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

package tuskex.desktop.main.funds.transactions;

import tuskex.common.crypto.PubKeyRing;
import tuskex.core.support.dispute.Dispute;
import tuskex.core.support.dispute.arbitration.ArbitrationManager;
import tuskex.core.support.dispute.refund.RefundManager;
import tuskex.core.trade.Tradable;
import tuskex.core.trade.Trade;
import tuskex.core.tsk.wallet.TskWalletService;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import monero.wallet.model.MoneroTxWallet;


@Slf4j
class TransactionAwareTrade implements TransactionAwareTradable {
    private final Trade trade;
    private final ArbitrationManager arbitrationManager;
    private final RefundManager refundManager;
    private final TskWalletService tskWalletService;
    private final PubKeyRing pubKeyRing;

    TransactionAwareTrade(Trade trade,
                          ArbitrationManager arbitrationManager,
                          RefundManager refundManager,
                          TskWalletService tskWalletService,
                          PubKeyRing pubKeyRing) {
        this.trade = trade;
        this.arbitrationManager = arbitrationManager;
        this.refundManager = refundManager;
        this.tskWalletService = tskWalletService;
        this.pubKeyRing = pubKeyRing;
    }

    @Override
    public boolean isRelatedToTransaction(MoneroTxWallet transaction) {
        String txId = transaction.getHash();

        boolean isMakerDepositTx = isMakerDepositTx(txId);
        boolean isTakerDepositTx = isTakerDepositTx(txId);
        boolean isPayoutTx = isPayoutTx(txId);
        boolean isDisputedPayoutTx = isDisputedPayoutTx(txId);

        return isMakerDepositTx || isTakerDepositTx || isPayoutTx || isDisputedPayoutTx;
    }

    private boolean isPayoutTx(String txId) {
      return txId.equals(trade.getPayoutTxId());
    }

    private boolean isMakerDepositTx(String txId) {
      return txId.equals(trade.getMaker().getDepositTxHash());
    }

    private boolean isTakerDepositTx(String txId) {
        return txId.equals(trade.getTaker().getDepositTxHash());
    }

    private boolean isDisputedPayoutTx(String txId) {
        String delegateId = trade.getId();
        ObservableList<Dispute> disputes = arbitrationManager.getDisputesAsObservableList();

        boolean isAnyDisputeRelatedToThis = arbitrationManager.getDisputedTradeIds().contains(trade.getId());

        return isAnyDisputeRelatedToThis && disputes.stream()
                .anyMatch(dispute -> {
                    String disputePayoutTxId = dispute.getDisputePayoutTxId();
                    boolean isDisputePayoutTx = txId.equals(disputePayoutTxId);

                    String disputeTradeId = dispute.getTradeId();
                    boolean isDisputeRelatedToThis = delegateId.equals(disputeTradeId);

                    return isDisputePayoutTx && isDisputeRelatedToThis;
                });
    }

//    boolean isDelayedPayoutTx(String txId) {
//        Transaction transaction = btcWalletService.getTransaction(txId);
//        if (transaction == null)
//            return false;
//
//        if (transaction.getLockTime() == 0)
//            return false;
//
//        if (transaction.getInputs() == null)
//            return false;
//
//        return transaction.getInputs().stream()
//                .anyMatch(input -> {
//                    TransactionOutput connectedOutput = input.getConnectedOutput();
//                    if (connectedOutput == null) {
//                        return false;
//                    }
//                    Transaction parentTransaction = connectedOutput.getParentTransaction();
//                    if (parentTransaction == null) {
//                        return false;
//                    }
//                    return isDepositTx(parentTransaction.getTxId());
//                });
//    }
//
//    private boolean isRefundPayoutTx(String txId) {
//        String tradeId = trade.getId();
//        ObservableList<Dispute> disputes = refundManager.getDisputesAsObservableList();
//
//        boolean isAnyDisputeRelatedToThis = refundManager.getDisputedTradeIds().contains(tradeId);
//
//        if (isAnyDisputeRelatedToThis) {
//            Transaction tx = btcWalletService.getTransaction(txId);
//            if (tx != null) {
//                for (TransactionOutput txo : tx.getOutputs()) {
//                    if (btcWalletService.isTransactionOutputMine(txo)) {
//                        try {
//                            Address receiverAddress = txo.getScriptPubKey().getToAddress(btcWalletService.getParams());
//                            Contract contract = checkNotNull(trade.getContract());
//                            String myPayoutAddressString = contract.isMyRoleBuyer(pubKeyRing) ?
//                                    contract.getBuyerPayoutAddressString() :
//                                    contract.getSellerPayoutAddressString();
//                            if (receiverAddress != null && myPayoutAddressString.equals(receiverAddress.toString())) {
//                                return true;
//                            }
//                        } catch (RuntimeException ignore) {
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }

    @Override
    public Tradable asTradable() {
        return trade;
    }
}
