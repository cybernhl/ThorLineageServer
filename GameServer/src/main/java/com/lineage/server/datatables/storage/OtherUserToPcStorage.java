package com.lineage.server.datatables.storage;

/**
 * GM物品紀錄
 * 
 * @author dexc
 * 
 */
public interface OtherUserToPcStorage {

    /**
     * 增加紀錄
     * 
     * @param itemname
     *            物品名稱
     * @param itemcount
     *            數量
     * @param pcobjid
     *            GM OBJID
     * @param pcname
     *            GM名稱
     * @param srcpcobjid
     *            對像OBJID
     * @param srcpcname
     *            對像名稱
     */
    public void add(final String itemname, final long itemcount, final int pcobjid, final String pcname, final int srcpcobjid, final String srcpcname);
}
