package com.lineage.echo;

/**
 * 182服務器封包編組設置<br>
 * 類名稱：OpcodesClient<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年10月3日 上午10:28:16<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:ver:21212<br>
 * @version<br>
 */
public class OpcodesClient {
	
	public OpcodesClient() {
		
	}

	// 客戶端封包

	// TODO 初始化
	public static final int C_OPCODE_PING = 32;

	/**
	 * 要求接收伺服器版本
	 */
	public static final int C_OPCODE_CLIENTVERSION = -12;

	/**
	 * 要求自動登入伺服器
	 */
	public static final int C_OPCODE_AUTO = -68;

	/**
	 * 要求登入伺服器--輸入完賬號進入角色界面
	 */
	public static final int C_OPCODE_LOGINPACKET = 1;//182

	/**
	 * 要求離開遊戲
	 */
	public static final int C_OPCODE_QUITGAME = 15;//182

	/**
	 * 視窗失焦
	 */
	public static final int C_OPCODE_WINDOWS = -102;
	
	// XXX 攻擊相關
	/**
	 * 要求角色攻擊
	 */
	public static final int C_OPCODE_ATTACK = 23;//182

	/**
	 * 要求使用遠距武器
	 */
	public static final int C_OPCODE_ARROWATTACK = 24;//182

	/**
	 * 要求使用技能
	 */
	public static final int C_OPCODE_USESKILL = 20;//182

	/**
	 * 要求決鬥
	 */
	public static final int C_OPCODE_FIGHT = -115;

	/**
	 * 要求攻擊指定物件
	 */
	public static final int C_OPCODE_SELECTTARGET = -126;

	// XXX 動作相關

	/**
	 * 要求改變角色面向
	 */
	public static final int C_OPCODE_CHANGEHEADING = 9;//182

	/**
	 * 要求丟棄物品(丟棄置地面)
	 */
	public static final int C_OPCODE_DROPITEM = 12;//182

	/**
	 * 要求撿取物品
	 */
	public static final int C_OPCODE_PICKUPITEM = 11;//182

	/**
	 * 要求角色表情動作
	 */
	public static final int C_OPCODE_EXTCOMMAND = -100;

	/**
	 * 要求門的控制/寶箱的開啟
	 */
	public static final int C_OPCODE_DOOR = 14;//182

	// XXX 移動相關

	/**
	 * 要求角色移動
	 */
	public static final int C_OPCODE_MOVECHAR = 10;//182

	/**
	 * 要求座標傳送(洞穴口)
	 */
	public static final int C_OPCODE_ENTERPORTAL = 115;//182

	/**
	 * 要求更新周圍物件(傳送後)
	 */
	public static final int C_OPCODE_TELEPORT = -76;

	/**
	 * 要求更新周圍物件(座標點/洞穴點切換進出後)
	 */
	public static final int C_OPCODE_TELEPORT2 = -58;

	/**
	 * 要求座標異常重整
	 */
	public static final int C_OPCODE_MOVELOCK = -17;

	/**
	 * 要求取消釣魚
	 */
	public static final int C_OPCODE_FISHCLICK = -127;

	// XXX 其它

	/**
	 * 要求變更與使用倉庫密碼
	 */
	public static final int C_OPCODE_PWD = -6;

    /**
     * 修改密碼(登錄界面)
     */
    public static final int C_OPCODE_CHANGE_PASSWORD = 6;//182
    
	/**
	 * 要求回到登入畫面
	 */
	public static final int C_OPCODE_RETURNTOLOGIN = 2;//182

	/**
	 * 要求進入遊戲
	 */
	public static final int C_OPCODE_LOGINTOSERVER = 5;//182

	/**
	 * 要求顯示人物列表(公告視窗後)
	 */
	public static final int C_OPCODE_COMMONCLICK = 51;//182

	/**
	 * 要求創造角色--創建完角色點確認
	 */
	public static final int C_OPCODE_NEWCHAR = 112;//182
	
	/**
	 * 要求進入角色創建界面
	 */
	public static final int C_OPCODE_NEWCHARWIN = 67;//182

	/**
	 * 要求切換角色
	 */
	public static final int C_OPCODE_CHANGECHAR = 16;//182

	/**
	 * 要求角色刪除
	 */
	public static final int C_OPCODE_DELETECHAR = 7;//182

	/**
	 * 要求人物重設
	 */
	public static final int C_OPCODE_CHARRESET = -78;

	/**
	 * 要求死亡後重新開始
	 */
	public static final int C_OPCODE_RESTART = 120;//182

	/**
	 * 要求執行線上人物列表命令(GM管理選單)
	 */
	public static final int C_OPCODE_CALL = -118;

	/**
	 * 要求紀錄快速鍵
	 */
	public static final int C_OPCODE_CHARACTERCONFIG = -47;

	/**
	 * 要求增加記憶座標
	 */
	public static final int C_OPCODE_BOOKMARK = 44;//182
	
    /** 請求 傳送OK . */
    public static final int C_OPCODE_POTALOK = 78;

	/**
	 * 要求刪除記憶座標
	 */
	public static final int C_OPCODE_BOOKMARKDELETE = 45;//182

	/**
	 * 要求變更領地稅率
	 */
	public static final int C_OPCODE_TAXRATE = 65;//182

	/**
	 * 城堡寶庫(要求領出資金)
	 */
	public static final int C_OPCODE_DRAWAL = 66;//182

	/**
	 * 城堡寶庫(要求存入資金)
	 */
	public static final int C_OPCODE_DEPOSIT = 71;//182

	/**
	 * 要求維修物品清單
	 */
	public static final int C_OPCODE_FIX_WEAPON_LIST = 69;//182

	/**
	 * 要求物品維修、領取寵物
	 */
	public static final int C_OPCODE_SELECTLIST = 68;//182

	/**
	 * 要求使用物品
	 */
	public static final int C_OPCODE_USEITEM = 28;//182

	/**
	 * 要求給予物品
	 */
	public static final int C_OPCODE_GIVEITEM = 17;//182

	/**
	 * 要求刪除物品
	 */
	public static final int C_OPCODE_DELETEINVENTORYITEM = 118;//182

	/**
	 * 要求使用信件系統
	 */
	public static final int C_OPCODE_MAIL = -60;

	/**
	 * 要求寵物回報選單
	 */
	public static final int C_OPCODE_PETMENU = 79;//182

	/**
	 * 要求使用寵物道具
	 */
	public static final int C_OPCODE_USEPETITEM = -30;

	/**
	 * 要求查詢PK次數(checkpk)
	 */
	public static final int C_OPCODE_CHECKPK = -81;
	
	// XXX 佈告欄相關

	/**
	 * 要求讀取 公佈欄/ 拍賣公告 訊息列表
	 */
	public static final int C_OPCODE_BOARD = 105;//182

	/**
	 * 要求讀取 公佈欄內容
	 */
	public static final int C_OPCODE_BOARDREAD = 86;//182

	/**
	 * 要求刪除 公佈欄訊息
	 */
	public static final int C_OPCODE_BOARDDELETE = 87;//182

	/**
	 * 要求加入 公佈欄訊息
	 */
	public static final int C_OPCODE_BOARDWRITE = 84;//182

	/**
	 * 要求下一頁 公佈欄訊息
	 */
	public static final int C_OPCODE_BOARDBACK = 85;//182

	// XXX 人際關係相關

	/**
	 * 要求脫離隊伍
	 */
	public static final int C_OPCODE_LEAVEPARTY = 95;// 182

	/**
	 * 要求踢出隊伍
	 */
	public static final int C_OPCODE_BANPARTY = 94;//182

	/**
	 * 要求隊伍名單
	 */
	public static final int C_OPCODE_PARTY = 96;//182

	/**
	 * 要求邀請加入隊伍(要求創立隊伍)
	 */
	public static final int C_OPCODE_CREATEPARTY = 93;//182

	/**
	 * 要求隊伍對話控制(命令/chatparty)
	 */
	public static final int C_OPCODE_CAHTPARTY = -131;

	/**
	 * 要求創立血盟
	 */
	public static final int C_OPCODE_CREATECLAN = 33;//182

	/**
	 * 要求加入血盟
	 */
	public static final int C_OPCODE_JOINCLAN = 34;//182

	/**
	 * 要求脫離血盟
	 */
	public static final int C_OPCODE_LEAVECLANE = 22;//182

	/**
	 * 要求查詢血盟成員
	 */
	public static final int C_OPCODE_PLEDGE = 72;//182

	/**
	 * 要求使用血盟階級功能功能(/rank 人物 見習)
	 */
	public static final int C_OPCODE_RANK = -86;

	/**
	 * 要求驅逐人物離開血盟
	 */
	public static final int C_OPCODE_BANCLAN = 50;//182

	/**
	 * 要求上傳盟徽
	 */
	public static final int C_OPCODE_EMBLEM = 47;//182

	/**
	 * 要求宣戰/投降/休戰
	 */
	public static final int C_OPCODE_WAR = 49;//182

	/**
	 * 要求更新盟輝
	 */
	public static final int C_OPCODE_CLAN = 48;//182

	/**
	 * 要求角色建立封號(/title)
	 */
	public static final int C_OPCODE_TITLE = 43;//182

	/**
	 * 要求婚姻的執行(/propose)
	 */
	public static final int C_OPCODE_PROPOSE = -91;

	/**
	 * 要求查詢玩家(/who)
	 */
	public static final int C_OPCODE_WHO = 26;//182

	/**
	 * 要求新增朋友名單
	 */
	public static final int C_OPCODE_ADDBUDDY = 110;//182

	/**
	 * 要求查詢朋友名單
	 */
	public static final int C_OPCODE_BUDDYLIST = 109;//182

	/**
	 * 要求刪除朋友名單
	 */
	public static final int C_OPCODE_DELBUDDY = 111;//182

	// XXX 商店相關

	/**
	 * 要求交易(雙方交易)
	 */
	public static final int C_OPCODE_TRADE = 52;//182

	/**
	 * 要求增加交易物品(雙方交易)
	 */
	public static final int C_OPCODE_TRADEADDITEM = 56;//182

	/**
	 * 要求完成交易(雙方交易)
	 */
	public static final int C_OPCODE_TRADEADDOK = 55;//182

	/**
	 * 要求取消交易(雙方交易)
	 */
	public static final int C_OPCODE_TRADEADDCANCEL = 54;//182

	/**
	 * 要求角色商店清單(個人商店)
	 */
	public static final int C_OPCODE_PRIVATESHOPLIST = -106;

	/**
	 * 要求開設個人商店(個人商店)
	 */
	public static final int C_OPCODE_SHOP = -40;

	/**
	 * 要求完成學習魔法(金幣)
	 */
	public static final int C_OPCODE_SKILLBUYOK = 74;//182

	/**
	 * 要求學習魔法清單(金幣)
	 */
	public static final int C_OPCODE_SKILLBUY = 73;//182

	/**
	 * 要求完成學習魔法(材料)
	 */
	public static final int C_OPCODE_SKILLBUYOKITEM = -120;

	/**
	 * 要求學習魔法清單(材料)
	 */
	public static final int C_OPCODE_SKILLBUYITEM = 82;//182

	// XXX 對話相關

	/**
	 * 要求選取觀看頻道
	 */
	public static final int C_OPCODE_LOGINTOSERVEROK = 13;//182

	/**
	 * 要求使用一般聊天頻道
	 */
	public static final int C_OPCODE_CHAT = 19;//182

	/**
	 * 要求使用密語聊天頻道
	 */
	public static final int C_OPCODE_CHATWHISPER = 27;//182

	/**
	 * 要求使用廣播聊天頻道
	 */
	public static final int C_OPCODE_CHATGLOBAL = 119;//182

	/**
	 * 要求使用拒絕名單(開啟指定人物訊息)
	 */
	public static final int C_OPCODE_EXCLUDE = 37;//182

	/**
	 * 要求物件對話視窗
	 */
	public static final int C_OPCODE_NPCTALK = 41;//182

	/**
	 * 要求物件對話視窗結果
	 */
	public static final int C_OPCODE_NPCACTION = 39;//182

	/**
	 * 要求物件對話視窗數量選取結果
	 */
	public static final int C_OPCODE_AMOUNT = 81;// 182

	/**
	 * 要求列表物品取得(確認購買或者販賣)
	 */
	public static final int C_OPCODE_RESULT = 40;//182

	/**
	 * 要求點選項目的結果(Y/N)
	 */
	public static final int C_OPCODE_ATTR = 35;//182

	// XXX 固定時間封包
	
	/**
	 * 要求更新時間
	 */
	public static final int C_OPCODE_KEEPALIVE = -66;

	// XXX 未處理之部分(不具有操作碼)

	/**
	 * 要求簡訊服務
	 */
	public static final int C_OPCODE_MSG = -82;
	
	/**
	 * 要求退出鬼魂(觀看模式)
	 */
	public static final int C_OPCODE_EXIT_GHOST = -85;
	
	/**
	 * 要求下船
	 */
	public static final int C_OPCODE_SHIP = -54;

	/**
	 * 要求管理城堡治安
	 */
	public static final int C_OPCODE_CASTLESECURITY = -15;
	
	/**
	 * 要求設置城內治安管理OK
	 */
	public static final int C_OPCODE_SETCASTLESECURITY = -26;
	
	/**
	 * 要求決定下次圍城時間(官方已取消使用)
	 */
	public static final int C_OPCODE_CHANGEWARTIME = 77;//182
	
	/**
	 * 要求決定圍城時間OK
	 */
	public static final int C_OPCODE_SELECTWARTIME = 76;//182
	
	/**
	 * 僱請傭兵(購買傭兵完成)
	 */
	public static final int C_OPCODE_HIRESOLDIER = -21;
	
	/**
	 * 要求配置已僱用的士兵
	 */
	public static final int C_OPCODE_PUTSOLDIER = -57;
	
	/**
	 * 要求配置已僱用的士兵OK
	 */
	public static final int C_OPCODE_PUTHIRESOLDIER = -94;
	
	/**
	 * 要求配置城牆上的弓箭手OK
	 */
	public static final int C_OPCODE_PUTBOWSOLDIER = -110;
	
	/**
	 * 要求進入遊戲(確定服務器登入訊息)
	 */
	public static final int C_OPCODE_COMMONINFO = -122;
	
	//XXX 未知 unknown

	/**
	 * 要求提取天寶
	 */
	public static final int C_OPCODE_CNITEM = -1;

	/**
	 * 要求確認未知購物清單2
	 */
	public static final int C_OPCODE_SHOPX2 = -2;
	
	/**
	 * 要求新增帳號
	 */
	public static final int C_OPCODE_NEWACC = -3;

	


}
