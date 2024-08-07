# See docs/installing.md

build: localnet tuskex

clean:
	./gradlew clean

clean-localnet:
	rm -rf .localnet

localnet:
	mkdir -p .localnet

tuskex:
	./gradlew build

update-dependencies:
	./gradlew --refresh-dependencies && ./gradlew --write-verification-metadata sha256

# build tuskex without tests
skip-tests: localnet
	./gradlew build -x test -x checkstyleMain -x checkstyleTest

# quick build desktop and daemon apps without tests
tuskex-apps:
	./gradlew :core:compileJava :desktop:build -x test -x checkstyleMain -x checkstyleTest

refresh-deps:
	./gradlew --write-verification-metadata sha256 && ./gradlew build --refresh-keys --refresh-dependencies -x test -x checkstyleMain -x checkstyleTest

deploy:
	# create a new screen session named 'localnet'
	screen -dmS localnet
	# deploy each node in its own named screen window
	for target in \
		seednode-local \
		user1-desktop-local \
		user2-desktop-local \
		arbitrator-desktop-local; do \
			screen -S localnet -X screen -t $$target; \
			screen -S localnet -p $$target -X stuff "make $$target\n"; \
		done;
	# give bitcoind rpc server time to start
	sleep 5

bitcoind:
	./.localnet/bitcoind \
		-regtest \
		-peerbloomfilters=1 \
		-datadir=.localnet/ \
		-rpcuser=tuskex \
		-rpcpassword=1234 \

btc-blocks:
	./.localnet/bitcoin-cli \
		-regtest \
		-rpcuser=tuskex \
		-rpcpassword=1234 \
		generatetoaddress 101 bcrt1q6j90vywv8x7eyevcnn2tn2wrlg3vsjlsvt46qz

.PHONY: build seednode localnet

# Local network

monerod1-local:
	./.localnet/monerod \
		--testnet \
		--no-igd \
		--hide-my-port \
		--data-dir .localnet/tsk_local/node1 \
		--p2p-bind-ip 127.0.0.1 \
		--log-level 0 \
		--add-exclusive-node 127.0.0.1:48080 \
		--add-exclusive-node 127.0.0.1:58080 \
		--rpc-access-control-origins http://localhost:8080 \
		--fixed-difficulty 500 \
		--disable-rpc-ban \

monerod2-local:
	./.localnet/monerod \
		--testnet \
		--no-igd \
		--hide-my-port \
		--data-dir .localnet/tsk_local/node2 \
		--p2p-bind-ip 127.0.0.1 \
		--p2p-bind-port 48080 \
		--rpc-bind-port 48081 \
		--zmq-rpc-bind-port 48082 \
		--log-level 0 \
		--confirm-external-bind \
		--add-exclusive-node 127.0.0.1:28080 \
		--add-exclusive-node 127.0.0.1:58080 \
		--rpc-access-control-origins http://localhost:8080 \
		--fixed-difficulty 500 \
		--disable-rpc-ban \

monerod3-local:
	./.localnet/monerod \
		--testnet \
		--no-igd \
		--hide-my-port \
		--data-dir .localnet/tsk_local/node3 \
		--p2p-bind-ip 127.0.0.1 \
		--p2p-bind-port 58080 \
		--rpc-bind-port 58081 \
		--zmq-rpc-bind-port 58082 \
		--log-level 0 \
		--confirm-external-bind \
		--add-exclusive-node 127.0.0.1:28080 \
		--add-exclusive-node 127.0.0.1:48080 \
		--rpc-access-control-origins http://localhost:8080 \
		--fixed-difficulty 500 \
		--disable-rpc-ban \

#--proxy 127.0.0.1:49775 \

funding-wallet-local:
	./.localnet/monero-wallet-rpc \
		--testnet \
		--daemon-address http://localhost:28081 \
		--rpc-bind-port 28084 \
		--rpc-login rpc_user:abc123 \
		--rpc-access-control-origins http://localhost:8080 \
		--wallet-dir ./.localnet \

funding-wallet-stagenet:
	./.localnet/monero-wallet-rpc \
		--stagenet \
		--rpc-bind-port 38084 \
		--rpc-login rpc_user:abc123 \
		--rpc-access-control-origins http://localhost:8080 \
		--wallet-dir ./.localnet \
		--daemon-ssl-allow-any-cert \
		--daemon-address http://127.0.0.1:38081 \

funding-wallet-mainnet:
	./.localnet/monero-wallet-rpc \
		--rpc-bind-port 18084 \
		--rpc-login rpc_user:abc123 \
		--rpc-access-control-origins http://localhost:8080 \
		--wallet-dir ./.localnet \

# use .bat extension for windows binaries
APP_EXT :=
ifeq ($(OS),Windows_NT)
	APP_EXT := .bat
endif

seednode-local:
	./tuskex-seednode$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=2002 \
		--appName=tuskex-TSK_LOCAL_Seed_2002 \
		--tskNode=http://localhost:28081 \

seednode2-local:
	./tuskex-seednode$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=2003 \
		--appName=tuskex-TSK_LOCAL_Seed_2003 \
		--tskNode=http://localhost:28081 \

arbitrator-daemon-local:
	# Arbitrator needs to be registered before making trades
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=4444 \
		--appName=tuskex-TSK_LOCAL_arbitrator \
		--apiPassword=apitest \
		--apiPort=9998 \
		--passwordRequired=false \
		--useNativeTskWallet=false \

arbitrator-desktop-local:
	# Arbitrator needs to be registered before making trades
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=4444 \
		--appName=tuskex-TSK_LOCAL_arbitrator \
		--apiPassword=apitest \
		--apiPort=9998 \
		--useNativeTskWallet=false \

arbitrator2-daemon-local:
	# Arbitrator needs to be registered before making trades
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=7777 \
		--appName=tuskex-TSK_LOCAL_arbitrator2 \
		--apiPassword=apitest \
		--apiPort=10001 \
		--useNativeTskWallet=false \

arbitrator2-desktop-local:
	# Arbitrator needs to be registered before making trades
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=7777 \
		--appName=tuskex-TSK_LOCAL_arbitrator2 \
		--apiPassword=apitest \
		--apiPort=10001 \
		--useNativeTskWallet=false \

user1-daemon-local:
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=5555 \
		--appName=tuskex-TSK_LOCAL_user1 \
		--apiPassword=apitest \
		--apiPort=9999 \
		--walletRpcBindPort=38091 \
		--passwordRequired=false \
		--useNativeTskWallet=false \

user1-desktop-local:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=5555 \
		--appName=tuskex-TSK_LOCAL_user1 \
		--apiPassword=apitest \
		--apiPort=9999 \
		--walletRpcBindPort=38091 \
		--logLevel=info \
		--useNativeTskWallet=false \

user2-desktop-local:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=6666 \
		--appName=tuskex-TSK_LOCAL_user2 \
		--apiPassword=apitest \
		--apiPort=10000 \
		--walletRpcBindPort=38092 \
		--useNativeTskWallet=false \

user2-daemon-local:
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=6666 \
		--appName=tuskex-TSK_LOCAL_user2 \
		--apiPassword=apitest \
		--apiPort=10000 \
		--walletRpcBindPort=38092 \
		--passwordRequired=false \
		--useNativeTskWallet=false \

user3-desktop-local:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=7778 \
		--appName=tuskex-TSK_LOCAL_user3 \
		--apiPassword=apitest \
		--apiPort=10002 \
		--walletRpcBindPort=38093 \
		--useNativeTskWallet=false \

user3-daemon-local:
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_LOCAL \
		--useLocalhostForP2P=true \
		--useDevPrivilegeKeys=true \
		--nodePort=7778 \
		--appName=tuskex-TSK_LOCAL_user3 \
		--apiPassword=apitest \
		--apiPort=10002 \
		--walletRpcBindPort=38093 \
		--passwordRequired=false \
		--useNativeTskWallet=false \

# Stagenet network

monerod-stagenet:
	./.localnet/monerod \
		--stagenet \
		--bootstrap-daemon-address auto \
		--rpc-access-control-origins http://localhost:8080 \

monerod-stagenet-custom:
	./.localnet/monerod \
		--stagenet \
		--no-zmq \
		--p2p-bind-port 39080 \
		--rpc-bind-port 39081 \
		--bootstrap-daemon-address auto \
		--rpc-access-control-origins http://localhost:8080 \

seednode-stagenet:
	./tuskex-seednode$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=3002 \
		--appName=tuskex-TSK_STAGENET_Seed_3002 \
		--tskNode=http://127.0.0.1:38081 \

seednode2-stagenet:
	./tuskex-seednode$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=3003 \
		--appName=tuskex-TSK_STAGENET_Seed_3003 \
		--tskNode=http://127.0.0.1:38081 \

arbitrator-daemon-stagenet:
	# Arbitrator needs to be registered before making trades
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_STAGENET_arbitrator \
		--apiPassword=apitest \
		--apiPort=3200 \
		--passwordRequired=false \
		--tskNode=http://127.0.0.1:38081 \
		--useNativeTskWallet=false \

# Arbitrator needs to be registered before making trades
arbitrator-desktop-stagenet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_STAGENET_arbitrator \
		--apiPassword=apitest \
		--apiPort=3200 \
		--tskNode=http://127.0.0.1:38081 \
		--useNativeTskWallet=false \

user1-daemon-stagenet:
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_STAGENET_user1 \
		--apiPassword=apitest \
		--apiPort=3201 \
		--passwordRequired=false \
		--useNativeTskWallet=false \

user1-desktop-stagenet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_STAGENET_user1 \
		--apiPassword=apitest \
		--apiPort=3201 \
		--useNativeTskWallet=false \

user2-daemon-stagenet:
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_STAGENET_user2 \
		--apiPassword=apitest \
		--apiPort=3202 \
		--passwordRequired=false \
		--useNativeTskWallet=false \

user2-desktop-stagenet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_STAGENET_user2 \
		--apiPassword=apitest \
		--apiPort=3202 \
		--useNativeTskWallet=false \

user3-desktop-stagenet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_STAGENET_user3 \
		--apiPassword=apitest \
		--apiPort=3203 \
		--useNativeTskWallet=false \

tuskex-desktop-stagenet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_STAGENET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=Tuskex \
		--apiPassword=apitest \
		--apiPort=3204 \
		--useNativeTskWallet=false \

# Mainnet network

monerod:
	./.localnet/monerod \
		--bootstrap-daemon-address auto \
		--rpc-access-control-origins http://localhost:8080 \

seednode:
	./tuskex-seednode$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=1002 \
		--appName=tuskex-TSK_MAINNET_Seed_1002 \
		--tskNode=http://127.0.0.1:20241 \

seednode2:
	./tuskex-seednode$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=1003 \
		--appName=tuskex-TSK_MAINNET_Seed_1003 \
		--tskNode=http://127.0.0.1:20241 \

arbitrator-daemon-mainnet:
	# Arbitrator needs to be registered before making trades
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_MAINNET_arbitrator \
		--apiPassword=apitest \
		--apiPort=1200 \
		--passwordRequired=false \
		--tskNode=http://127.0.0.1:20241 \
		--useNativeTskWallet=false \

arbitrator-desktop-mainnet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_MAINNET_arbitrator \
		--apiPassword=apitest \
		--apiPort=1200 \
		--tskNode=http://127.0.0.1:20241 \
		--useNativeTskWallet=false \

tuskex-daemon-mainnet:
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=Tuskex \
		--apiPassword=apitest \
		--apiPort=1201 \
		--useNativeTskWallet=false \
		--ignoreLocalTskNode=false \

tuskex-desktop-mainnet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=Tuskex \
		--apiPassword=apitest \
		--apiPort=1201 \
		--useNativeTskWallet=false \
		--ignoreLocalTskNode=false \

user1-daemon-mainnet:
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_MAINNET_user1 \
		--apiPassword=apitest \
		--apiPort=1202 \
		--passwordRequired=false \
		--useNativeTskWallet=false \
		--ignoreLocalTskNode=false \

user1-desktop-mainnet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_MAINNET_user1 \
		--apiPassword=apitest \
		--apiPort=1202 \
		--useNativeTskWallet=false \
		--ignoreLocalTskNode=false \

user2-daemon-mainnet:
	./tuskex-daemon$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_MAINNET_user2 \
		--apiPassword=apitest \
		--apiPort=1203 \
		--passwordRequired=false \
		--useNativeTskWallet=false \
		--ignoreLocalTskNode=false \

user2-desktop-mainnet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_MAINNET_user2 \
		--apiPassword=apitest \
		--apiPort=1203 \
		--useNativeTskWallet=false \
		--ignoreLocalTskNode=false \

user3-desktop-mainnet:
	./tuskex-desktop$(APP_EXT) \
		--baseCurrencyNetwork=TSK_MAINNET \
		--useLocalhostForP2P=false \
		--useDevPrivilegeKeys=false \
		--nodePort=9999 \
		--appName=tuskex-TSK_MAINNET_user3 \
		--apiPassword=apitest \
		--apiPort=1204 \
		--useNativeTskWallet=false \
		--ignoreLocalTskNode=false \
