/*    */ package com.ontologycentral.eurostat;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.OutputStreamWriter;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class ConsumerPrices
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws IOException
/*    */   {
/* 16 */     if (args.length != 1) {
/* 17 */       System.err.println("USAGE: java ConsumerPrices cp0000.tsv");
/*    */     }
/*    */ 
/* 20 */     String fname = args[0];
/* 21 */     fname = fname.substring(0, fname.lastIndexOf(46));
/*    */ 
/* 23 */     BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname + ".tsv"), "ISO_8859-1"));
/*    */ 
/* 25 */     PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname + ".ttl", true), "utf-8"));
/*    */ 
/* 27 */     String line = null;
/*    */ 
/* 29 */     int row = 0;
/* 30 */     long bnodeid = 0L;
/*    */ 
/* 32 */     List header = new ArrayList();
/*    */ 
/* 34 */     while ((line = br.readLine()) != null) {
/* 35 */       StringTokenizer st = new StringTokenizer(line, "\t");
/*    */ 
/* 37 */       int col = 0;
/*    */ 
/* 39 */       String unit = null;
/* 40 */       String geo = null;
/*    */ 
/* 42 */       while (st.hasMoreTokens()) {
/* 43 */         String tok = st.nextToken().trim();
/* 44 */         if (row == 0) {
/* 45 */           header.add(tok);
/*    */         }
/* 47 */         else if (col == 0) {
/* 48 */           StringTokenizer st2 = new StringTokenizer(tok, ",");
/*    */ 
/* 50 */           unit = st2.nextToken();
/* 51 */           geo = st2.nextToken();
/*    */         } else {
/* 53 */           pw.println("_:bn" + fname + bnodeid + " <" + Dictionary.PREFIX + "ns#unit> <" + Dictionary.PREFIX + "unit#" + unit + "> .");
/* 54 */           pw.println("_:bn" + fname + bnodeid + " <" + Dictionary.PREFIX + "ns#geo> <" + Dictionary.PREFIX + "geo#" + geo + "> .");
/* 55 */           pw.println("_:bn" + fname + bnodeid + " <" + Dictionary.PREFIX + "ns#time> \"" + (String)header.get(col) + "\" .");
/* 56 */           pw.println("_:bn" + fname + bnodeid + " <" + Dictionary.PREFIX + "ns#value> \"" + tok + "\" .");
/*    */ 
/* 58 */           bnodeid += 1L;
/*    */         }
/*    */ 
/* 61 */         ++col;
/*    */       }
/* 63 */       ++row;
/*    */     }
/*    */ 
/* 66 */     pw.close();
/*    */ 
/* 68 */     br.close();
/*    */   }
/*    */ }

/* Location:           /home/aharth/workspace/estatwrap/stat/dist/eurostat.jar
 * Qualified Name:     com.ontologycentral.eurostat.ConsumerPrices
 * JD-Core Version:    0.5.4
 */