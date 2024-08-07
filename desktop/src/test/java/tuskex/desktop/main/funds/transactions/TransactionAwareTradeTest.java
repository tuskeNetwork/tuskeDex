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

import tuskex.core.support.dispute.Dispute;
import tuskex.core.support.dispute.arbitration.ArbitrationManager;
import tuskex.core.support.dispute.refund.RefundManager;
import tuskex.core.trade.Trade;
import tuskex.core.tsk.wallet.TskWalletService;
import javafx.collections.FXCollections;
import monero.wallet.model.MoneroTxWallet;
import org.bitcoinj.core.Sha256Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionAwareTradeTest {
    private static final Sha256Hash XID = Sha256Hash.wrap("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");

    private MoneroTxWallet transaction;
    private ArbitrationManager arbitrationManager;
    private Trade delegate;
    private TransactionAwareTradable trade;
    private RefundManager refundManager;
    private TskWalletService tskWalletService;

    @BeforeEach
    public void setUp() {
        this.transaction = mock(MoneroTxWallet.class);
        when(transaction.getHash()).thenReturn(XID.toString());

        delegate = mock(Trade.class, RETURNS_DEEP_STUBS);
        arbitrationManager = mock(ArbitrationManager.class, RETURNS_DEEP_STUBS);
        refundManager = mock(RefundManager.class, RETURNS_DEEP_STUBS);
        tskWalletService = mock(TskWalletService.class, RETURNS_DEEP_STUBS);
        trade = new TransactionAwareTrade(delegate, arbitrationManager, refundManager, tskWalletService, null);
    }

    @Test
    public void testIsRelatedToTransactionWhenPayoutTx() {
        when(delegate.getPayoutTxId()).thenReturn(XID.toString());
        assertTrue(trade.isRelatedToTransaction(transaction));
    }

    @Test
    public void testIsRelatedToTransactionWhenMakerDepositTx() {
        when(delegate.getMaker().getDepositTxHash()).thenReturn(XID.toString());
        assertTrue(trade.isRelatedToTransaction(transaction));
    }

    @Test
    public void testIsRelatedToTransactionWhenTakerDepositTx() {
        when(delegate.getTaker().getDepositTxHash()).thenReturn(XID.toString());
        assertTrue(trade.isRelatedToTransaction(transaction));
    }

    @Test
    public void testIsRelatedToTransactionWhenDisputedPayoutTx() {
        final String tradeId = "7";

        Dispute dispute = mock(Dispute.class);
        when(dispute.getDisputePayoutTxId()).thenReturn(XID.toString());
        when(dispute.getTradeId()).thenReturn(tradeId);

        when(arbitrationManager.getDisputesAsObservableList())
                .thenReturn(FXCollections.observableArrayList(Set.of(dispute)));

        when(arbitrationManager.getDisputedTradeIds())
                .thenReturn(Set.of(tradeId));

        when(delegate.getId()).thenReturn(tradeId);

        assertTrue(trade.isRelatedToTransaction(transaction));
    }
}
