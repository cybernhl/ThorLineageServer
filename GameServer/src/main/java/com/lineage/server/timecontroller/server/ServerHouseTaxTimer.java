package com.lineage.server.timecontroller.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.server.datatables.lock.AuctionBoardReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.templates.L1AuctionBoardTmp;
import com.lineage.server.templates.L1House;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldClan;

/**
 * 血盟小屋稅收時間軸
 *
 * @author dexc
 *
 */
public class ServerHouseTaxTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerHouseTaxTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 600 * 1000;// 10分鐘
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			checkTaxDeadline();
			
		} catch (final Exception e) {
			_log.error("血盟小屋稅收時間軸異常重啟", e);
            _timer.cancel(false);
            // GeneralThreadPool.get().cancel(_timer, false);
			final ServerHouseTaxTimer houseTaxTimer = new ServerHouseTaxTimer();
			houseTaxTimer.start();
		}
	}
	
	private static Calendar getRealTime() {
		final TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		final Calendar cal = Calendar.getInstance(tz);
		return cal;
	}

	private static void checkTaxDeadline() {
		try {
			final Collection<L1House> houseList = HouseReading.get().getHouseTableList().values();
			if (!houseList.isEmpty()) {
				for (final L1House house : houseList) {
					if (!house.isOnSale()) { // 競賣中
						if (house.getTaxDeadline().before(getRealTime())) {
							sellHouse(house);
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static void sellHouse(final L1House house) {
		final L1AuctionBoardTmp board = new L1AuctionBoardTmp();
		if (board != null) {
			// 競賣揭示板新規書迂
			final int houseId = house.getHouseId();
			board.setHouseId(houseId);
			board.setHouseName(house.getHouseName());
			board.setHouseArea(house.getHouseArea());
			final TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
			final Calendar cal = Calendar.getInstance(tz);
			cal.add(Calendar.DATE, 5); // 5日後
			cal.set(Calendar.MINUTE, 0); // 分、秒切捨
			cal.set(Calendar.SECOND, 0);
			board.setDeadline(cal);
			board.setPrice(100000);
			board.setLocation(house.getLocation());
			board.setOldOwner("");
			board.setOldOwnerId(0);
			board.setBidder("");
			board.setBidderId(0);
			AuctionBoardReading.get().insertAuctionBoard(board);
			house.setOnSale(true); // 競賣中設定
			house.setPurchaseBasement(true); // 地下未購入設定
			cal.add(Calendar.DATE, ConfigAlt.HOUSE_TAX_INTERVAL);
			house.setTaxDeadline(cal);

			HouseReading.get().updateHouse(house); // DB書迂
			// 以前所有者消
			final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
			for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
				final L1Clan clan = iter.next();
				if (clan.getHouseId() == houseId) {
					clan.setHouseId(0);
					ClanReading.get().updateClan(clan);
				}
			}
		}
	}
}
