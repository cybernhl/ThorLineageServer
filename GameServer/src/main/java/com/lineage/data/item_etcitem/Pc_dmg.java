package com.lineage.data.item_etcitem;



import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.CalcStat;
import com.william.DexDmg;
import com.william.StrDmg;

/**
 * terry770106 2019/03/10
 */
public class Pc_dmg extends ItemExecutor {

	/**
	 *
	 */
	private Pc_dmg() {
	}

	public static ItemExecutor get() {
		return new Pc_dmg();
	}

	

	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item){
		 L1ItemInstance weapon = pc.getWeapon();
		 
			if (weapon == null) {
				pc.sendPackets(new S_SystemMessage("沒有裝備武器，所以準確查詢資料。"));
				return;
			}
			pc.resetBaseMr();
		  String[] info = new String[22];
		
			 if(pc.getName() != null){
				  info[1]= String.valueOf(pc.getName());
			
				}
			 if(pc.getLevel() > 0){
				  info[2]= String.valueOf(pc.getLevel());
				}
			 if(pc.getMr()/* + CalcStat.calcStatMr(pc.getWis())*/ > 0){
				  info[3]= String.valueOf(pc.getMr()/* + pc.get_other().get_Mr()*/);
				}
			 if(pc.getStr() >= 0){
				  info[4]= String.valueOf(pc.getStr()/* + pc.get_other().get_Str()*/);
				}
			 if(pc.getDex() >= 0){
				  info[5]= String.valueOf(pc.getDex() /*+ pc.get_other().get_Dex()*/);
				}
			 if(pc.getInt() >= 0){
				  info[6]= String.valueOf(pc.getInt()/* + pc.get_other().get_Int()*/);
				}
			 if(pc.getCon() >= 0){
				  info[7]= String.valueOf(pc.getCon() /*+ pc.get_other().get_Con()*/);
				}
			 if(pc.getWis() >= 0){
				  info[8]= String.valueOf(pc.getWis() /*+ pc.get_other().get_Wis()*/);
				}
			 if(pc.getCha() >= 0){
				  info[9]= String.valueOf(pc.getCha()/* + pc.get_other().get_Cha()*/);
				}
			
		
			
			   //近戰最低傷害
			   int dmg = StrDmg.getStrDmgSkill(pc, pc.getStr());
			   if(weapon.getItem().getType1() != 20){
	         int _weaponAddDmgmin = weapon.getItem().getDmgModifier() + pc.getDmgup() + dmg + weapon.getEnchantLevel() + weapon.getAttrEnchantLevel()
	        		 +weapon.getDmgByMagic() + weapon.getHolyDmgByMagic()/* + pc.get_other().get_Attack()*/;
	         if(_weaponAddDmgmin >= 0){
				  info[10]= String.valueOf(_weaponAddDmgmin);
	         }
	         }else{
	        	 info[10]= String.valueOf(0);
	         }
	       
	       
			 //近戰最高傷害(物理)
			 int _weaponAddDmgmax = weapon.getItem().getDmgModifier()  + pc.getDmgup() + weapon.getItem().getDmgSmall() + dmg + weapon.getEnchantLevel() +weapon.getAttrEnchantLevel()
					 +weapon.getDmgByMagic() + weapon.getHolyDmgByMagic()/* + pc.get_other().get_Attack()*/;
			  if(weapon.getItem().getType1() != 20){
			   if(_weaponAddDmgmax >= 0){
				  info[11]= String.valueOf(_weaponAddDmgmax);
			   }
			  }else{
				  info[11]= String.valueOf(0);
			  }
			  
			  if(weapon.getItem().getType1() != 20){
			   int _weaponAddDmgmax1 = weapon.getItem().getDmgModifier() + pc.getDmgup() + weapon.getItem().getDmgLarge() + dmg + weapon.getEnchantLevel()+ weapon.getAttrEnchantLevel()
					   +weapon.getDmgByMagic() + weapon.getHolyDmgByMagic()/* + pc.get_other().get_Attack()*/;
			   if(_weaponAddDmgmax1 >= 0){
				  info[20]= String.valueOf(_weaponAddDmgmax1);
			   }
			  }else{
				  info[20]= String.valueOf(0);
			  }
			   //---------------------------
			 //遠距離最低傷害 (物理)
			   if(weapon.getItem().getType1() == 20){
				   int dmg1 = DexDmg.getDexDmgSkill(pc, pc.getDex());
			 int _weaponAddBowDmgmin = weapon.getItem().getDmgModifier()  + pc.getBowDmgup() + dmg1 + weapon.getEnchantLevel()+weapon.getAttrEnchantLevel()
					 +weapon.getDmgByMagic() + weapon.getHolyDmgByMagic()/* + pc.get_other().get_BowAttack()*/;
			  if(_weaponAddBowDmgmin >= 0){  
				 info[12]= String.valueOf(_weaponAddBowDmgmin);
			  }
			   }else{
					 info[12]= String.valueOf(0);
			   }
			 //遠距離最高傷害(物理)(小)
			  L1ItemInstance _arrow = pc.getInventory().getArrow();
			  if(weapon.getItem().getType1() == 20 && _arrow != null){
				  int dmg1 = DexDmg.getDexDmgSkill(pc, pc.getDex());
				  L1ItemInstance _arrow1 = pc.getInventory().getArrow();  
			 int _weaponAddBowDmgmax = weapon.getItem().getDmgModifier()  + pc.getBowDmgup() + _arrow1.getItem().getDmgSmall() + dmg1 + weapon.getEnchantLevel() + weapon.getAttrEnchantLevel()+1
					 +weapon.getDmgByMagic() + weapon.getHolyDmgByMagic()/* +  + pc.get_other().get_BowAttack()*/;//+1 是 *1.3的補足
			// }else{
			//  _weaponAddBowDmgmax = weapon.getItem().getDmgModifier() + weapon.getItemAttack() + pc.getBowDmgup();
			
			  if(_weaponAddBowDmgmax >= 0){  
				info[13]= String.valueOf(_weaponAddBowDmgmax);
			  }
			  }else{
					info[13]= String.valueOf(0);
			  }
			  //遠距離最高傷害(物理)(大)
			  if(weapon.getItem().getType1() == 20 && _arrow != null){
				  int dmg1 = DexDmg.getDexDmgSkill(pc, pc.getDex());
				  L1ItemInstance _arrow1 = pc.getInventory().getArrow();  
			 int _weaponAddBowDmgmax = weapon.getItem().getDmgModifier()  + pc.getBowDmgup() + _arrow1.getItem().getDmgLarge() + dmg1 + weapon.getEnchantLevel() + weapon.getAttrEnchantLevel() +1
					 +weapon.getDmgByMagic() + weapon.getHolyDmgByMagic() /*+ pc.get_other().get_BowAttack()*/;//+1 是 *1.3的補足
			// }else{
			//  _weaponAddBowDmgmax = weapon.getItem().getDmgModifier() + weapon.getItemAttack() + pc.getBowDmgup();
			
			  if(_weaponAddBowDmgmax >= 0){  
				info[21]= String.valueOf(_weaponAddBowDmgmax);
			  }
			  }else{
					info[21]= String.valueOf(0);
			  }
			
			  
			  
			  if(pc.isElf() && weapon.getItem().getType1() == 20){//妖精 拿弓
					 if(pc.getClassFeature().getAttackLevel(pc.getLevel()) >= 0){
						  info[14]= String.valueOf(pc.getClassFeature().getAttackLevel(pc.getLevel()));
						}else{
						   info[14]= String.valueOf(0);
						}
					 
					  } else if(!pc.isElf() && weapon.getItem().getType1() != 20){//其他職業
						 if(pc.getClassFeature().getAttackLevel(pc.getLevel()) >= 0){
							  info[14]= String.valueOf(pc.getClassFeature().getAttackLevel(pc.getLevel()));
							}else{
							   info[14]= String.valueOf(0);
							}
					  }else{
						   info[14]= String.valueOf(0);
					  }
			  
			 int adddmg = StrDmg.getStrDmgSkill(pc, pc.getStr());
			  if(weapon.getItem().getType1() != 20){
				if (adddmg >= 0) {
				  info[15]= String.valueOf(adddmg);
				}
			  }else{
				  info[15]= String.valueOf(0);
			  }
				
			int Bowdmg = DexDmg.getDexDmgSkill(pc, pc.getDex());
			  if(weapon.getItem().getType1() == 20){
				if (Bowdmg >= 0) {
				  info[16]= String.valueOf(Bowdmg /*+/"* 1.3"*/);
				}
			  }else{
				  info[16]= String.valueOf(0);
			  }
			  //武器加成 
				 if(weapon.getEnchantLevel() >= 0){
					  info[17]= String.valueOf(weapon.getEnchantLevel()+"+"+weapon.getItem().getDmgModifier());
					}
				//防具減傷
			 int Red= weapon.getItem().getDamageReduction();
				  info[18]= String.valueOf(Red);
			
			 if(weapon.getAttrEnchantLevel() >= 0){
				  info[19]= String.valueOf(weapon.getAttrEnchantLevel());
				}
			  pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "pcmena", info));		
	}
}
