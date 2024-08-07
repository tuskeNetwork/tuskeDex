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

package tuskex.core.account.witness;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tuskex.common.config.Config;
import tuskex.common.persistence.PersistenceManager;
import tuskex.network.p2p.storage.payload.PersistableNetworkPayload;
import tuskex.network.p2p.storage.persistence.HistoricalDataStoreService;
import java.io.File;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountAgeWitnessStorageService extends HistoricalDataStoreService<AccountAgeWitnessStore> {
    private static final String FILE_NAME = "AccountAgeWitnessStore";


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public AccountAgeWitnessStorageService(@Named(Config.STORAGE_DIR) File storageDir,
                                           PersistenceManager<AccountAgeWitnessStore> persistenceManager) {
        super(storageDir, persistenceManager);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getFileName() {
        return FILE_NAME;
    }

    @Override
    protected void initializePersistenceManager() {
        persistenceManager.initialize(store, PersistenceManager.Source.NETWORK);
    }

    @Override
    public boolean canHandle(PersistableNetworkPayload payload) {
        return payload instanceof AccountAgeWitness;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected AccountAgeWitnessStore createStore() {
        return new AccountAgeWitnessStore();
    }
}
