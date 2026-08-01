package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.*;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.PolyTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;

/**
 * 要求使用技能
 *
 * @author daien
 *
 */
public class C_UseSkill extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_UseSkill.class);

	// 隱身狀態下可用的魔法
	private static final int[] _cast_with_invis = {
		HEAL, LIGHT, SHIELD, TELEPORT, HOLY_WEAPON, CURE_POISON, ENCHANT_WEAPON, DETECTION,
		14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57,
		60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, REDUCTION_ARMOR,
		BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100,
		101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114, 115, 116,
		117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149,
		150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169, 170,
		171, SOUL_OF_FLAME, ADDITIONAL_FIRE};

	public C_UseSkill(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			
			final L1PcInstance pc = client.getActiveChar();

			/*final long noetime = System.currentTimeMillis();
			if (noetime - pc.get_spr_skill_time() <= SprTable.get().spr_skill_speed(pc.getTempCharGfx())) {
				if (!pc.isGm()) {
					pc.getNetConnection().kick();
					return;
				}
			}
			pc.set_spr_skill_time(noetime);*/

			if (pc.isDead()) { // 死亡
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			if (pc.isPrivateShop()) {// 商店村模式
				return;
			}
			
//            // 掛機狀態無法釋放技能
//            if (pc.isActived()) {
//            	return;
//            }

			if (pc.getInventory().getWeight182() >= 197) { // 重量過重
				// 316 \f1你攜帶太多物品，因此無法使用法術。
				pc.sendPackets(new S_ServerMessage(316));
				return;
			}
			
			if (!pc.getMap().isUsableSkill()) {
				// 563 \f1你無法在這個地方使用。
				pc.sendPackets(new S_ServerMessage(563));
				return;
			}

			// 技能延遲狀態
			if (pc.isSkillDelay()) {
				return;
			}

			boolean isError = false;

			// 變身限制
			final int polyId = pc.getTempCharGfx();
			final L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
			// 該變身無法使用魔法
			if ((poly != null) && !poly.canUseSkill()) {
				isError = true;
			}
			
			// 麻痺・凍結狀態
			if (pc.isParalyzed() && !isError) {
				isError = true;
			}

			// 下列狀態無法使用魔法(魔法封印)
			if (pc.hasSkillEffect(SILENCE) && !isError) {
				isError = true;
			}

			// 下列狀態無法使用魔法(封印禁地)
			if (pc.hasSkillEffect(AREA_OF_SILENCE) && !isError) {
				isError = true;
			}

			// 下列狀態無法使用魔法(沈默毒素效果)
			if (pc.hasSkillEffect(STATUS_POISON_SILENCE) && !isError) {
				isError = true;
			}

			// 無法攻擊/使用道具/技能/回城的狀態
			if (pc.isParalyzedX() && !isError) {
				isError = true;
			}

			if (isError) {
				// 285 \f1在此狀態下無法使用魔法。
				pc.sendPackets(new S_ServerMessage(285));
				return;
			}

			// 加載封包內容
			final int row = this.readC();
			final int column = this.readC();
			
			// 計算使用的技能編號(>> 1: 除)  (<< 1: 乘) 3=*8
			final int skillId = (row << 3) + column + 1;

			if (skillId > 176) {
				return;
			}
			if (skillId < 0) {
				return;
			}
//			if (    // 法師未開放魔法停止封包發送
//					skillId == 55 || skillId == 63 || skillId == 65
//					|| skillId == 66 || skillId == 72
//					|| skillId == 75 || skillId == 76 || skillId == 78
//					|| skillId == 79 || skillId == 80 || skillId == 52
//
//					// 妖精未開放魔法停止封包發送
//					|| skillId == 167 || skillId == 162 || skillId == 161
//					|| skillId == 157 || skillId == 153 || skillId == 152) {
//				pc.sendPackets(new S_SystemMessage("未開啟的魔法."));
//				return;
//			}

			// 隱身狀態可用魔法限制
			if (pc.isInvisble() || pc.isInvisDelay()) {
				if (!this.isInvisUsableSkill(skillId)) {
					// 1003：透明狀態無法使用的魔法。  
					pc.sendPackets(new S_ServerMessage(1003));
					return;
				}
			}
			
			// 技能合法判斷
			if (!pc.isSkillMastery(skillId)) {
				//_log.info(pc.getAccountName() + ":" + pc.getName() + "(" + pc.getType() + ") 非法技能:" + skillId);
				return;
			}
			
			// 檢查地圖使用權
			CheckUtil.isUserMap(pc);

			String charName = null;
			//String message = null;
			
			int targetId = 0;
			int targetX = 0;
			int targetY = 0;
			if (pc.getInventory().getWeight182() >= 24) { // 重量過重
				// 110 \f1當負重過重的時候，無法戰鬥。
				pc.sendPackets(new S_ServerMessage(110));
				// _log.error("要求角色攻擊:重量過重");
				return;
			}
			boolean checkLoc = false;
			if (decrypt.length > 4) {
				boolean isAttack = false;// 為攻擊型態技能
				switch (skillId) {
				case 43:// 加速術43
					try {
						targetId = readD();
						checkLoc = true;
						
					} catch (final Exception e) {
					}
					break;
					
				case 52:// 神聖疾走52
				case 54:// 強力加速術54
				case 101:// 行走加速101
				case 150:// 風之疾走150
					try {
						targetId = readD();
						
					} catch (final Exception e) {
					}
					break;
					
				case 116:// 呼喚盟友 
				case 118:// 援護盟友
					try {
						charName = readS();
						
					} catch (final Exception e) {
					}
					break;

				case 113:// 精準目標   1600
					try {
						targetId = readD();
						targetX = readH();
						targetY = readH();

						pc.setText(readS());
						checkLoc = true;
						
					} catch (final Exception e) {
					}
					break;

				case 5:// 指定傳送 1100
				case 69:// 集體傳送術 1200
					try {
						readH(); // MapID
						targetId = readD(); // Bookmark ID
						
					} catch (final Exception e) {
					}
					break;

				case 58:// 火牢 1100
				case 63:// 治癒能量風暴 1100
					try {
						targetX = readH();
						targetY = readH();
						
					} catch (final Exception e) {
					}
					break;

				case 4:// 光箭  1100
				case 6:// 冰箭  1100
				case 7:// 風刃   1100
				case 10:// 寒冷戰慄  1100
				case 15:// 火箭  1300
				case 16:// 地獄之牙 1300
				case 17:// 極光雷電 1500
				case 18:// 起死回生術  1500
				case 22:// 寒冰氣息 1300
				case 25:// 燃燒的火球 1600
				case 28:// 吸血鬼之吻 1250
				case 30:// 巖牢 1600
				case 34:// 極道落雷 1250
				case 38:// 冰錐  1100
				case 45:// 地裂術 1100
				case 46:// 烈炎術 1650
				case 50:// 冰矛圍籬  1600
				case 65:// 雷霆風暴  3000
				case 74:// 流星雨 4000
				case 77:// 究極光裂術 6000
				case 108:// 會心一擊 1800
				case 132:// 三重矢 1250
					try {
						targetId = readD();
						targetX = readH();
						targetY = readH();
						isAttack = true;
						checkLoc = true;
						//_log.info("技能ID:" + skillId + "/"+targetId+"/"+targetX+"/"+targetY);

					} catch (final Exception e) {
					}
					break;
					
				case 1:// 初級治癒術 1300
				case 8:// 神聖武器  1500
				case 9:// 解毒術  1250
				case 11:// 毒咒 1650
				case 19:// 中級治癒術 1250
				case 20:// 闇盲咒術 1650
				case 23:// 能量感測 1100
				case 26:// 通暢氣脈術 1250
				case 27:// 壞物術 4200
				case 29:// 緩速術 1600
				case 33:// 木乃伊的詛咒  6200
				case 35:// 高級治癒術  1500
				case 36:// 迷魅術  1250
				case 37:// 聖潔之光 1250
				case 39:// 魔力奪取  1650
				case 40:// 黑闇之影 1650
				case 41:// 造屍術
				case 42:// 體魄強健術  1650
				//case 43:// 加速術 900
				case 44:// 魔法相消術  1450
				case 47:// 弱化術 1650
				case 48:// 祝福魔法武器  1250
				case 55:// 狂暴術  1250
				case 56:// 疾病術 1650
				case 57:// 全部治癒術 1650
				case 61:// 返生術 
				case 64:// 魔法封印 1650
				case 66:// 沉睡之霧 2750
				case 67:// 變形術 
				case 68:// 聖結界 2750
				case 71:// 藥水霜化術 1650
				case 75:// 終極返生術
				case 76:// 集體緩速術 4200
				case 79:// 靈魂昇華 1250
				case 87:// 衝擊之暈 9500
				case 103:// 暗黑盲咒 2700
				case 133:// 弱化屬性 1250
				case 145:// 釋放元素 1250
				case 148:// 火焰武器 1450
				case 149:// 風之神射 1450
				case 151:// 大地防護 1450
				case 152:// 地面障礙 1250
				case 153:// 魔法消除 1250
				case 157:// 大地屏障 1250
				case 158:// 生命之泉 1450
				case 160:// 水之防護 1450
				case 165:// 生命呼喚 1450
				case 167:// 風之枷鎖 1250
				case 170:// 水之元氣 1450
				case 173:// 污濁之水 2200
				case 174:// 精準射擊 4250
					try {
						targetId = readD();
						checkLoc = true;
						//_log.info("技能ID:" + skillId + "/"+targetId+"/"+targetX+"/"+targetY);
						
					} catch (final Exception e) {
					}
					break;
					
				case 2:// 日光術 1300
				case 3:// 保護罩  1300
				case 12:// 擬似魔法武器  1500
				case 13:// 無所遁形術 1650
				case 14:// 負重強化  1250
				case 21:// 鎧甲護持 1500
				case 31:// 魔法屏障 1250
				case 32:// 冥想術 1250
				case 49:// 體力回復術  1650
				case 51:// 召喚術 2200
				//case 52:// 神聖疾走  1250 
				case 53:// 龍捲風 2500
				//case 54:// 強力加速術 
				case 59:// 冰雪暴  2600
				case 60:// 隱身術 1250
				case 62:// 震裂術 2600
				case 70:// 火風暴 3000
				case 72:// 強力無所遁形術 1650
				case 73:// 創造魔法武器 1500
				case 78:// 絕對屏障 13000
				case 80:// 冰雪颶風 4000
				//case 88:// 增幅防禦 4500
				case 89:// 尖刺盔甲 6450
				case 90:// 堅固防護 4450
				//case 91:// 反擊屏障 4450
				case 97:// 暗隱術    11700
				case 98:// 附加劇毒  2750
				case 99:// 影之防護 2750
				case 100:// 提煉魔石  2000
				//case 101:// 行走加速 1750
				case 102:// 燃燒鬥志 4700
				case 104:// 毒性抵抗 1750
				case 105:// 雙重破壞 1750
				case 106:// 暗影閃避 1750
				case 107:// 暗影之牙 1700
				case 109:// 力量提升 1750
				case 110:// 敏捷提升 1750
				case 111:// 閃避提升 1750
				case 114:// 激勵士氣  1600
				case 115:// 鋼鐵士氣 1500
				case 117:// 衝擊士氣 1500
				case 129:// 魔法防禦 1300 
				case 130:// 心靈轉換 1450
				case 131:// 世界樹的呼喚
				case 134:// 鏡反射 350
				case 137:// 淨化精神 1450
				case 138:// 屬性防禦 1450
				case 146:// 魂體轉換 2500
				case 147:// 單屬性防禦 1450
				//case 150:// 風之疾走 1450
				case 154:// 召喚屬性精靈 1400
				case 155:// 烈炎氣息 1400
				case 156:// 暴風之眼 1400
				case 159:// 大地的祝福 1450
				case 161:// 封印禁地 10300
				case 162:// 召喚強力屬性精靈 1450
				case 163:// 烈炎武器 1450
				case 164:// 生命的祝福 1650
				case 166:// 暴風神射 1450
				case 168:// 鋼鐵防護 1450
				case 169:// 體能激發 1450
				case 171:// 屬性之火 1450
				case 172:// 暴風疾走 1450
				case 175:// 烈焰之魂
				case 176:// 能量激發
					try {
						targetId = readD();
						//_log.info("技能ID:" + skillId + "/"+targetId+"/"+targetX+"/"+targetY);
						
					} catch (final Exception e) {
					}
					break;

				case 88:// 增幅防禦4500
					try {
						if (pc.getInventory().getTypeEquipped(2, 7) >= 1) {
							targetId = readD();
						} else {
							return;
						}
						
					} catch (final Exception e) {
					}
					break;

				case 91:// 反擊屏障4450
					try {
						if (pc.getInventory().getTypeEquipped(1, 3) >= 1) {
							targetId = readD();
						} else {
							return;
						}
						
					} catch (final Exception e) {
					}
					break;
					
				default:
					try {
						targetId = readD();
						//_log.info("技能ID:" + skillId + "/"+targetId+"/"+targetX+"/"+targetY);
						
					} catch (final Exception e) {
						/*OutErrorMsg.put(this.getClass().getSimpleName(), 
								"檢查 C_UseSkill 技能碼定位(核心管理者參考) SkillId: " + skillId, 
								e);*/
					}
					break;
				}
				if (targetId != 0) {
					final L1Object target = World.get().findObject(targetId);
					if (checkLoc) {
						// 不在畫面內
						if (target == null || !World.get().getVisibleObjects(pc, target)) {
							/*OutErrorMsg.put(pc.getId(), 
									pc.getName() + " 對像人物不在同畫面內 SkillId: " + skillId + " 對像:" + target.getId());*/
							return;
						}
					}
					  if (isAttack) {
						  if (target instanceof L1NpcInstance) {
							  if (((L1NpcInstance) target).isDead()) {
//								  pc.sendPackets(new S_OwnCharPack(pc));
//								  pc.removeAllKnownObjects();
//								  pc.updateObject();
//								  pc.sendVisualEffectAtTeleport();
//								  pc.sendPackets(new S_CharVisualUpdate(pc));
								  return;
							  }
						  }
	                        // 目標對象是PC
	                        if (target instanceof L1PcInstance) {
	                            L1PcInstance tg = (L1PcInstance) target;
	                            pc.setNowTarget(tg);
	                            pc.setPetTarget(tg);
	                        }
	                        // 目標對象是寵物
	                        if (target instanceof L1PetInstance) {

	                        }
	                        // 目標對象是招喚獸
	                        if (target instanceof L1PetInstance) {

	                        }
	                    }
	                }
	            }

			// 絕對屏障解除
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
				pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
				pc.startHpRegeneration();
				pc.startMpRegeneration();
			}
			
			// 冥想術解除
			pc.killSkillEffectTimer(MEDITATION);

			try {
				// 呼喚盟友/援護盟友
				if ((skillId == 116) || (skillId == 118)) {
					if (charName.isEmpty()) {
						// 輸入名稱為空
						return;
					}

					final L1PcInstance target = World.get().getPlayer(charName);

					if (target == null) {
						// 73 \f1%0%d 不在線上。
						pc.sendPackets(new S_ServerMessage(73, charName));
						return;
					}

					// 無法攻擊/使用道具/技能/回城的狀態 XXX
					/*if (target.isParalyzedX()) {
						return;
					}*/

					if (pc.getClanid() != target.getClanid()) {
						// 您只能邀請您血盟中的成員。
						pc.sendPackets(new S_ServerMessage(414));
						return;
					}
					
					targetId = target.getId();
					if (skillId == 116) {// 呼喚盟友
						// 移動連續同員場合、向前回向
						final int callClanId = pc.getCallClanId();
						if ((callClanId == 0) || (callClanId != targetId)) {
							pc.setCallClanId(targetId);
							pc.setCallClanHeading(pc.getHeading());
						}
					}
				}

				if (skillId == L1SkillId.CURSE_POISON) {
					if (pc.isSafetyZone()) {
						return;
					}
				}
				final L1SkillUse skilluse = new L1SkillUse();
				skilluse.handleCommands(
						pc, 
						skillId, 
						targetId, 
						targetX, 
						targetY, 
						//message, 
						0, 
						L1SkillUse.TYPE_NORMAL
						);

			} catch (final Exception e) {
				/*OutErrorMsg.put(this.getClass().getSimpleName(), 
						"檢查 C_UseSkill 程式執行位置(核心管理者參考) SkillId: " + skillId, 
						e);*/
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	/**
	 * 該技能可否在隱身狀態使用
	 * @param useSkillid 技能編號
	 * @return true:可 false:不可
	 */
	private boolean isInvisUsableSkill(int useSkillid) {
		for (final int skillId : _cast_with_invis) {
			if (skillId == useSkillid) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
