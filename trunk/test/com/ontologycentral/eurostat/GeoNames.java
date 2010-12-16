/*    */ package com.ontologycentral.eurostat;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.OutputStreamWriter;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.net.ConnectException;
/*    */ import java.util.List;
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.StringTokenizer;
/*    */ import org.geonames.Toponym;
/*    */ import org.geonames.ToponymSearchCriteria;
/*    */ import org.geonames.ToponymSearchResult;
/*    */ import org.geonames.WebService;
/*    */ 
/*    */ public class GeoNames
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 22 */     if (args.length != 2) {
/* 23 */       System.err.println("USAGE: java GeoNames geo.dic geo-map.ttl");
/* 24 */       System.exit(1);
/*    */     }
/*    */ 
/* 27 */     String fname = args[0];
/* 28 */     String outfname = args[1];
/*    */ 
/* 30 */     File f = new File(fname);
/*    */ 
/* 32 */     BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "ISO_8859-1"));
/* 33 */     PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfname), "utf-8"));
/*    */ 
/* 35 */     String line = null;
/* 36 */     while ((line = br.readLine()) != null) {
/* 37 */       line = line.trim();
/* 38 */       if (line.length() <= 0)
/*    */         continue;
/*    */       try
/*    */       {
/* 42 */         StringTokenizer st = new StringTokenizer(line, "\t");
/* 43 */         String id = st.nextToken().trim().toLowerCase();
/* 44 */         String label = st.nextToken();
/*    */ 
/* 46 */         long uri = getURI(label);
/* 47 */         if (uri > 0L)
/* 48 */           pw.println("<" + Dictionary.PREFIX + "geo#" + id + "> <http://www.w3.org/2002/07/owl#sameAs> <http://sws.geonames.org/" + uri + "/> .");
/*    */       }
/*    */       catch (NoSuchElementException ne) {
/* 51 */         System.err.println(line + " " + ne);
/*    */       } catch (ConnectException ce) {
/* 53 */         System.err.println(ce.getMessage());
/*    */       }
/*    */     }
/*    */ 
/* 57 */     pw.close();
/*    */   }
/*    */ 
/*    */   public static long getURI(String text) throws Exception {
/* 61 */     ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
/* 62 */     searchCriteria.setQ(text);
/* 63 */     ToponymSearchResult searchResult = WebService.search(searchCriteria);
/* 64 */     List li = searchResult.getToponyms();
/*    */ 
/* 66 */     System.out.print(text + " matched with ");
/*    */ 
/* 68 */     if (li.size() > 0) {
/* 69 */       System.out.println(((Toponym)li.get(0)).getName());
/* 70 */       return ((Toponym)li.get(0)).getGeoNameId();
/*    */     }
/* 72 */     System.out.println(" - ");
/*    */ 
/* 75 */     return -1L;
/*    */   }
/*    */ }

/* Location:           /home/aharth/workspace/estatwrap/stat/dist/eurostat.jar
 * Qualified Name:     com.ontologycentral.eurostat.GeoNames
 * JD-Core Version:    0.5.4
 */