package com.custom.clan;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ClanStatData {
    private final int level;
    private final int next_level;
    private final int need_count;
    private final int dmgup, bow_dmgup, hit, bow_hit, ac, mr, hp, mp, sp, str, dex, con, wis, _int, cha, add_exp;
    private final List<Pair<Integer, Integer>> needItems = new ArrayList<>();
    public ClanStatData(int level, int need_count, int dmgup, int bow_dmgup, int hit, int bow_hit, int ac, int mr, int hp, int mp, int sp, int str, int dex, int con, int wis, int _int, int cha, String[] need_item_id, String[] need_item_count, int add_exp) {
        this.level = level;
        this.next_level = level + 1;
        this.need_count = need_count;
        this.dmgup = dmgup;
        this.bow_dmgup = bow_dmgup;
        this.hit = hit;
        this.bow_hit = bow_hit;
        this.ac = ac;
        this.mr = mr;
        this.hp = hp;
        this.mp = mp;
        this.sp = sp;
        this.str = str;
        this.dex = dex;
        this.con = con;
        this.wis = wis;
        this._int = _int;
        this.cha = cha;
        this.add_exp = add_exp;
        for (int i = 0; i < need_item_id.length; i++) {
            this.needItems.add(new Pair<>(Integer.parseInt(need_item_id[i].replaceAll(" ", "").replaceAll("　", "")),
                    Integer.parseInt(need_item_count[i].replaceAll(" ", "").replaceAll("　", ""))));
        }
    }

    public int getLevel() {
        return level;
    }
    public int getNeedCount() { return need_count; }

    public int getNextLevel() {
        return next_level;
    }

    public int getDmgup() {
        return dmgup;
    }

    public int getBowDmgup() {
        return bow_dmgup;
    }

    public int getHit() {
        return hit;
    }

    public int getBowHit() {
        return bow_hit;
    }

    public int getAc() {
        return ac;
    }

    public int getMr() {
        return mr;
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }

    public int getSp() {
        return sp;
    }

    public int getStr() {
        return str;
    }

    public int getDex() {
        return dex;
    }

    public int getCon() {
        return con;
    }

    public int getWis() {
        return wis;
    }

    public int getInt() {
        return _int;
    }

    public int getCha() {
        return cha;
    }
    public int getAddExp() {
        return this.add_exp;
    }

    public List<Pair<Integer, Integer>> getNeedItems() {
        return this.needItems;
    }
}
