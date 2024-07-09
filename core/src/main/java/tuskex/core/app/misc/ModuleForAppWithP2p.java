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

package tuskex.core.app.misc;

import com.google.inject.Singleton;
import tuskex.common.ClockWatcher;
import tuskex.common.app.AppModule;
import tuskex.common.config.Config;
import tuskex.common.crypto.KeyRing;
import tuskex.common.crypto.KeyStorage;
import tuskex.common.proto.network.NetworkProtoResolver;
import tuskex.common.proto.persistable.PersistenceProtoResolver;
import tuskex.core.alert.AlertModule;
import tuskex.core.app.TorSetup;
import tuskex.core.filter.FilterModule;
import tuskex.core.network.CoreBanFilter;
import tuskex.core.network.p2p.seed.DefaultSeedNodeRepository;
import tuskex.core.offer.OfferModule;
import tuskex.core.proto.network.CoreNetworkProtoResolver;
import tuskex.core.proto.persistable.CorePersistenceProtoResolver;
import tuskex.core.trade.TradeModule;
import tuskex.core.user.Preferences;
import tuskex.core.user.User;
import tuskex.core.tsk.TskConnectionModule;
import tuskex.core.tsk.TskModule;
import tuskex.network.crypto.EncryptionServiceModule;
import tuskex.network.p2p.P2PModule;
import tuskex.network.p2p.network.BanFilter;
import tuskex.network.p2p.network.BridgeAddressProvider;
import tuskex.network.p2p.seed.SeedNodeRepository;

import java.io.File;

import static com.google.inject.name.Names.named;
import static tuskex.common.config.Config.KEY_STORAGE_DIR;
import static tuskex.common.config.Config.PREVENT_PERIODIC_SHUTDOWN_AT_SEED_NODE;
import static tuskex.common.config.Config.REFERRAL_ID;
import static tuskex.common.config.Config.STORAGE_DIR;
import static tuskex.common.config.Config.USE_DEV_MODE;
import static tuskex.common.config.Config.USE_DEV_MODE_HEADER;
import static tuskex.common.config.Config.USE_DEV_PRIVILEGE_KEYS;

public class ModuleForAppWithP2p extends AppModule {

    public ModuleForAppWithP2p(Config config) {
        super(config);
    }

    @Override
    protected void configure() {
        bind(Config.class).toInstance(config);

        bind(KeyStorage.class).in(Singleton.class);
        bind(KeyRing.class).in(Singleton.class);
        bind(User.class).in(Singleton.class);
        bind(ClockWatcher.class).in(Singleton.class);
        bind(NetworkProtoResolver.class).to(CoreNetworkProtoResolver.class).in(Singleton.class);
        bind(PersistenceProtoResolver.class).to(CorePersistenceProtoResolver.class).in(Singleton.class);
        bind(Preferences.class).in(Singleton.class);
        bind(BridgeAddressProvider.class).to(Preferences.class).in(Singleton.class);
        bind(TorSetup.class).in(Singleton.class);

        bind(SeedNodeRepository.class).to(DefaultSeedNodeRepository.class).in(Singleton.class);
        bind(BanFilter.class).to(CoreBanFilter.class).in(Singleton.class);

        bind(File.class).annotatedWith(named(STORAGE_DIR)).toInstance(config.storageDir);
        bind(File.class).annotatedWith(named(KEY_STORAGE_DIR)).toInstance(config.keyStorageDir);

        bindConstant().annotatedWith(named(USE_DEV_PRIVILEGE_KEYS)).to(config.useDevPrivilegeKeys);
        bindConstant().annotatedWith(named(USE_DEV_MODE)).to(config.useDevMode);
        bindConstant().annotatedWith(named(USE_DEV_MODE_HEADER)).to(config.useDevModeHeader);
        bindConstant().annotatedWith(named(REFERRAL_ID)).to(config.referralId);
        bindConstant().annotatedWith(named(PREVENT_PERIODIC_SHUTDOWN_AT_SEED_NODE)).to(config.preventPeriodicShutdownAtSeedNode);

        // ordering is used for shut down sequence
        install(new TradeModule(config));
        install(new EncryptionServiceModule(config));
        install(new OfferModule(config));
        install(new P2PModule(config));
        install(new TskModule(config));
        install(new AlertModule(config));
        install(new FilterModule(config));
        install(new TskConnectionModule(config));
    }
}
