package com.lineage.server.model.Instance;


import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigGuaji;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.NpcAiThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;

public class PcAI implements Runnable {
	private static final Log _log = LogFactory.getLog(PcAI.class);
	private static Random _random = new Random();
	private final L1PcInstance _pc;
	
    public PcAI(final L1PcInstance pc) {
        _pc = pc;
    }
    
    public void startAI() {
        NpcAiThreadPool.get().execute(this);
    }
    
    @Override
    public void run() {
        try {
        	//System.out.println("===AI執行===");
        	//_npc.setAiRunning(true);           
            while (_pc.getMaxHp() > 0) {
            /*    while (_pc.isSleeped() || _pc.isParalyzedX() || _pc.isParalyzed()) {
                    Thread.sleep(200);
                }*/
                //System.out.println("AI啟動2222");
                // AI的處理
                if (AIProcess()) {
                    break;
                }// */

                try {
                    // 移動速度延遲
                    Thread.sleep(getRightInterval(2));

                } catch (final Exception e) {
                    break;
                }
            }

            do {
                try {
                    Thread.sleep(getRightInterval(1));

                } catch (final Exception e) {
                    break;
                }
            } while (_pc.isDead());

            _pc.allTargetClear();
            _pc.setAiRunning(false);
            _pc.setActived(false);
            Thread.sleep(10);

        } catch (final Exception e) {
            _log.error("pcAI發生例外狀況: " + this._pc.getName(), e);
        }
    }
    
    /**
     * AI的處理
     * 
     * @return true:AI終了 false:AI續行
     */
    private boolean AIProcess() {
        try {
            if (_pc.isDead()) {
                return true;
            }

            if (_pc.getOnlineStatus() == 0) {
                return true;
            }

            if (_pc.getCurrentHp() <= 0) {
                return true;
            }
            if (!_pc.isActived()) {
            	return true;
            }
            
        	if (_pc.hasSkillEffect(51234)) {
				 final Collection<L1Object> allObj3 = World.get() .getVisibleObjects(_pc, 12);
				   for (final Iterator<L1Object> iter1 = allObj3.iterator(); iter1.hasNext();) {
			            final L1Object obj = iter1.next();
			            if ((obj instanceof L1PcInstance)) {
			            	L1Teleport.randomTeleport(_pc, true);
			            	_pc.sendPackets(new S_ServerMessage("偵測到周唯有玩家自動瞬移。"));
			            }
				   }
			}
            if (this._pc.getWeapon() == null) {
                _pc.sendPackets(new S_ServerMessage("\\fU沒拿武器，自動關閉掛機！"));
                _pc.setActived(false);
		    		_pc.sendPackets(new S_ServerMessage("自動狩獵已停止。"));
		    		_pc.set_fwgj(0);
	                _pc.setlslocx(0);
	                _pc.setlslocy(0);
					_pc.killSkillEffectTimer(9997);
					_pc.killSkillEffectTimer(9996);	 
	              /*  if (_pc.getQuest().get_step(8780) == 1) {
	       			 _pc.killSkillEffectTimer(8132);
	       			_pc.addWeightReduction(-ConfigGuaji.guajiWeight);
	       			_pc.sendPackets(new S_OwnCharStatus(_pc));
	       			}*/
		    		 L1PcUnlock.Pc_Unlock(_pc);//更新畫面
		    		
                return true;
              }
            if (_pc.getInventory().getWeight182() >= 197) { // 重量過重
    			// 110 \f1當負重過重的時候，無法戰鬥。
            	 _pc.sendPackets(new S_ServerMessage(110));
                 _pc.setActived(false);
 		    		_pc.sendPackets(new S_ServerMessage("自動狩獵已停止。"));
 		    		_pc.set_fwgj(0);
 	                _pc.setlslocx(0);
 	                _pc.setlslocy(0);
 					_pc.killSkillEffectTimer(9997);
 					_pc.killSkillEffectTimer(9996);
 	               L1Teleport.teleport(_pc, 33440, 32802, (short)4, 0, true);
 	              /* if (_pc.getQuest().get_step(8780) == 1) {
 		       			 _pc.killSkillEffectTimer(8132);
 		       			_pc.addWeightReduction(-ConfigGuaji.guajiWeight);
 		       			_pc.sendPackets(new S_OwnCharStatus(_pc));
 		       			}*/
 		    		 L1PcUnlock.Pc_Unlock(_pc);//更新畫面
    			return true;
    		}
            //時間超過停止掛機
        	Calendar date = Calendar.getInstance();
	 		int nowHour = date.get(Calendar.HOUR_OF_DAY);
	 		if(ConfigGuaji.checktimeguaji){
            if (nowHour < ConfigGuaji.timestart || nowHour >= ConfigGuaji.timeend) {
            	 _pc.setActived(false);
		    		_pc.sendPackets(new S_ServerMessage("自動狩獵已停止。"));
		    		_pc.set_fwgj(0);
	                _pc.setlslocx(0);
	                _pc.setlslocy(0);
					_pc.killSkillEffectTimer(9997);
					_pc.killSkillEffectTimer(9996);
	              /*  if (_pc.getQuest().get_step(8780) == 1) {
		       			 _pc.killSkillEffectTimer(8132);
			       			_pc.addWeightReduction(-ConfigGuaji.guajiWeight);
			       			_pc.sendPackets(new S_OwnCharStatus(_pc));
		       			}*/
		    		 L1PcUnlock.Pc_Unlock(_pc);//更新畫面
		    		 return true;
            }
	 		}
        
        	

             if (_pc.getlslocx() > 0 &&_pc.getlslocy()> 0 && _pc.getLocation().getTileLineDistance(new Point(_pc.getlslocx(), _pc.getlslocy())) > _pc.get_fwgj()) {
        		    L1Teleport.teleport(_pc, _pc.getlslocx(), _pc.getlslocy(), (short)_pc.getMapId(), 0, true);
        		    _pc.targetClear();
        		    }
          	
  		
            //_pc.setSleepTime(300);
            // 現有目標有效性檢查
            _pc.checkTarget();

            boolean searchTarget = true;
            if (_pc.is_now_target() != null) {
                searchTarget = false;
            }
            
            if (searchTarget) {
                //進行目標搜索
//            	System.out.println("AI啟動3333");
                _pc.searchTarget();
            }	
            
            if (_pc.is_now_target() == null) {
            	if (!_pc.isPathfinding()) {
            		_pc.setrandomMoveDirection(_random.nextInt(8));
            	}
                _pc.noTarget();
            	Thread.sleep(50);
                return false;
            } else {
            	_pc.onTarget();
            	if (_pc.isPathfinding()) {
            		_pc.setPathfinding(false);
            	}
            }

            Thread.sleep(50);

        } catch (final Exception e) {
            _log.error("pcAI發生例外狀況: " + this._pc.getName(), e);
        }
        return false; // NPC AI 繼續執行
    }
    
    /**
     * 正常的速度
     * 
     * @param type
     *            檢測類型
     * @return 正常應該接收的速度(MS)
     */
    private int getRightInterval(final int type) {
        int interval = 0;

        switch (type) {
            case 1:
                interval = SprTable.get().getAttackSpeed(
                        this._pc.getTempCharGfx(),
                        this._pc.getCurrentWeapon() + 1);
              //  interval *= 1.05;
                break;

            case 2:
                interval = SprTable.get().getMoveSpeed(
                        this._pc.getTempCharGfx(), this._pc.getCurrentWeapon());
                break;

            default:
                return 0;
        }
        return intervalR(type, interval);
    }

    private int intervalR(final int type, int interval) {
        try {

            if (this._pc.isHaste()) {
                interval *= 0.755;// 0.755
            }

            if (type== 2 && this._pc.isFastMovable()) {
                interval *= 0.755;// 0.665
            }

           /* if (type == 2 && this._pc.isFastAttackable()) {
                interval *= 0.665;// 0.775
            }*/

            if (this._pc.isBrave()) {
                interval *= 0.755;// 0.755
            }

          /*  if (this._pc.isBraveX()) {
                interval *= 0.755;// 0.755
            }*/

            if (this._pc.isElfBrave()) {
                interval *= 0.855;// 0.855
            }

            if (type == 1 && this._pc.isElfBrave()) {
                interval *= 0.9;// 0.9
            }

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return interval;
    }

}
