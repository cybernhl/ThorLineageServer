package com.lineage.echo;

import com.lineage.server.WriteLogTxt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.clientpackets.C_AddBookmark;
import com.lineage.server.clientpackets.C_AddBuddy;
import com.lineage.server.clientpackets.C_Amount;
import com.lineage.server.clientpackets.C_Attack;
import com.lineage.server.clientpackets.C_AttackBow;
import com.lineage.server.clientpackets.C_Attr;
import com.lineage.server.clientpackets.C_AuthLogin;
import com.lineage.server.clientpackets.C_BanClan;
import com.lineage.server.clientpackets.C_BanParty;
import com.lineage.server.clientpackets.C_Board;
import com.lineage.server.clientpackets.C_BoardBack;
import com.lineage.server.clientpackets.C_BoardDelete;
import com.lineage.server.clientpackets.C_BoardRead;
import com.lineage.server.clientpackets.C_BoardWrite;
import com.lineage.server.clientpackets.C_Buddy;
import com.lineage.server.clientpackets.C_ChangeHeading;
import com.lineage.server.clientpackets.C_ChangePassword;
import com.lineage.server.clientpackets.C_ChangeWarTime;
import com.lineage.server.clientpackets.C_Chat;
import com.lineage.server.clientpackets.C_ChatGlobal;
import com.lineage.server.clientpackets.C_ChatWhisper;
import com.lineage.server.clientpackets.C_CheckPK;
import com.lineage.server.clientpackets.C_Clan;
import com.lineage.server.clientpackets.C_CommonClick;
import com.lineage.server.clientpackets.C_CreateChar;
import com.lineage.server.clientpackets.C_CreateClan;
import com.lineage.server.clientpackets.C_CreateParty;
import com.lineage.server.clientpackets.C_DelBuddy;
import com.lineage.server.clientpackets.C_DeleteBookmark;
import com.lineage.server.clientpackets.C_DeleteChar;
import com.lineage.server.clientpackets.C_DeleteInventoryItem;
import com.lineage.server.clientpackets.C_Deposit;
import com.lineage.server.clientpackets.C_Door;
import com.lineage.server.clientpackets.C_Drawal;
import com.lineage.server.clientpackets.C_DropItem;
import com.lineage.server.clientpackets.C_Emblem;
import com.lineage.server.clientpackets.C_EnterPortal;
import com.lineage.server.clientpackets.C_Exclude;
import com.lineage.server.clientpackets.C_ExtraCommand;
import com.lineage.server.clientpackets.C_FixWeaponList;
import com.lineage.server.clientpackets.C_GiveItem;
import com.lineage.server.clientpackets.C_ItemUSe;
import com.lineage.server.clientpackets.C_JoinClan;
import com.lineage.server.clientpackets.C_LeaveClan;
import com.lineage.server.clientpackets.C_LeaveParty;
import com.lineage.server.clientpackets.C_LoginToServer;
import com.lineage.server.clientpackets.C_LoginToServerOK;
import com.lineage.server.clientpackets.C_Mail;
import com.lineage.server.clientpackets.C_MoveChar;
import com.lineage.server.clientpackets.C_NPCAction;
import com.lineage.server.clientpackets.C_NPCTalk;
import com.lineage.server.clientpackets.C_NewCharSelect;
import com.lineage.server.clientpackets.C_NewCharWin;
import com.lineage.server.clientpackets.C_Party;
import com.lineage.server.clientpackets.C_Password;
import com.lineage.server.clientpackets.C_PickUpItem;
import com.lineage.server.clientpackets.C_Pledge;
import com.lineage.server.clientpackets.C_Restart;
import com.lineage.server.clientpackets.C_Result;
import com.lineage.server.clientpackets.C_ReturnToLogin;
import com.lineage.server.clientpackets.C_SelectList;
import com.lineage.server.clientpackets.C_SelectTarget;
import com.lineage.server.clientpackets.C_ServerVersion;
import com.lineage.server.clientpackets.C_Shop;
import com.lineage.server.clientpackets.C_ShopList;
import com.lineage.server.clientpackets.C_SkillBuy;
import com.lineage.server.clientpackets.C_SkillBuyItem;
import com.lineage.server.clientpackets.C_SkillBuyOK;
import com.lineage.server.clientpackets.C_TaxRate;
import com.lineage.server.clientpackets.C_Title;
import com.lineage.server.clientpackets.C_Trade;
import com.lineage.server.clientpackets.C_TradeAddItem;
import com.lineage.server.clientpackets.C_TradeCancel;
import com.lineage.server.clientpackets.C_TradeOK;
import com.lineage.server.clientpackets.C_UnLock;
import com.lineage.server.clientpackets.C_UseSkill;
import com.lineage.server.clientpackets.C_War;
import com.lineage.server.clientpackets.C_Who;
import com.lineage.server.datatables.sql.OpcodesTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 客戶端要求使用封包<br>
 * 類名稱：ClientUseOP<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午8:02:59<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class ClientUseOP {

	private static final Log _log = LogFactory.getLog(ClientUseOP.class);
	
	public static void execute(byte[] data, ClientExecutor client) {

		final L1PcInstance pc = client.getActiveChar();

		try {
			int opId = data[0] & 0xff;
			client.setPacketCheck(opId);
//			if (OpcodesTable.get().getOpBroad(opId, "client") == 1) {
//				System.out.println("C包：" + OpcodesTable.get().getOpClassName(opId, "client") + " : " + OpcodesTable.get().getOpName(opId, "client") + " : " + opId);
//			}
			if (client.getActiveChar() != null && client.getActiveChar().getId() == 917282931) {
				WriteLogTxt.NormalLog(client.getActiveChar().getName() + "的封包監聽.txt", OpcodesTable.get().getOpClassName(opId, "client"));
			}
			switch (opId) {
			case OpcodesClient.C_OPCODE_CLIENTVERSION:// 要求驗證客戶端版本
				BasePacketPooling.setPool(new C_ServerVersion(data, client));
				break;

			case OpcodesClient.C_OPCODE_QUITGAME:// 要求離開遊戲
				//LineageServer.close();
				break;

			case OpcodesClient.C_OPCODE_CHANGE_PASSWORD:// 修改密碼(登錄界面)
				BasePacketPooling.setPool(new C_ChangePassword(data, client));
				break;

			case OpcodesClient.C_OPCODE_LOGINPACKET:// 要求登入伺服器
				BasePacketPooling.setPool(new C_AuthLogin(data, client));
				break;

			case OpcodesClient.C_OPCODE_COMMONCLICK:// 登入時的公告確認
				BasePacketPooling.setPool(new C_CommonClick(data, client));
				break;

			case OpcodesClient.C_OPCODE_RETURNTOLOGIN:// 要求回到登入畫面
				BasePacketPooling.setPool(new C_ReturnToLogin(data, client));
				break;
				
			case OpcodesClient.C_OPCODE_NEWCHARWIN:// 要求進入角色創建界面
				BasePacketPooling.setPool(new C_NewCharWin(data, client));
				break;

			case OpcodesClient.C_OPCODE_NEWCHAR:// 要求確認創造角色完成
				BasePacketPooling.setPool(new C_CreateChar(data, client));
				break;

			case OpcodesClient.C_OPCODE_DELETECHAR:// 要求刪除角色
				BasePacketPooling.setPool(new C_DeleteChar(data, client));
				break;

			case OpcodesClient.C_OPCODE_LOGINTOSERVER:// 要求進入遊戲
				BasePacketPooling.setPool(new C_LoginToServer(data, client));
				break;

			case OpcodesClient.C_OPCODE_CHANGECHAR:// 要求切換角色
				BasePacketPooling.setPool(new C_NewCharSelect(data, client));
				break;

			case OpcodesClient.C_OPCODE_MOVECHAR:// 要求角色移動
				BasePacketPooling.setPool(new C_MoveChar(data, client));
				break;

			case OpcodesClient.C_OPCODE_DOOR:// 要求開關門
				BasePacketPooling.setPool(new C_Door(data, client));
				break;

			case OpcodesClient.C_OPCODE_BOOKMARK:// 要求增加記憶座標
				BasePacketPooling.setPool(new C_AddBookmark(data, client));
				break;
			case OpcodesClient.C_OPCODE_POTALOK:
				BasePacketPooling.setPool(new C_UnLock(data, client));				
				break;

			case OpcodesClient.C_OPCODE_BOOKMARKDELETE:// 要求刪除記憶座標
				BasePacketPooling.setPool(new C_DeleteBookmark(data, client));
				break;

			case OpcodesClient.C_OPCODE_CHAT:// 要求使用一般聊天頻道
				BasePacketPooling.setPool(new C_Chat(data, client));
				break;

			case OpcodesClient.C_OPCODE_CHATGLOBAL:// 要求使用廣播聊天頻道
				BasePacketPooling.setPool(new C_ChatGlobal(data, client));
				break;

			case OpcodesClient.C_OPCODE_WHO:// 要求查詢遊戲人數
				BasePacketPooling.setPool(new C_Who(data, client));
				break;

			case OpcodesClient.C_OPCODE_CHATWHISPER:// 要求使用密語聊天頻道
				BasePacketPooling.setPool(new C_ChatWhisper(data, client));
				break;

			case OpcodesClient.C_OPCODE_LOGINTOSERVEROK:// 要求選取觀看頻道
				BasePacketPooling.setPool(new C_LoginToServerOK(data, client));
				break;

			case OpcodesClient.C_OPCODE_CREATECLAN:// 要求創立血盟
				BasePacketPooling.setPool(new C_CreateClan(data, client));
				break;

			case OpcodesClient.C_OPCODE_LEAVECLANE:// 要求離開血盟
				BasePacketPooling.setPool(new C_LeaveClan(data, client));
				break;

			case OpcodesClient.C_OPCODE_BANCLAN:// 要求驅逐人物離開血盟
				BasePacketPooling.setPool(new C_BanClan(data, client));
				break;

			case OpcodesClient.C_OPCODE_JOINCLAN:// 要求加入血盟
				BasePacketPooling.setPool(new C_JoinClan(data, client));
				break;

			case OpcodesClient.C_OPCODE_ATTR:// 要求點選項目的結果(Y/N)
				BasePacketPooling.setPool(new C_Attr(data, client));
				break;

			case OpcodesClient.C_OPCODE_EMBLEM:// 要求上傳盟徽
				BasePacketPooling.setPool(new C_Emblem(data, client));
				break;

			case OpcodesClient.C_OPCODE_CLAN:// 要求盟徽下載
				BasePacketPooling.setPool(new C_Clan(data, client));
				break;

			case OpcodesClient.C_OPCODE_CHANGEHEADING:// 要求改變角色面向
				BasePacketPooling.setPool(new C_ChangeHeading(data, client));
				break;

			case OpcodesClient.C_OPCODE_USEITEM:// 要求使用物品
				BasePacketPooling.setPool(new C_ItemUSe(data, client));
				break;

			case OpcodesClient.C_OPCODE_DROPITEM:// 要求丟棄物品
				BasePacketPooling.setPool(new C_DropItem(data, client));
				break;

			case OpcodesClient.C_OPCODE_PICKUPITEM:// 要求撿取物品
				BasePacketPooling.setPool(new C_PickUpItem(data, client));
				break;

			case OpcodesClient.C_OPCODE_ATTACK:// 要求角色攻擊(近距離)
				BasePacketPooling.setPool(new C_Attack(data, client));
				break;

			case OpcodesClient.C_OPCODE_ARROWATTACK:// 要求角色攻擊(遠距離)
				BasePacketPooling.setPool(new C_AttackBow(data, client));
				break;

			case OpcodesClient.C_OPCODE_USESKILL:// 要求使用技能
				BasePacketPooling.setPool(new C_UseSkill(data, client));
				break;

			case OpcodesClient.C_OPCODE_NPCTALK:// 要求物件對話視窗
				BasePacketPooling.setPool(new C_NPCTalk(data, client));
				break;

			case OpcodesClient.C_OPCODE_NPCACTION:// 要求物件對話視窗結果
				BasePacketPooling.setPool(new C_NPCAction(data, client));
				break;
				
			case OpcodesClient.C_OPCODE_AMOUNT:// 要求物件對話視窗數量選取結果
				BasePacketPooling.setPool(new C_Amount(data, client));
				break;
				
			case OpcodesClient.C_OPCODE_RESULT:// 要求列表物品取得(確認購買或者販賣)
				BasePacketPooling.setPool(new C_Result(data, client));
				break;

			case OpcodesClient.C_OPCODE_SHOP:// 要求開設個人商店(個人商店)
				BasePacketPooling.setPool(new C_Shop(data, client));
				break;

			case OpcodesClient.C_OPCODE_TRADE:// 要求交易(個人)
				BasePacketPooling.setPool(new C_Trade(data, client));
				break;

			case OpcodesClient.C_OPCODE_TRADEADDCANCEL:// 要求取消交易(個人)
				BasePacketPooling.setPool(new C_TradeCancel(data, client));
				break;

			case OpcodesClient.C_OPCODE_TRADEADDOK:// 要求完成交易(個人)
				BasePacketPooling.setPool(new C_TradeOK(data, client));
				break;

			case OpcodesClient.C_OPCODE_TRADEADDITEM:// 要求交易(添加物品)
				BasePacketPooling.setPool(new C_TradeAddItem(data, client));
				break;

			case OpcodesClient.C_OPCODE_ENTERPORTAL:// 要求傳送 (進入地監)
				BasePacketPooling.setPool(new C_EnterPortal(data, client));
				break;

			case OpcodesClient.C_OPCODE_MAIL:// 要求郵件封包
				BasePacketPooling.setPool(new C_Mail(data, client));
				break;

			case OpcodesClient.C_OPCODE_RESTART:// 要求死亡後重新開始
				BasePacketPooling.setPool(new C_Restart(data, client));
				break;
				
			case OpcodesClient.C_OPCODE_CREATEPARTY:// 要求邀請加入隊伍(要求創立隊伍)
				BasePacketPooling.setPool(new C_CreateParty(data, client));
				break;
				
			case OpcodesClient.C_OPCODE_LEAVEPARTY:// 要求脫離隊伍
				BasePacketPooling.setPool(new C_LeaveParty(data, client));
				break;

			case OpcodesClient.C_OPCODE_PARTY:// 要求查詢隊伍成員
				BasePacketPooling.setPool(new C_Party(data, client));
				break;

			case OpcodesClient.C_OPCODE_BANPARTY:// 要求驅逐隊伍
				BasePacketPooling.setPool(new C_BanParty(data, client));
				break;

			case OpcodesClient.C_OPCODE_GIVEITEM:// 要求給予物品
				BasePacketPooling.setPool(new C_GiveItem(data, client));
				break;

			case OpcodesClient.C_OPCODE_TITLE:// 要求賦予封號
				BasePacketPooling.setPool(new C_Title(data, client));
				break;

			case OpcodesClient.C_OPCODE_DELETEINVENTORYITEM:// 要求刪除物品
				BasePacketPooling.setPool(new C_DeleteInventoryItem(data,
						client));
				break;

			case OpcodesClient.C_OPCODE_SKILLBUY:// 要求學習魔法(金幣)
				BasePacketPooling.setPool(new C_SkillBuy(data, client));
				break;
				
			case OpcodesClient.C_OPCODE_SKILLBUYITEM:// 要求學習魔法清單(材料)
				BasePacketPooling.setPool(new C_SkillBuyItem(data, client));
				break;

			case OpcodesClient.C_OPCODE_SKILLBUYOK:// 要求學習魔法完成
				BasePacketPooling.setPool(new C_SkillBuyOK(data, client));
				break;

			case OpcodesClient.C_OPCODE_PLEDGE:// 要求查詢血盟成員
				BasePacketPooling.setPool(new C_Pledge(data, client));
				break;

			case OpcodesClient.C_OPCODE_BOARD:// 要求瀏覽公佈欄
				BasePacketPooling.setPool(new C_Board(data, client));
				break;

			case OpcodesClient.C_OPCODE_BOARDWRITE:// 要求寫入公佈欄訊息
				BasePacketPooling.setPool(new C_BoardWrite(data, client));
				break;

			case OpcodesClient.C_OPCODE_BOARDREAD:// 要求閱讀佈告單個欄訊息
				BasePacketPooling.setPool(new C_BoardRead(data, client));
				break;

			case OpcodesClient.C_OPCODE_BOARDDELETE:// 要求刪除公佈欄內容
				BasePacketPooling.setPool(new C_BoardDelete(data, client));
				break;

			case OpcodesClient.C_OPCODE_BOARDBACK:// 要求翻頁 next
				BasePacketPooling.setPool(new C_BoardBack(data, client));
				break;

			case OpcodesClient.C_OPCODE_TAXRATE:// 要求稅收設定封包
				BasePacketPooling.setPool(new C_TaxRate(data, client));
				break;

			case OpcodesClient.C_OPCODE_DEPOSIT:// 要求存入資金
				BasePacketPooling.setPool(new C_Deposit(data, client));
				break;

			case OpcodesClient.C_OPCODE_DRAWAL:// 要求領出資金
				BasePacketPooling.setPool(new C_Drawal(data, client));
				break;

			case OpcodesClient.C_OPCODE_SELECTWARTIME:// 要求選擇
														// 變更攻城時間
				BasePacketPooling.setPool(new C_ChangeWarTime(data, client));
				break;

			case OpcodesClient.C_OPCODE_CHANGEWARTIME:// 要求選擇攻城時間
				BasePacketPooling.setPool(new C_ChangeWarTime(data, client));
				break;

			case OpcodesClient.C_OPCODE_WAR:// 要求宣戰
				BasePacketPooling.setPool(new C_War(data, client));
				break;

			case OpcodesClient.C_OPCODE_EXCLUDE:// 要求使用拒絕名單(開啟指定人物訊息)
				BasePacketPooling.setPool(new C_Exclude(data, client));
				break;

			case OpcodesClient.C_OPCODE_CHECKPK:// 要求查詢PK次數
				BasePacketPooling.setPool(new C_CheckPK(data, client));
				break;

			case OpcodesClient.C_OPCODE_PRIVATESHOPLIST:// 個人商店販賣或購買
				BasePacketPooling.setPool(new C_ShopList(data, client));
				break;

			case OpcodesClient.C_OPCODE_FIX_WEAPON_LIST:// 要求維修物品清單
				BasePacketPooling.setPool(new C_FixWeaponList(data, client));
				break;

			case OpcodesClient.C_OPCODE_SELECTLIST:// 要求維修物品、領取寵物
				BasePacketPooling.setPool(new C_SelectList(data, client));
				break;

			case OpcodesClient.C_OPCODE_BUDDYLIST:// 要求查詢好友名單
				BasePacketPooling.setPool(new C_Buddy(data, client));
				break;

			case OpcodesClient.C_OPCODE_ADDBUDDY:// 要求添加好友
				BasePacketPooling.setPool(new C_AddBuddy(data, client));
				break;

			case OpcodesClient.C_OPCODE_DELBUDDY:// 要求刪除好友
				BasePacketPooling.setPool(new C_DelBuddy(data, client));
				break;

			case OpcodesClient.C_OPCODE_PETMENU:// 要求攻擊指定物件(寵物&召喚)
				BasePacketPooling.setPool(new C_SelectTarget(data, client));
				break;

			case OpcodesClient.C_OPCODE_EXTCOMMAND:// 要求控制+數字動作請求
				BasePacketPooling.setPool(new C_ExtraCommand(data, client));
				break;

			default:
				if ((data[0] & 0xff) != 32 && (data[0] & 0xff) != 29) {
					if (pc != null && pc.isGm()) {
						pc.sendPackets(new S_SystemMessage("未處理封包: " + (data[0] & 0xff) + " : "
								+ pc.getName() + ":" + pc.getX() + ":" + pc.getY() + ":" + pc.getMapId() + ":" + pc.getHeading()));
						System.out.println("未處理封包: " + (data[0] & 0xff) + " : "
								+ pc.getName() + ":" + pc.getX() + ":" + pc.getY() + ":" + pc.getMapId() + ":" + pc.getHeading());
					} else {
						System.out.println("未處理封包: " + (data[0] & 0xff));
					}
				}
				break;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
