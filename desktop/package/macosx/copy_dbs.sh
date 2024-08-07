#!/bin/bash

cd $(dirname $0)/../../../

version="0.0.1"

# Set TUSKEX_DIR as environment var to the path of your locally synced Tuskex data directory e.g. TUSKEX_DIR=~/Library/Application\ Support/Tuskex

dbDir=$TUSKEX_DIR/btc_mainnet/db
resDir=p2p/src/main/resources

# Only commit new TradeStatistics3Store if you plan to add it to
# https://github.com/bisq-network/bisq/blob/0345c795e2c227d827a1f239a323dda1250f4e69/common/src/main/java/tuskex/common/app/Version.java#L40 as well.
cp "$dbDir/TradeStatistics3Store" "$resDir/TradeStatistics3Store_${version}_BTC_MAINNET"
cp "$dbDir/AccountAgeWitnessStore" "$resDir/AccountAgeWitnessStore_${version}_BTC_MAINNET"
cp "$dbDir/DaoStateStore" "$resDir/DaoStateStore_BTC_MAINNET"
cp "$dbDir/SignedWitnessStore" "$resDir/SignedWitnessStore_BTC_MAINNET"
