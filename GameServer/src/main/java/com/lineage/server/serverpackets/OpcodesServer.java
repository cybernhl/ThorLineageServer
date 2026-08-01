package com.lineage.server.serverpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 182服務器封包編組設置<br>
 * 類名稱：OpcodesServer<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年10月3日 上午10:28:47<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:ver:21212<br>
 * @version<br>
 */
public interface OpcodesServer {

	public OpcodesServer init(ClientExecutor c);

	public OpcodesServer init(L1PcInstance pc);
	/**
	 * 伺服器版本
	 */
	public static final int S_OPCODE_SERVERVERSION = 0;//182
	
	
	/**
	 * 登入狀態
	 */
	public static final int S_OPCODE_LOGINRESULT = 2;//182
	
	/**
	 * 公告視窗
	 */
	public static final int S_OPCODE_COMMONNEWS = 56;//182
	
	/**
	 * 宣告進入遊戲
	 */
	public static final int S_OPCODE_UNKNOWN1 = 7;//182
	
	
	
	
	/**
	 * 要求傳送 (有動畫) 傳送鎖定 added by terry0412
	 */
	public static final int S_OPCODE_TELEPORT = 4;
	
	/**
	 * 封包盒子
	 */
	public static final int S_OPCODE_PACKETBOX = 123;//182
	
	/**
	 * 立即中斷連線
	 */
	public static final int S_OPCODE_DISCONNECT = 102;//182

	// TODO 頻道相關
	
	/**
	 * 廣播頻道
	 */
	public static final int S_OPCODE_GLOBALCHAT = 15;//182
	
	/**
	 * 一般頻道
	 */
	public static final int S_OPCODE_NORMALCHAT = 19;//182
	
	/**
	 * 使用密語聊天頻道
	 */
	public static final int S_OPCODE_WHISPERCHAT = 20;//182
	
	/**
	 * NPC 對話(文字對話)
	 */
	public static final int S_OPCODE_NPCSHOUT = 45;//182
	
	// TODO 角色
	
	/**
	 * 角色列表
	 */
	public static final int S_OPCODE_CHARAMOUNT = 3;//182
	
	/**
	 * 角色列表資訊
	 */
	public static final int S_OPCODE_CHARLIST = 4;//182 
	
	/**
	 * 創造角色(新創)
	 */
	public static final int S_OPCODE_NEWCHARPACK = 5;//182
	
	/**
	 * 角色移除(立即/非立即)
	 */
	public static final int S_OPCODE_DETELECHAROK = 6;//182
	
	/**
	 * 角色資訊
	 */
	public static final int S_OPCODE_OWNCHARSTATUS = 12;//182
	
	/**
	 * 角色能力狀態(力量,敏捷等)
	 */
	public static final int S_OPCODE_OWNCHARSTATUS2 = 72;
	
	/**
	 * 角色盟徽
	 */
	public static final int S_OPCODE_EMBLEM = 53;//182
	
	/**
	 * 角色封號
	 */
	public static final int S_OPCODE_CHARTITLE = 47;//182
	
	/**
	 * 角色名稱變紫色
	 */
	public static final int S_OPCODE_PINKNAME = 106;//182
	
	/**
	 * 角色皇冠
	 */
	public static final int S_OPCODE_CASTLEMASTER = 71;//182
	
	/**
	 * 角色重置升級能力
	 */
	//public static final int S_OPCODE_CHARRESET = -75;

	//TODO 物件
	
	/**
	 * 物件封包
	 */
	public static final int S_OPCODE_CHARPACK = 11;//182

	/**
	 * 物件刪除
	 */
	public static final int S_OPCODE_REMOVE_OBJECT = 21;//182
	
	/**
	 * 物件血條
	 */
	public static final int S_OPCODE_HPMETER = 104;//182

	/**
	 * 物件屬性(門)
	 */
	public static final int S_OPCODE_ATTRIBUTE = 34;//182
	
	/**
	 * 物件復活
	 */
	public static final int S_OPCODE_RESURRECTION = 17;//182
	
	// TODO 動畫控制
	
	/**
	 * 物件移動
	 */
	public static final int S_OPCODE_MOVEOBJECT = 18;//182
	
	/**
	 * 物件攻擊(傷害力變更封包類型為 writeH(0x0000))
	 */
	public static final int S_OPCODE_ATTACKPACKET = 35;//182
	
	/**
	 * 物件動作種類(長時間)
	 */
	public static final int S_OPCODE_CHARVISUALUPDATE = 29;//182
	
	/**
	 * 物件動作種類(短時間)
	 */
	public static final int S_OPCODE_DOACTIONGFX = 32;//182
	
	/**
	 * 產生動畫(物件)
	 */
	public static final int S_OPCODE_SKILLSOUNDGFX = 55;//182
	
	/**
	 * 產生動畫(地點)
	 */
	public static final int S_OPCODE_EFFECTLOCATION = 83;//182
	
	/**
	 * 範圍魔法
	 */
	public static final int S_OPCODE_RANGESKILLS = 57;//182
	
	// TODO 訊息
	
	/**
	 * 郵件系統
	 */
	//public static final int S_OPCODE_MAIL = -114;
	
	/**
	 * 血盟戰爭訊息(編號,血盟名稱,目標血盟名稱)
	 */
	public static final int S_OPCODE_WAR = 54;//182
	
	/**
	 * NPC對話視窗
	 */
	public static final int S_OPCODE_SHOWHTML = 42;//182
	
	/**
	 * 選取物品數量
	 */
	public static final int S_OPCODE_INPUTAMOUNT = 91;//182
	
	/**
	 * 伺服器訊息(行數/行數,附加字串)
	 */
	public static final int S_OPCODE_SERVERMSG = 16;//182
	
	/**
	 * 選項(Yes/No)
	 */
	public static final int S_OPCODE_YES_NO = 36;//182
	
	/**
	 * 物品鑒定資訊訊息
	 */
	public static final int S_OPCODE_IDENTIFYDESC = 63;//182
	
	/**
	 * 畫面中紅色訊息
	 */
	public static final int S_OPCODE_BLUEMESSAGE = 25;//182
	
	// TODO 屬性更新顯示
	
	/**
	 * 更新物件亮度
	 */
	public static final int S_OPCODE_LIGHT = 27;//182
	
	/**
	 * 更新遊戲天氣
	 */
	public static final int S_OPCODE_WEATHER = 51;//182
	
	/**
	 * 更新物件面向
	 */
	public static final int S_OPCODE_CHANGEHEADING = 28;//182
	
	/**
	 * 更新物件名稱
	 */
	public static final int S_OPCODE_CHANGENAME = 88;//182
	
	/**
	 * 更新HP顯示
	 */
	public static final int S_OPCODE_HPUPDATE = 13;//182
	
	/**
	 * 更新MP顯示
	 */
	public static final int S_OPCODE_MPUPDATE = 77;//182
	
	/**
	 * 更新角色所在的地圖
	 */
	public static final int S_OPCODE_MAPID = 40;//182
	
	/**
	 * 查看各類小地圖
	 */
	public static final int S_OPCODE_USEMAP = 110;//182
	
	/**
	 * 更新遊戲時間
	 */
	public static final int S_OPCODE_GAMETIME = 33;//182
	
	/**
	 * 更新經驗值
	 */
	public static final int S_OPCODE_EXP = 81;//182
	
	/**
	 * 更新正義值
	 */
	public static final int S_OPCODE_LAWFUL = 89;//182
	
	/**
	 * 更新魔攻與魔防
	 */
	public static final int S_OPCODE_SPMR = 86;//182
	
	// TODO 佈告欄
	
	/**
	 * 佈告欄列表
	 */
	public static final int S_OPCODE_BOARD = 95;//182
	
	/**
	 * 佈告欄(訊息閱讀)
	 */
	public static final int S_OPCODE_BOARDREAD = 96;//182
	
	/**
	 * 盟屋拍賣公告欄列表
	 */
	public static final int S_OPCODE_HOUSELIST = 115;// 暫定115 可能有錯
	
	/**
	 * 血盟小屋地圖(地點)
	 */
	public static final int S_OPCODE_HOUSEMAP = 116;//182
	
	// TODO 魔法效果
	
	/**
	 * 魔法效果:毒素
	 */
	public static final int S_OPCODE_POISON = 50;
	
	/**
	 * 魔法效果:勇敢藥水纇
	 */
	public static final int S_OPCODE_SKILLBRAVE = 98;//182
	
	/**
	 * 魔法效果:防禦
	 */
	public static final int S_OPCODE_SKILLICONSHIELD = 109;//182
	
	/**
	 * 魔法效果:加速纇
	 */
	public static final int S_OPCODE_SKILLHASTE = 41;//182
	
	/**
	 * 魔法效果:精準目標
	 */
	public static final int S_OPCODE_TRUETARGET = 122;//182
	
	/**
	 * 魔法效果:水底呼吸
	 */
	public static final int S_OPCODE_BLESSOFEVA = 119;//182
	
	/**
	 * 魔法效果:物件隱形
	 */
	public static final int S_OPCODE_INVIS = 52;//182
	
	/**
	 * 魔法效果:操作混亂(醉酒)
	 */
	//public static final int S_OPCODE_LIQUOR = -2;
	
	/**
	 * 魔法效果:詛咒
	 */
	public static final int S_OPCODE_PARALYSIS = 37;//182
	
	/**
	 * 魔法效果:敏捷提升
	 */
	public static final int S_OPCODE_DEXUP = 108;//182
	
	/**
	 * 魔法效果:力量提升
	 */
	public static final int S_OPCODE_STRUP = 107;//182
	
	/**
	 * 魔法效果:暗盲咒術
	 */
	public static final int S_OPCODE_CURSEBLIND = 10;//182
	
	// TODO 清單

	/**
	 * 更新物品顯示名稱(背包)
	 */
	public static final int S_OPCODE_ITEMNAME = 24;//182
	
	/**
	 * 更新物品可使用數量(背包)
	 */
	public static final int S_OPCODE_ITEMAMOUNT = 111;//182
	
	/**
	 * 物品增加(背包)
	 */
	public static final int S_OPCODE_ADDITEM = 22;//182
	
	/**
	 * 物品刪除(背包)
	 */
	public static final int S_OPCODE_DELETEINVENTORYITEM = 23;//182
	
	/**
	 * 物品色彩狀態(背包)
	 */
	public static final int S_OPCODE_ITEMCOLOR = 14;//182
	
	/**
	 * 物品名單(倉庫)
	 */
	public static final int S_OPCODE_SHOWRETRIEVELIST = 49;//182
	
	/**
	 * 損壞武器清單
	 */
	public static final int S_OPCODE_SELECTLIST = 73;//182
	
	/**
	 * 角色座標名單
	 */
	public static final int S_OPCODE_BOOKMARKS = 48;//182
	
	
	
	public static final int S_OPCODE_POTAL = 85; // 畫面會刷新
	
	// TODO 交易
	
	/**
	 * NPC物品購買清單(人物賣出)
	 */
	public static final int S_OPCODE_SHOWSHOPSELLLIST = 44;//182
	
	
	
	/**
	 * NPC物品販賣清單(人物買入)
	 */
	public static final int S_OPCODE_SHOWSHOPBUYLIST = 43;//182
	
	/**
	 * 交易封包(雙方交易)
	 */
	public static final int S_OPCODE_TRADE = 60;//182
	
	/**
	 * 交易狀態(雙方交易)
	 */
	public static final int S_OPCODE_TRADESTATUS = 62;//182
	
	/**
	 * 交易增加物品(雙方交易)
	 */
	public static final int S_OPCODE_TRADEADDITEM = 61;//182
	
	/**
	 * 交易商店清單(購買/賣出-個人商店)
	 */
	//public static final int S_OPCODE_PRIVATESHOPLIST = -112;
	
	// TODO 其它
	
	/**
	 * 傳送控制戒指
	 */
	public static final int S_OPCODE_ABILITY = 38;//182
	
	/**
	 * 撥放音效
	 */
	public static final int S_OPCODE_SOUND = 74;//182
	
	/**
	 * 角色鎖定(座標異常重整)
	 */
	//public static final int S_OPCODE_CHARLOCK = -127;
	
	/**
	 * 選擇一個目標
	 */
	public static final int S_OPCODE_SELECTTARGET = 87;//182
	
	/**
	 * 城堡寶庫(要求存入資金)
	 */
	public static final int S_OPCODE_DEPOSIT = 76;//182
	
	/**
	 * 城堡寶庫(要求領出資金)
	 */
	public static final int S_OPCODE_DRAWAL = 70;//182

	/**
	 * 僱請傭兵(傭兵購買視窗)
	 */
	//public static final int S_OPCODE_HIRESOLDIER = -123;
	
	/**
	 * 配置城牆上的弓箭手列表(傭兵購買視窗)
	 */
	//public static final int S_OPCODE_PUTBOWSOLDIERLIST = -23;
	
	/**
	 * 傭兵配置清單
	 */
	//public static final int S_OPCODE_PUTSOLDIER = -77;
	
	/**
	 * 稅收設定
	 */
	public static final int S_OPCODE_TAXRATE = 69;//182
	
	/**
	 * 圍城時間設定
	 */
	public static final int S_OPCODE_WARTIME = 84;//182
	
	// XXX 魔法
	
	/**
	 * 魔法購買清單(金幣)
	 */
	public static final int S_OPCODE_SKILLBUY = 78;//182
	
	/**
	 * 魔法購買清單(材料)
	 */
	//public static final int S_OPCODE_SKILLBUYITEM = -39;
	
	/**
	 * 學習魔法材料不足
	 */
	//public static final int S_OPCODE_ITEMERROR = -121;
	
	/**
	 * 魔法清單(增加)
	 */
	public static final int S_OPCODE_ADDSKILL = 30;//182
	
	/**
	 * 魔法清單(移除)
	 */
	public static final int S_OPCODE_DELSKILL = 31;//182
	
	// TODO 其他

	/**
	 * 畫面中紅色訊息(登入來源)
	 */
	//public static final int S_OPCODE_RED = -115;
	
	/**
	 * 更新物件外型
	 */
	public static final int S_OPCODE_POLY = 39;//182
	
	// XXX 未完成
	
	/**
	 * 可配置排列傭兵數(HTML)(EX:僱用的總傭兵數:XX 可排列的傭兵數:XX )
	 */
	//public static final int S_OPCODE_PUTHIRESOLDIER = -44;

	/**
	 * Ping Time
	 */
	//public static final int S_OPCODE_PINGTIME = -90;
	
	/**
	 * 強制登出人物
	 */
	//public static final int S_OPCODE_CHAROUT = -110;
	
	/**
	 * 服務器登入訊息(使用string.tbl)
	 */
	public static final int S_OPCODE_COMMONINFO = 63;//182
	
	/**
	 * 閱讀郵件(舊)
	 */
	public static final int S_OPCODE_LETTER = 94;//182
	
	//XXX unknown

	/**
	 * 未知購物清單1
	 * Server op: 0
	 */
	//public static final int S_OPCODE_SHOPX1 = -0;
	
	/**
	 * 未知購物清單2
	 * Server op: 71
	 */
	//public static final int S_OPCODE_SHOPX2 = -71;
		
	/**
	 * 物理範圍攻擊
	 * Server op: 0000
	 */
	//public static final int S_OPCODE_ATTACKRANGE = -1;


}
