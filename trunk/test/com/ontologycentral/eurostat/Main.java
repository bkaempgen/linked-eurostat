/*    */ package com.ontologycentral.eurostat;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class Main
/*    */ {
/*    */   private static final String USAGE = "USAGE: java -jar eurostat.jar <utility> \nwhere <utility> one of\n\tData\t\t convert data from http://europa.eu/estatref/download/everybody/data/\n\tDictionary\t convert data from http://europa.eu/estatref/download/everybody/dic/\n\tGeoNames\t map geo dictionary entries to GeoNames";
/*    */   private static final String PREFIX = "com.ontologycentral.eurostat.";
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 16 */     if (args.length == 0)
/* 17 */       usage();
/*    */     else
/*    */       try {
/* 20 */         Class cls = Class.forName("com.ontologycentral.eurostat." + args[0]);
/*    */ 
/* 22 */         Method mainMethod = cls.getMethod("main", new Class[] { [Ljava.lang.String.class });
/*    */ 
/* 24 */         String[] mainArgs = new String[args.length - 1];
/* 25 */         System.arraycopy(args, 1, mainArgs, 0, mainArgs.length);
/*    */ 
/* 27 */         long time = System.currentTimeMillis();
/*    */ 
/* 29 */         mainMethod.invoke(null, new Object[] { mainArgs });
/*    */ 
/* 31 */         long time1 = System.currentTimeMillis();
/*    */ 
/* 33 */         System.err.println("time elapsed " + (time1 - time) + " ms");
/*    */       } catch (Throwable e) {
/* 35 */         e.printStackTrace();
/* 36 */         usage();
/*    */       }
/*    */   }
/*    */ 
/*    */   private static void usage()
/*    */   {
/* 42 */     System.err.println("USAGE: java -jar eurostat.jar <utility> \nwhere <utility> one of\n\tData\t\t convert data from http://europa.eu/estatref/download/everybody/data/\n\tDictionary\t convert data from http://europa.eu/estatref/download/everybody/dic/\n\tGeoNames\t map geo dictionary entries to GeoNames");
/* 43 */     System.exit(-1);
/*    */   }
/*    */ }

/* Location:           /home/aharth/workspace/estatwrap/stat/dist/eurostat.jar
 * Qualified Name:     com.ontologycentral.eurostat.Main
 * JD-Core Version:    0.5.4
 */