package com.lineage.server.model.Instance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.lineage.data.npc.game.CustomDots;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.types.Point;

/**
 * 移動AI
 * 
 * @author dexc
 * 
 */
public class NpcMove extends NpcMoveExecutor {

    private static final Log _log = LogFactory.getLog(NpcMove.class);

    private Iterator<int[]> _list = null;

    private final L1NpcInstance _npc;

    private int noActionCount = 0;
    private int glanceError = 0;
    private long lastTargetId = 0;

    public void addGlaneError() {
        this.glanceError++;
    }

    public void clearGlaneError() {
        this.glanceError = 0;
    }

    public final int getGlanceError() {
        return this.glanceError = 0;
    }

    public NpcMove(final L1NpcInstance npc) {
        _npc = npc;
    }

    /**
     * 往指定面向移動1格
     * 
     * @param dir
     *            方向
     */
    @Override
    public void setDirectionMove(int dir) {
        if (!_npc.isAiRunning()) {
            _npc.startAI();
        }

        if (dir >= 0) {
            int locx = _npc.getX();
            int locy = _npc.getY();
            int check = 0;
            locx += HEADING_TABLE_X[dir];
            locy += HEADING_TABLE_Y[dir];


            _npc.setHeading(dir);

            if (!(_npc instanceof L1DollInstance)) {
                // 解除舊座標障礙宣告
                this._npc.getMap().setPassable(_npc.getLocation(), true);
            }
            _npc.broadcastPacketAll(new S_MoveCharPacket(_npc, locx, locy));
            _npc.setX(locx);
            _npc.setY(locy);

            if (!(_npc instanceof L1DollInstance)) {
                // 新增座標障礙宣告
                _npc.getMap().setPassable(_npc.getLocation(), false);
            }

            // movement_distancet 超過最大移動距離
            if (_npc.getMovementDistance() > 0) {
                if ((_npc instanceof L1GuardInstance) || (_npc instanceof L1MerchantInstance) || (_npc instanceof L1MonsterInstance)) {
                    if (_npc.getLocation().getLineDistance(new Point(_npc.getHomeX(), _npc.getHomeY())) > _npc.getMovementDistance()) {
                        _npc.teleport(_npc.getHomeX(), _npc.getHomeY(), _npc.getHeading());
                    }
                }
            }
            // 士兵的怨靈、怨靈、哈蒙將軍的怨靈
            if ((_npc.getNpcTemplate().get_npcId() >= 45912) && (_npc.getNpcTemplate().get_npcId() <= 45916)) {
                if (!((_npc.getX() >= 32591) && (_npc.getX() <= 32644) && (_npc.getY() >= 32643) && (_npc.getY() <= 32688) && (_npc.getMapId() == 4))) {
                    _npc.teleport(_npc.getHomeX(), _npc.getHomeY(), _npc.getHeading());
                }
            }
        }
    }

    @Override
    public void clear() {
        if (_list != null) {
            _list = null;
        }
        this.noActionCount = 0;
    }

    /**
     * 追蹤方向返回<BR>
     * 一般返回到目標為止最適合的移動方向
     * 
     * @param x
     *            目標點Ｘ
     * @param y
     *            目標點Ｙ
     * @param d
     *            距離
     * @return 移動方向
     */
    @Override
    public int moveDirection(int x, int y) {
        if (!_npc.isAiRunning()) {
            _npc.startAI();
        }

        int dir = 0;
        try {
            // 取回與目標點距離
            final double d = _npc.getLocation().getLineDistance(new Point(x, y));
            // 被施放黑闇之影 距離超過2 (追蹤停止)
            if ((_npc.hasSkillEffect(L1SkillId.DARKNESS) == true) && (d >= 2D)) {
                return -1;

            } else if (d > 30D) { // 距離超過30 (追蹤停止)
                return -1;

            } else if (d > L1NpcInstance.DISTANCE) { // 距離超過courceRange(重新取回移動方向)
                dir = targetDirection(x, y);
                dir = checkObject(dir);
                dir = openDoor(dir);

            } else { // 決定最短距離方向
                dir = _serchCource(x, y);
                if (dir == -1) { // 遇到障礙重新取回移動方向
                    this.noActionCount++;
                    if (this.noActionCount >= 10) {
                        _exsistCharacterBetweenTarget(0);
                        this.noActionCount = 0;
                    }
                    // 移動方向障礙點檢查(對新的面相取回移動方向)
                    dir = targetDirection(x, y);
                    dir = checkObject(dir);
                    dir = openDoor(dir);
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return dir;
    }

    /**
     * 前進方向障礙者攻擊判斷
     *
     * @param dir
     * @return
     */
    public boolean _exsistCharacterBetweenTarget(final int dir) {
        try {
            // 執行者非MOB
            if (!(_npc instanceof L1MonsterInstance)) {
                return false;
            }
            // 無首要目標
            if (_npc.is_now_target() == null) {
                return false;
            }

            final Point pt = _npc.getLocation();

            final ArrayList<L1Object> objects = World.get().getVisibleObjects(_npc, 1);
            for (final Iterator<L1Object> iter = objects.iterator(); iter.hasNext();) {
                final L1Object object = iter.next();
                if (object instanceof L1MonsterInstance) {
                    continue;
                }
                boolean isCheck = false;
                int r = pt.getTileLineDistance(object.getLocation());
                if (r <= 3) {
                    isCheck = true;
                }
                if (isCheck) {
                    boolean isHate = false;
                    // 判斷障礙
                    if (object instanceof L1PcInstance) {// 障礙者是玩家
                        final L1PcInstance pc = (L1PcInstance) object;
                        if (!pc.isGhost() && !pc.isGmInvis()) { // 鬼魂模式及GM隱身排除
                            isHate = true;
                        }
                    } else if (object instanceof L1PetInstance) {// 障礙者是寵物
                        isHate = true;
                    } else if (object instanceof L1SummonInstance) {// 障礙者是 召換獸
                        isHate = true;
                    }
                    if (isHate) {
                        // 重新設置障礙者為攻擊目標
                        final L1Character cha = (L1Character) object;
                        _npc._hateList.add(cha, 0);
                        _npc._target = cha;
                        return true;
                    }
                }
            }
            objects.clear();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 傳回目標的反方向
     * 
     * @param tx
     *            目標點Ｘ
     * @param ty
     *            目標點Ｙ
     */
    @Override
    public int targetReverseDirection(final int tx, final int ty) {
        if (!_npc.isAiRunning()) {
            _npc.startAI();
        }
        int dir = targetDirection(tx, ty);
        return HEADING_RD[dir];
    }

    /**
     * 目標點方向計算
     * 
     * @param tx
     *            目標X
     * @param ty
     *            目標Y
     * @return
     */
    private int targetDirection(final int tx, final int ty) {
        if (!_npc.isAiRunning()) {
            _npc.startAI();
        }
        final float dis_x = Math.abs(_npc.getX() - tx); // X點方向距離
        final float dis_y = Math.abs(_npc.getY() - ty); // Y點方向距離
        final float dis = Math.max(dis_x, dis_y); // 取回2者最大質
        if (dis == 0) {
            return _npc.getHeading(); // 距離為0表示不須改變面向
        }
        final int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 上下左右優先丸
        final int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 上下左右優先丸

        int dir_x = 0;
        int dir_y = 0;
        if (_npc.getX() < tx) {
            dir_x = 1;
        }
        if (_npc.getX() > tx) {
            dir_x = -1;
        }
        if (_npc.getY() < ty) {
            dir_y = 1;
        }
        if (_npc.getY() > ty) {
            dir_y = -1;
        }

        if (avg_x == 0) {
            dir_x = 0;
        }
        if (avg_y == 0) {
            dir_y = 0;
        }

        if (dir_x == 1 && dir_y == -1) {
            return 1; // 上
        }
        if (dir_x == 1 && dir_y == 0) {
            return 2; // 右上
        }
        if (dir_x == 1 && dir_y == 1) {
            return 3; // 右
        }
        if (dir_x == 0 && dir_y == 1) {
            return 4; // 右下
        }
        if (dir_x == -1 && dir_y == 1) {
            return 5; // 下
        }
        if (dir_x == -1 && dir_y == 0) {
            return 6; // 左下
        }
        if (dir_x == -1 && dir_y == -1) {
            return 7; // 左
        }
        if (dir_x == 0 && dir_y == -1) {
            return 0; // 左上
        }
        return _npc.getHeading();
    }

    /**
     * 指定座標直線上無障礙物可通行
     * 
     * @param tx
     *            座標X值
     * @param ty
     *            座標Y值
     * @param th
     *            方向
     * @return true:可以通過 false:不能通過
     */
    @SuppressWarnings("unused")
    private boolean glanceCheck(final int tx, final int ty, final int th) {
        final L1Map map = _npc.getMap();
        int chx = _npc.getX();
        int chy = _npc.getY();

        for (int i = 0; i < 15; i++) {
            if (((chx == tx) && (chy == ty)) || ((chx + 1 == tx) && (chy - 1 == ty)) || ((chx + 1 == tx) && (chy == ty)) || ((chx + 1 == tx) && (chy + 1 == ty))
                    || ((chx == tx) && (chy + 1 == ty)) || ((chx - 1 == tx) && (chy + 1 == ty)) || ((chx - 1 == tx) && (chy == ty)) || ((chx - 1 == tx) && (chy - 1 == ty))
                    || ((chx == tx) && (chy - 1 == ty))) {
                break;
            } else {
                if (!map.isPassable(chx, chy, th, _npc)) {
                    return false;

                } else if (map.isExistDoor(chx, chy) == 0x03) {
                    return false;
                }
                if (chx < tx) {
                    if (chy == ty) {
                        chx++;
                    } else if (chy > ty) {
                        chx++;
                        chy--;

                    } else if (chy < ty) {
                        chx++;
                        chy++;
                    }

                } else if (chx == tx) {
                    if (chy < ty) {
                        chy++;

                    } else if (chy > ty) {
                        chy--;
                    }

                } else if (chx > tx) {
                    if (chy == ty) {
                        chx--;

                    } else if (chy < ty) {
                        chx--;
                        chy++;

                    } else if (chy > ty) {
                        chx--;
                        chy--;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 對於前進方向是否有障礙的確認 第一點移動產生障礙 轉向2,3點判斷
     * 
     * @param d
     *            方向
     * @return
     */
    @Override
    public int checkObject(final int h) {
        if (!_npc.isAiRunning()) {
            _npc.startAI();
        }
        if ((h >= 0) && (h <= 7)) {
            final int x = _npc.getX();
            final int y = _npc.getY();

            final int h2 = _heading2[h];
            final int h3 = _heading3[h];

            if (_npc.getMap().isPassable(x, y, h, _npc)) {
                return h;

            } else if (_npc.getMap().isPassable(x, y, h2, _npc)) {
                return h2;

            } else if (_npc.getMap().isPassable(x, y, h3, _npc)) {
                return h3;
            }
        }
        return -1;
//        // 進行方向
//        L1Map map = L1WorldMap.get().getMap(_npc.getMapId());
//        int x = _npc.getX();
//        int y = _npc.getY();
//        if (h == 1) {
//            if (map.isPassable(x, y, 1, null)) {
//                return 1;
//            } else if (map.isPassable(x, y, 0, null)) {
//                return 0;
//            } else if (map.isPassable(x, y, 2, null)) {
//                return 2;
//            }
//        } else if (h == 2) {
//            if (map.isPassable(x, y, 2, null)) {
//                return 2;
//            } else if (map.isPassable(x, y, 1, null)) {
//                return 1;
//            } else if (map.isPassable(x, y, 3, null)) {
//                return 3;
//            }
//        } else if (h == 3) {
//            if (map.isPassable(x, y, 3, null)) {
//                return 3;
//            } else if (map.isPassable(x, y, 2, null)) {
//                return 2;
//            } else if (map.isPassable(x, y, 4, null)) {
//                return 4;
//            }
//        } else if (h == 4) {
//            if (map.isPassable(x, y, 4, null)) {
//                return 4;
//            } else if (map.isPassable(x, y, 3, null)) {
//                return 3;
//            } else if (map.isPassable(x, y, 5, null)) {
//                return 5;
//            }
//        } else if (h == 5) {
//            if (map.isPassable(x, y, 5, null)) {
//                return 5;
//            } else if (map.isPassable(x, y, 4, null)) {
//                return 4;
//            } else if (map.isPassable(x, y, 6, null)) {
//                return 6;
//            }
//        } else if (h == 6) {
//            if (map.isPassable(x, y, 6, null)) {
//                return 6;
//            } else if (map.isPassable(x, y, 5, null)) {
//                return 5;
//            } else if (map.isPassable(x, y, 7, null)) {
//                return 7;
//            }
//        } else if (h == 7) {
//            if (map.isPassable(x, y, 7, null)) {
//                return 7;
//            } else if (map.isPassable(x, y, 6, null)) {
//                return 6;
//            } else if (map.isPassable(x, y, 0, null)) {
//                return 0;
//            }
//        } else if (h == 0) {
//            if (map.isPassable(x, y, 0, null)) {
//                return 0;
//            } else if (map.isPassable(x, y, 7, null)) {
//                return 7;
//            } else if (map.isPassable(x, y, 1, null)) {
//                return 1;
//            }
//        }
//        return -1;
    }

    @Override
    public int openDoor(final int h) {
        if (!_npc.isAiRunning()) {
            _npc.startAI();
        }
        if (h != -1) {
            if (!_npc.getMap().isDoorPassable(_npc.getX(), _npc.getY(), h, _npc)) {
                // _aie = _hce + 1;
                return -1;
            }
        }
        return h;
    }

    /**
     * 移動位置
     * 
     * @param ary
     * @param d
     */
    private void _moveLocation(final int[] ary, final int d) {
        switch (d) {
        case 1:
            ary[0] = ary[0] + 1;
            ary[1] = ary[1] - 1;
            break;

        case 2:
            ary[0] = ary[0] + 1;
            break;

        case 3:
            ary[0] = ary[0] + 1;
            ary[1] = ary[1] + 1;
            break;

        case 4:
            ary[1] = ary[1] + 1;
            break;

        case 5:
            ary[0] = ary[0] - 1;
            ary[1] = ary[1] + 1;
            break;

        case 6:
            ary[0] = ary[0] - 1;
            break;

        case 7:
            ary[0] = ary[0] - 1;
            ary[1] = ary[1] - 1;
            break;

        case 0:
            ary[1] = ary[1] - 1;
            break;
        }
        ary[2] = d;
    }

    /**
     * 取得正面
     * 
     * @param ary
     * @param d
     */
    private void _getFront(final int[] ary, final int d) {
        switch (d) {
        case 1:
            ary[4] = 2;
            ary[3] = 0;
            ary[2] = 1;
            ary[1] = 3;
            ary[0] = 7;
            break;

        case 2:
            ary[4] = 2;
            ary[3] = 4;
            ary[2] = 0;
            ary[1] = 1;
            ary[0] = 3;
            break;

        case 3:
            ary[4] = 2;
            ary[3] = 4;
            ary[2] = 1;
            ary[1] = 3;
            ary[0] = 5;
            break;

        case 4:
            ary[4] = 2;
            ary[3] = 4;
            ary[2] = 6;
            ary[1] = 3;
            ary[0] = 5;
            break;

        case 5:
            ary[4] = 4;
            ary[3] = 6;
            ary[2] = 3;
            ary[1] = 5;
            ary[0] = 7;
            break;

        case 6:
            ary[4] = 4;
            ary[3] = 6;
            ary[2] = 0;
            ary[1] = 5;
            ary[0] = 7;
            break;

        case 7:
            ary[4] = 6;
            ary[3] = 0;
            ary[2] = 1;
            ary[1] = 5;
            ary[0] = 7;
            break;

        case 0:
            ary[4] = 2;
            ary[3] = 6;
            ary[2] = 0;
            ary[1] = 1;
            ary[0] = 7;
            break;
        }
    }

    /**
     * 返回目標最短路徑的方向
     * 
     * @param x
     *            目標點Ｘ
     * @param y
     *            目標點Ｙ
     * @return 最新面向
     */
    private int _serchCource(final int x, final int y) {
        int i;
        final int locCenter = L1NpcInstance.DISTANCE + 1;
        final int diff_x = x - locCenter;
        final int diff_y = y - locCenter;
        int[] locBace = { _npc.getX() - diff_x, _npc.getY() - diff_y, 0, 0 };
        final int[] locNext = new int[4];
        int[] locCopy;
        final int[] dirFront = new int[5];
        final boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
        final LinkedList<int[]> queueSerch = new LinkedList<int[]>();

        // 設置探索地圖
        for (int j = L1NpcInstance.DISTANCE * 2 + 1; j > 0; j--) {
            for (i = L1NpcInstance.DISTANCE - Math.abs(locCenter - j); i >= 0; i--) {
                serchMap[j][locCenter + i] = true;
                serchMap[j][locCenter - i] = true;
            }
        }

        final int[] firstCource = { 2, 4, 6, 0, 1, 3, 5, 7 };
        for (i = 0; i < 8; i++) {
            System.arraycopy(locBace, 0, locNext, 0, 4);
            this._moveLocation(locNext, firstCource[i]);
            if ((locNext[0] - locCenter == 0) && (locNext[1] - locCenter == 0)) {
                return firstCource[i];
            }
            if (serchMap[locNext[0]][locNext[1]]) {
                final int tmpX = locNext[0] + diff_x;
                final int tmpY = locNext[1] + diff_y;
                boolean found = false;
                switch (i) {
                case 0:
                    found = _npc.getMap().isPassable(tmpX, tmpY + 1, i, _npc);
                    break;

                case 1:
                    found = _npc.getMap().isPassable(tmpX - 1, tmpY + 1, i, _npc);
                    break;

                case 2:
                    found = _npc.getMap().isPassable(tmpX - 1, tmpY, i, _npc);
                    break;

                case 3:
                    found = _npc.getMap().isPassable(tmpX - 1, tmpY - 1, i, _npc);
                    break;

                case 4:
                    found = _npc.getMap().isPassable(tmpX, tmpY - 1, i, _npc);
                    break;

                case 5:
                    found = _npc.getMap().isPassable(tmpX + 1, tmpY - 1, i, _npc);
                    break;

                case 6:
                    found = _npc.getMap().isPassable(tmpX + 1, tmpY, i, _npc);
                    break;

                case 7:
                    found = _npc.getMap().isPassable(tmpX + 1, tmpY + 1, i, _npc);
                    break;
                }
                if (found) {
                    locCopy = new int[4];
                    System.arraycopy(locNext, 0, locCopy, 0, 4);
                    locCopy[2] = firstCource[i];
                    locCopy[3] = firstCource[i];
                    queueSerch.add(locCopy);
                }
                serchMap[locNext[0]][locNext[1]] = false;
            }
        }
        locBace = null;

        // 最短路徑
        while (queueSerch.size() > 0) {
            locBace = queueSerch.removeFirst();
            this._getFront(dirFront, locBace[2]);
            for (i = 4; i >= 0; i--) {
                System.arraycopy(locBace, 0, locNext, 0, 4);
                this._moveLocation(locNext, dirFront[i]);
                if ((locNext[0] - locCenter == 0) && (locNext[1] - locCenter == 0)) {
                    return locNext[3];
                }
                if (serchMap[locNext[0]][locNext[1]]) {
                    final int tmpX = locNext[0] + diff_x;
                    final int tmpY = locNext[1] + diff_y;
                    boolean found = false;
                    switch (i) {
                    case 0:
                        found = _npc.getMap().isPassable(tmpX, tmpY + 1, i, _npc);
                        break;

                    case 1:
                        found = _npc.getMap().isPassable(tmpX - 1, tmpY + 1, i, _npc);
                        break;

                    case 2:
                        found = _npc.getMap().isPassable(tmpX - 1, tmpY, i, _npc);
                        break;

                    case 3:
                        found = _npc.getMap().isPassable(tmpX - 1, tmpY - 1, i, _npc);
                        break;

                    case 4:
                        found = _npc.getMap().isPassable(tmpX, tmpY - 1, i, _npc);
                        break;
                    }
                    if (found) {
                        locCopy = new int[4];
                        System.arraycopy(locNext, 0, locCopy, 0, 4);
                        locCopy[2] = dirFront[i];
                        queueSerch.add(locCopy);
                    }
                    serchMap[locNext[0]][locNext[1]] = false;
                }
            }
            locBace = null;
        }
        return -1;
    }
}
