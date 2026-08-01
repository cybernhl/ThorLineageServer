/*    */ package com.lineage.data.event;
/*    */ 
/*    */ import com.lineage.data.executor.EventExecutor;
/*    */ import com.lineage.server.templates.L1Event;
/*    */ import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ public class lvreward_Trial extends EventExecutor
/*    */ {
/* 17 */   private static final Log _log = LogFactory.getLog(lvreward_Trial.class);
/*    */ 
/* 19 */   public static boolean START = false;
/*    */ 
/*    */   public static EventExecutor get()
/*    */   {
/* 28 */     return new lvreward_Trial();
/*    */   }
/*    */ 
/*    */   public void execute(L1Event event)
/*    */   {
/*    */     try {
/* 34 */       START = true;
/*    */ 
/* 36 */       //  lvgiveitemcount.get();
/*    */     }
/*    */     catch (Exception e) {
/* 39 */       _log.error(e.getLocalizedMessage(), e);
/*    */     }
/*    */   }
/*    */ }