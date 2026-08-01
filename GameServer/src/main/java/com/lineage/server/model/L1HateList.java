package com.lineage.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 攻擊目標清單
 * @author daien
 *
 */
public class L1HateList {
	
	private final Map<L1Character, Integer> _hateMap;

	/**
	 * 攻擊目標清單
	 * @param hateMap
	 */
	private L1HateList(final Map<L1Character, Integer> hateMap) {
		this._hateMap = hateMap;
	}

	/**
	 * 攻擊目標清單
	 */
	public L1HateList() {
		this._hateMap = new HashMap<L1Character, Integer>();
	}

	/**
	 * 將指定的值與此映射中的指定鍵關聯（可選操作）。<BR>
	 * 如果此映射以前包含一個該鍵的映射關係，<BR>
	 * 則用指定值替換舊值（當且僅當 m.containsKey(k) 返回 true 時，<BR>
	 * 才能說映射 m 包含鍵 k 的映射關係）<BR>
	 * @param cha 攻擊目標
	 * @param hate
	 */
	public synchronized void add(final L1Character cha, final int hate) {
		if (cha == null) {
			return;
		}
		Integer h = this._hateMap.get(cha);
		if (h != null) {
			this._hateMap.put(cha, h + hate);
			
		} else {
			this._hateMap.put(cha, hate);
		}
	}

	/**
	 * 攻擊目標清單中
	 * @param cha
	 * @return true:是 false:否
	 */
	public synchronized boolean isHate(final L1Character cha) {
		if (this._hateMap.get(cha) != null) {
			return true;
		}
		return false;
	}

	/**
	 * 返回指定鍵所映射的值；如果此映射不包含該鍵的映射關係，則返回 null。<BR>
	 * <BR>
	 * 更確切地講，如果此映射包含滿足 (key==null ? k==null : key.equals(k)) 的鍵 k 到值 v 的映射關係，<BR>
	 * 則此方法返回 v；否則返回 null。（最多只能有一個這樣的映射關係）。<BR>
	 * <BR>
	 * 如果此映射允許 null 值，則返回 null 值並不一定 表示該映射不包含該鍵的映射關係；<BR>
	 * 也可能該映射將該鍵顯示地映射到 null。使用 containsKey 操作可區分這兩種情況。<BR>
	 * @param cha
	 * @return
	 */
	public synchronized int get(final L1Character cha) {
		return this._hateMap.get(cha);
	}

	/**
	 * 如果此映射包含指定鍵的映射關係，則返回 true。<BR>
	 * 更確切地講，當且僅當此映射包含針對滿足 (key==null ? k==null : key.equals(k)) 的鍵 k 的映射關係時，<BR>
	 * 返回 true。（最多只能有一個這樣的映射關係）。<BR>
	 * @param cha
	 * @return
	 */
	public synchronized boolean containsKey(final L1Character cha) {
		return this._hateMap.containsKey(cha);
	}

	/**
	 * 如果存在一個鍵的映射關係，則將其從此映射中移除（可選操作）。<BR>
	 * 更確切地講，如果此映射包含從滿足 (key==null ? k==null :key.equals(k)) 的鍵 k 到值 v 的映射關係，<BR>
	 * 則移除該映射關係。（該映射最多只能包含一個這樣的映射關係。）<BR>
	 * <BR>
	 * 返回此映射中以前關聯該鍵的值，如果此映射不包含該鍵的映射關係，則返回 null。<BR>
	 * <BR>
	 * 如果此映射允許 null 值，則返回 null 值並不一定 表示該映射不包含該鍵的映射關係；也可能該映射將該鍵顯示地映射到 null。<BR>
	 * <BR>
	 * 調用返回後，此映射將不再包含指定鍵的映射關係。<BR>
	 * @param cha
	 */
	public synchronized void remove(final L1Character cha) {
		this._hateMap.remove(cha);
	}

	/**
	 * 從此映射中移除所有映射關係（可選操作）。此調用返回後，該映射將為空。
	 */
	public synchronized void clear() {
		this._hateMap.clear();
	}

	/**
	 * 如果此映射未包含鍵-值映射關係，則返回 true。
	 * @return
	 */
	public synchronized boolean isEmpty() {
		return this._hateMap.isEmpty();
	}

    /**
     * 傳回最大仇恨物件
     * @return
     */
	public synchronized L1Character getMaxHateCharacter() {
		L1Character cha = null;
		int hate = Integer.MIN_VALUE;

		for (final Map.Entry<L1Character, Integer> e : this._hateMap.entrySet()) {
			if (hate < e.getValue()) {
				cha = e.getKey();
				hate = e.getValue();
			}
		}
		return cha;
	}

	public synchronized void removeInvalidCharacter(final L1NpcInstance npc) {
		final ArrayList<L1Character> invalidChars = new ArrayList<L1Character>();
		for (final L1Character cha : this._hateMap.keySet()) {
			if ((cha == null) || cha.isDead() || !npc.knownsObject(cha)) {
				invalidChars.add(cha);
			}
		}

		for (final L1Character cha : invalidChars) {
			this._hateMap.remove(cha);
		}
	}

	public synchronized int getTotalHate() {
		int totalHate = 0;
		for (final int hate : this._hateMap.values()) {
			totalHate += hate;
		}
		return totalHate;
	}

	public synchronized int getTotalLawfulHate() {
		int totalHate = 0;
		for (final Map.Entry<L1Character, Integer> e : this._hateMap.entrySet()) {
			if (e.getKey() instanceof L1PcInstance) {
				totalHate += e.getValue();
			}
		}
		return totalHate;
	}

	public synchronized int getPartyHate(final L1Party party) {
		int partyHate = 0;

		for (final Map.Entry<L1Character, Integer> e : this._hateMap.entrySet()) {
			L1PcInstance pc = null;
			if (e.getKey() instanceof L1PcInstance) {
				pc = (L1PcInstance) e.getKey();
			}
			if (e.getKey() instanceof L1NpcInstance) {
				final L1Character cha = ((L1NpcInstance) e.getKey()).getMaster();
				if (cha instanceof L1PcInstance) {
					pc = (L1PcInstance) cha;
				}
			}

			if ((pc != null) && party.isMember(pc)) {
				partyHate += e.getValue();
			}
		}
		return partyHate;
	}

	public synchronized int getPartyLawfulHate(final L1Party party) {
		int partyHate = 0;

		for (final Map.Entry<L1Character, Integer> e : this._hateMap.entrySet()) {
			L1PcInstance pc = null;
			if (e.getKey() instanceof L1PcInstance) {
				pc = (L1PcInstance) e.getKey();
			}

			if ((pc != null) && party.isMember(pc)) {
				partyHate += e.getValue();
			}
		}
		return partyHate;
	}

	/**
	 * 構造一個映射關係與指定 Map 相同的新 HashMap。<BR>
	 * 所創建的 HashMap 具有默認加載因子 (0.75) 和足以容納指定 Map 中映射關係的初始容量。<BR>
	 * @return
	 */
	public synchronized L1HateList copy() {
		return new L1HateList(new HashMap<L1Character, Integer>(this._hateMap));
	}

	/**
	 * 返回此映射中包含的映射關係的 Set 視圖。<BR>
	 * 該 set 受映射支持，<BR>
	 * 所以對映射的更改可在此 set 中反映出來，反之亦然。<BR>
	 * 如果對該 set 進行迭代的同時修改了映射（通過迭代器自己的 remove 操作，<BR>
	 * 或者通過對迭代器返回的映射項執行 setValue 操作除外），<BR>
	 * 則迭代結果是不確定的。set 支持元素移除，<BR>
	 * 通過 Iterator.remove、Set.remove、removeAll、retainAll 和 clear 操作可從映射中移除相應的映射關係。<BR>
	 * 它不支持 add 或 addAll 操作。<BR>
	 * @return
	 */
	public synchronized Set<Entry<L1Character, Integer>> entrySet() {
		return this._hateMap.entrySet();
	}

	/**
	 * 返回此映射中包含的鍵的 Set 視圖。<BR>
	 * 該 set 受映射支持，<BR>
	 * 所以對映射的更改可在此 set 中反映出來，反之亦然。<BR>
	 * 如果對該 set 進行迭代的同時修改了映射（通過迭代器自己的 remove 操作除外），<BR>
	 * 則迭代結果是不確定的。set 支持元素移除，<BR>
	 * 通過 Iterator.remove、Set.remove、removeAll、retainAll 和 clear 操作可從映射中移除相應的映射關係。<BR>
	 * 它不支持 add 或 addAll 操作。<BR>
	 * @return
	 */
	public synchronized ArrayList<L1Character> toTargetArrayList() {
		return new ArrayList<L1Character>(this._hateMap.keySet());
	}

	/**
	 * 返回此映射中包含的值的 Collection 視圖。<BR>
	 * 該 collection 受映射支持，<BR>
	 * 所以對映射的更改可在此 collection 中反映出來，反之亦然。<BR>
	 * 如果對該 collection 進行迭代的同時修改了映射（通過迭代器自己的 remove 操作除外），<BR>
	 * 則迭代結果是不確定的。collection 支持元素移除，<BR>
	 * 通過 Iterator.remove、Collection.remove、removeAll、retainAll 和 clear 操作可從映射中移除相應的映射關係。<BR>
	 * 它不支持 add 或 addAll 操作。<BR>
	 * @return
	 */
	public synchronized ArrayList<Integer> toHateArrayList() {
		return new ArrayList<Integer>(this._hateMap.values());
	}
}
