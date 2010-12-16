/*    */ package com.ontologycentral.eurostat;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.OutputStreamWriter;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class Dictionary
/*    */ {
/* 15 */   public static String PREFIX = "http://ontologycentral.com/2009/01/eurostat/";
/*    */ 
/* 17 */   public static void main(String[] args) throws IOException { if (args.length != 2) {
/* 18 */       System.err.println("USAGE: java Dictionary langdir outdir");
/* 19 */       System.exit(1);
/*    */     }
/*    */ 
/* 22 */     File langdir = new File(args[0]);
/* 23 */     File outdir = new File(args[1]);
/* 24 */     String lang = args[0];
/* 25 */     if (lang.endsWith("/")) {
/* 26 */       lang = lang.substring(0, 2);
/*    */     }
/*    */ 
/* 29 */     if ((!langdir.isDirectory()) || (!outdir.isDirectory())) {
/* 30 */       System.err.println("args have to be directories");
/* 31 */       System.exit(1);
/*    */     }
/*    */ 
/* 34 */     for (String fname : langdir.list()) {
/* 35 */       fname = fname.substring(0, fname.lastIndexOf('.'));
/*    */ 
/* 37 */       File f = new File(langdir + "/" + fname + ".dic");
/* 38 */       if (f.exists()) {
/* 39 */         System.out.print("converting " + fname + "...");
/* 40 */         BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "ISO_8859-1"));
/*    */ 
/* 42 */         PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outdir + "/" + fname + ".ttl", true), "utf-8"));
/*    */ 
/* 44 */         String line = null;
/*    */ 
/* 46 */         while ((line = br.readLine()) != null) {
/* 47 */           line = line.trim();
/* 48 */           if (line.length() <= 0)
/*    */             continue;
/*    */           try
/*    */           {
/* 52 */             StringTokenizer st = new StringTokenizer(line, "\t");
/* 53 */             String id = st.nextToken().trim();
/* 54 */             String label = st.nextToken().trim();
/*    */ 
/* 56 */             pw.println("<" + PREFIX + fname + "#" + id + "> <http://www.w3.org/2000/01/rdf-schema#label> \"" + label + "\"@" + lang + " .");
/*    */           } catch (NoSuchElementException ne) {
/* 58 */             System.err.println(line + " " + ne);
/*    */           }
/*    */         }
/* 61 */         pw.close();
/*    */ 
/* 63 */         br.close();
/* 64 */         System.out.println("done");
/*    */       }
/*    */     } }
/*    */ 
/*    */ }

/* Location:           /home/aharth/workspace/estatwrap/stat/dist/eurostat.jar
 * Qualified Name:     com.ontologycentral.eurostat.Dictionary
 * JD-Core Version:    0.5.4
 */