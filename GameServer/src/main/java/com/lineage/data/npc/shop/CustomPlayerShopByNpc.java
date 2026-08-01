package com.lineage.data.npc.shop;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.config.ConfigOther;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldPcShop;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomPlayerShopByNpc extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(CustomPlayerShopByNpc.class);
    public static final CustomPlayerShopByNpc get = new CustomPlayerShopByNpc();
    private final Map<Integer, ShopInfo> shopInfo = new HashMap<>();
    private final Set<Integer> worldItems = new HashSet<>();
    private static final int coinId = 40308;
    private static final Lock lock = new ReentrantLock(false);

    public CustomPlayerShopByNpc() {
    }

    public final Set<Integer> getWorldItems() {
        return this.worldItems;
    }

    public final void UseShop(final L1PcInstance pc) {
        CustomPlayerShopByNpc.lock.lock();
        try {
            if (ConfigOther.PLAYER_SHOP_MAP_96 && pc.getMapId() != 96) {
                pc.sendPackets(new S_HelpMessage("\\fW只有掛賣地圖可以使用。"));
                return;
            }

            for (final Map.Entry<Integer, ShopInfo> info : shopInfo.entrySet()) {
                if (info.getValue().getPlayerId() == pc.getId()) {
                    if (info.getValue().getOpenType() == 4) {
                        info.getValue().resetOpenType(0, pc.getMapId(), pc.getX() + 1, pc.getY() + 1);
                        info.getValue().setSpawn();
                    } else {
                        pc.sendPackets(new S_HelpMessage("\\fW您已經開啟掛賣商人了"));
                    }
                    return;
                }
            }
            int polyId = 143;

            if (pc.get_sex() == 0) {
                if (pc.isCrown()) {
                    polyId = 18891;

                } else if (pc.isKnight()) {
                    polyId = 18893;

                } else if (pc.isElf()) {
                    polyId = 18895;

                } else if (pc.isWizard()) {
                    polyId = 18897;

                }/* else if (pc.isDarkelf()) {
                polyId = 18891;
            }*/
            } else {
                if (pc.isCrown()) {
                    polyId = 18892;

                } else if (pc.isKnight()) {
                    polyId = 18894;

                } else if (pc.isElf()) {
                    polyId = 18896;

                } else if (pc.isWizard()) {
                    polyId = 18898;

                }/* else if (pc.isDarkelf()) {
                polyId = 18891;

            }*/
            }
            final ShopInfo info = new ShopInfo(0, pc.getName(), pc.getId(), pc.getMapId(), pc.getX() + 1, pc.getY() + 1, 0, "無標題", 0, polyId);
            final int id = info.createShop();
            if (id == -1) {
                return;
            }
            info.setId(id);
            shopInfo.put(id, info);
            info.setSpawn();
        } finally {
            CustomPlayerShopByNpc.lock.unlock();
        }
    }

    public final void SellItem(final L1PcInstance pc, final int npcObjectId, final int objid, final int count) {
        CustomPlayerShopByNpc.lock.lock();
        try {
            if (count <= 0) {
                pc.sendPackets(new S_HelpMessage("\\fW販售數量必須大於0"));
                return;
            }
            final L1ItemInstance item = pc.getInventory().getItem(objid);
            if (item == null) {
                pc.sendPackets(new S_HelpMessage("\\fW找不到這個道具，請重新在試。"));
                return;
            }
            if (!item.getItem().isTradable()) {
                pc.sendPackets(new S_HelpMessage("\\fW無法上架不能交易的道具。"));
                return;
            }
            if (!item.isIdentified()) {
                pc.sendPackets(new S_HelpMessage("\\fW無法上架未鑑定的道具。"));
                return;
            }
            if (item.getCount() < count) {
                pc.sendPackets(new S_HelpMessage("\\fW您身上沒有那麼多個。"));
                return;
            }
            final Object[] petlist = pc.getPetList().values().toArray();
            for (final Object petObject : petlist) {
                if (petObject instanceof L1PetInstance) {
                    final L1PetInstance pet = (L1PetInstance) petObject;
                    if (item.getId() == pet.getItemObjId()) {
                        // \f1%0%d是不可轉移的…
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        return;
                    }
                }
            }
            final L1NpcInstance npc = WorldPcShop.get().get(npcObjectId);
            if (npc == null) {
                pc.sendPackets(new S_HelpMessage("\\fW請確認您已經開啟掛賣商店。"));
                return;
            }
            if (!this.shopInfo.containsKey(npc.getShopObjectId())) {
                pc.sendPackets(new S_HelpMessage("\\fW請確認您已經開啟掛賣商店。"));
                return;
            }
            final ShopInfo info = this.shopInfo.get(npc.getShopObjectId());
            if (info.getPlayerId() == pc.getId()) {
                info.setTempItemObjectId(objid);
                info.setTempItemCount(count);
                pc.sendPackets(new S_ItemCount(npcObjectId, 1, 2000000000, "pcshop_input", "I"));
            } else {
                pc.sendPackets(new S_HelpMessage("\\fW只能上架自己的掛賣"));
                return;
            }
        } finally {
            CustomPlayerShopByNpc.lock.unlock();
        }
    }

    public final void SetSellItemPrice(final L1PcInstance pc, final int objectId, final int amount, final String cmd) {
        CustomPlayerShopByNpc.lock.lock();
        try {
            final L1NpcInstance npc = WorldPcShop.get().get(objectId);
            if (npc == null) {
                pc.sendPackets(new S_HelpMessage("\\fW請確認您已經開啟掛賣商店。"));
                return;
            }
            if (!this.shopInfo.containsKey(npc.getShopObjectId())) {
                pc.sendPackets(new S_HelpMessage("\\fW請確認您已經開啟掛賣商店。"));
                return;
            }
            final ShopInfo info = this.shopInfo.get(npc.getShopObjectId());
            if (info.getPlayerId() == pc.getId()) {
                if (info.tempItemObjectId == -1 || info.tempItemCount == -1) {
                    pc.sendPackets(new S_HelpMessage("\\fW發生未知錯誤，請重新在試。"));
                    return;
                }
                final L1ItemInstance item = pc.getInventory().getItem(info.tempItemObjectId);
                if (item == null) {
                    pc.sendPackets(new S_HelpMessage("\\fW找不到這個道具，請重新在試。"));
                    return;
                }
                if (item.getCount() < info.getTempItemCount()) {
                    pc.sendPackets(new S_HelpMessage("\\fW您身上沒有那麼多個。"));
                    return;
                }
                if (info.getItems().size() >= 30) {
                    pc.sendPackets(new S_HelpMessage("\\fW最多只能上架30樣道具。"));
                    return;
                }
                final L1ItemInstance copy = CreateCopyItem(item, info.getTempItemCount());
                if (info.addItem(copy, info.getTempItemCount(), amount)) {
                    WriteLogTxt.NormalLog("掛賣上架紀錄",
                            "玩家" + ":【 " + pc.getName() + " 】 " + "把" + "【 + " + copy.getEnchantLevel() + " ["
                                    + "] " +copy.getViewName() + "(" + copy.getCount() + ")" + " 】" + " 給上架了 售價: " + amount + "。" + "(時間"
                                    + new Timestamp(System.currentTimeMillis()) + ")。");
                    pc.getInventory().removeItem(item, info.getTempItemCount());
                    sendNpcTalk(pc, info);
                }
                info.setTempItemObjectId(-1);
                info.setTempItemCount(-1);
            } else {
                pc.sendPackets(new S_HelpMessage("\\fW只能上架自己的掛賣"));
                return;
            }
        } finally {
            CustomPlayerShopByNpc.lock.unlock();
        }
    }

    public final void sendNpcTalk(final L1PcInstance pc, final ShopInfo info) {
        if (info == null || info.npc == null) {
            return;
        }
        String[] data = new String[]{pc.getName() + " 的拍賣場", info.getOpenType() == 2 ? "開店中" : info.getOpenType() == 1 ? "整理中" : "關店中", info.getTitle(), String.valueOf(String.format("%,d", info.getTotalCoin()))};
        pc.sendPackets(new S_NPCTalkReturn(info.npc.getId(), "pcshop", data));
    }

    public final void BuyItem(final L1PcInstance pc, final int npcObjectId, final List<Pair<Integer, Integer>> items) {
        CustomPlayerShopByNpc.lock.lock();
        try {
            final L1NpcInstance npc = WorldPcShop.get().get(npcObjectId);
            if (npc == null) {
                pc.sendPackets(new S_HelpMessage("\\fW找不到該商店。"));
                return;
            }
            if (!this.shopInfo.containsKey(npc.getShopObjectId())) {
                pc.sendPackets(new S_HelpMessage("\\fW找不到該商店。"));
                return;
            }
            final ShopInfo info = this.shopInfo.get(npc.getShopObjectId());
            if (info.getPlayerId() != pc.getId()) {
                if (info.getOpenType() < 2) {
                    return;
                }
            }
            long total_coin = 0;
            for (final Pair<Integer, Integer> item : items) {
                if (!info.getItems().containsKey(item.getKey())) {
                    continue;
                }
                final CustomPlayerShopItem sItem = info.getItems().get(item.getKey());
                if (sItem.getItem().getCount() < item.getValue()) {
                    continue;
                }
                final L1ItemInstance add = CreateCopyItem(sItem.getItem(), item.getValue());
                if (pc.getInventory().checkAddItem(add, item.getValue()) != L1Inventory.OK) {
                    continue;
                }
                if (info.getPlayerId() != pc.getId()) {
                    final long price = ((long) item.getValue() * (long) sItem.getPrice());
                    if (pc.getInventory().checkItemX(coinId, price) == null) {
                        pc.sendPackets(new S_HelpMessage("\\fW金幣不足。"));
                        continue;
                    }
                    total_coin += price - (((double) price) * 0.05);
                    pc.getInventory().consumeItem(coinId, price);
                    WriteLogTxt.NormalLog("掛賣購買紀錄",
                            "玩家" + ":【 " + pc.getName() + " 】 " + "把 " + info.getPcName() + " 的 掛賣商店 的" + "【 + " + add.getEnchantLevel() + " ["
                                    + "] " +add.getViewName() + "(" + add.getCount() + ")" + " 】" + " 給購買了 花費: " + price + "。" + "(時間"
                                    + new Timestamp(System.currentTimeMillis()) + ")。");
                } else {
                    WriteLogTxt.NormalLog("掛賣下架紀錄",
                            "玩家" + ":【 " + pc.getName() + " 】 " + "把【 + " + add.getEnchantLevel() + " ["
                                    + "] " +add.getViewName() + "(" + add.getCount() + ")" + " 】" + " 給下架了 (時間"
                                    + new Timestamp(System.currentTimeMillis()) + ")。");
                }

                pc.getInventory().storeItem(add);
                if (sItem.getItem().getCount() == item.getValue()) {
                    info.updateItemData(sItem.getItem(), true);
                    info.getItems().remove(sItem.getItem().getId());
                } else {
                    sItem.getItem().setCount(sItem.getItem().getCount() - item.getValue());
                    info.updateItemData(sItem.getItem(), false);
                }
            }
            if (info.getPlayerId() == pc.getId()) {
                sendNpcTalk(pc, info);
            } else {
                long total = (long) info.getTotalCoin() + total_coin;
                if (total >= Integer.MAX_VALUE) {
                    info.setTotalCoin(Integer.MAX_VALUE);
                } else {
                    if (total_coin > 0) {
                        info.addTotalCoin((int) total_coin);
                    }
                }
            }
        } finally {
            CustomPlayerShopByNpc.lock.unlock();
        }

    }

    public final L1ItemInstance CreateCopyItem(final L1ItemInstance item, final long count) {
        final L1ItemInstance ret = ItemTable.get().createItem(item.getItem().getItemId());
        final L1Pet pet = PetReading.get().getTemplate(item.getId());
        if (pet != null) {
            PetReading.get().storeCopyNewPet(pet, ret.getId());
        }
        ret.set_showId(item.get_showId());
        ret.setCount(count);
        ret.setEquipped(false);
        ret.setEnchantLevel(item.getEnchantLevel());
        ret.setIdentified(item.isIdentified());
        ret.set_durability(item.get_durability());
        ret.setChargeCount(item.getChargeCount());
        ret.setRemainingTime(item.getRemainingTime());
        ret.setLastUsed(item.getLastUsed());
        ret.setBless(item.getBless());
        ret.setAttrEnchantKind(item.getAttrEnchantKind());
        ret.setAttrEnchantLevel(item.getAttrEnchantLevel());
        ret.setGamNo(item.getGamNo());
        ret.setAttachIndex(item.getAttachIndex());
        ret.setGemHoleIndex(item.getGemHoleIndex());
        ret.setGemHole(item.getGemHole());
        ret.setSpecialStat(item.getSpecialStat());
        ret.setCanAbilityType(item.getCanAbilityType());
        ret.setAbilityPos1(item.getAbilityPos1ID());
        ret.setAbilityPos2(item.getAbilityPos2ID());
        ret.setAbilityPos3(item.getAbilityPos3ID());
        return ret;
    }

    public static CustomPlayerShopByNpc getInstance() {
        return get;
    }

    public static NpcExecutor get() {
        return getInstance();
    }

    public final void loadALl() {
        this.shopInfo.clear();
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactoryLogin.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `character_pc_shop_data`");
            rs = ps.executeQuery();

            while (rs.next()) {
                final int objid = rs.getInt("id");
                final String pcName = rs.getString("pc_name");
                final int playerId = rs.getInt("player_id");
                final int mapid = rs.getInt("map_id");
                final int local_x = rs.getInt("local_x");
                final int local_y = rs.getInt("local_y");
                final int total_coin = rs.getInt("total_coin");
                final String title = rs.getString("title");
                final int openType = rs.getInt("open_type");
                final int polyId = rs.getInt("polyId");
                final ShopInfo info = new ShopInfo(objid, pcName, playerId, mapid, local_x, local_y, total_coin, title, openType, polyId);
                info.loadItems();
                this.shopInfo.put(objid, info);
                if (openType != 4) {
                    info.setSpawn();
                }
            }
            System.out.println("載入人物掛賣商店 " + this.shopInfo.size() + " 個");
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        if (npc.getShopObjectId() <= -1) {
            return;
        }
        if (!this.shopInfo.containsKey(npc.getShopObjectId())) {
            return;
        }
        final ShopInfo info = this.shopInfo.get(npc.getShopObjectId());
        pc.setShopObjectId(info.getObjectId());
        if (info.getPlayerId() == pc.getId()) {
            sendNpcTalk(pc, info);
        } else {
            if (pc.isGm() && pc.isRemovePcShop()) {
                info.removeNpcByGM();
                return;
            }
            // 買東西
            if (info.getOpenType() < 2) {
                return;
            }
            if (info.getOpenType() == 4) {
                return;
            }
            pc.sendPackets(new S_PcShopSellList(npc.getId(), info.getItems(), false));
        }
    }

    public final void resetShopTitle(final L1PcInstance pc, final String title, final int shopId) {
        if (!this.shopInfo.containsKey(shopId)) {
            return;
        }
        if (title.length() >= 15) {
            pc.sendPackets(new S_HelpMessage("\\fW商店標題不可大於15個字。"));
            return;
        }
        if (title.isEmpty()) {
            pc.sendPackets(new S_HelpMessage("\\fW商店名稱不可為空。"));
            return;
        }
        final ShopInfo info = this.shopInfo.get(shopId);
        info.getLock().lock();
        try {
            info.setTitle(title);
            info.updateData();
            info.setSpawn();
            sendNpcTalk(pc, info);
        } finally {
            info.getLock().unlock();
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        if (pc.getShopObjectId() == -1) {
            return;
        }
        if (!this.shopInfo.containsKey(pc.getShopObjectId())) {
            return;
        }
        if (this.shopInfo.get(pc.getShopObjectId()).getPlayerId() != pc.getId()) {
            return;
        }
        final ShopInfo info = this.shopInfo.get(pc.getShopObjectId());
        switch (cmd) {
            case "0": // 賣場狀態
            {
                if (info.getOpenType() == 0) {
                    if (info.getItems().size() <= 0) {
                        pc.sendPackets(new S_HelpMessage("\\fW您還未上架任何商品。"));
                        return;
                    }
                    info.resetOpenType(2);
                } else {
                    info.resetOpenType(0);
                }
                sendNpcTalk(pc, info);
                break;
            }
            case "1": // 賣場標題
                pc.setShopObjectId(info.getObjectId());
                pc.setPcShopReName(true);
                pc.sendPackets(new S_Message_YN(325));
                break;
            case "2": // 上架商品
                info.resetOpenType(0);
                pc.sendPackets(new S_PcShopBuyList(npc.getId(), pc));
                break;
            case "3": // 下架商品
                info.resetOpenType(0);
                pc.sendPackets(new S_PcShopSellList(npc.getId(), this.shopInfo.get(pc.getShopObjectId()).getItems(), true));
                break;
            case "4": // 關閉商店
            {
                info.resetOpenType(0);
                if (info.getTotalCoin() > 0) {
                    long total = pc.getInventory().countItems(coinId) + (long) info.getTotalCoin();
                    if (total > Integer.MAX_VALUE) {
                        pc.sendPackets(new S_HelpMessage("\\fW您身上的金幣過多"));
                        return;
                    }
                    WriteLogTxt.NormalLog("掛賣領回紀錄",
                            "玩家" + ":【 " + pc.getName() + " 】 " + "領回了 金幣: " + info.getTotalCoin() + " (時間"
                                    + new Timestamp(System.currentTimeMillis()) + ")。");
                    pc.getInventory().storeItem(coinId, info.getTotalCoin());
                    info.setTotalCoin(0);
                }
                for (final Map.Entry<Integer, CustomPlayerShopItem> item : info.getItems().entrySet()) {
                    final L1ItemInstance copy = CreateCopyItem(item.getValue().getItem(), item.getValue().getItem().getCount());
                    WriteLogTxt.NormalLog("掛賣領回紀錄",
                            "玩家" + ":【 " + pc.getName() + " 】 " + "把【 + " + copy.getEnchantLevel() + " ["
                                    + "] " +copy.getViewName() + "(" + copy.getCount() + ")" + " 】" + " 給領回了 (時間"
                                    + new Timestamp(System.currentTimeMillis()) + ")。");
                    pc.getInventory().storeItem(copy);
                    info.updateItemData(item.getValue().getItem(), true);
                }
                info.getItems().clear();
                info.closeShop();
                pc.sendPackets(new S_CloseList(pc.getId()));
                break;
            }
            case "5": // 開啟商店
            {
                if (info.getItems().size() <= 0) {
                    pc.sendPackets(new S_HelpMessage("\\fW您還未上架任何商品。"));
                    return;
                }
                info.resetOpenType(2);
                info.setSpawn();
                pc.sendPackets(new S_CloseList(pc.getId()));
                break;
            }
            case "6": // 餘額結算
            {
                long total = pc.getInventory().countItems(coinId) + (long) info.getTotalCoin();
                if (total > Integer.MAX_VALUE) {
                    pc.sendPackets(new S_HelpMessage("\\fW您身上的金幣過多"));
                    return;
                }
                info.resetOpenType(0);
                info.setSpawn();
                if (info.getTotalCoin() > 0) {
                    WriteLogTxt.NormalLog("掛賣領回紀錄",
                            "玩家" + ":【 " + pc.getName() + " 】 " + "領回了 金幣: " + info.getTotalCoin() + " (時間"
                                    + new Timestamp(System.currentTimeMillis()) + ")。");
                    pc.getInventory().storeItem(coinId, info.getTotalCoin());
                    info.setTotalCoin(0);
                }
                sendNpcTalk(pc, info);
                break;
            }
        }
    }

    public static class CustomPlayerShopItem {
        private final L1ItemInstance item;
        private int price, sellType;

        public CustomPlayerShopItem(final L1ItemInstance item, final int price, final int sellType) {
            this.item = item;
            this.price = price;
            this.sellType = sellType;
        }

        public final L1ItemInstance getItem() {
            return this.item;
        }

        public final int getPrice() {
            return this.price;
        }

        public final int getSellType() {
            return this.sellType;
        }
    }

    public static class ShopInfo {
        private int playerId, mapid, local_x, local_y;
        private int id;
        private String title;
        private int openType; // 0 = 關店 1 = 整理中 2 = 開店

        private int polyId;

        private int total_coin;

        private String pcName;

        private L1NpcInstance npc;
        private int tempItemObjectId = -1, tempItemCount = -1;
        private final ReentrantLock lock = new ReentrantLock();

        private Map<Integer, CustomPlayerShopItem> items = new LinkedHashMap<>();

        public ShopInfo(final int id, final String pcName, final int playerId, final int mapid, final int local_x, final int local_y, final int total_coin, final String title, final int openType, final int polyId) {
            this.id = id;
            this.pcName = pcName;
            this.playerId = playerId;
            this.mapid = mapid;
            this.local_x = local_x;
            this.local_y = local_y;
            this.total_coin = total_coin;
            this.title = title;
            this.openType = openType;
            this.polyId = polyId;
        }

        public final int createShop() {
            int ret = -1;
            Connection co = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                co = DatabaseFactoryLogin.get().getConnection();
                ps = co.prepareStatement("INSERT INTO `character_pc_shop_data` (pc_name, player_id, map_id, local_x, local_y, total_coin, title, open_type, polyId) values (?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
                ps.setString(1, this.pcName);
                ps.setInt(2, this.playerId);
                ps.setInt(3, this.mapid);
                ps.setInt(4, this.local_x);
                ps.setInt(5, this.local_y);
                ps.setInt(6, this.total_coin);
                ps.setString(7, this.title);
                ps.setInt(8, this.openType);
                ps.setInt(9, this.polyId);
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    ret = rs.getInt(1);
                }
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(ps);
                SQLUtil.close(co);
            }
            return ret;
        }

        public final int reopenShop() {
            int ret = -1;
            Connection co = null;
            PreparedStatement ps = null;
            try {
                co = DatabaseFactoryLogin.get().getConnection();
                ps = co.prepareStatement("UPDATE `character_pc_shop_data` SET pc_name = ?, map_id = ?, local_x = ?, local_y = ?, total_coin = ?, title = ?, open_type = ?, polyId = ? WHERE `player_id` = ?");
                ps.setString(1, this.pcName);
                ps.setInt(2, this.mapid);
                ps.setInt(3, this.local_x);
                ps.setInt(4, this.local_y);
                ps.setInt(5, this.total_coin);
                ps.setString(6, this.title);
                ps.setInt(7, this.openType);
                ps.setInt(8, this.polyId);
                ps.setInt(9, this.playerId);
                ps.executeUpdate();
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(ps);
                SQLUtil.close(co);
            }
            return ret;
        }

        public final void setTitle(final String title) {
            this.title = title;
        }
        public final void loadItems() {
            Connection co = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                co = DatabaseFactoryLogin.get().getConnection();
                ps = co.prepareStatement("SELECT * FROM `character_pc_shop_items` where `shop_id` = ?");
                ps.setInt(1, this.id);
                rs = ps.executeQuery();

                while (rs.next()) {
                    final int objid = rs.getInt("objid");
                    final int item_id = rs.getInt("item_id");
                    final int sellType = rs.getInt("sell_type");
                    final int price = rs.getInt("price");

                    final L1Item itemTemplate = ItemTable.get().getTemplate(item_id);
                    if (itemTemplate == null) {
                        continue;
                    }
                    final long count = rs.getLong("count");
                    final int enchantlvl = rs.getInt("enchantlvl");
                    final int is_id = rs.getInt("is_id");
                    final int durability = rs.getInt("durability");
                    final int charge_count = rs.getInt("charge_count");
                    final int remaining_time = rs.getInt("remaining_time");
                    final Timestamp last_used = rs.getTimestamp("last_used");
                    final int bless = rs.getInt("bless");
                    final int attr_enchant_kind = rs.getInt("attr_enchant_kind");
                    final int attr_enchant_level = rs.getInt("attr_enchant_level");
                    final String gamno = rs.getString("gamno");
                    final int attach_index = rs.getInt("attach_index");
                    final int specialStat = rs.getInt("special_stat");
                    final int gem_hole = rs.getInt("gem_hole");
                    final int gem_hole_index = rs.getInt("gem_hole_index");
                    final int proctect = rs.getInt("proctect");
                    final int proctectRom = rs.getInt("proctect_rom");
                    final int proctectType = rs.getInt("proctect_type");
                    final int ability_pos_1 = rs.getInt("ability_pos_1");
                    final int ability_pos_2 = rs.getInt("ability_pos_2");
                    final int ability_pos_3 = rs.getInt("ability_pos_3");
                    final int can_ability_type = rs.getInt("can_ability_type");

                    final L1ItemInstance item = new L1ItemInstance();
                    item.setId(objid);
                    item.setItem(itemTemplate);
                    item.setCount(count);
                    item.setEquipped(false);
                    item.setEnchantLevel(enchantlvl);
                    item.setIdentified(is_id != 0);
                    item.set_durability(durability);
                    item.setChargeCount(charge_count);
                    item.setRemainingTime(remaining_time);
                    item.setLastUsed(last_used);
                    item.setBless(bless);
                    item.setAttrEnchantKind(attr_enchant_kind);
                    item.setAttrEnchantLevel(attr_enchant_level);
                    item.setGamNo(gamno);
                    item.setAttachIndex(attach_index);
                    item.setSpecialStat(specialStat);
                    item.setGemHole(gem_hole);
                    item.setGemHoleIndex(gem_hole_index);
                    item.setproctect(proctect > 0);
                    item.setProctectRom(proctectRom);
                    item.setProctectType(proctectType);
                    item.setAbilityPos1(ability_pos_1);
                    item.setAbilityPos2(ability_pos_2);
                    item.setAbilityPos3(ability_pos_3);
                    item.setCanAbilityType(can_ability_type);
                    this.items.put(objid, new CustomPlayerShopItem(item, price, sellType));
                    CustomPlayerShopByNpc.getInstance().worldItems.add(objid);
                }
            } catch (final SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(ps);
                SQLUtil.close(co);
            }
        }

        public final boolean addItem(final L1ItemInstance item, final int count, final int price) {
            this.lock.lock();
            boolean ret = false;
            Connection co = null;
            PreparedStatement ps = null;
            try {
                co = DatabaseFactoryLogin.get().getConnection();
                ps = co.prepareStatement("INSERT INTO `character_pc_shop_items` (shop_id, objid, char_id, item_name, item_id,sell_type,price,`count`,enchantlvl,is_id,durability,charge_count,remaining_time,last_used,bless,attr_enchant_kind,attr_enchant_level,gamno,attach_index,special_stat,proctect,proctect_rom,proctect_type,gem_hole,gem_hole_index,can_ability_type,ability_pos_1,ability_pos_2,ability_pos_3) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ps.setInt(1, this.id);
                ps.setInt(2, item.getId());
                ps.setInt(3, getPlayerId());
                ps.setString(4, item.getName());
                ps.setInt(5, item.getItemId());
                ps.setInt(6, 1);
                ps.setInt(7, price);
                ps.setLong(8, count);
                ps.setInt(9, item.getEnchantLevel());
                ps.setInt(10, item.isIdentified() ? 1 : 0);
                ps.setInt(11, item.get_durability());
                ps.setInt(12, item.getChargeCount());
                ps.setInt(13, item.getRemainingTime());
                ps.setTimestamp(14, item.getLastUsed());
                ps.setInt(15, item.getBless());
                ps.setInt(16, item.getAttrEnchantKind());
                ps.setInt(17, item.getAttrEnchantLevel());
                ps.setString(18, item.getGamNo());
                ps.setInt(19, item.getAttachIndex());
                ps.setInt(20, item.getSpecialStat());
                ps.setInt(21, item.getproctect() ? 1 : 0);
                ps.setInt(22, item.getProctectRom());
                ps.setInt(23, item.getProctectType());
                ps.setInt(24, item.getGemHole());
                ps.setInt(25, item.getGemHoleIndex());
                ps.setInt(26, item.getCanAbilityType());
                ps.setInt(27, item.getAbilityPos1ID());
                ps.setInt(28, item.getAbilityPos2ID());
                ps.setInt(29, item.getAbilityPos3ID());
                if (ps.executeUpdate() > 0) {
                    ret = true;
                    this.items.put(item.getId(), new CustomPlayerShopItem(item, price, 1));
                }
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(ps);
                SQLUtil.close(co);
                this.lock.unlock();
            }
            return ret;
        }

        public final void addTotalCoin(final int add) {
            this.lock.lock();
            try {
                this.total_coin += add;
                this.updateData();
            } finally {
                this.lock.unlock();
            }
        }

        public final ReentrantLock getLock() {
            return this.lock;
        }

        public final void setId(final int id) {
            this.id = id;
        }

        public final void setTempItemObjectId(final int id) {
            this.tempItemObjectId = id;
        }

        public final int getTempItemObjectId() {
            return this.tempItemObjectId;
        }

        public final void setTempItemCount(final int count) {
            this.tempItemCount = count;
        }

        public final int getTempItemCount() {
            return this.tempItemCount;
        }

        public final int getObjectId() {
            return this.id;
        }

        public final int getPlayerId() {
            return this.playerId;
        }

        public final int getMapId() {
            return this.mapid;
        }

        public final int getLocal_X() {
            return this.local_x;
        }

        public final int getLocal_Y() {
            return this.local_y;
        }

        public final int getTotalCoin() {
            return this.total_coin;
        }

        public final void setTotalCoin(final int set) {
            this.lock.lock();
            try {
                this.total_coin = set;
                this.updateData();
            } finally {
                this.lock.unlock();
            }
        }

        public final String getTitle() {
            return this.title;
        }

        public final int getOpenType() {
            this.lock.lock();
            try {
                return this.openType;
            } finally {
                this.lock.unlock();
            }
        }

        public final int getPolyId() {
            return this.polyId;
        }

        public final String getPcName() {
            return this.pcName;
        }

        public final void updateData() {
            Connection co = null;
            PreparedStatement ps = null;
            try {
                co = DatabaseFactoryLogin.get().getConnection();
                ps = co.prepareStatement("UPDATE `character_pc_shop_data` SET `total_coin` = ?, `title` = ?, `open_type` = ? WHERE `player_id` = ?");
                ps.setInt(1, this.total_coin);
                ps.setString(2, this.title);
                ps.setInt(3, this.openType);
                ps.setInt(4, this.playerId);
                ps.executeUpdate();
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(ps);
                SQLUtil.close(co);
            }
        }

        public final void updateItemData(final L1ItemInstance item, boolean remove) {
            Connection co = null;
            PreparedStatement ps = null;
            try {
                co = DatabaseFactoryLogin.get().getConnection();
                if (remove) {
                    ps = co.prepareStatement("DELETE FROM `character_pc_shop_items` WHERE `objid` = ?");
                    ps.setInt(1, item.getId());
                    ps.executeUpdate();
                } else {
                    ps = co.prepareStatement("UPDATE `character_pc_shop_items` SET `count` = ? WHERE `objid` = ?");
                    ps.setLong(1, item.getCount());
                    ps.setInt(2, item.getId());
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(ps);
                SQLUtil.close(co);
            }
        }

        public final Map<Integer, CustomPlayerShopItem> getItems() {
            return this.items;
        }

        public final void closeShop() {
            Connection con = null;
            PreparedStatement ps = null;
            try {
                con = DatabaseFactoryLogin.get().getConnection();
                ps = con.prepareStatement("DELETE FROM `character_pc_shop_data` WHERE `player_id` = ?");
                ps.setInt(1, this.playerId);
                if (ps.executeUpdate() > 0) {
                    removeNpc(true);
                    CustomPlayerShopByNpc.getInstance().shopInfo.remove(this.id);
                }
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(ps);
                SQLUtil.close(con);
            }
        }

        public final void resetOpenType(final int openType) {
            resetOpenType(openType, 0, 0, 0);
        }
        public final void resetOpenType(final int openType, int mapid, int local_x, int local_y) {
            this.lock.lock();
            try {
                if (this.openType == 4) {
                    this.mapid = mapid;
                    this.local_x = local_x;
                    this.local_y = local_y;
                    reopenShop();
                }
                this.openType = openType;
                updateData();
                setSpawn();
            } finally {
                this.lock.unlock();
            }
        }
        public final void removeNpcByGM() {
            Connection con = null;
            PreparedStatement ps = null;
            try {
                con = DatabaseFactoryLogin.get().getConnection();
                ps = con.prepareStatement("UPDATE `character_pc_shop_data` SET `open_type` = 4 WHERE `player_id` = ?");
                ps.setInt(1, this.playerId);
                if (ps.executeUpdate() > 0) {
                    this.openType = 4;
                    removeNpc(true);
                }
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(ps);
                SQLUtil.close(con);
            }
        }

        public final void removeNpc(boolean clear) {
            if (this.npc == null) {
                return;
            }
            World.get().removeObject(this.npc);
            World.get().removeVisibleObject(this.npc);
            this.npc.broadcastPacketAll(new S_RemoveObject(this.npc));
            if (clear) {
                this.npc = null;
            }
        }

        public final void setSpawn() {
            boolean add = false;
            if (this.npc == null) {
                add = true;
                final L1Npc npc = NpcTable.get().getTemplate(3067888);
                this.npc = new L1NpcInstance(npc);
                this.npc.setId(IdFactoryNpc.get().nextId());
            }
            this.npc.setTempCharGfx(this.polyId);
            this.npc.setMap((short) getMapId());
            this.npc.setX(getLocal_X());
            this.npc.setHeading((int) ((new Random().nextDouble() * (8)) + 0));
            this.npc.setY(getLocal_Y());
            this.npc.setStatus(polyId == 18891 || polyId == 18893 || polyId == 18892 || polyId == 18894 ? 4 : polyId == 18895 || polyId == 18896 ? 20 : 40);
            this.npc.setName(this.pcName + " : " + this.title + (this.openType == 0 ? "(關閉中)" : ""));
            this.npc.setNameId(this.pcName + " : " + this.title + (this.openType == 0 ? "(關閉中)" : ""));
            this.npc.setShopObjectId(getObjectId());
            if (add) {
                World.get().storeObject(this.npc);
                World.get().addVisibleObject(this.npc);
            } else {
                this.npc.broadcastPacketAll(new S_RemoveObject(this.npc));
                this.npc.broadcastPacketAll(new S_NPCPack(this.npc));
            }
        }
    }
}
