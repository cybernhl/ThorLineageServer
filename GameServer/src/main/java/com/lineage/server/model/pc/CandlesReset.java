/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT,
 * THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package com.lineage.server.model.pc;

import com.lineage.config.ConfigAlt;

/**
 * 回憶蠟燭重置屬性點(270專用).
 * 
 * @version 2015年10月8日上午9:51:42
 * @author jrwz
 */
public final class CandlesReset {
    // 各職業初始化屬性(王族, 騎士, 精靈, 法師, 黑妖)
    public static final int[] ORIGINAL_STR = new int[] { 13, 16, 11, 8, 12 };
    public static final int[] ORIGINAL_DEX = new int[] { 10, 12, 12, 7, 15 };
    public static final int[] ORIGINAL_CON = new int[] { 10, 14, 12, 12, 8 };
    public static final int[] ORIGINAL_WIS = new int[] { 11, 9, 12, 12, 10 };
    public static final int[] ORIGINAL_CHA = new int[] { 13, 12, 9, 8, 9 };
    public static final int[] ORIGINAL_INT = new int[] { 10, 8, 12, 12, 11 };
    // 各職業初始化可分配點數(王族, 騎士, 精靈, 法師, 黑妖)
    public static final int[] ORIGINAL_AMOUNT = new int[] { 8, 4, 7, 16, 10 };
    // 各職業單項屬性最大值(王族, 騎士, 精靈, 法師, 黑妖)
    public static final int[] MAX_STR = new int[] { 20, 20, 18, 20, 18 };
    public static final int[] MAX_DEX = new int[] { 18, 16, 18, 18, 18 };
    public static final int[] MAX_CON = new int[] { 18, 18, 18, 18, 18 };
    public static final int[] MAX_WIS = new int[] { 18, 13, 18, 18, 18 };
    public static final int[] MAX_CHA = new int[] { 18, 16, 16, 18, 18 };
    public static final int[] MAX_INT = new int[] { 18, 12, 18, 18, 18 };
    /** 單項屬性最大值. */
    public static final int MAX_VALUE = ConfigAlt.POWER;
    /** 職業. */
    public static final String[] TYPE = { "[王族]", "[騎士]", "[妖精]", "[法師]", "[黑妖]" };
    /** 初始可用點數(與職業相關). */
    public int originalAmount;
    /** 初始點數(力量). */
    public int srcStr;
    /** 初始點數(敏捷). */
    public int srcDex;
    /** 初始點數(體質). */
    public int srcCon;
    /** 初始點數(精神). */
    public int srcWis;
    /** 初始點數(智力). */
    public int srcInt;
    /** 初始點數(魅力). */
    public int srcCha;
    /** 剩餘點數(力量). */
    public int oStr;
    /** 剩餘點數(敏捷). */
    public int oDex;
    /** 剩餘點數(體質). */
    public int oCon;
    /** 剩餘點數(精神). */
    public int oWis;
    /** 剩餘點數(智力). */
    public int oInt;
    /** 剩餘點數(魅力). */
    public int oCha;
    /** 剩餘可用總點數. */
    public int availablePoints;
    /** 對話檔數據. */
    public String[] msg;

    /** 升級點數(力量). */
    public int upStr;
    /** 升級點數(敏捷). */
    public int upDex;
    /** 升級點數(體質). */
    public int upCon;
    /** 升級點數(精神). */
    public int upWis;
    /** 升級點數(智力). */
    public int upInt;
    /** 升級點數(魅力). */
    public int upCha;
    /** 升級可用總點數. */
    public int upPoints;

    /** 萬能藥點數(力量). */
    public int elixirStr;
    /** 萬能藥點數(敏捷). */
    public int elixirDex;
    /** 萬能藥點數(體質). */
    public int elixirCon;
    /** 萬能藥點數(精神). */
    public int elixirWis;
    /** 萬能藥點數(智力). */
    public int elixirInt;
    /** 萬能藥點數(魅力). */
    public int elixirCha;
    /** 萬能藥可用總點數. */
    public int elixirStats;

    /** 狀態(1=升級點、2=萬能藥點). */
    public int status;
}
