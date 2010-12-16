/*     */ package com.ontologycentral.estatwrap.convert;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ class Line
/*     */ {
/* 175 */   List<String> _dim1 = new ArrayList();
/*     */   List<String> _cols;
/*     */ 
/*     */   public Line(String line)
/*     */   {
/* 179 */     List cols = new ArrayList();
/*     */ 
/* 181 */     StringTokenizer st = new StringTokenizer(line, "\t");
/* 182 */     while (st.hasMoreTokens()) {
/* 183 */       cols.add(st.nextToken().trim());
/*     */     }
/*     */ 
/* 186 */     String legend = (String)cols.get(0);
/*     */ 
/* 188 */     this._cols = cols.subList(1, cols.size());
/*     */ 
/* 190 */     st = new StringTokenizer(legend, ",");
/* 191 */     while (st.hasMoreTokens()) {
/* 192 */       String tok = st.nextToken().trim();
/* 193 */       this._dim1.add(tok);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<String> getCols() {
/* 198 */     return this._cols;
/*     */   }
/*     */ 
/*     */   public List<String> getDim1() {
/* 202 */     return this._dim1;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 206 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 208 */     sb.append(this._dim1);
/* 209 */     sb.append(":");
/* 210 */     sb.append(this._cols);
/*     */ 
/* 212 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           /home/aharth/workspace/estatwrap/stat/dist/eurostat.jar
 * Qualified Name:     com.ontologycentral.eurostat.Line
 * JD-Core Version:    0.5.4
 */