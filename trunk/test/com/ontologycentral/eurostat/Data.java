/*     */ package com.ontologycentral.eurostat;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.List;
/*     */ import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.ontologycentral.estatwrap.convert.Header;
import com.ontologycentral.estatwrap.convert.Line;
/*     */ 
/*     */ public class Data
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws IOException
/*     */   {
/*  19 */     if (args.length != 2) {
/*  20 */       System.err.println("USAGE: java Data datadir outdir");
/*  21 */       System.exit(1);
/*     */     }
/*     */ 
/*  24 */     File datadir = new File(args[0]);
/*  25 */     File outdir = new File(args[1]);
/*     */ 
/*  27 */     if ((!datadir.isDirectory()) || (!outdir.isDirectory())) {
/*  28 */       System.err.println("args have to be directories");
/*  29 */       System.exit(1);
/*     */     }
/*     */ 
/*  32 */     for (String fname : datadir.list()) {
/*  33 */       fname = fname.substring(0, fname.indexOf(46));
/*     */ 
/*  35 */       File f = new File(datadir + "/" + fname + ".tsv.gz");
/*  36 */       if (f.exists()) {
/*  37 */         System.out.print("converting " + fname + "...");
/*     */         try {
/*  39 */           convert(fname, f, outdir);
/*     */         } catch (IOException ioex) {
/*  41 */           System.err.println(ioex.getMessage());
/*     */         }
/*  43 */         System.out.println("done");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void convert(String fname, File f, File outdir) throws IOException {
/*  49 */     BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f)), "ISO_8859-1"));
/*  50 */     File outf = new File(outdir + "/" + fname + ".ttl.gz");
/*     */ 
/*  52 */     if (!outf.exists()) {
/*  53 */       PrintWriter pw = new PrintWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outf)), "utf-8"));
/*     */ 
/*  55 */       String line = null;
/*     */ 
/*  57 */       int rows = 0;
/*  58 */       Header h = null;
/*  59 */       Line l = null;
/*     */ 
/*  61 */       if ((line = br.readLine()) != null) {
/*  62 */         ++rows;
/*  63 */         line = line.trim();
/*  64 */         if (line.length() <= 0) {
/*  65 */           System.err.println("could not read header!");
/*     */         }
/*     */ 
/*  68 */         h = new Header(line);
/*     */       }
/*     */ 
/*  71 */       while ((line = br.readLine()) != null) {
/*  72 */         ++rows;
/*  73 */         line = line.trim();
/*  74 */         if (line.length() <= 0) {
/*     */           continue;
/*     */         }
/*     */ 
/*  78 */         l = new Line(line);
/*     */ 
/*  80 */         toTurtle(h, l, pw, fname, rows);
/*     */       }
/*     */ 
/*  83 */       pw.close();
/*     */ 
/*  85 */       br.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void toTurtle(Header h, Line l, PrintWriter pw, String fname, int bnodeid) {
/*  90 */     List hd1 = h.getDim1();
/*  91 */     List ld1 = l.getDim1();
/*     */ 
/*  93 */     if (hd1.size() != ld1.size()) {
/*  94 */       System.err.println("header dimensions and line dimensions don't match!");
/*     */     }
/*     */ 
/*  97 */     List hcol = h.getCols();
/*  98 */     List lcol = l.getCols();
/*     */ 
/* 100 */     if (hcol.size() != lcol.size()) {
/* 101 */       System.err.println("header columns and line columns don't match!");
/*     */     }
/*     */ 
/* 104 */     for (int i = 0; i < hcol.size(); ++i)
/*     */     {
/* 106 */       if (((String)lcol.get(i)).equals(":")) {
/*     */         continue;
/*     */       }
/* 109 */       String bnode = "_:bn" + fname + bnodeid + (String)hcol.get(i);
/*     */ 
/* 111 */       for (int j = 0; j < hd1.size(); ++j) {
/* 112 */         pw.println(bnode + " <" + Dictionary.PREFIX + "ns#" + (String)hd1.get(j) + "> <" + Dictionary.PREFIX + (String)hd1.get(j) + "#" + (String)ld1.get(j) + "> .");
/*     */       }
/*     */ 
/* 115 */       pw.println(bnode + " <" + Dictionary.PREFIX + "ns#" + h.getDim2() + "> <" + Dictionary.PREFIX + h.getDim2() + "#" + (String)hcol.get(i) + "> .");
/* 116 */       pw.println(bnode + " <" + Dictionary.PREFIX + "ns#value> \"" + (String)lcol.get(i) + "\" .");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/aharth/workspace/estatwrap/stat/dist/eurostat.jar
 * Qualified Name:     com.ontologycentral.eurostat.Data
 * JD-Core Version:    0.5.4
 */